package ru.kmwork.rabbitmq;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

@Slf4j
@UtilityClass
public class SslUtils {

    private static final String KEY_PASSWORD = "12345678";
    public static final String SSL_PROTOCOL = "TLSv1.2";

    static {
        System.setProperty("javax.net.debug", "all");
    }


    private static byte[] readFileFromPemFile(String nameTag, String fileResourceName) throws Exception {
        String pem = IOUtils.resourceToString(fileResourceName, StandardCharsets.UTF_8);
        Pattern parse = Pattern.compile("(?m)(?s)^---*BEGIN ([^-]+)---*$([^-]+)^---*END[^-]+-+$");
        Matcher m = parse.matcher(pem);
        Base64.Decoder decoder = Base64.getMimeDecoder();

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

    private static void addCert(List<Certificate> certList, String certPath) throws Exception {
        final byte[] certBytes = readFileFromPemFile("CERTIFICATE", "/rabbitmq_keys/" + certPath);
        final CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
        Certificate cert = certFactory.generateCertificate(new ByteArrayInputStream(certBytes));
        certList.add(cert);
    }

    public static SSLContext createSslContext() throws Exception {

        List<Certificate> certList = new ArrayList<>();

        addCert(certList, "client_certificate.pem");
        addCert(certList, "ca_certificate.pem");


        PrivateKey privateKey = getPrimaryKey("/rabbitmq_keys/client_key.p12", KEY_PASSWORD);

        if (privateKey == null)
            throw new RuntimeException("RSA private key not found in PEM file");

        char[] keyStorePassword = KEY_PASSWORD.toCharArray();

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
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(keyStore, keyStorePassword);
        SSLContext sslContext = SSLContext.getInstance(SSL_PROTOCOL);
        sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), new SecureRandom());

        return sslContext;
    }


    private static PrivateKey getPrimaryKey(String pathToPKCS12File, String keyPassword) {
        try {
            byte[] keyAsBytes = IOUtils.resourceToByteArray(pathToPKCS12File);
            KeyStore ks = KeyStore.getInstance("PKCS12");
            ks.load(new ByteArrayInputStream(keyAsBytes), keyPassword.toCharArray());
            return (PrivateKey) ks.getKey(ks.aliases().nextElement(), keyPassword.toCharArray());
        } catch (Exception e) {
            log.error("[Private:Key] ошибка чтения ");
            System.exit(-122);
        }
        return null;
    }

}
