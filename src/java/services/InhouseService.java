/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package services;

import entities.User;
import entities.facades.GameFacade;
import entities.facades.UserFacade;
import java.io.Serializable;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.inject.Named;

/**
 *
 * @author rulyone
 */
@Stateless
@LocalBean
@Named
public class InhouseService implements Serializable {
    
    private @EJB UserFacade userFac;
    private @EJB GameFacade gameFac;

    public void register(String steamId, String displayName) {
        
        User user = new User();
        user.setDisplayName(displayName);
        user.setSteamId(steamId);
        user.setScore(1000);
        userFac.create(user);
        
    }
       
    public User findBySteamId(String steamId) {
        return userFac.findBySteamId(steamId);
    }
    
    public void saveGame() {
        
    }
    
}
