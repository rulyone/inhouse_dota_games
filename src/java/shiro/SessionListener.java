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
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import org.apache.shiro.subject.SimplePrincipalCollection;
import services.InhouseApp;

/**
 *
 * @author rulyone
 */
public class SessionListener implements HttpSessionListener {
    
    private InhouseApp inhouse = lookupInhouseAppBean();

    
    @Override
    public void sessionCreated(HttpSessionEvent se) {
        
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        
        SimplePrincipalCollection principalSessionKey = (SimplePrincipalCollection) se.getSession().getAttribute("org.apache.shiro.subject.support.DefaultSubjectContext_PRINCIPALS_SESSION_KEY");
        if (principalSessionKey != null) {
            //EL USUARIO SE FUE POR TIMEOUT DE LA SESSION, POR ENDE HAY QUE REMOVERLO DE USERS ONLINE.
            inhouse.removeFromOnlineUsers(principalSessionKey.getPrimaryPrincipal().toString());
        }
        //System.out.println("SessionDestroyed: " + principalSessionKey.getPrimaryPrincipal());
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
