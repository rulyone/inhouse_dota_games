/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package exceptions;

/**
 *
 * @author rulyone
 */
public class UserAlreadyConcededException extends Exception {

    public UserAlreadyConcededException() {
    }

    public UserAlreadyConcededException(String message) {
        super(message);
    }
    
    
}
