/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package shiro;

import entities.User;
import entities.facades.UserFacade;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import services.InhouseApp;

/**
 *
 * @author rulyone
 */
public class JpaRealm extends AuthorizingRealm  {
    
    private InhouseApp inhouse = lookupInhouseAppBean();
    private UserFacade userFacade = lookupUserFacadeBean();
    
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken at) throws AuthenticationException {
        UsernamePasswordToken token = (UsernamePasswordToken) at;
        User user = userFacade.findByDisplayName(token.getUsername());
        if( user != null ) {  
            if (inhouse.getCurrentUsers().contains(user)) {
                throw new ConcurrentAccessException("Ya está logeada esta cuenta, debes deslogearte primero.");
            }
            SimpleAuthenticationInfo s = new SimpleAuthenticationInfo(user.getDisplayName(), user.getPassword(), getName());
            return s;
        } else {
            throw new UnknownAccountException("Username no existe.");
        }
    }    
    
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection pc) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    private UserFacade lookupUserFacadeBean() {
        try {
            Context c = new InitialContext();
            return (UserFacade) c.lookup("java:global/inhouse/UserFacade!entities.facades.UserFacade");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
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
