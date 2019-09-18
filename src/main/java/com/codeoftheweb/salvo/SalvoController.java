package com.codeoftheweb.salvo;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api")
public class SalvoController {

    //Repos
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private GamePlayerRepository gamePlayerRepository;
    @Autowired
    private ShipRepository shipRepository;
    @Autowired
    private ScoreRepository scoreRepository;
    @Autowired
    private PlayerRepository playerRepository;
    @Autowired
    private SalvoRepository salvoRepository;

    //Routing

    @RequestMapping("/games")
    public List<Object> getGames(){
        return gameRepository.findAll()
                .stream()
                .map(this::gameDTO)
                .collect(Collectors.toList());
    }

    @RequestMapping("/players")
    public List<Object> getPlayers(){
        return playerRepository.findAll()
                .stream()
                .map(this::playerDTO)
                .collect(Collectors.toList());
    }

    @RequestMapping(path="/games/players/{gamePlayer_id}/ships", method = RequestMethod.POST)
    public ResponseEntity<Map<String,Object>> postShips(@PathVariable long gamePlayer_id,
                                                @RequestBody List<Ship> ships) {

        GamePlayer gamePlayer = gamePlayerRepository.findById(gamePlayer_id).orElse(null);
    if(gamePlayer != null) {
        for (Ship ship : ships) {
            ship.setGamePlayer(gamePlayer);
            shipRepository.save(ship);
        }
        return new ResponseEntity<>(makeMap("Success", "Ships Created"),HttpStatus.CREATED);
    }else{
        return new ResponseEntity<>(makeMap("Missing data","wrong id"), HttpStatus.UNAUTHORIZED);
    }


}


    @RequestMapping(path="/games/players/{gamePlayer_id}/salvos", method = RequestMethod.POST)
    public ResponseEntity<Map<String,Object>> postSalvos(@PathVariable long gamePlayer_id,
                                                         @RequestBody List<Salvo> salvos){
        GamePlayer gamePlayer = gamePlayerRepository.findById(gamePlayer_id).orElse(null);

        if(gamePlayer != null){

            for (Salvo salvo: salvos){

                salvo.setGamePlayer(gamePlayer);
                salvo.getTurn();
                salvoRepository.save(salvo);
            }

            return new ResponseEntity<>(makeMap("Success", "Ships Created"),HttpStatus.CREATED);

        }else{
            return new ResponseEntity<>(makeMap("Missing data","wrong id"), HttpStatus.UNAUTHORIZED);
        }
    }

    @RequestMapping(path = "/games", method = RequestMethod.POST)
    public ResponseEntity<Object> createGame(Authentication authentication) {
        if (authentication != null) {
            Player player = playerRepository.findByUserName(authentication.getName());
            if (player != null) {

                Game game = new Game(new Date().toString());
                gameRepository.save(game);
                gamePlayerRepository.save(new GamePlayer(player, game));

                return new ResponseEntity<>(HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>("No User Found", HttpStatus.UNAUTHORIZED);
            }
        } else {
                return new ResponseEntity<>("No User Logged In", HttpStatus.UNAUTHORIZED);

        }
    }

    @RequestMapping(path = "/games/{game_id}/players", method = RequestMethod.POST)
    public ResponseEntity<Object> joinGame(@PathVariable Long game_id, Authentication authentication) {
                if(authentication != null) {
                    Player player = playerRepository.findByUserName(authentication.getName());
                    if (game_id != null) {
                        Game game = gameRepository.findById(game_id).orElse(null);
                        GamePlayer gamePlayer = game.getGamePlayerSet().stream().findAny().orElse(null);

                        if(player.getId() != gamePlayer.getPlayer().getId()){
                            gamePlayerRepository.save(new GamePlayer(player, game));
                            return new ResponseEntity<>(HttpStatus.CREATED);
                        }else{
                            return new ResponseEntity<>("Missing data", HttpStatus.FORBIDDEN);
                        }

                    }else{
                        return new ResponseEntity<>("Missing data", HttpStatus.FORBIDDEN);
                    }
                }else{
                    return new ResponseEntity<>("No User Logged In", HttpStatus.UNAUTHORIZED);
                }
    }


    @RequestMapping("/game_view/{gamePlayer_id}")
    public Map<String, Object> findPlayers(@PathVariable Long gamePlayer_id, Authentication authentication){
        Map<String, Object> map = new LinkedHashMap<>();
        GamePlayer gamePlayer = gamePlayerRepository.findById(gamePlayer_id).orElse(null);
        if (authentication != null ) {
            map.put("user", playerRepository.findByUserName(authentication.getName()));
        }
        if ( gamePlayer!= null) {
           map.put("game", gameDTO(gamePlayer.getGame()));
           map.put("ships", gamePlayer.getShipSet()
                   .stream()
                   .map(this::shipDTO)
                   .collect(Collectors.toList()));
           map.put("salvoes", gamePlayer.getSalvoes()
                    .stream()
                    .map(this::salvoDTO)
                    .collect(Collectors.toList())
            );
            GamePlayer opponent = getOpponent(gamePlayer);
            if (opponent != null) {
                map.put("salvoesOpponent", opponent.getSalvoes()
                        .stream()
                        .map(this::salvoDTO)
                        .collect(Collectors.toList())

                );
                if(opponent.getShipSet().isEmpty() || gamePlayer.getShipSet().isEmpty()){
                    map.put("gameStatus", "wait");
                }else{
                    map.put("gameStatus", "start");
                }
                map.put("hitsOnUser", getHits(gamePlayer));
            }
        } else {
            map.put("Error", "Not an existing game");
        }

        return map;

    }



    //LeaderBoard
    @RequestMapping("/leaderboard")
    private List<Map<String,Object>> leaderboard() {
        List<Map<String,Object>> list = new ArrayList<>();
        for (Player player : playerRepository.findAll()) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("player", player.getUserName());
            map.put("playerId", player.getId());
            map.put("points", player.getScoreSet()
                    .stream().mapToDouble(Score::getScore).sum());
            map.put("win", player.getScoreSet().stream().filter(score -> score.getScore() == 1).count());
            map.put("tie", player.getScoreSet().stream().filter(score -> score.getScore() == 0.5).count());
            map.put("lose", player.getScoreSet().stream().filter(score -> score.getScore() == 0).count());
            list.add(map);
        }

        return list;
    }

    //password encryption
    @RequestMapping(path = "/players", method = RequestMethod.POST)
    public ResponseEntity<Object> register(

            @RequestParam String userName, @RequestParam String password) {

        if (userName.isEmpty() || password.isEmpty()) {
            return new ResponseEntity<>("Missing data", HttpStatus.FORBIDDEN);
        }

        if (playerRepository.findByUserName(userName) !=  null) {
            return new ResponseEntity<>("Name already in use", HttpStatus.FORBIDDEN);
        }

        playerRepository.save(new Player(userName, passwordEncoder.encode(password)));
        return new ResponseEntity<>(HttpStatus.CREATED);
    }



    //Extra Methods
    private GamePlayer getOpponent(GamePlayer gamePlayer) {
        return gamePlayer
                .getGame()
                .getGamePlayerSet()
                .stream()
                .filter(gp -> gp.getId() != gamePlayer.getId())
                .findFirst()
                .orElse(null);
    }

    private List<String> getHits(GamePlayer gamePlayer) {
        return getShipsLocation(getOpponent(gamePlayer))
                .stream()
                .filter(cell -> getSalvosLocation(gamePlayer).contains(cell))
                .collect(Collectors.toList());

    }

    private List<String> getShipsLocation(GamePlayer gamePlayer) {
        List list = new ArrayList();
        for (Ship ship : gamePlayer.getShipSet()) {
            for (String shipLocation : ship.getShiplocations()) {
                list.add(shipLocation);
            }
        }
        return list;
    }

    private List<String> getSalvosLocation(GamePlayer gamePlayer) {
        List list = new ArrayList();
        for (Salvo salvo : gamePlayer.getSalvoes()) {
            for (String salvoLocation : salvo.getSalvoLocations()) {
                list.add(salvoLocation);
            }
        }

        return list;
    }

    private Map<String, Object> makeMap(String key, Object value) {
        Map<String, Object> map = new HashMap<>();
        map.put(key, value);
        return map;
    }



    //DTO

    public Map<String,Object> gameDTO (Game game) {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("gameId", game.getId());
        dto.put("created", game.getCreationDate());
        dto.put("gamePlayers", game.getGamePlayerSet().stream().map(this::gamePlayerDTO).collect(Collectors.toList()));
        return  dto;
    }
    public Map<String,Object> gamePlayerDTO (GamePlayer gamePlayer) {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("gamePlayerId", gamePlayer.getId());
        dto.put("joinDate", gamePlayer.getJoinDate());
        dto.put("player", playerDTO(gamePlayer.getPlayer()));
        return  dto;
    }

    public Map<String,Object> playerDTO (Player player) {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("playerId", player.getId());
        dto.put("userName", player.getUserName());
        return  dto;
    }

    public Map<String,Object> shipDTO (Ship ship) {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("shipType", ship.getShipType());
        dto.put("shiplocations", ship.getShiplocations());
        return  dto;
    }

    public Map<String,Object> salvoDTO (Salvo salvo) {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("turn", salvo.getTurn());
        dto.put("salvoLocations", salvo.getSalvoLocations());
        dto.put("player", playerDTO(salvo.getGamePlayer().getPlayer()));
        return  dto;
    }

}
