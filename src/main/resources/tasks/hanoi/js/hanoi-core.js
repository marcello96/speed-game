!function(e){if("object"==typeof exports)module.exports=e();else if("function"==typeof define&&define.amd)define(e);else{var f;"undefined"!=typeof window?f=window:"undefined"!=typeof global?f=global:"undefined"!=typeof self&&(f=self),f.Hanoi=e()}}(function(){var define,module,exports;return (function e(t,n,r){function s(o,u){if(!n[o]){if(!t[o]){var a=typeof require=="function"&&require;if(!u&&a)return a(o,!0);if(i)return i(o,!0);throw new Error("Cannot find module '"+o+"'")}var f=n[o]={exports:{}};t[o][0].call(f.exports,function(e){var n=t[o][1][e];return s(n?n:e)},f,f.exports,e,t,n,r)}return n[o].exports}var i=typeof require=="function"&&require;for(var o=0;o<r.length;o++)s(r[o]);return s})(
{1:[function(_dereq_,module,exports){

  function Game (nrOfDisks) {
    if (nrOfDisks == 2)
      this.towers = [[2, 1], [], []];
    else if (nrOfDisks == 3)
      this.towers = [[3, 2, 1], [], []];
    else if (nrOfDisks == 4)
      this.towers = [[4, 3, 2, 1], [], []];
    else if (nrOfDisks == 5)
      this.towers = [[5, 4, 3, 2, 1], [], []];
    else if (nrOfDisks == 6)
      this.towers = [[6, 5, 4, 3, 2, 1], [], []];
  };

  Game.prototype.isValidMove = function (startTowerIdx, endTowerIdx) {
    var startTower = this.towers[startTowerIdx];
    var endTower = this.towers[endTowerIdx];

    if (startTower.length === 0) {
      return false;
    } else if (endTower.length == 0) {
      return true;
    } else {
      var topStartDisc = startTower[startTower.length - 1];
      var topEndDisc = endTower[endTower.length - 1];
      return topStartDisc <= topEndDisc;
    }
  };

  Game.prototype.isWon = function () {
    // move all the discs to the last tower
    return (this.towers[2].length == nrOfDisks);
  };

  Game.prototype.move = function (startTowerIdx, endTowerIdx) {
    if (this.isValidMove(startTowerIdx, endTowerIdx)) {
      this.towers[endTowerIdx].push(this.towers[startTowerIdx].pop());
      return true;
    } else {
      return false;
    }
  };

  Game.prototype.print = function () {
    console.log(JSON.stringify(this.towers));
  };

  module.exports = Game;

},{}],2:[function(_dereq_,module,exports){
// index.js is probably overkill in this example; would make more sense
// if there were more source files.

module.exports = {
  Game: _dereq_("./game")
};

},{"./game":1}]},{},[2])
(2)
});
