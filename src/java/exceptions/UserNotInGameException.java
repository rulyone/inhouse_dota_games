/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package exceptions;

/**
 *
 * @author rulyone
 */
public class UserNotInGameException extends Exception {

    public UserNotInGameException() {
    }

    public UserNotInGameException(String message) {
        super(message);
    }
    
}
