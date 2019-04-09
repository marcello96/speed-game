var config_endpoint = "/memo/config" //"http://192.168.0.100:8082/memo/config"
var postScore_endpoint = "/memo/end" //"http://192.168.0.100:8082/game/end"

function main(){
    window.name = 
    JSON.stringify({
        group : "ab34",
        nick : "Robert",
        age : 26
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

function ageToRememberTime(){
    var age = JSON.parse(window.name)["age"]
    if(age <= 10) return 30;
    if(age >= 11 && age <= 15) return 25;
    if(age >= 16  && age <= 20) return 20;
    if(age >= 21 && age <= 25) return 15;
    if(age >= 26) return 10;
}


function ageToTilesList(){
    var age = JSON.parse(window.name)["age"]
    if(age <= 10) return ['lvl1/8-ball', 'lvl1/kronos', 'lvl1/baked-potato', 'lvl1/dinosaur', 'lvl1/rocket', 'lvl1/skinny-unicorn','lvl1/that-guy', 'lvl1/zeppelin', ];
    if(age >= 11 && age <= 15) return ['lvl2/boar','lvl2/buffalo','lvl2/bull','lvl2/deer','lvl2/elephant','lvl2/fox','lvl2/gorilla','lvl2/wolf'];
    if(age >= 16  && age <= 20) return ['lvl3/vector1', 'lvl3/vector2','lvl3/vector3','lvl3/vector4','lvl3/vector5','lvl3/vector6','lvl3/vector7','lvl3/vector8'];
    if(age >= 21 && age <= 25) return ['lvl4/8-ball', 'lvl4/kronos', 'lvl4/baked-potato', 'lvl4/dinosaur', 'lvl4/rocket',    'lvl4/skinny-unicorn','lvl4/that-guy', 'lvl4/zeppelin', 'lvl4/boar','lvl4/buffalo','lvl4/bull', 'lvl4/deer','lvl4/elephant','lvl4/fox','lvl4/gorilla','lvl4/wolf', 'lvl4/sheep', 'lvl4/lion'];
    if(age >= 26) return  ['lvl5/vector1','lvl5/vector2','lvl5/vector3','lvl5/vector4','lvl5/vector5','lvl5/vector6','lvl5/vector7','lvl5/vector8','lvl5/vector9','lvl5/vector10','lvl5/vector11','lvl5/vector12','lvl5/vector13','lvl5/vector14','lvl5/vector15','lvl5/vector16','lvl5/vector17','lvl5/vector18'];
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
