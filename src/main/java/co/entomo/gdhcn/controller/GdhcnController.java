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

@RestController
@AllArgsConstructor
public class GdhcnController 
{
	private final GdhcnService gdhcnService;
	@PostMapping(value = "/v2/gdhcnIps", produces = {"application/json"})
	public ResponseEntity<String> gdhcnIps(@RequestBody QrCodeRequest qrCodeRequest) throws GdhcnValidationException
	{
		String cwt = gdhcnService.uploadIpsJson(qrCodeRequest);
		return ResponseEntity.of(Optional.of(cwt));
	}
	
	@PostMapping(value = "/v2/validateCwt", produces = {"application/fhir+json"})
	public ResponseEntity<ValidateCwtResponse> validateCwtContent(@RequestBody Map<String, String> body) throws GdhcnValidationException
	{
		String qrCodeContent = body.get("qrCodeContent").toString();
		ValidateCwtResponse status = gdhcnService.validateCwt(qrCodeContent);
		return ResponseEntity.of(Optional.of(status));
	}
	@CrossOrigin(origins = "*", allowedHeaders = "*")
	@GetMapping(value = "/v2/ips-json/{jsonId}", produces = {"application/json"})
	public ResponseEntity<String> getIpsJson(@PathVariable("jsonId") String jsonId)
	{
		String jsonContent = gdhcnService.downloadJson(jsonId);
		return ResponseEntity.of(Optional.of(jsonContent));
	}
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
