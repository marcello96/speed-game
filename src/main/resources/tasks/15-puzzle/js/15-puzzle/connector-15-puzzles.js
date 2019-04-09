var config_endpoint = "/15-puzzle/config"
var postScore_endpoint = "/15-puzzle/endgit "

function main(){
    getJSON(config_endpoint, afterConfigFetched);
}

function getJSON(link, callback) {
    var xobj = new XMLHttpRequest();
    xobj.overrideMimeType("application/json");
    xobj.open('GET', link, false);
    xobj.send(null);
    callback(xobj.responseText);
}

function afterConfigFetched(configJSON){
    console.log("Got response: " + configJSON);
    window.name = configJSON
}

function sendScoreAndReturnControl(score){
    var adapterData = JSON.parse(window.name);git
    postScoreJson(postScore_endpoint, score);
}

function getGridSize() {
    var age = JSON.parse(window.name)["age"];
    //var age = 9;
    if(age < 10) {
        return 3;
    } else if(age < 15) {
        return 4;
    } else if(age < 20) {
        return 5;
    } else {
        return 6;
    }
}

function getRemainingMoves() {
    var age = JSON.parse(window.name)["age"];
    //var age = 21;
    if(age < 8) {
        return 31 * 10 ;
    } else if(age < 10) {
        return 31 * 5;
    } else if(age < 15) {
        return 80 * 4;
    } else {
        return 80 * 2;
    }
}

function postScoreJson(link, score) {
    var data = JSON.parse(window.name);

    var xobj = new XMLHttpRequest();
    xobj.open('POST', link, true);
    xobj.overrideMimeType("application/json");
    xobj.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
    xobj.withCredentials = true;

    xobj.onreadystatechange = function() {
        if (xobj.readyState === 4 && xobj.status === 200) {
            console.log(xobj.responseText);
            window.location = xobj.responseText
        }
    };

    var sentPayload =
        JSON.stringify(
            {
                group : data["group"],
                nick : data["nick"],
                age : data["age"],
                result: score
            }
        );
    console.log("Sending: " + sentPayload)
    xobj.send(sentPayload);
}
