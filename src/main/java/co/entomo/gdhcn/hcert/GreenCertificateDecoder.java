package co.entomo.gdhcn.hcert;

import static org.apache.commons.compress.utils.IOUtils.toByteArray;

import COSE.CoseException;
import COSE.KeyKeys;
import COSE.Message;
import COSE.MessageTag;
import COSE.OneKey;
import COSE.Sign1Message;
import co.entomo.gdhcn.exceptions.GdhcnValidationException;

import com.upokecenter.cbor.CBORObject;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECPoint;

import nl.minvws.encoding.Base45;
import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
public class GreenCertificateDecoder {

  private final OneKey publicKey;

  public GreenCertificateDecoder(OneKey publicKey) {
    this.publicKey = publicKey;
  }



  /**
   * Decodes base45 encoded string -> Deflate -> COSE -> CBOR -> arbitrary Json String
   *
   * @param base45String
   * @return
   * @throws CompressorException
   * @throws IOException
   * @throws CoseException
   */
  public String decode(String base45String) throws CompressorException, IOException, CoseException {
    if(!base45String.startsWith("HC1:"))
      throw new RuntimeException("Base45 string not valid according to specification");

    base45String = base45String.substring(4);
    byte[] decodedBytes = Base45.getDecoder().decode(base45String);

    byte[] coseBytes = getCoseBytes(decodedBytes);

    byte[] cborBytes = getCborBytes(coseBytes);

    return getJsonString(cborBytes);
  }

  public static String getJsonString(byte[] cborBytes) throws IOException {
    CBORObject cborObject = CBORObject.DecodeFromBytes(cborBytes);
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    cborObject.WriteJSONTo(byteArrayOutputStream);

    return byteArrayOutputStream.toString("UTF-8");
  }

  private byte[] getCborBytes(byte[] coseBytes) throws CoseException {
    Sign1Message msg = (Sign1Message) Message.DecodeFromBytes(coseBytes, MessageTag.Sign1);
    return msg.GetContent();
  }

  public static byte[] getCoseBytes(byte[] decodedBytes) throws CompressorException, IOException {

    CompressorInputStream compressedStream = new CompressorStreamFactory()
            .createCompressorInputStream(CompressorStreamFactory.DEFLATE,
                    new ByteArrayInputStream(decodedBytes));

    return toByteArray(compressedStream);
  }
  public boolean validate(Sign1Message msg){
      try {
          return msg.validate(publicKey);
      } catch (CoseException e) {
          throw new RuntimeException(e);
      }
  }
}