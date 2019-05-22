var postScore_endpoint = "/MatchGame/end";
// Get the modal
var modal = document.getElementById('pass-modal');

/*
// Get the button that opens the modal
var btn = document.getElementById("myBtn");
*/

// Get the <span> element that closes the modal
var span = document.getElementsByClassName("close")[0];

// When the user clicks on <span> (x), close the modal
span.onclick = function() {
    modal.style.display = "none";
};

// When the user clicks anywhere outside of the modal, close it
window.onclick = function(event) {
    if (event.target == modal) {
        modal.style.display = "none";
    }
};
/*
 *checkTime
 *time: Time play duration.
 */

function checkTime(time) {
    var check=false;
    var flagg=false;

    if(time<60) {
        check=true;
        var equationNumber = sessionStorage.getItem("eq_no");

        if(sessionStorage.equations) {
            equationsArr=sessionStorage.getItem("equations").split(",");
        } else {
            equationsArr=[];
        }

        for(var i=0;i<equationsArr.length;i++) {
            if(equationNumber==equationsArr[i]) {
                flagg=true;
            }
        }

        if(!flagg)
            equationsArr.push(equationNumber);
        sessionStorage.setItem("equations",equationsArr.toString());
        console.log(equationsArr.length);
    }
    return check;
}


/*
 *pass_or_fail_msg: function to retrive pass or fail popup
 *eq_ewsult: Boolean T if player solved eq
 *eq_time: Time play duration.
 */

function pass_or_fail_msg(eq_result,eq_time) {
    var footer=document.getElementsByClassName('modal-footer')[0];
    var mbody=document.getElementsByClassName('modal-body')[0];
    var header=document.getElementsByClassName('modal-header')[0];
    var eq_no=sessionStorage.eq_no;

    if(eq_result == true) {
        console.log('equation result true');

        timeUnderMinute = checkTime(eq_time);
        if(timeUnderMinute==true) {
            // time is under minute
        }

        if(eq_no==9)
        {
            //tasks is eq_array from
            flash_eq_arr = sessionStorage.equations.split(",");
        }

        header.innerHTML='<h1> Gratulacje :)</h1>';
        mbody.innerHTML='<button id="cont" onclick="move_next();" class="retry-button">Następne</button>';
        modal.style.display = "block";
    } else {
        header.innerHTML='<h1>Spróbuj jeszcze raz</h1>';
        mbody.innerHTML='<button id="ret" onclick="reloading();" class="retry-button">Spróbuj jeszcze raz</button>';
        modal.style.display = "block";
    }

}

function move_next(){
    console.log('time of game');
    console.log(totalSeconds);
    const score = compute_score();
	sendScoreAndReturnControl(score);
    //var eq=sessionStorage.eq_no;
    //eq++;
    //sessionStorage.eq_no=eq;
    //location.reload();
}


function compute_score() {
    if (totalSeconds < 10) {
        return 1;
    } if (totalSeconds > 60) {
        return 0;
    } else {
        return -0.014 * totalSeconds + 1.14
    }
}

function sendScoreAndReturnControl(score){
    var adapterData = JSON.parse(window.name);
    postScoreJson(postScore_endpoint, score);
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
            window.location = xobj.responseText;
        }
    };

    var sentPayload =
        JSON.stringify(
            {
                group : data["group"],
                nick : data["nick"],
                age : data["age"],
                result : score
            }
        );
    console.log("Sending: " + sentPayload);
    xobj.send(sentPayload);
}