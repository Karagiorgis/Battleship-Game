var app = new Vue({
  el: "#app",
  data: {
    info: [],
    ships: [],
    salvoes: [],
    oppSalvoes: [],
    gameStatus: "",
    user: [],
    opponent: [],
    letters: ["A", "B", "C", "D", "E", "F", "G", "H", "I", "J"],
    numbers: ["1", "2", "3", "4", "5", "6", "7", "8", "9", "10"],
    gameShips: [],
    selectedShip: [],
    selectedSalvos: [],
    gameInfo: [],
    rotateShip: false,
    shots: 5,
    done: false,
    shipPlaceError: "",
    salvoHits: [],
    lengthShip: 0,
    turn: 1,
    sunked: 0,
    post: "ready"
  },

  created: function () {
    this.getData(location.search.split('=')[1]);
    this.refresh();
  },

  methods: {
    refresh: function () {
      setInterval(function () {
        app.getData(location.search.split('=')[1]);
        app.post = 'ready';
      }, 2000);
    },

    getData: function (url) {

      fetch("http://localhost:8080/api/game_view/" + url)
        .then(response => response.json())
        .then(json => {
          app.info = json;
          app.getPlayers();
          app.gameStatus = app.info.gameStatus;
          if (app.ships.length == 0) {
            app.ships = app.info.ships;
          }
          if (app.gameShips.length == 0) {
            this.createDefaultShips();
          }
          if (app.info.salvoes.length != 0) {
            app.oppSalvoes = app.info.salvoesOpponent;
            app.salvoHits = app.info.hitsOnUser;
            app.salvoes = app.info.salvoes;
            app.sunk();
            app.printSunk();
          }
        })
        .catch(error => console.log("error", error))

    },

    createDefaultShips: function () {
      app.gameShips.push({
        "shipType": "Aircraft-Carrier",
        "length": 5,
      }, {
        "shipType": "Battleship",
        "length": 4,

      }, {
        "shipType": "Submarine",
        "length": 3,

      }, {
        "shipType": "Destroyer",
        "length": 3,

      }, {
        "shipType": "Patrol-Boat",
        "length": 2,
      });

      app.shipPlaceError = "Place yours ships";
    },

    printShip(letter, number) {
      let classType;
      for (let i = 0; i < this.ships.length; i++) {
        if (this.ships[i].shiplocations.includes(letter + number)) {
          classType = 'ship-location ' + this.ships[i].shipType;
          break;
        }
      }
      return classType;
    },

    printSalvoes(letter, number) {

      let classType;
      for (let i = 0; i < this.salvoes.length; i++) {
        if (this.salvoes[i].salvoLocations.includes(letter + number)) {
          classType = 'salvoes-location ' + this.salvoes[i].turn;
          break;
        }
      }
      return classType;
    },


    getPlayers() {

      let players = app.info.game.gamePlayers;
      let url = location.search.split('=')[1];
      app.user = [];
      app.opponent = [];
      for (var i = 0; i < players.length; i++) {
        if (players[i].player.playerId == url) {
          app.user.push(players[i].player);
        } else {
          app.opponent.push(players[i].player);
        }

      }
    },

    shipHits(letter, number) {
      let classType;
      for (let i = 0; i < this.oppSalvoes.length; i++) {
        if (this.oppSalvoes[i].salvoLocations.includes(letter + number)) {
          classType = 'ship-location-hit ' + this.oppSalvoes[i].turn;
          break;
        }
      }
      return classType;

    },

    mySalvoHits(letter, number) {
      let classType;
      for (let i = 0; i < this.salvoHits.length; i++) {
        if (this.salvoHits.includes(letter + number)) {
          classType = 'salvo-location-hit ';
          break;
        }
      }
      return classType;

    },

    redirect() {
      let url = window.location.href.split("/").slice(0, 3).join("/", "");
      window.location.href = url + "/games.html";
    },

    getGridLocation: function (box) {

      var locations = [];
      var letter = box.target.id.split('').slice(0, 1);
      var number = box.target.cellIndex;

      if (app.selectedShip.length != 0 && box.target.classList.length == 0) {

        for (var i = 0; i < app.selectedShip.length; i++) {
          if (app.rotateShip == false && (number + app.selectedShip.length) <= 11) {
            locations.push(letter + (number + i));
          } else {
            let val = String.fromCharCode(letter[0].charCodeAt() + i) + number;
            if (app.letters.includes(val[0])) {
              locations.push(val)
            } else {
              app.shipPlaceError = "cannot place ships here *";
              return;
            }
          }
        }

        app.ships.push({
          "shipType": app.selectedShip.shipType,
          "shiplocations": locations,
          "sunk": false,
          "length": app.selectedShip.length,
        })

        app.selectedShip.length = 0;
        app.shipPlaceError = "Ship is placed *";

      } else {
        app.shipPlaceError = "cannot place ships here *";
      }
    },

    getGridSalvoLocation: function (box) {

      if (app.ships.length == 5 && app.shots > 0) {
        app.shots -= 1;
        var locations = [];
        var letter = box.target.id.split('').slice(0, 1);
        var number = box.target.cellIndex;

        if (box.target.style.background != "red") {

          box.target.style.background = "red";
          locations.push(letter + number);
          app.selectedSalvos.push(locations[0]);

          if (app.selectedSalvos.length == 5) {
            app.salvoes.push({
              "salvoLocations": app.selectedSalvos
            })
          }
        } else {
          console.log("cannot place ship here")
        }
      } else {
        app.shipPlaceError = "please place all ships *";
      }
    },

    postShips() {
      let url = location.search.split('=')[1];
      fetch("http://localhost:8080/api/games/players/" + url + "/ships", {
          credentials: 'include',
          method: 'POST',
          headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
          },
          body: JSON.stringify(app.ships),
        })
        .then(r => {
          console.log(r)
          this.getData(url);
        })
        .catch(e => console.log(e))

    },

    postSalvos() {
      
      let url = location.search.split('=')[1];
      let salvoes = [];
      salvoes.push({"salvoLocations": app.selectedSalvos})
      
      fetch("http://localhost:8080/api/games/players/" + url + "/salvos", {
          credentials: 'include',
          method: 'POST',
          headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
          },
          body: JSON.stringify(salvoes),
        })
        .then(r => {
          console.log(r)
          app.selectedSalvos = [];
          app.shots = 5;
          app.post = 'posting';
          app.getData(url);
        })
        .catch(e => console.log(e))

    },


    sunk: function () {

      var salvo = [];
      var hits = [];

      for (var i = 0; i < app.oppSalvoes.length; i++) {
        for (var j = 0; j < app.oppSalvoes[i].salvoLocations.length; j++) {
          salvo.push(app.oppSalvoes[i].salvoLocations[j])
        }
      }

      for (var k = 0; k < app.ships.length; k++) {
        for (var h = 0; h < app.ships[k].shiplocations.length; h++) {
          if (salvo.includes(app.ships[k].shiplocations[h])) {
            hits.push(app.ships[k].shiplocations[h])
          }

        }
      }

      app.checkShip(hits);


    },

    checkShip: function (hits) {

      for (var i = 0; i < app.ships.length; i++) {
        var intersection = hits.filter(element => app.ships[i].shiplocations.includes(element));
        if (intersection.length == app.ships[i].shiplocations.length) {
          app.ships[i].sunk = "true";
        }
      }

    },

    removeShip: function () {
      for (var i = 0; i < app.ships.length; i++) {
        if (app.selectedShip.shipType == app.ships[i].shipType) {
          app.selectedShip.length = app.ships[i].length;
          app.ships.splice(i, 1);
        }
      }

    },

    printSunk: function () {

      for (var i = 0; i < app.ships.length; i++) {
        if (app.ships[i].sunk == "true") {
          var table = document.getElementById("table");
          var td = table.getElementsByTagName("td");
          for (var j = 0; j < td.length; j++) {
            let name = td[j].className.split(" ").splice(1, 1);
            if (app.ships[i].shipType == name) {
              td[j].className = "sunk";
            }
          }
        }
      }
    },
    
    //    getTurn: function () {
    //      for (var i = 0; i < app.info.salvoes.length; i++) {
    //        if(app.turn < app.info.salvoes[i].turn){
    //        app.turn = app.info.salvoes[i].turn;
    //        }
    //      }
    //
    //    }


    //    getDataGame: function () {
    //      let url = location.search.split('=')[1];
    //      fetch("http://localhost:8080/api/game_view/" + url)
    //        .then(response => response.json())
    //        .then(json => {
    //          app.gameInfo = json;
    //        })
    //        .catch(error => console.log("error", error))
    //
    //    },


  }





})
