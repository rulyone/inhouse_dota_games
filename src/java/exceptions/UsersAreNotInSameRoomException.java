/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package exceptions;

/**
 *
 * @author rulyone
 */
public class UsersAreNotInSameRoomException extends Exception {

    public UsersAreNotInSameRoomException() {
        super();
    }

    public UsersAreNotInSameRoomException(String message) {
        super(message);
    }
    
}
