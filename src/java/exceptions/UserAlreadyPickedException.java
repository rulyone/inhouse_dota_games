/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package exceptions;

/**
 *
 * @author rulyone
 */
public class UserAlreadyPickedException extends Exception {

    public UserAlreadyPickedException() {
        super();
    }

    public UserAlreadyPickedException(String message) {
        super(message);
    }
    
}
