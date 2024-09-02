package co.entomo.gdhcn.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import co.entomo.gdhcn.exceptions.GdhcnIPSAlreadyAccessedException;
import co.entomo.gdhcn.vo.*;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import co.entomo.gdhcn.exceptions.GdhcnValidationException;
import co.entomo.gdhcn.service.GdhcnService;
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
	public ResponseEntity<ValidateCwtResponse> vshcValidation(@Valid @RequestBody ValidateRequest body) throws GdhcnValidationException
	{
		ValidateCwtResponse status = gdhcnService.vshcValidation(body.getQrCodeContent());
		return ResponseEntity.of(Optional.of(status));
	}
	/**
	 * Endpoint for retrieving IPS JSON data.
	 *
	 * @param manifestId the ID of the JSON data to retrieve.
	 * @return a {@link ResponseEntity} containing the JSON content.
	 */
	@CrossOrigin(origins = "*", allowedHeaders = "*")
	@GetMapping(value = "/v2/ips-json/{manifestId}", produces = {"application/json"})
	public ResponseEntity<String> getIpsJson(@PathVariable("manifestId") String manifestId) throws GdhcnValidationException {
		String jsonContent = gdhcnService.downloadJson(manifestId);
		return ResponseEntity.of(Optional.of(jsonContent));
	}
	/**
	 * Endpoint for getting manifest data.
	 *
	 * @param manifestRequest the request body containing the manifest request data.
	 * @param manifestId          the ID of the JSON data for the manifest.
	 * @return a {@link ResponseEntity} containing the manifest data in JSON format.
	 * @throws GdhcnValidationException if there is a validation error.
	 */
	@CrossOrigin(origins = "*", allowedHeaders = "*")
	@PostMapping(value = "/v2/manifests/{manifestId}", produces = {"application/json"})
	public ResponseEntity<Map<String, List<Map<String,String>>>> getManifest(@RequestBody ManifestRequest manifestRequest, @PathVariable("manifestId") String manifestId) throws GdhcnValidationException {
		Map<String, List<Map<String, String>>> response = null;
		try {
			response = gdhcnService.getManifest(manifestRequest, manifestId);
		} catch (GdhcnValidationException e) {
			throw e;
		}
		return ResponseEntity.of(Optional.of(response));
	}
}
