package co.entomo.gdhcn.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import co.entomo.gdhcn.vo.ManifestRequest;
import co.entomo.gdhcn.vo.ValidateCwtResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import co.entomo.gdhcn.exceptions.GdhcnValidationException;
import co.entomo.gdhcn.service.GdhcnService;
import co.entomo.gdhcn.vo.QrCodeRequest;
import co.entomo.gdhcn.vo.StepStatus;
import lombok.AllArgsConstructor;
/**
 * @author Uday Matta
 * @organization Entomo Labs
 * REST controller for handling GDHCN-related requests.
 * This controller provides endpoints for issuing and validating VSHC (Vaccination Status Health Certificate),
 * retrieving IPS JSON data, and getting manifests.
 */
@RestController
@AllArgsConstructor
public class GdhcnController 
{
	private final GdhcnService gdhcnService;

	/**
	 * Endpoint for issuing a VSHC (Vaccination Status Health Certificate).
	 *
	 * @param qrCodeRequest the request body containing the QR code data.
	 * @return a {@link ResponseEntity} containing the issued VSHC in JSON format.
	 * @throws GdhcnValidationException if there is a validation error.
	 */

	@PostMapping(value = "/v2/vshcIssuance", produces = {"application/json"})
	public ResponseEntity<String> vshcIssuance(@RequestBody QrCodeRequest qrCodeRequest) throws GdhcnValidationException
	{
		String cwt = gdhcnService.vshcIssuance(qrCodeRequest);
		return ResponseEntity.of(Optional.of(cwt));
	}

	/**
	 * Endpoint for validating a VSHC.
	 *
	 * @param body the request body containing the QR code content.
	 * @return a {@link ResponseEntity} containing the validation response in FHIR JSON format.
	 * @throws GdhcnValidationException if there is a validation error.
	 */
	@PostMapping(value = "/v2/vshcValidation", produces = {"application/fhir+json"})
	public ResponseEntity<ValidateCwtResponse> vshcValidation(@RequestBody Map<String, String> body) throws GdhcnValidationException
	{
		String qrCodeContent = body.get("qrCodeContent").toString();
		ValidateCwtResponse status = gdhcnService.vshcValidation(qrCodeContent);
		return ResponseEntity.of(Optional.of(status));
	}
	/**
	 * Endpoint for retrieving IPS JSON data.
	 *
	 * @param jsonId the ID of the JSON data to retrieve.
	 * @return a {@link ResponseEntity} containing the JSON content.
	 */
	@CrossOrigin(origins = "*", allowedHeaders = "*")
	@GetMapping(value = "/v2/ips-json/{jsonId}", produces = {"application/json"})
	public ResponseEntity<String> getIpsJson(@PathVariable("jsonId") String jsonId)
	{
		String jsonContent = gdhcnService.downloadJson(jsonId);
		return ResponseEntity.of(Optional.of(jsonContent));
	}
	/**
	 * Endpoint for getting manifest data.
	 *
	 * @param manifestRequest the request body containing the manifest request data.
	 * @param jsonId          the ID of the JSON data for the manifest.
	 * @return a {@link ResponseEntity} containing the manifest data in JSON format.
	 * @throws GdhcnValidationException if there is a validation error.
	 */
	@CrossOrigin(origins = "*", allowedHeaders = "*")
	@PostMapping(value = "/v2/manifests/{jsonId}", produces = {"application/json"})
	public ResponseEntity<Map<String, List<Map<String,String>>>> getManifest(@RequestBody ManifestRequest manifestRequest, @PathVariable("jsonId") String jsonId) throws GdhcnValidationException {
		Map<String, List<Map<String, String>>> response = null;
		try {
			response = gdhcnService.getManifest(manifestRequest, jsonId);
		} catch (GdhcnValidationException e) {
			throw e;
		}
		return ResponseEntity.of(Optional.of(response));
	}
}
