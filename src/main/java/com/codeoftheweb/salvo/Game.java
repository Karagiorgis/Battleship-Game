package com.codeoftheweb.salvo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Set;

@Entity
@Component
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;
    public String creationDate;

    @JsonIgnore
    @OneToMany(mappedBy="game", fetch=FetchType.EAGER)
    Set<GamePlayer> gamePlayerSet;

    @OneToMany(mappedBy="game", fetch=FetchType.EAGER)
    Set<Score> scoreSet;

    public Game() {

    }

    public Game(String creationDate) {
        this.creationDate = creationDate;
    }

    public Set<Score> getScoreSet() {
        return scoreSet;
    }

    public void setScoreSet(Set<Score> scoreSet) {
        this.scoreSet = scoreSet;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Set<GamePlayer> getGamePlayerSet() {
        return gamePlayerSet;
    }

    public void setGamePlayerSet(Set<GamePlayer> gamePlayerSet) {
        this.gamePlayerSet = gamePlayerSet;
    }


}
