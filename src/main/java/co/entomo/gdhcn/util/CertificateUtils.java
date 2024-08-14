package co.entomo.gdhcn.util;

import java.io.ByteArrayInputStream;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

import org.springframework.stereotype.Component;

import co.entomo.gdhcn.exceptions.GdhcnValidationException;
import lombok.extern.slf4j.Slf4j;
import nl.minvws.encoding.Base45;
/**
 *  @author Uday Matta
 *  @organization entomo Labs
 * Utility class for handling cryptographic operations related to certificates.
 * This class provides methods for extracting private keys, generating Key Identifiers (KID),
 * and extracting public keys from certificate data.
 */
@Slf4j
@Component
public class CertificateUtils {

	/**
	 * Extracts a {@link PrivateKey} from the given PEM-formatted private key content.
	 *
	 * @param privateKeyContent the PEM-formatted private key as a {@code String}.
	 * @param countryCode the country code associated with the private key (not currently used in the method).
	 * @return the extracted {@link PrivateKey}, or {@code null} if an error occurs.
	 */
	public PrivateKey getPrivateKey(String privateKeyContent, String countryCode) 
	{
		try 
		{
			String privateKeyPEMTrimmed = privateKeyContent
			        .replace("-----BEGIN PRIVATE KEY-----", "")
			        .replace("-----END PRIVATE KEY-----", "")
			        .replaceAll("\\s", "");
			// Base64 decode the data
			byte[] privateKeyDER = Base64.getDecoder().decode(privateKeyPEMTrimmed);
			PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyDER);
			KeyFactory keyFactory = KeyFactory.getInstance("EC"); // Change to "EC
			PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
			return privateKey;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * Generates a Key Identifier (KID) by extracting the Subject Key Identifier (SKI)
	 * extension from the given public key content.
	 *
	 * @param publicKeyContent the Base64-encoded public key as a {@code String}.
	 * @return a byte array representing the truncated KID (first 8 bytes of the SHA-256 hash).
	 * @throws GdhcnValidationException if the KID cannot be generated.
	 */
	public byte[] getKid(String publicKeyContent) throws GdhcnValidationException{
        try {
            //byte[] certBytes = publicKeyContent.getBytes(StandardCharsets.UTF_8);
        	byte[] certBytes = Base64.getDecoder().decode(publicKeyContent);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(certBytes);

            // Load the certificate from the input stream
            CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
            X509Certificate certificate = (X509Certificate) certFactory.generateCertificate(inputStream);
            byte[] subjectKeyId = certificate.getExtensionValue("2.5.29.14");
            
            byte[] keyIdentifier = new byte[subjectKeyId.length - 4];
            System.arraycopy(subjectKeyId, 4, keyIdentifier, 0, keyIdentifier.length);
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(keyIdentifier);
            byte[] truncated = new byte[8];
            System.arraycopy(encodedhash, 0, truncated, 0, truncated.length);
            return truncated;
        } catch (CertificateException e) {
            log.info("CertificateException" ,e);
        } catch (NoSuchAlgorithmException e) {
            log.info("NoSuchAlgorithmException", e);
        }
        throw new GdhcnValidationException("Unable to find Kid from Public Key");
    }
	/**
	 * Extracts a {@link PublicKey} from the given Base64-encoded public key content.
	 *
	 * @param publicKeyContent the Base64-encoded public key content as a {@code String}.
	 * @return the extracted {@link PublicKey}, or {@code null} if an error occurs.
	 */
	public PublicKey getPublicKey(String publicKeyContent) 
	{
		try {
			byte[] certificateDER = Base64.getDecoder().decode(publicKeyContent);

			// Load certificate
			CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
			ByteArrayInputStream inputStream = new ByteArrayInputStream(certificateDER);
			Certificate certificate = certificateFactory.generateCertificate(inputStream);

			PublicKey publicKey = certificate.getPublicKey();
			return publicKey;
		} catch (CertificateException e) {
			e.printStackTrace();
		}
		return null;
	}
}
