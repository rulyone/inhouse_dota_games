/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package exceptions;

/**
 *
 * @author rulyone
 */
public class NotSufficientPlayersInPoolException extends Exception {

    public NotSufficientPlayersInPoolException() {
        super();
    }

    public NotSufficientPlayersInPoolException(String message) {
        super(message);
    }
    
}
