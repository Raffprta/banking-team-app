leaderboard = [];

// Function that is called immediately to sign in and authenticate a user to Google Play via OAUTH.
function auth(){
    gapi.auth.authorize({client_id: "689728249892-r4l3ip9nrblqkun6aql2bsbrsjr21ksj.apps.googleusercontent.com",
                     scope: 'https://www.googleapis.com/auth/games https://www.googleapis.com/auth/appstate',
                     immediate: true}, requestLeaderboard);
}

// Function that returns JSON data representing the leaderboard.
function requestLeaderboard(){
  // Load the games script.
  gapi.client.load('games','v1',function(response) {
      // Attempt to get leaderboard.
      console.log('gapi.client loaded.');
      var json= { leaderboardId: 'CgkIpMCHuIkUEAIQBg', collection: 'PUBLIC', timeSpan:'ALL_TIME'};
      // Register async. callback
      var request = gapi.client.games.scores.listWindow(json);
      
      // Set the leaderboard.
      request.execute(function(response) { 
        leaderboard = response;
        drawTable(leaderboard);
      });
      
  });
  
}

// Generic function to draw a table from data retrieved from: http://jsfiddle.net/mjaric/sEwM6/
function drawTable(data) {
    $("#loading").hide();
    for (var i = 0; i < data.numScores; i++) {
        drawRow(data.items[i]);
    }
}

function drawRow(rowData) {
    var row = $("<tr />")
    $("#leaderboardTable").append(row);
    row.append($("<td>" + rowData.formattedScoreRank + "</td>"));
    row.append($("<td>" + rowData.player.name.givenName + "</td>"));
    row.append($("<td>" + rowData.player.name.familyName + "</td>"));
    row.append($("<td> <a href='https://plus.google.com/" + rowData.player.playerId + "'>" + rowData.player.displayName + "</a></td>"));
    row.append($("<td>" + rowData.scoreValue + "</td>"));
}