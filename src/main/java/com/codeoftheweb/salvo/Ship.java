package com.codeoftheweb.salvo;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Ship {



    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;
    public String shipType;

    @ElementCollection
    @Column(name="Shiplocations")
    private List<String> ShipLocations = new ArrayList<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="gamePlayer_id")
    private GamePlayer gamePlayer;

    public Ship(){}

    public Ship(String shipType, List<String> shipLocations, GamePlayer gamePlayer) {
        this.shipType = shipType;
        this.ShipLocations = shipLocations;
        this.gamePlayer = gamePlayer;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getShipType() {
        return shipType;
    }

    public void setShipType(String shipType) {
        this.shipType = shipType;
    }

    public List<String> getShiplocations() {
        return ShipLocations;
    }

    public void setShiplocations(List<String> shiplocations) {
        ShipLocations = shiplocations;
    }

    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }

    public void setGamePlayer(GamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
    }
}
