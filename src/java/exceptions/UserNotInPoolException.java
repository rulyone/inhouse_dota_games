/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package exceptions;

/**
 *
 * @author rulyone
 */
public class UserNotInPoolException extends Exception {

    public UserNotInPoolException() {
    }

    public UserNotInPoolException(String message) {
        super(message);
    }
    
}
