import levels from './levels';
import store from './store';
import {
  canMoveTo,
  distanceFromPlayer,
  maxMovesMet,
  winConditionsMet,
} from './helpers';
import { EASY, MEDIUM, HARD } from './difficulty-levels'
import { log, playSoundEffect } from './util';
import { START_GAME, LOAD_LEVEL, LOSE, MOVE, WIN } from './actions';
import { LOSE_AUDIO, MOVE_AUDIO, WIN_AUDIO } from './audio';
import { NEXT_LEVEL_DELAY } from './misc';


export function startGame (levelNumber) {

  store.dispatch({ type: START_GAME });

  loadLevel(levelNumber);
}

export function getJSON(link, callback) {
   var xobj = new XMLHttpRequest();
   xobj.overrideMimeType("application/json");
   xobj.open('GET', link, false);
   xobj.send(null);
   callback(xobj.responseText);

}

export function afterConfigFetched(configJSON){
  console.log("Got response: " + configJSON);
  window.name = configJSON
}

export function getDifficultyByAge() {
    var age = JSON.parse(window.name)["age"];
    //var age = 7;
    if(age < 10) {
        return EASY;
    } else if (age < 15){
        return MEDIUM;
    } else
      return HARD;
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

// Loads the given level.
export function loadLevel (levelNumber) {
  const levelExists = levelNumber in levels;

  if (!levelExists) {
    log('warn', `There is no level ${levelNumber}.`);
    levelNumber = 0;
  }

  store.dispatch({ type: LOAD_LEVEL, levelNumber });
}

// Moves the player up, down, left, or right.
export function move (rowDelta, columnDelta) {
  const { playerPosition } = store.getState();
  const row = playerPosition.row + rowDelta;
  const column = playerPosition.column + columnDelta;
  moveTo(row, column);
}

// Moves the player to a specific location.
export function moveTo (row, column) {
  const state = store.getState();

  if (!canMoveTo(state, row, column)) {
    return;
  }

  const invalidMoveDistance = distanceFromPlayer(state, row, column) !== 1;

  // Ensure player only move one spot at a time.
  if (invalidMoveDistance) {
    return;
  }

  store.dispatch({ type: MOVE, row, column });
  playSoundEffect(MOVE_AUDIO);
  checkForWinOrLoss();
}

// Resets the current level.
export function reset () {
  const { currentLevelNumber } = store.getState();
  loadLevel(currentLevelNumber);
}

// Checks whether the level has been won or lost.
function checkForWinOrLoss () {
  const state = store.getState();

  if (winConditionsMet(state)) {
    win();
  } else if (maxMovesMet(state)) {
    lose();
  }
}

// Loads the next level after a pause.
function loadNextLevelAfterDelay () {
  const { currentLevelNumber } = store.getState();
  const nextLevelNumber = currentLevelNumber + 1;
  setTimeout(() => loadLevel(nextLevelNumber), NEXT_LEVEL_DELAY);
}

// Admonishes the player then restarts the current level.
function lose () {
  store.dispatch({ type: LOSE });
  playSoundEffect(LOSE_AUDIO);
  resetAfterDelay();
}

// Reloads the current level after a pause.
function resetAfterDelay () {
  setTimeout(reset, NEXT_LEVEL_DELAY);
}

// Congratulates the player then move onto the next level.
function win () {
  const { moveCount } = store.getState();
  log('info', `Completed in ${moveCount} moves.`);
  store.dispatch({ type: WIN });
  const { startingTime } = store.getState();
  const { endTime } = store.getState();

  console.log('info', `Completed in ${endTime - startingTime} time.`);
  const deltaTime = endTime - startingTime;
  var score = 0.0;
  if(deltaTime < 15000)
  {
  console.log("1 pkt");
  score = 1;
  }

  else if (deltaTime < 60000)
  {
  score = (45000 - deltaTime)/45000;
  console.log(`${(45000 - deltaTime)/45000} pkt`);
  }

  else console.log("0 pkt");
  const {numberOfTries} = store.getState();

  console.log('info', `Completed in ${numberOfTries} tries.`);
  playSoundEffect(WIN_AUDIO);
  postScoreJson("/hexahedral/end", score);

  // loadNextLevelAfterDelay();
}
