var config_endpoint = "/sudoku/config" //http://192.168.0.100:8082/sudoku/config"
var postScore_endpoint = "/game/end" //"http://192.168.0.100:8082/game/end"

function main(){
    window.name = 
    JSON.stringify({
        group : "ab34",
        nick : "Robert",
        age : 15
    })
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
    var adapterData = JSON.parse(window.name); 
    postScoreJson(postScore_endpoint, score);
}

function ageToErasedNumber(){
    var age = JSON.parse(window.name)["age"]
    if(age <= 10) return 81 - 65;
    if(age >= 11 && age <= 15) return 81 - 50;
    if(age >= 16  && age <= 20) return 81 - 45;
    if(age >= 21 && age <= 25) return 81 - 40;
    if(age >= 26) return 81 - 35;
}

function postScoreJson(link, score) {
    var data = JSON.parse(window.name); 

    var xobj = new XMLHttpRequest();
    xobj.open('POST', link, true);
    xobj.overrideMimeType("application/json");
    xobj.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
    xobj.withCredentials = true
    
    xobj.onreadystatechange = function() {
        if (xobj.readyState === 4 && xobj.status === 200) {
            console.log(xobj.responseText)
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
