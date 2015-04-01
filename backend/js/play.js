leaderboard = [];
achievements = [];
achievementsState = [];
player_id = "";
auth = false;

function signinCallbackLeaderboard(authResult) {
  if (authResult['status']['signed_in']) {
    document.getElementById('signinButton').setAttribute('style', 'display: none');
	auth = true;
	authLeaderboard();
  } else {
    console.log('Sign-in state: ' + authResult['error']);
  }
}


function signinCallbackAchievements(authResult) {
  if (authResult['status']['signed_in']) {
    document.getElementById('signinButton').setAttribute('style', 'display: none');
	auth = true;
	authAchievements();
  } else {
    console.log('Sign-in state: ' + authResult['error']);
  }
}

// Function that is called immediately to sign in and authenticate a user to Google Play via OAUTH.
function authLeaderboard(){
    if(auth){
      gapi.auth.authorize({client_id: "689728249892-r4l3ip9nrblqkun6aql2bsbrsjr21ksj.apps.googleusercontent.com",
                     scope: 'https://www.googleapis.com/auth/games https://www.googleapis.com/auth/appstate',
                     immediate: true}, requestLeaderboard);
	}
}

function authAchievements(){
    if(auth){
      gapi.auth.authorize({client_id: "689728249892-r4l3ip9nrblqkun6aql2bsbrsjr21ksj.apps.googleusercontent.com",
                     scope: 'https://www.googleapis.com/auth/games https://www.googleapis.com/auth/appstate',
                     immediate: true}, requestAchievements);
    }
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
        drawTable(leaderboard, drawRowLeaderboard);
      });
      
  });
  
}

// Function that returns JSON data representing the player.
function requestAchievements(){
  // Load the games script.
  gapi.client.load('games','v1',function(response) {
      // Attempt to get gapi.
      console.log('gapi.client loaded.');
      var json= {playerId: player_id, maxResults: "100"};
      // Register async. callback
      
      var request = gapi.client.games.achievements.list(json)
      
      // Set the achievements state
      request.execute(function(response) { 
          achievementsState = response;
          request = gapi.client.games.achievementDefinitions.list(json)
      
          // Set the achievements definition.
          request.execute(function(response) { 
            achievements = response;
            drawTable(achievements, drawRowAchievements);
          });
      });
           
      
  });
  
}

// Generic function to draw a table from data retrieved from: http://jsfiddle.net/mjaric/sEwM6/
function drawTable(data, rowMethod) {
    $("#loading").hide();
    for (var i = 0; i < data.items.length; i++) {
        rowMethod(data.items[i], i);
    }
}

function drawRowLeaderboard(rowData, i) {
    var row = $("<tr />")
    $("#leaderboardTable").append(row);
    row.append($("<td>" + rowData.formattedScoreRank + "</td>"));
    row.append($("<td>" + rowData.player.name.givenName + "</td>"));
    row.append($("<td>" + rowData.player.name.familyName + "</td>"));
    row.append($("<td> <a href='https://plus.google.com/" + rowData.player.playerId + "'>" + rowData.player.displayName + "</a></td>"));
    row.append($("<td>" + rowData.scoreValue + "</td>"));
}

function drawRowAchievements(rowData, i) {
    var row = $("<tr />")
    $("#achievementsTable").append(row);
    row.append($("<td><img height='50' width='50' src='" + rowData.unlockedIconUrl + "' /></td>"));
    row.append($("<td>" + rowData.name + "</td>"));
    row.append($("<td>" + rowData.description + "</td>"));
    // Display and render incrmental achievements
    if(rowData.achievementType == "INCREMENTAL"){
      row.append($("<td>Incremental with " + achievementsState.items[i].currentSteps + " steps out of " + rowData.totalSteps + " left to unlock</td>"));
    }else{
      row.append($("<td>Standard Achievement, unlock once!</td>"));
    }
    if(achievementsState.items[i].achievementState == "UNLOCKED"){
      row.append($("<td> This achievement is unlocked!</td>"));
    }else {
      row.append($("<td> This achievement is locked!</td>"));
    }
}