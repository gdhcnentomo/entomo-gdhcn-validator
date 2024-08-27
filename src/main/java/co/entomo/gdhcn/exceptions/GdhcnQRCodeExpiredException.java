package co.entomo.gdhcn.exceptions;/**
 * @author Uday Matta
 */

/**
 * @organization Entomo Labs
 */
public class GdhcnQRCodeExpiredException extends  GdhcnValidationException{
    public GdhcnQRCodeExpiredException(String message){
        super(message);
    }
}
