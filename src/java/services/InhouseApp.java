/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package services;

import entities.Game;
import entities.Phase;
import entities.Result;
import entities.User;
import entities.facades.GameFacade;
import entities.facades.UserFacade;
import exceptions.*;
import java.io.Serializable;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.inject.Named;
import org.richfaces.application.push.MessageException;
import org.richfaces.application.push.TopicKey;
import org.richfaces.application.push.TopicsContext;

/**
 *
 * @author rulyone
 */
@Singleton
@LocalBean
@Named
public class InhouseApp implements Serializable {

    private TopicKey topic;
    
    private TopicKey currentUsersTopic;
    
    private TopicKey chatTopic;
    
    private static final String DEFAULT_TOPIC = "DEFAULT_TOPIC";
    private static final String CURRENT_USERS_TOPIC = "CURRENT_USERS_TOPIC";
    private static final String CHAT_TOPIC = "CHAT_TOPIC";
    
    private @EJB UserFacade userFac;
    private @EJB GameFacade gameFac;
    
    private List<User> currentUsers = new ArrayList<User>();
    
    private List<Game> challengePhaseGames = new ArrayList<Game>();
    private Map<Long, TopicKey> challengeTopics = new HashMap<Long, TopicKey>();
    
    private List<Game> signPhaseGames = new ArrayList<Game>();
    private Map<Long, TopicKey> signTopics = new HashMap<Long, TopicKey>();
    
    private List<Game> pickingPhaseGames = new ArrayList<Game>();
    private Map<Long, TopicKey> pickingTopics = new HashMap<Long, TopicKey>();
    
    private List<Game> playingPhaseGames = new ArrayList<Game>();
    private Map<Long, TopicKey> playingTopics = new HashMap<Long, TopicKey>();
    
    @PostConstruct
    public void init() {
        topic = new TopicKey(DEFAULT_TOPIC);
        currentUsersTopic = new TopicKey(CURRENT_USERS_TOPIC);
        chatTopic = new TopicKey(CHAT_TOPIC);
        
        TopicsContext topicsContext = TopicsContext.lookup();
        topicsContext.getOrCreateTopic(topic);
        topicsContext.getOrCreateTopic(currentUsersTopic);
        topicsContext.getOrCreateTopic(chatTopic);
    }
    
    private void checkNull(Object... os) {
        for (Object object : os) {
            if (object == null) {
                throw new NullPointerException();
            }
        }
    }
    
    public void sendMessage(User caller, String message) {
        checkNull(caller, message);
        this.push(chatTopic, caller.getDisplayName() + ": " + message);
    }
    
    public void challenge(User caller, User challenged) throws UserHasPendingGameException, UserChallengedItselfException {
        checkNull(caller, challenged);
        //check none of them has pending games.
        checkPendingGames(caller);
        checkPendingGames(challenged);
        
        if (caller.equals(challenged)) {
            throw new UserChallengedItselfException("You can't challenge yourself.");
        }
        
        //create game and add to current games.
        Game game = new Game();
        game.setFechaInicio(new Date()); //re-set when last player is picked.
        game.setPhase(Phase.CHALLENGE_PHASE);
        game.setPool(new ArrayList<User>());
        game.setResult(Result.NOT_REPORTED);
        game.setTeamA(new ArrayList<User>());
        game.getTeamA().add(caller);
        game.setTeamB(new ArrayList<User>());
        game.getTeamB().add(challenged);
        game.setCaptainA(caller);
        game.setCaptainB(challenged);
        
        
        
        gameFac.create(game);
        
        caller.getGamesTeamA().add(game);
        challenged.getGamesTeamB().add(game);
        userFac.edit(caller);
        userFac.edit(challenged);
        
        this.challengePhaseGames.add(game);
        //this.addChallengeTopicKeyAndPush(game.getId());
        this.push(topic, "SYSTEM: User " + caller.getDisplayName() + " has challenged " + challenged.getDisplayName());
    }
    
    public void acceptChallenge(User caller) throws UserNotChallengedException {
        checkNull(caller);
        //check caller has been challenged
        boolean wasChallenged = false;
        for (int i = 0; i < challengePhaseGames.size(); i++) {
            Game game = challengePhaseGames.get(i);
            if (game.getTeamB().get(0).equals(caller)) {
                game.setPhase(Phase.SIGNING_PHASE);
                this.challengePhaseGames.remove(game);
                this.signPhaseGames.add(game);
                wasChallenged = true;
                break;
            }
        }
        if (!wasChallenged) {
            throw new UserNotChallengedException("You haven't been challenged.");
        }
        this.push(topic, "SYSTEM: User " + caller.getDisplayName() + " has accepted the challenge.");
    }
    
    public void rejectChallenge(User caller) throws UserIsNotCaptainException {
        checkNull(caller);
        //check caller is captain
        boolean itsCaptain = false;
        for (int i = 0; i < challengePhaseGames.size(); i++) {
            Game game = challengePhaseGames.get(i);
            if (game.getCaptainA().equals(caller) || game.getCaptainA().equals(caller)) {
                itsCaptain = true;
                this.challengePhaseGames.remove(i);
                List<User> teamA = game.getTeamA();
                for (int j = 0; j < teamA.size(); j++) {
                    User user = teamA.get(j);
                    user.getGamesTeamA().remove(game);
                    userFac.edit(user);
                }
                List<User> teamB = game.getTeamB();
                for (int j = 0; j < teamB.size(); j++) {
                    User user = teamB.get(j);
                    user.getGamesTeamB().remove(game);
                    userFac.edit(user);
                }
                
                gameFac.remove(game);
                break;
            }
        }
        if (!itsCaptain) {
            throw new UserIsNotCaptainException("You're not a captain.");
        }
        this.push(topic, "SYSTEM: User " + caller.getDisplayName() + " has rejected the challenge.");
    }
    
    public void sign(User caller, Game game) throws UserHasPendingGameException {
        checkNull(caller, game);
        //check caller doesn't have pending games.
        checkPendingGames(caller);
        for (int i = 0; i < signPhaseGames.size(); i++) {
            Game g = signPhaseGames.get(i);            
            if (g.equals(game)) {
                g.getPool().add(caller);
                break;
            }
        }
        this.push(topic, "SYSTEM: User " + caller.getDisplayName() + " has signed to game #" + game.getId() + ".");
    }
    
    public void out(User caller) throws UserNotInGameException {
        checkNull(caller);
        //check caller is in a signPhaseGame
        boolean wasInGame = false;
        long gameId = -1;
        for (int i = 0; i < signPhaseGames.size(); i++) {
            Game game = signPhaseGames.get(i);
            if (game.getPool().contains(caller)) {
                game.getPool().remove(caller);
                gameId = game.getId();
                wasInGame = true;
                if (game.getPool().size() < 8) {
                    game.setCaptainAReady(false);
                    game.setCaptainBReady(false);
                }
                break;
            }
        }
        if (!wasInGame) {
            throw new UserNotInGameException("You've not signed to this game.");
        }
        this.push(topic, "SYSTEM: User " + caller.getDisplayName() + " has signed out from game #" + gameId + ".");
    }
    
    public void ready(User caller) throws NotSufficientPlayersInPoolException {
        checkNull(caller);
        for (int i = 0; i < signPhaseGames.size(); i++) {
            Game game = signPhaseGames.get(i);            
            if (game.getTeamA().get(0).equals(caller)) {
                if (game.getPool().size() < 8) {
                    throw new NotSufficientPlayersInPoolException("There are not suffiecient players in the pool.");
                }
                if (game.isCaptainAReady()) {
                    //do nothing.
                    return ;
                }
                game.setCaptainAReady(true);                
            }
            if (game.getTeamB().get(0).equals(caller)) {
                if (game.getPool().size() < 8) {
                    throw new NotSufficientPlayersInPoolException("There are not suffiecient players in the pool.");
                }
                if (game.isCaptainBReady()) {
                    //do nothing
                    return ;
                }
                game.setCaptainBReady(true);
            }
            if (game.isCaptainAReady() && game.isCaptainBReady()) {
                game.setPhase(Phase.PICKING_PHASE);
                signPhaseGames.remove(game);
                pickingPhaseGames.add(game);
                this.push(topic, "SYSTEM: Game #" + game.getId() + " is now on the PICK PHASE.");
                return ;
            }
        }
    }
    
    public void pick(User caller, User picked) throws UserAlreadyPickedException, UserNotInPoolException, NotYourTurnToPickException {
        checkNull(caller, picked);
        for (int i = 0; i < pickingPhaseGames.size(); i++) {
            Game game = pickingPhaseGames.get(i);
            if (game.getCaptainA().equals(caller)) {
                //it's the team A captain.
                //check picked players hasn't been picked already.
                if (game.getTeamA().contains(picked) || game.getTeamB().contains(picked)) {
                    throw new UserAlreadyPickedException("The user has already been picked.");
                }
                //check he's in the pool
                if (!game.getPool().contains(picked)) {
                    throw new UserNotInPoolException("The user is not in the pool.");
                }
                //check it's captain A turn to pick
                if (!game.isTeamATurnToPick()) {
                    throw new NotYourTurnToPickException("It's not your turn yet.");
                }
                game.getPool().remove(picked);
                game.getTeamA().add(picked);
                
                int sizeA = game.getTeamA().size();
                int sizeB = game.getTeamB().size();
                if ((sizeA == 2 && sizeB == 3) || (sizeA == 3 && sizeB == 3) || (sizeA == 4 && sizeB == 5)) {
                    //le toca pickear al team A
                    game.setTeamATurnToPick(true);
                }else{
                    game.setTeamATurnToPick(false);
                }
                
                if (sizeA == 5 && sizeB == 5) {
                    //last pick
                    pickingPhaseGames.remove(game);
                    playingPhaseGames.add(game);
                }  
                
                this.push(topic, "SYSTEM: User " + caller.getDisplayName() + " has picked the user " + picked.getDisplayName() + ".");
                
            }else if (game.getCaptainB().equals(caller)) {
                //it's the team B captain.
                //check picked player hasn't been picked already.
                if (game.getTeamA().contains(picked) || game.getTeamB().contains(picked)) {
                    throw new UserAlreadyPickedException("The user has already been picked.");
                }
                //check he's in the pool
                if (!game.getPool().contains(picked)) {
                    throw new UserNotInPoolException("The user is not in the pool.");
                }
                //check it's captain B turn to pick
                if (game.isTeamATurnToPick()) {
                    throw new NotYourTurnToPickException("It's not your turn yet.");
                }
                game.getPool().remove(picked);
                game.getTeamB().add(picked);
                
                int sizeA = game.getTeamA().size();
                int sizeB = game.getTeamB().size();
                if ((sizeA == 2 && sizeB == 3) || (sizeA == 3 && sizeB == 3) || (sizeA == 4 && sizeB == 5)) {
                    //le toca pickear al team A
                    game.setTeamATurnToPick(true);
                }else{
                    game.setTeamATurnToPick(false);
                }
                
                if (sizeA == 5 && sizeB == 5) {
                    //last pick
                    pickingPhaseGames.remove(game);
                    playingPhaseGames.add(game);                    
                }  
                
                this.push(topic, "SYSTEM: User " + caller.getDisplayName() + " has picked the user " + picked.getDisplayName() + ".");
            }            
        }
    }
    
    public void concedeGame(User caller) throws UserAlreadyConcededException {
        checkNull(caller);
        for (int i = 0; i < playingPhaseGames.size(); i++) {
            Game game = playingPhaseGames.get(i);
            if (game.getTeamA().contains(caller)) {
                //check if he already conceded
                if (game.getConcedeTeamA().contains(caller)) {                    
                    throw new UserAlreadyConcededException("You have conceded the game already.");
                }
                game.getConcedeTeamA().add(caller);
                if (game.getConcedeTeamA().size() == 3) {
                    //game conceded
                    game.setPhase(Phase.REPORTED_PHASE);
                    game.setResult(Result.WIN_TEAM_B);
                    gameFac.edit(game);
                    List<User> teamA = game.getTeamA();
                    for (int j = 0; j < teamA.size(); j++) {
                        User user = teamA.get(j);
                        user.getGamesTeamA().add(game);
                        user.setScore(user.getScore() - 3);
                        userFac.edit(user);
                    }
                    List<User> teamB = game.getTeamB();
                    for (int j = 0; j < teamB.size(); j++) {
                        User user = teamB.get(j);
                        user.getGamesTeamB().add(game);
                        user.setScore(user.getScore() + 5);
                        userFac.edit(user);
                    }
                    playingPhaseGames.remove(game);                    
                }
                this.push(topic, "SYSTEM: User " + caller.getDisplayName() + " has conceded the game #" + game.getId() + ".");
                return ;
            }else if (game.getTeamB().contains(caller)) {
                //check if he already conceded
                if (game.getConcedeTeamB().contains(caller)) {
                    throw new UserAlreadyConcededException("You have conceded the game already.");
                }
                game.getConcedeTeamB().add(caller);
                if (game.getConcedeTeamB().size() == 3) {
                    //game conceded
                    game.setPhase(Phase.REPORTED_PHASE);
                    game.setResult(Result.WIN_TEAM_A);
                    gameFac.edit(game);
                    List<User> teamA = game.getTeamA();
                    for (int j = 0; j < teamA.size(); j++) {
                        User user = teamA.get(j);
                        user.getGamesTeamA().add(game);
                        user.setScore(user.getScore() + 5);
                        userFac.edit(user);
                    }
                    List<User> teamB = game.getTeamB();
                    for (int j = 0; j < teamB.size(); j++) {
                        User user = teamB.get(j);
                        user.getGamesTeamB().add(game);
                        user.setScore(user.getScore() - 3);
                        userFac.edit(user);
                    }
                    playingPhaseGames.remove(game);                    
                }
                this.push(topic, "SYSTEM: User " + caller.getDisplayName() + " has conceded the game #" + game.getId() + ".");
                return ;
            }
        }
    }
    
    
    private void checkPendingGames(User user) throws UserHasPendingGameException {
        checkNull(user);
        for (int i = 0; i < challengePhaseGames.size(); i++) {
            Game game = challengePhaseGames.get(i);
            if (game.getTeamA().get(0).equals(user) || game.getTeamB().get(0).equals(user)) {
                throw new UserHasPendingGameException("The user has pending games.");
            }
        }
        for (int i = 0; i < signPhaseGames.size(); i++) {
            Game game = signPhaseGames.get(i);
            if (game.getTeamA().contains(user) || game.getTeamB().contains(user) || game.getPool().contains(user)) {
                throw new UserHasPendingGameException("The user has pending games.");
            }
        }
        for (int i = 0; i < pickingPhaseGames.size(); i++) {
            Game game = pickingPhaseGames.get(i);
            if (game.getTeamA().contains(user) || game.getTeamB().contains(user) || game.getPool().contains(user)) {
                throw new UserHasPendingGameException("The user has pending games.");
            }
        }
        for (int i = 0; i < playingPhaseGames.size(); i++) {
            Game game = playingPhaseGames.get(i);
            if (game.getTeamA().contains(user) || game.getTeamB().contains(user)) {
                throw new UserHasPendingGameException("The user has pending games.");
            }
        }
    }

    public List<Game> getChallengePhaseGames() {
        return challengePhaseGames;
    }

    public List<User> getCurrentUsers() {
        return currentUsers;
    }

    public List<Game> getPickingPhaseGames() {
        return pickingPhaseGames;
    }

    public List<Game> getPlayingPhaseGames() {
        return playingPhaseGames;
    }

    public List<Game> getSignPhaseGames() {
        return signPhaseGames;
    }

    public void removeFromOnlineUsers(String displayName) {
        checkNull(displayName);
        Iterator<User> iterator = currentUsers.iterator();
        while (iterator.hasNext()) {
            User user = iterator.next();
            if (user.getDisplayName().equals(displayName)) {
                iterator.remove();
                this.push(currentUsersTopic, "SYSTEM: User " + user.getDisplayName() + " has logged OUT.");
                return ;
            }
        }
        
//        for (int i = 0; i < currentUsers.size(); i++) {
//            User user = currentUsers.get(i);
//            if (user.getDisplayName().equals(displayName)) {
//                this.currentUsers.remove(i);
//                this.push(currentUsersTopic, "SYSTEM: User " + user.getDisplayName() + " has logged OUT.");
//                return ;
//            }
//        }
    }

    public void addToOnlineUsers(String displayName) {
        checkNull(displayName);
        User u = userFac.findByDisplayName(displayName);
        if (u != null) {
            this.currentUsers.add(u);
            Collections.sort(currentUsers);
            this.push(currentUsersTopic, "SYSTEM: User " + u.getDisplayName() + " has logged IN.");
        }
    }
    
    private void push(TopicKey tk, String msg) {
        TopicsContext topicsContext = TopicsContext.lookup();
        try {
            topicsContext.publish(tk, msg);
        } catch (MessageException ex) {
            Logger.getLogger(InhouseApp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    //DE ACÁ PARA ABAJO TRABAJO PARA FUTURO... PARA SOLO HACER PUSH DE LOS GAMES UNO A UNO Y EVITAR
    //COLAPSO DEL SERVIDOR.

    private void addChallengeTopicKeyAndPush(Long gameId) {
        TopicKey tk = new TopicKey(DEFAULT_TOPIC, gameId.toString());        
        TopicsContext topicsContext = TopicsContext.lookup();        
        topicsContext.getOrCreateTopic(tk);
        this.challengeTopics.put(gameId, tk);
        try {
            topicsContext.publish(tk, null);
        } catch (MessageException ex) {
            Logger.getLogger(InhouseApp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void addPickingTopicKeyAndPush(Long gameId) {
        TopicKey tk = new TopicKey(DEFAULT_TOPIC, gameId.toString());        
        TopicsContext topicsContext = TopicsContext.lookup();        
        topicsContext.getOrCreateTopic(tk);
        this.pickingTopics.put(gameId, tk);
        try {
            topicsContext.publish(tk, null);
        } catch (MessageException ex) {
            Logger.getLogger(InhouseApp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void addSignTopicKeyAndPush(Long gameId) {
        TopicKey tk = new TopicKey(DEFAULT_TOPIC, gameId.toString());        
        TopicsContext topicsContext = TopicsContext.lookup();        
        topicsContext.getOrCreateTopic(tk);
        this.signTopics.put(gameId, tk);
        try {
            topicsContext.publish(tk, null);
        } catch (MessageException ex) {
            Logger.getLogger(InhouseApp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void addPlayingTopicKeyAndPush(Long gameId) {
        TopicKey tk = new TopicKey(DEFAULT_TOPIC, gameId.toString());        
        TopicsContext topicsContext = TopicsContext.lookup();        
        topicsContext.getOrCreateTopic(tk);
        this.playingTopics.put(gameId, tk);
        try {
            topicsContext.publish(tk, null);
        } catch (MessageException ex) {
            Logger.getLogger(InhouseApp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public Date getCurrentDate() {
        return new Date();
    }
    
}
