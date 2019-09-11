var app = new Vue({
  el: "#app",
  data: {
    info: [],
    board: [],
    games: [],
    page: "login",
    user: false,
    active_el: 0,
    userName: "",
    password: "",
    error: "",
    playerId: ""
  },

  created: function () {
    this.getLeaderBoard();
    this.refresh();
  },

  methods: {

    refresh: function () {
      setInterval(function () {
        app.getData();
      }, 3000);
    },

    getData: function () {

      fetch("http://localhost:8080/api/games")
        .then(response => response.json())
        .then(json => {
          app.info = json;
          if (app.info) {
            app.getGames();
          }
        })
        .catch(error => console.log("error", error))
    },

    getGames: function () {
      var games = app.info;
      app.games = [];
      for (var i = 0; i < games.length; i++) {

        if (games[i].gamePlayers.length == 2) {
          app.games.push({
            "gameId": games[i].gameId,
            "dateCreated": games[i].created,
            "user1": games[i].gamePlayers[0].player.userName,
            "user2": games[i].gamePlayers[1].player.userName,
          })

        } else {
          app.games.push({
            "gameId": games[i].gameId,
            "dateCreated": games[i].created,
            "user1": games[i].gamePlayers[0].player.userName,
            "user2": "waiting for player",
          })
        }
      }

    },

    getLeaderBoard: function () {

      fetch("http://localhost:8080/api/leaderboard")
        .then(response => response.json())
        .then(json => {
          app.board = json
        })
        .catch(err => console.error(err))

    },

    login: function () {
      if (app.userName == "" || app.password == "") {
        app.error = "Please fill the information *";
      } else {
        console.log("login")
        fetch("http://localhost:8080/api/login", {
            credentials: 'include',
            method: 'POST',
            headers: {
              'Accept': 'application/json',
              'Content-Type': 'application/x-www-form-urlencoded'
            },
            body: "userName=" + app.userName + "&password=" + app.password + "",
          })
          .then(r => {
            if (r.status == 200) {
              app.user = true;
              app.getData();
              app.page = "home";
              app.password = "";
            } else {
              app.error = "Wrong login details *";
            }
          })
          .catch(e => console.log(e))
      }

    },


    logout: function () {

      fetch("http://localhost:8080/api/logout", {
          credentials: 'include',
          method: 'POST',
          headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/x-www-form-urlencoded'
          },
        })
        .then(r => {
          if (r.status == 200) {
            console.log(r)
            app.user = false;
            app.page = "login";
            app.password = "";
            app.userName = "";
          }
        })
        .catch(e => console.log(e))
    },

    createAcc: function () {

      if (app.userName == "" || app.password == "") {
        app.error = "Please fill the req fields*";
      } else {

        fetch("http://localhost:8080/api/players", {
            credentials: 'include',
            method: 'POST',
            headers: {
              'Accept': 'application/json',
              'Content-Type': 'application/x-www-form-urlencoded'
            },
            body: "userName=" + app.userName + "&password=" + app.password + "",
          })
          .then(r => {
            if (r.status == 201) {
              app.error = "Succes";
            } else {
              app.error = "User already exists!";
            }
          })
          .catch(e => console.log(e))
      }
    },

    createGame: function () {
      fetch("http://localhost:8080/api/games", {
          credentials: 'include',
          method: 'POST',
          headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/x-www-form-urlencoded'
          },
        })
        .then(r => {
          if (r.status == 201) {
            console.log(r)
            app.getData();
            app.getPlayerId();
          }
        })
        .catch(e => console.log(e))
    },

    joinGame: function (gameId) {

      fetch("http://localhost:8080/api/games/" + gameId.target.value + "/players", {
          credentials: 'include',
          method: 'POST',
          headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/x-www-form-urlencoded'
          },
          body: 'game_id=' + gameId.target.value + '',
        })
        .then(r => {
          console.log(r)
          app.getData();
          app.getPlayerId();
        })
        .catch(e => console.log(e))

    },


    activate: function (el) {
      this.active_el = el;
    },

    getPlayerId: function () {
      for (var i = 0; i < app.board.length; i++) {
        if (app.userName == app.board[i].player) {
          app.playerId = app.board[i].playerId;
        }
      }

      if (app.playerId.length != 0) {
        let url = window.location.href.split("/").slice(0, 3).join("/", "");
        window.location.href = url + "/game.html?gp=" + app.playerId + "";
      }
    },








  }





})
