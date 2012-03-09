/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.*;

/**
 *
 * @author rulyone
 */
@Entity
public class Game implements Serializable {
    
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToMany
    @JoinTable(name="user_game_team_a")
    private List<User> teamA;
    @ManyToMany
    @JoinTable(name="user_game_team_b")
    private List<User> teamB;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fechaInicio;
    private Result result;
//    @ManyToOne
//    private Room room;

    //transient data
    private transient @Transient List<User> pool;
    private transient @Transient Phase phase;
    private transient @Transient boolean captainAReady = false;
    private transient @Transient boolean captainBReady = false;
    private transient @Transient boolean teamATurnToPick = true;
    private transient @Transient List<User> concedeTeamA = new ArrayList<User>();
    private transient @Transient List<User> concedeTeamB = new ArrayList<User>();
    private transient @Transient User captainA;
    private transient @Transient User captainB;
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getCaptainA() {
        return captainA;
    }

    public void setCaptainA(User captainA) {
        this.captainA = captainA;
    }

    public User getCaptainB() {
        return captainB;
    }

    public void setCaptainB(User captainB) {
        this.captainB = captainB;
    }

    public List<User> getConcedeTeamA() {
        return concedeTeamA;
    }

    public void setConcedeTeamA(List<User> concedeTeamA) {
        this.concedeTeamA = concedeTeamA;
    }

    public List<User> getConcedeTeamB() {
        return concedeTeamB;
    }

    public void setConcedeTeamB(List<User> concedeTeamB) {
        this.concedeTeamB = concedeTeamB;
    }

    public boolean isTeamATurnToPick() {
        return teamATurnToPick;
    }

    public void setTeamATurnToPick(boolean teamATurnToPick) {
        this.teamATurnToPick = teamATurnToPick;
    }

    public boolean isCaptainAReady() {
        return captainAReady;
    }

    public void setCaptainAReady(boolean captainAReady) {
        this.captainAReady = captainAReady;
    }

    public boolean isCaptainBReady() {
        return captainBReady;
    }

    public void setCaptainBReady(boolean captainBReady) {
        this.captainBReady = captainBReady;
    }

//    public Room getRoom() {
//        return room;
//    }

    public Phase getPhase() {
        return phase;
    }

    public void setPhase(Phase phase) {
        this.phase = phase;
    }

    public List<User> getPool() {
        return pool;
    }

    public void setPool(List<User> pool) {
        this.pool = pool;
    }

//    public void setRoom(Room room) {
//        this.room = room;
//    }

    public Date getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(Date fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public List<User> getTeamA() {
        return teamA;
    }

    public void setTeamA(List<User> teamA) {
        this.teamA = teamA;
    }

    public List<User> getTeamB() {
        return teamB;
    }

    public void setTeamB(List<User> teamB) {
        this.teamB = teamB;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Game)) {
            return false;
        }
        Game other = (Game) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Game[ id=" + id + " ]";
    }
    
}
