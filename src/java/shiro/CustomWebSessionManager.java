/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package shiro;

import java.util.Collection;
import javax.inject.Inject;
import org.apache.shiro.session.InvalidSessionException;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.SessionContext;
import org.apache.shiro.session.mgt.SessionKey;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import services.InhouseApp;

/**
 *
 * @author rulyone
 */
public class CustomWebSessionManager extends DefaultWebSessionManager {
    
    private @Inject InhouseApp inhouse;

    @Override
    protected void onExpiration(Session session) {
        super.onExpiration(session);     
        System.out.println("on expiration...");
    }

    @Override
    protected void onStart(Session session, SessionContext context) {
        super.onStart(session, context);
        Collection<Session> activeSessions = this.getActiveSessions();
        System.out.println("on start...");
        for (Session s : activeSessions) {
            System.out.println(s.getId());
        }
    }

    @Override
    protected void onStop(Session session) {
        super.onStop(session);
        System.out.println("on stop...");
        Collection<Session> activeSessions = this.getActiveSessions();
        for (Session s : activeSessions) {
            System.out.println(s.getId());
        }
    }

    @Override
    protected void onInvalidation(Session session, InvalidSessionException ise, SessionKey key) {
        super.onInvalidation(session, ise, key);
        System.out.println("on invalidation...");
        Collection<Session> activeSessions = this.getActiveSessions();
        for (Session s : activeSessions) {
            System.out.println(s.getId());
        }
    }

    @Override
    protected Collection<Session> getActiveSessions() {
        return super.getActiveSessions();
    }
    
}
