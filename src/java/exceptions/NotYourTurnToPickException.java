/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package exceptions;

/**
 *
 * @author rulyone
 */
public class NotYourTurnToPickException extends Exception {

    public NotYourTurnToPickException() {
        super();
    }

    public NotYourTurnToPickException(String message) {
        super(message);
    }
    
}
