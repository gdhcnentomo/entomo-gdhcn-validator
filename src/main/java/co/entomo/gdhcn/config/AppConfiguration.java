package co.entomo.gdhcn.config;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfiguration {
	@Value("${tng.tls.pem}")
	private String certificateFile;;
	@Value("${tng.tls.key}")
	private String keyFile;
	@Bean
	public ModelMapper modelMapper() {
	    return new ModelMapper();
	}

	private String readFileToString(String filePath) throws IOException {
        StringBuilder content = new StringBuilder();
        try (FileInputStream fis = new FileInputStream(filePath);
             InputStreamReader isr = new InputStreamReader(fis);
             BufferedReader reader = new BufferedReader(isr)) {

            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append(System.lineSeparator());
            }
        }
        return content.toString();
    }
	@Bean
	public HttpClient httpClient()  throws Exception
	{
		CertificateFactory cf = CertificateFactory.getInstance("X.509");
        FileInputStream certInputStream = new FileInputStream(certificateFile);
        X509Certificate certificate = (X509Certificate) cf.generateCertificate(certInputStream);

        // Load the private key
        FileInputStream keyInputStream = new FileInputStream(keyFile);
        String privakeKeyContent = readFileToString(keyFile);
        
        privakeKeyContent = privakeKeyContent.replace("-----BEGIN PRIVATE KEY-----", "")
        .replace("-----END PRIVATE KEY-----", "")
        .replaceAll("\\s", "");
        
        byte[] privateKeyDER = Base64.getDecoder().decode(privakeKeyContent);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyDER);
        KeyFactory keyFactory = KeyFactory.getInstance("EC"); // Change to "EC
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);

        // Create a KeyStore and load the certificate and private key
        KeyStore keyStore = KeyStore.getInstance("JKS");
        keyStore.load(null, null);
        keyStore.setKeyEntry("alias", privateKey, new char[0], new java.security.cert.Certificate[]{certificate});

        // Create an SSLContext using the KeyStore
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(keyStore, new char[0]);
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(kmf.getKeyManagers(), null, null);

        // Create an HttpClient with the SSLContext
        HttpClient client = HttpClient.newBuilder().sslContext(sslContext)
                .build();
        return client;
	}
}
