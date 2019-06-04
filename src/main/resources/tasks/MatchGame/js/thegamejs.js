const timerVar = setInterval(countTimer, 1000);
var totalSeconds = 60;

function countTimer() {
   --totalSeconds;
    let seconds = totalSeconds;
    if (seconds < 10) {
        seconds = '0' + seconds;
    }
    if (totalSeconds < 1) {
        document.getElementById("timer").innerHTML = '00:00' + ":" + '60 s';
        postScoreJson(postScore_endpoint, 0);
    } else {
        document.getElementById("timer").innerHTML = '00:00' + ":" + seconds + ' s';
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