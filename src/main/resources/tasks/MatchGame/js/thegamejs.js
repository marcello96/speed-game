const timerVar = setInterval(countTimer, 1000);
let totalSeconds = -1;

function countTimer() {
   ++totalSeconds;
    const hour = Math.floor(totalSeconds / 3600);
    const minute = Math.floor((totalSeconds - hour * 3600) / 60);
    let seconds = totalSeconds - (hour * 3600 + minute * 60);
    if (seconds < 10) {
        seconds = '0' + seconds;
    }
    document.getElementById("timer").innerHTML = '00:00' + ":" + seconds + ' s';
    if (totalSeconds > 60) {
        // next game
        console.log('end game');
		postScoreJson(postScore_endpoint, 0);
    }
}
    
function reloading() {
    const div = document.getElementById("divequation");
    div.innerHTML = '';
    divequation();
    draw_equation(equations[eq_num].first, equations[eq_num].op, equations[eq_num].sec, equations[eq_num].res);
    moves_mode(equations[eq_num].mode, equations[eq_num].sol[0].length);
    modal.style.display = "none";
}

var config_endpoint = "/MatchGame/config";
getJSON(config_endpoint, afterConfigFetched);
countTimer();

function getJSON(link, callback) {
	console.log("Sending request for config: " + link);
    var xobj = new XMLHttpRequest();
    xobj.overrideMimeType("application/json");
    xobj.open('GET', link, false);
    xobj.send(null);
    callback(xobj.responseText);
}

function afterConfigFetched(configJSON){
    console.log("Got response: " + configJSON);
    window.name = configJSON;
}