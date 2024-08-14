package co.entomo.gdhcn.service;


import java.util.List;
import java.util.Map;

import co.entomo.gdhcn.exceptions.GdhcnValidationException;
import co.entomo.gdhcn.vo.ManifestRequest;
import co.entomo.gdhcn.vo.QrCodeRequest;
import co.entomo.gdhcn.vo.StepStatus;
import co.entomo.gdhcn.vo.ValidateCwtResponse;

/**
 * The {@code GdhcnService} interface provides methods for issuing and validating QR codes,
 * retrieving manifests, and downloading JSON data, including JSON Web Encryption (JWE) formatted data.
 */
public interface GdhcnService {

	/**
	 * Issues a QR code based on the provided {@link QrCodeRequest}.
	 *
	 * @param qrCodeRequest the request object containing data required to generate the QR code.
	 * @return a {@code String} representing the issued QR code or related identifier.
	 * @throws GdhcnValidationException if validation errors occur during the issuance process.
	 */
	String vshcIssuance(QrCodeRequest qrCodeRequest) throws GdhcnValidationException;

	/**
	 * Validates the encoded content of a QR code.
	 *
	 * @param qrCodeEncodedContent the encoded content of the QR code to be validated.
	 * @return a {@link ValidateCwtResponse} containing the results of the validation.
	 * @throws GdhcnValidationException if validation errors occur during the validation process.
	 */
	ValidateCwtResponse vshcValidation(String qrCodeEncodedContent) throws GdhcnValidationException;

	/**
	 * Retrieves a manifest based on the provided {@link ManifestRequest} and JSON identifier.
	 *
	 * @param manifestRequest the request object containing details required to fetch the manifest.
	 * @param jsonId the identifier for the specific JSON data or manifest being requested.
	 * @return a {@code Map<String, List<Map<String, String>>>} representing the manifest data.
	 * @throws GdhcnValidationException if validation errors occur during the process of fetching the manifest.
	 */
	Map<String, List<Map<String,String>>> getManifest(ManifestRequest manifestRequest, String jsonId) throws GdhcnValidationException;

	/**
	 * Downloads JSON Web Encryption (JWE) formatted JSON data based on the provided JSON identifier.
	 *
	 * @param jsonId the identifier for the JWE JSON data to be downloaded.
	 * @return a {@code String} representing the JWE JSON data.
	 */
	String downloadJWEJson(String jsonId);

	/**
	 * Downloads standard JSON data (not JWE) based on the provided JSON identifier.
	 *
	 * @param jsonId the identifier for the JSON data to be downloaded.
	 * @return a {@code String} representing the JSON data.
	 */
	String downloadJson(String jsonId);
}
