'use strict';
/* Memory Game Models and Business Logic */

function Tile(title) {
  this.title = title;
  this.flipped = true;
}

Tile.prototype.flip = function() {
  this.flipped = !this.flipped;
}



function Game(tileNames) {
  var tileDeck = makeDeck(tileNames);

  this.grid = makeGrid(tileDeck);
  this.message = Game.MESSAGE_CLICK;
  this.unmatchedPairs = tileNames.length;

  this.flipTile = function(tile) {
    if (tile.flipped) {
      return;
    }

    tile.flip();

    if (!this.firstPick || this.secondPick) {

      if (this.secondPick) {
        this.firstPick.flip();
        this.secondPick.flip();
        this.firstPick = this.secondPick = undefined;
      }

      this.firstPick = tile;
      this.message = Game.MESSAGE_ONE_MORE;

    } else {

      if (this.firstPick.title === tile.title) {
        this.unmatchedPairs--;
        this.message = (this.unmatchedPairs > 0) ? Game.MESSAGE_MATCH : Game.MESSAGE_WON;
        this.firstPick = this.secondPick = undefined;
        
        var unmatchedPairs = this.unmatchedPairs
        var delayMillis = 1000
        setTimeout(
          function(){
            onCardRotationEnd(unmatchedPairs)
          }, 
          delayMillis
        );

      } else {
        this.secondPick = tile;
        this.message = Game.MESSAGE_MISS;
      }
    }
  }

  this.coverCardsAfterTime = function(timeInSec, $scope){
    var grid = this.grid;
    this.updateTimeDisplay(timeInSec, $scope)
    setTimeout(
      function(){
        coverAllCards(grid, $scope)
      },
      timeInSec * 1000
    )
  } 

  this.updateTimeDisplay = function(timeInSec, $scope){
    var seconds_left = timeInSec;
    var game = this;
    this.message = "Czas na zapamiętanie kart: " + seconds_left;
    var interval = setInterval(function() {
        game.message = "Czas na zapamiętanie kart: " +  (--seconds_left);

        if (seconds_left <= 0)
        {
            game.message = Game.MESSAGE_CLICK;
            clearInterval(interval);
        }
        $scope.$apply()
    }, 1000);
  }
}

function coverAllCards(grid, $scope){
  console.log("Covering all cards")
  for (var i = 0; i < grid.length; i++){
    for(var j = 0; j < grid[i].length; j++){
      grid[i][j].flip();
    }
  }
  $scope.$apply()
}

function onCardRotationEnd (unmatchedPairs){
  if(unmatchedPairs == 0) {
    sendScoreAndReturnControl(1)
  }
}


Game.MESSAGE_CLICK = 'Dotknij jedną z kart.';
Game.MESSAGE_ONE_MORE = 'Wybierz jeszcze jedną kartę.'
Game.MESSAGE_MISS = 'Spróbuj ponownie.';
Game.MESSAGE_MATCH = 'Wspaniale! Oby tak dalej!';
Game.MESSAGE_WON = 'Wygrałeś';


/* Create an array with two of each tileName in it */
function makeDeck(tileNames) {
  var tileDeck = [];
  tileNames.forEach(function(name) {
    tileDeck.push(new Tile(name));
    tileDeck.push(new Tile(name));
  });
  console.log("making deck: " + tileDeck)
  return tileDeck;
}


function makeGrid(tileDeck) {
  var gridDimension = Math.sqrt(tileDeck.length),
      grid = [];

  for (var row = 0; row < gridDimension; row++) {
    grid[row] = [];
    for (var col = 0; col < gridDimension; col++) {
        grid[row][col] = removeRandomTile(tileDeck);
    }
  }

  return grid;
}


function removeRandomTile(tileDeck) {
  var i = Math.floor(Math.random()*tileDeck.length);
  return tileDeck.splice(i, 1)[0];
}