/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entities.facades;

import entities.User;
import javax.ejb.Stateless;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

/**
 *
 * @author rulyone
 */
@Stateless
@Named
public class UserFacade extends AbstractFacade<User> {
    @PersistenceContext(unitName = "inhousePU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public UserFacade() {
        super(User.class);
    }
    
    public User findBySteamId(String steamId) {
        try {
            return em.createNamedQuery("User.findBySteamId", User.class).setParameter("steamId", steamId).getSingleResult();
        } catch (NoResultException  ex) {
            return null;
        }
        
    }
    
    public User findByDisplayName(String displayName) {
        try {
            return em.createNamedQuery("User.findByDisplayName", User.class).setParameter("displayName", displayName).getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }
    
}
