/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import javax.persistence.*;

/**
 *
 * @author rulyone
 */
@Entity
@NamedQueries({
    @NamedQuery(name="User.findBySteamId", query="SELECT u FROM User u WHERE u.steamId = :steamId"),
    @NamedQuery(name="User.findByDisplayName", query="SELECT u FROM User u WHERE u.displayName = :displayName")
})
public class User implements Serializable, Comparable {
    
    
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String steamId;
    @Column(nullable = false, unique = true)
    private String displayName;
    @Column(nullable = false)
    private String password;
    @ManyToMany(mappedBy = "teamB")
    private List<Game> gamesTeamB;
    @ManyToMany(mappedBy = "teamA")
    private List<Game> gamesTeamA;
    @Column(nullable = false)
    private Integer score;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Game> getGamesTeamA() {
        return gamesTeamA;
    }

    public void setGamesTeamA(List<Game> gamesTeamA) {
        this.gamesTeamA = gamesTeamA;
    }

    public List<Game> getGamesTeamB() {
        return gamesTeamB;
    }

    public void setGamesTeamB(List<Game> gamesTeamB) {
        this.gamesTeamB = gamesTeamB;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getSteamId() {
        return steamId;
    }

    public void setSteamId(String steamId) {
        this.steamId = steamId;
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
        if (!(object instanceof User)) {
            return false;
        }
        User other = (User) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "controller.User[ id=" + id + " ]";
    }
    
    @Override
    public int compareTo(Object o) {       
        if (this.equals(o)) {
            return 0;
        }
        User u = (User) o;
        if (this.getScore().compareTo(u.getScore()) == 0) {
            return this.getDisplayName().compareTo(u.getDisplayName());
        }
        return this.getScore().compareTo(u.getScore());
    }
    
}
