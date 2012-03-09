/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import entities.Game;
import entities.User;
import exceptions.*;
import java.io.Serializable;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.shiro.SecurityUtils;
import org.jboss.seam.international.status.Messages;
import services.InhouseApp;
import services.InhouseService;

/**
 *
 * @author rulyone
 */
@Named(value = "userMB")
@SessionScoped
public class UserMB implements Serializable {
    
    @Inject private InhouseService services;
    @Inject private InhouseMB inhouseMB;
    @Inject private InhouseApp inhouse;
    @Inject private Messages msgs;
    
    private User user;   
    
    private String msg;
    
    /**
     * Creates a new instance of UserMB
     */
    public UserMB() {
    }
    
    public String sendMessage() {
        if (user == null) {
            msgs.error("Debes estar logeado para usar esta característica.");
            return "/login.xhtml?faces-redirect=true";
        }
        inhouse.sendMessage(user, msg);
        return null;
    }
    
    public String challenge(User challenged) {
        if (user == null) {
            msgs.error("Debes estar logeado para usar esta característica.");
            return "/login.xhtml?faces-redirect=true";
        }
        try {
            inhouse.challenge(user, challenged);
            msgs.info("You have challenged the user " + challenged.getDisplayName());
        } catch (UserHasPendingGameException ex) {
            msgs.error(ex.getMessage());
        } catch (UserChallengedItselfException ex) {
            msgs.error(ex.getMessage());
        }
        return null;
    }
    
    public String acceptChallenge() {
        if (user == null) {
            msgs.error("Debes estar logeado para usar esta característica.");
            return "/login.xhtml?faces-redirect=true";
        }
        try {
            inhouse.acceptChallenge(user);
            msgs.info("You have accepted the challeng.");
        } catch (UserNotChallengedException ex) {
            msgs.error(ex.getMessage());
        }
        return null;
    }
    
    public String rejectChallenge() {
        if (user == null) {
            msgs.error("Debes estar logeado para usar esta característica.");
            return "/login.xhtml?faces-redirect=true";
        }
        try {
            inhouse.rejectChallenge(user);
            msgs.info("You have rejected the challenge.");
        } catch (UserIsNotCaptainException ex) {
            msgs.error(ex.getMessage());
        }
        return null;
    }
    
    public String sign(Game game) {
        if (user == null) {
            msgs.error("Debes estar logeado para usar esta característica.");
            return "/login.xhtml?faces-redirect=true";
        }
        try {
            inhouse.sign(user, game);
            msgs.info("You have signed to the game.");
        } catch (UserHasPendingGameException ex) {
            msgs.error(ex.getMessage());
        }
        return null;
    }
    
    public String out() {
        if (user == null) {
            msgs.error("Debes estar logeado para usar esta característica.");
            return "/login.xhtml?faces-redirect=true";
        }
        try {
            inhouse.out(user);
            msgs.info("You are no longer signed to the game.");
        } catch (UserNotInGameException ex) {
            msgs.error(ex.getMessage());
        }
        return null;
    }
    
    public String ready() {
        if (user == null) {
            msgs.error("Debes estar logeado para usar esta característica.");
            return "/login.xhtml?faces-redirect=true";
        }
        try {
            inhouse.ready(user);
            msgs.info("You are now flaged as ready to pick players.");
        } catch (NotSufficientPlayersInPoolException ex) {
            msgs.error(ex.getMessage());
        }
        return null;
    }
    
    public String pick(User picked) {
        if (user == null) {
            msgs.error("Debes estar logeado para usar esta característica.");
            return "/login.xhtml?faces-redirect=true";
        }
        try {
            inhouse.pick(user, picked);
            msgs.info("You have picked the user " + picked.getDisplayName());
        } catch (UserAlreadyPickedException ex) {
            msgs.error(ex.getMessage());
        } catch (UserNotInPoolException ex) {
            msgs.error(ex.getMessage());
        } catch (NotYourTurnToPickException ex) {
            msgs.error(ex.getMessage());
        }
        return null;
    }
    
    public String concedeGame() {
        if (user == null) {
            msgs.error("Debes estar logeado para usar esta característica.");
            return "/login.xhtml?faces-redirect=true";
        }
        try {
            inhouse.concedeGame(user);
            msgs.info("You have conceded the game.");
        } catch (UserAlreadyConcededException ex) {
            msgs.error(ex.getMessage());
        }
        return null;
    }
    
    //////////////////////////////////////////////
    //////////////////////////////////////////////
    
    public InhouseMB getInhouseMB() {
        return inhouseMB;
    }

    public void setInhouseMB(InhouseMB inhouseMB) {
        this.inhouseMB = inhouseMB;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

}
