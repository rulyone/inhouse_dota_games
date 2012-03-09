/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package exceptions;

/**
 *
 * @author rulyone
 */
public class UserIsNotCaptainException extends Exception {

    public UserIsNotCaptainException() {
    }

    public UserIsNotCaptainException(String message) {
        super(message);
    }
    
}
