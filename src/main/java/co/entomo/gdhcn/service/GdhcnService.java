package co.entomo.gdhcn.service;


import java.util.List;
import java.util.Map;

import co.entomo.gdhcn.exceptions.GdhcnValidationException;
import co.entomo.gdhcn.vo.ManifestRequest;
import co.entomo.gdhcn.vo.QrCodeRequest;
import co.entomo.gdhcn.vo.StepStatus;
import co.entomo.gdhcn.vo.ValidateCwtResponse;

public interface GdhcnService 
{
	String uploadIpsJson(QrCodeRequest qrCodeRequest) throws GdhcnValidationException;
	ValidateCwtResponse validateCwt(String qrCodeEncodedContent)  throws GdhcnValidationException;
	Map<String, List<Map<String,String>>> getManifest(ManifestRequest manifestRequest, String jsonId) throws GdhcnValidationException;
	String downloadJWEJson(String jsonId);
	String downloadJson(String jsonId);
}
