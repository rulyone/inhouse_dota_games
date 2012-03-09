/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package shiro;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.apache.shiro.authc.*;
import org.apache.shiro.subject.PrincipalCollection;
import services.InhouseApp;

/**
 *
 * @author rulyone
 */

public class UserAuthenticationListener implements AuthenticationListener {
    
    private InhouseApp inhouse = lookupInhouseAppBean();

    @Override
    public void onSuccess(AuthenticationToken at, AuthenticationInfo ai) {
//        System.out.println("onsuccess: " + at.getPrincipal());
//        count++;
        UsernamePasswordToken token = (UsernamePasswordToken) at;
        inhouse.addToOnlineUsers(token.getUsername());
    }

    @Override
    public void onFailure(AuthenticationToken at, AuthenticationException ae) {
        System.out.println("onfailure: " + at.getPrincipal());
    }

    @Override
    public void onLogout(PrincipalCollection pc) {
//        System.out.println("onlogout: " + pc.getPrimaryPrincipal());
//        count--;
        inhouse.removeFromOnlineUsers(pc.getPrimaryPrincipal().toString());
    }

    private InhouseApp lookupInhouseAppBean() {
        try {
            Context c = new InitialContext();
            return (InhouseApp) c.lookup("java:global/inhouse/InhouseApp!services.InhouseApp");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }
    
}
