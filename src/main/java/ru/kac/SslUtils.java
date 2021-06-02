package ru.kac;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.crypto.EncryptedPrivateKeyInfo;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

@Slf4j
public class SslUtils {

    public static AppSslContextFactory createSslFactory() throws Exception {
        try {
//            SSLContext sslContext = SSLContext.getInstance("TLS");
//            System.setProperty("javax.net.ssl.trustStore","rabbitmq_keys/ca_certificate.pem");
//
//            KeyManager[] keyManager = getKeyManager("pkcs12", "src/main/resources/rabbitmq_keys/client_key.p12", "bunnies");
//            TrustManager[] trustManager = getTrustManager("jks", "path/to/CA.truststore", "trust_store_password");
//            sslContext.init(keyManager, trustManager, new SecureRandom());
            SSLContext sslContext = createSslContext();
            return new AppSslContextFactory(sslContext);
        } catch (Exception e) {
            throw new RuntimeException("Unable to setup keystore and truststore", e);
        }
    }

    /**
     * Some useful commands for looking at the client certificate and private key:
     * keytool -keystore certificate.p12 -list -storetype pkcs12 -v
     * openssl pkcs12 -info -in certificate.p12
     */
    private static KeyManager[] getKeyManager(String keyStoreType, String keyStoreFile, String keyStorePassword) throws Exception {
        KeyStore keyStore = KeyStore.getInstance(keyStoreType);
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyStore.load(ClassLoader.getSystemClassLoader().getResourceAsStream(keyStoreFile), keyStorePassword.toCharArray());
        kmf.init(keyStore, keyStorePassword.toCharArray());

        return kmf.getKeyManagers();
    }

    /**
     * Depending on what format (pem / cer / p12) you have received the CA in, you will need to use a combination of openssl and keytool
     * to convert it to JKS format in order to be loaded into the truststore using the method below.
     * <p>
     * You could of course use keytool to import this into the JREs TrustStore - my situation mandated I create it on the fly.
     * <p>
     * Useful command to look at the CA certificate:
     * keytool -keystore root_ca.truststore -list -storetype jks -v
     */
    private static TrustManager[] getTrustManager(String trustStoreType, String trustStoreFile, String trustStorePassword) throws Exception {
        KeyStore trustStore = KeyStore.getInstance(trustStoreType);
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustStore.load(ClassLoader.getSystemClassLoader().getResourceAsStream(trustStoreFile), trustStorePassword.toCharArray());
        tmf.init(trustStore);

        return tmf.getTrustManagers();
    }

    private static byte[] readFileFromPemFile(String nameTag, String fileResourceName) throws Exception {
        String pem = IOUtils.resourceToString(fileResourceName, StandardCharsets.UTF_8);
        Pattern parse = Pattern.compile("(?m)(?s)^---*BEGIN ([^-]+)---*$([^-]+)^---*END[^-]+-+$");
        Matcher m = parse.matcher(pem);
        CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
        Base64.Decoder decoder = Base64.getMimeDecoder();
        List<Certificate> certList = new ArrayList<>(); // java.security.cert.Certificate

        PrivateKey privateKey = null;

        int start = 0;
        while (m.find(start)) {
            String type = m.group(1);
            String base64Data = m.group(2);
            byte[] data = decoder.decode(base64Data);
            start += m.group(0).length();
            type = type.toUpperCase();
            if (type.contains(nameTag)) {
                log.debug("[SSL:Key] Успех, данные найдены: по nameTag = {}, fileResourceName = {}", nameTag, fileResourceName);
                return data;
            }
        }
        log.error("[SSL:Key] Нет данных по nameTag = {}, fileResourceName = {}", nameTag, fileResourceName);
        return null;
    }

    private static SSLContext createSslContext() throws Exception {

        CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
        List<Certificate> certList = new ArrayList<>(); // java.security.cert.Certificate

        byte[] certClientBytes = readFileFromPemFile("CERTIFICATE", "/rabbitmq_keys/client_certificate.pem");
        byte[] certServerBytes = readFileFromPemFile("CERTIFICATE", "/rabbitmq_keys/server_certificate.pem");
        byte[] privateKeyBytes = readFileFromPemFile("ENCRYPTED PRIVATE KEY", "/rabbitmq_keys/client_key.pem");
        Certificate clientCert = certFactory.generateCertificate(new ByteArrayInputStream(certClientBytes));
        certList.add(clientCert);

        Certificate serverCert = certFactory.generateCertificate(new ByteArrayInputStream(certServerBytes));
        certList.add(serverCert);


        EncryptedPrivateKeyInfo pkInfo = new EncryptedPrivateKeyInfo(privateKeyBytes);
        PBEKeySpec keySpec = new PBEKeySpec("bunnies".toCharArray()); // password
        SecretKeyFactory pbeKeyFactory = SecretKeyFactory.getInstance(pkInfo.getAlgName());
        PKCS8EncodedKeySpec encodedKeySpec = pkInfo.getKeySpec(pbeKeyFactory.generateSecret(keySpec));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(encodedKeySpec);

        if (privateKey == null)
            throw new RuntimeException("RSA private key not found in PEM file");

        char[] keyStorePassword = new char[0];

        KeyStore keyStore = KeyStore.getInstance("JKS");
        keyStore.load(null, null);

        int count = 0;
        for (Certificate cert : certList) {
            keyStore.setCertificateEntry("cert" + count, cert);
            count++;
        }
        Certificate[] chain = certList.toArray(new Certificate[certList.size()]);
        keyStore.setKeyEntry("key", privateKey, keyStorePassword, chain);

        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(keyStore);
        KeyManagerFactory kmf = KeyManagerFactory.getInstance("RSA");
        kmf.init(keyStore, keyStorePassword);
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), new SecureRandom());
        return sslContext;
    }
}
