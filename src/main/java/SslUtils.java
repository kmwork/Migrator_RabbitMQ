import java.security.KeyStore;
import java.security.SecureRandom;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

public class SslUtils {

    public static AppSslContextFactory createSslFactory() throws Exception {
        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");

            KeyManager[] keyManager = getKeyManager("pkcs12", "src/main/resources/rabbitmq_keys/client_key.p12", "bunnies");
            TrustManager[] trustManager = getTrustManager("jks", "path/to/CA.truststore", "trust_store_password");
            sslContext.init(keyManager, trustManager, new SecureRandom());

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
//    private SSLContext createSslContext() throws Exception {
//
//        String pem = IOUtils.resourceToString("rabbitmq_keys/client_key.pem", StandardCharsets.UTF_8);
//        Pattern parse = Pattern.compile("(?m)(?s)^---*BEGIN ([^-]+)---*$([^-]+)^---*END[^-]+-+$");
//        Matcher m = parse.matcher(pem);
//        CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
//        Base64.Decoder decoder = Base64.getMimeDecoder();
//        List<Certificate> certList = new ArrayList<>(); // java.security.cert.Certificate
//
//        PrivateKey privateKey = null;
//
//        int start = 0;
//        while (m.find(start)) {
//            String type = m.group(1);
//            String base64Data = m.group(2);
//            byte[] data = decoder.decode(base64Data);
//            start += m.group(0).length();
//            type = type.toUpperCase();
//            if (type.contains("CERTIFICATE")) {
//                Certificate cert = certFactory.generateCertificate(new ByteArrayInputStream(data));
//                certList.add(cert);
//            } else if (type.contains("RSA PRIVATE KEY")) {
//                // TODO: load and parse PKCS1 data structure to get the RSA private key
//                privateKey = ...
//            } else {
//                System.err.println("Unsupported type: " + type);
//            }
//
//        }
//        if (privateKey == null)
//            throw new RuntimeException("RSA private key not found in PEM file");
//
//        char[] keyStorePassword = new char[0];
//
//        KeyStore keyStore = KeyStore.getInstance("JKS");
//        keyStore.load(null, null);
//
//        int count = 0;
//        for (Certificate cert : certList) {
//            keyStore.setCertificateEntry("cert" + count, cert);
//            count++;
//        }
//        Certificate[] chain = certList.toArray(new Certificate[certList.size()]);
//        keyStore.setKeyEntry("key", privateKey, keyStorePassword, chain);
//
//        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
//        tmf.init(keyStore);
//        KeyManagerFactory kmf = KeyManagerFactory.getInstance("RSA");
//        kmf.init(keyStore, keyStorePassword);
//        SSLContext sslContext = SSLContext.getInstance("TLS");
//        sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), new SecureRandom());
//        return sslContext;
//    }
}
