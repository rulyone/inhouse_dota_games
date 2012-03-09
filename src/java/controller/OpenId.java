/*
 * Created tdmarti, HSLU T&amp;A - D3S (2011)
 */
package controller;
 
import entities.User;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import org.openid4java.OpenIDException;
import org.openid4java.consumer.ConsumerManager;
import org.openid4java.consumer.VerificationResult;
import org.openid4java.discovery.DiscoveryInformation;
import org.openid4java.discovery.Identifier;
import org.openid4java.message.AuthRequest;
import org.openid4java.message.AuthSuccess;
import org.openid4java.message.ParameterList;
import services.InhouseService;
 
/**
 * Authenticate the user against a openid provider
 * @author tdmarti
 */
@Named("openid")
@SessionScoped
public class OpenId implements Serializable {
    
    private @Inject InhouseMB inhouseMB;
    private @Inject InhouseService services;
    private @Inject UserMB userMB;
 
    /** Creates a new instance of OpenId */
    public OpenId() {
    }
 
    private static final String OPEN_ID_URL = "http://steamcommunity.com/openid";
    private String validatedId;
    /* ... */
    private ConsumerManager manager;
    private DiscoveryInformation discovered;
 
    public void login() throws IOException {
        //try {
            manager = new ConsumerManager();
        //} catch (ConsumerException e) {
        //    System.out.println("Error creating ConsumerManager.");
        //}
        validatedId = null;
        String returnToUrl = returnToUrl("/openid.xhtml");
        String url = authRequest(returnToUrl);
 
        if (url != null) {
            FacesContext.getCurrentInstance().getExternalContext().redirect(url);
        }
    }
 
    /**
     * Create the current url and add another url path fragment on it.
     * Obtain from the current context the url and add another url path fragment at
     * the end
     * @param urlExtension f.e. /nextside.xhtml
     * @return the hole url including the new fragment
     */
    private String returnToUrl(String urlExtension) {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        String returnToUrl = "http://" + request.getServerName() + ":" + request.getServerPort()
                + context.getApplication().getViewHandler().getActionURL(context, urlExtension);
        return returnToUrl;
    }
 
    /**
     * Create an authentication request.
     * It performs a discovery on the user-supplied identifier. Attempt it to 
     * associate with the OpenID provider and retrieve one service endpoint 
     * for authentication. It adds some attributes for exchange on the AuthRequest.
     * A List of all possible attributes can be found on @see http://www.axschema.org/types/
     * @param returnToUrl
     * @return the URL where the message should be sent
     * @throws IOException
     */
    private String authRequest(String returnToUrl) throws IOException {
        try {
            List discoveries = manager.discover(OPEN_ID_URL);
            
            //discovered = manager.associate(discoveries);
            //AuthRequest authReq = manager.authenticate(discovered, returnToUrl);
            manager.setMaxAssocAttempts(0);
            AuthRequest authReq = manager.authenticate(discoveries, returnToUrl);
            
//            FetchRequest fetch = FetchRequest.createFetchRequest();
//            fetch.addAttribute("email", "http://schema.openid.net/contact/email", true);
//            /* Some other attributes ... */
// 
//            authReq.addExtension(fetch);
            return authReq.getDestinationUrl(true);
        } catch (OpenIDException e) {
            // TODO
        }
        return null;
    }
 
    public String verify() {
        ExternalContext context = javax.faces.context.FacesContext
                .getCurrentInstance().getExternalContext();
        HttpServletRequest request = (HttpServletRequest) context.getRequest();
        validatedId = verifyResponse(request);
        return validatedId;
    }
 
    /**
     * Set the class members with date from the authentication response.
     * Extract the parameters from the authentication response (which comes
     * in as a HTTP request from the OpenID provider). Verify the response,
     * examine the verification result and extract the verified identifier.
     * @param httpReq httpRequest
     * @return users identifier.
     */
    private String verifyResponse(HttpServletRequest httpReq) {
        try {
            ParameterList response =
                    new ParameterList(httpReq.getParameterMap());
 
            StringBuffer receivingURL = httpReq.getRequestURL();
            String queryString = httpReq.getQueryString();
            if (queryString != null && queryString.length() > 0) {
                receivingURL.append("?").append(httpReq.getQueryString());
            }
 
            VerificationResult verification = manager.verify(
                    receivingURL.toString(),
                    response, discovered);
 
            Identifier verified = verification.getVerifiedId();
            if (verified != null) {
                AuthSuccess authSuccess =
                        (AuthSuccess) verification.getAuthResponse();
 
//                if (authSuccess.hasExtension(AxMessage.OPENID_NS_AX)) {
//                    FetchResponse fetchResp = (FetchResponse) authSuccess.getExtension(AxMessage.OPENID_NS_AX);
// 
//                    List emails = fetchResp.getAttributeValues("email");
//                    openIdEmail = (String) emails.get(0);
//                     /* Some other attributes ... */
//                }
                return verified.getIdentifier();
            }
        } catch (OpenIDException e) {
            // TODO
        }
        return null;
    }
 
    /**
     * hidden member for onLoad/Init event. 
     *@return always return the string pageLoaded
     */
    public String getOnLoad() throws IOException {
        String verify = verify();
        if (verify != null) {
            FacesContext.getCurrentInstance().getExternalContext().redirect(returnToUrl("/index.xhtml"));
            User u = services.findBySteamId(verify);
            //this.inhouseMB.getUsers().add(u);
            this.userMB.setUser(u);
            
        }else{
            FacesContext.getCurrentInstance().getExternalContext().redirect(returnToUrl("/login.xhtml"));
        }
        
        return "pageLoaded";
    }
 
    /**
     * Getter and Setter Method
     */
    
    public String getValidatedId() {
        return validatedId;
    }
    public DiscoveryInformation getDiscovered() {
        return discovered;
    }

    public void setDiscovered(DiscoveryInformation discovered) {
        this.discovered = discovered;
    }

    public ConsumerManager getManager() {
        return manager;
    }

    public void setManager(ConsumerManager manager) {
        this.manager = manager;
    }

    public InhouseMB getInhouseMB() {
        return inhouseMB;
    }

    public void setInhouseMB(InhouseMB inhouseMB) {
        this.inhouseMB = inhouseMB;
    }
    
}