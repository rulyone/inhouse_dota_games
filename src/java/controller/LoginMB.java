/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import entities.facades.UserFacade;
import java.io.Serializable;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.subject.Subject;
import org.jboss.seam.international.status.Messages;

/**
 *
 * @author rulyone
 */
@Named(value = "loginMB")
@SessionScoped
public class LoginMB implements Serializable {
    
    //private @Inject FacesContext fc;
    private @Inject Messages msgs;

    private String displayName;
    private String password;
    private boolean rememberMe;
    
    private @Inject UserMB userMB;
    private @Inject UserFacade userFac;

    /**
     * Creates a new instance of LoginMB
     */
    public LoginMB() {
    }

    public String login() {
        //Example using most common scenario of username/password pair:
        UsernamePasswordToken token = new UsernamePasswordToken(displayName, password);
        //”Remember Me” built-in:
        token.setRememberMe(rememberMe);

        Subject currentUser = SecurityUtils.getSubject();
        
        boolean logeado = false;
        try {
            currentUser.login(token);
            userMB.setUser(userFac.findByDisplayName(displayName));
            logeado = true;
        } catch (UnknownAccountException uae) {
            msgs.error(uae.getMessage());
        } catch (IncorrectCredentialsException ice) {
            msgs.error(ice.getMessage());
        } catch (LockedAccountException lae) {
            msgs.error(lae.getMessage());
        } catch (ExcessiveAttemptsException eae) {
            msgs.error(eae.getMessage());
        } catch (ConcurrentAccessException ex) {
            msgs.error(ex.getMessage());
        } catch (AuthenticationException ae) {
            //unexpected error?
            msgs.fatal(ae.getMessage());
        } 
        this.password = null;
        if (logeado) {
            msgs.info("Logeado satisfactoriamente.");
        }
        return "/index.xhtml?faces-redirect=true";

    }
    
    public String logout() {
        Subject currentUser = SecurityUtils.getSubject();
        currentUser.logout();
        this.displayName = null;
        this.password = null;
        return "/login.xhtml";
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isRememberMe() {
        return rememberMe;
    }

    public void setRememberMe(boolean rememberMe) {
        this.rememberMe = rememberMe;
    }
    
   
    
}
