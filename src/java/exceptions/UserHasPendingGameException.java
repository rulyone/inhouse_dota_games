/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package exceptions;

/**
 *
 * @author rulyone
 */
public class UserHasPendingGameException extends Exception {

    public UserHasPendingGameException() {
        super();
    }

    public UserHasPendingGameException(String message) {
        super(message);
    }
    
}
