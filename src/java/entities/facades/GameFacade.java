/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entities.facades;

import entities.Game;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author rulyone
 */
@Stateless
public class GameFacade extends AbstractFacade<Game> {
    @PersistenceContext(unitName = "inhousePU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public GameFacade() {
        super(Game.class);
    }
    
}
