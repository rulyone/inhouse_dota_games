/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package exceptions;

/**
 *
 * @author rulyone
 */
public class UserNotChallengedException extends Exception {

    public UserNotChallengedException() {
        super();
    }

    public UserNotChallengedException(String message) {
        super(message);
    }
    
}
