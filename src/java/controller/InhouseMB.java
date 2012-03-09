/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.richfaces.application.push.TopicKey;
import org.richfaces.application.push.TopicsContext;
import services.InhouseService;

/**
 *
 * @author rulyone
 */
@Named(value = "inhouseMB")
@ApplicationScoped
public class InhouseMB implements Serializable {

    private @Inject InhouseService services;
    
    private TopicKey topic;
    private static final String DEFAULT_TOPIC = "DEFAULT_TOPIC";
        
    /**
     * Creates a new instance of InhouseMB
     */
    public InhouseMB() {
    }
    
    @PostConstruct
    private void init() {
        topic = new TopicKey(DEFAULT_TOPIC);
        TopicsContext topicsContext = TopicsContext.lookup();
        topicsContext.getOrCreateTopic(topic);
    }
    
}
