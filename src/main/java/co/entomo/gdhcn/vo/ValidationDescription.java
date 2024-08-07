package co.entomo.gdhcn.vo;

public enum ValidationDescription {
    DECODE_BASE45("Decoding Base45 QR", "Incorrect QR Payload. Unable to decode. Check if the payload is Base45 Encoded and has \"HC1:\" prefixed"),
    DEFLATE_COSE_BYTES("Decompressing (Deflate) decoded QR Payload", "Unable to deflate decoded payload. Ensure zlib compression is used"),
    CONVERT_COSE_MESSAGE("Converting Decompressed Payload to CWT", "Unable to convert payload to CWT"),
    COSE_MESSAGE_PAYLOAD_TO_JSON("Extracting Claims from CWT", "Unable to extract claims from CWT. Ensure HCERT claim is tagged to -260 and subclaim as 5 (Smart Health Link)"),
    EXTRACT_COUNTRY_CODE("Extracting Country Code", "Unable to extract country code"),
    FETCH_PUBLIC_KEY_GDHCN("Connecting & Fetching Public Key from GDHCN", "Unable to fetch public key from GDHCN. Ensure the country is onboarded to GDCHN and public keys are active"),
    VALIDATE_SIGNATURE("Validating Signature", "Unable to validate the signature with keys from GDHCN"),
    EXTRACT_HCERT("Extracting Smart Health Link", "Unable to extract Smart Health Link from CWT. Ensure Smart Health Link is formed as per guidelines"),
    VALIDATE_EXPIRY("Verifying SHL QR Expiry", "Smart Health Link QR has been expired");

    private final String description;
    private final String errorMessage;

    private ValidationDescription(String description, String errorMessage) {
        this.description = description;
        this.errorMessage = errorMessage;
    }

    public String getDescription() {
        return description;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
