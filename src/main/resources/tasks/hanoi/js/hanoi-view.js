(function() {
  if (typeof Hanoi === 'undefined') {
    window.Hanoi = {};
  }

  var View = Hanoi.View = function (game, $el, nrOfDisks, endFunction) {
    this.selectedColumn = null;
    this.game = game;
    this.$el = $el;
    this.endFunction = endFunction;
    this.nrOfMoves = 0;
    this.nrOfDisks = nrOfDisks;
    this.render();
    this.bindEvents();
  };

  View.prototype.render = function () {
    $(this.$el).html('');
    for (var j = 0; j < 3; j++) {
      var col = this.game.towers[j];
      var $tower = $("<div data-col='" + j + "' class='tower'></div>");
      for (var i = 0; i < col.length; i++) {
        var $disk = $("<div class='disk'></div>");
        $disk.css("width", (col[i] * 30) + "px");
        $tower.append($disk);
      }
      $(this.$el).append($tower);
    }
  };

  View.prototype.bindEvents = function () {
    this.$el.on("click", ".tower", (function (myEvent) {
      var $tower = $(myEvent.currentTarget);
      this.clickTower($tower);
    }).bind(this));

  };

  View.prototype.clickTower = function ($tower) {
    if (this.selectedColumn === null) {
      this.selectedColumn = $tower.data('col');
      $tower.children().last().attr('class', 'h-disk');
    } else {
      var newCol = $tower.data('col');
      if (this.game.move(this.selectedColumn, newCol)) {
        this.render();
        this.nrOfMoves += 1;
        if (this.game.isWon()) {
          //alert('You won!');
          var minNrOfMoves = Math.pow(2, this.nrOfDisks) - 1;
          var result = (3*minNrOfMoves - (this.nrOfMoves)) / (2*minNrOfMoves);
          if (result < 0)
            result = 0;
          this.endFunction(result);
        }
      } else {
        this.render();
        alert('Invalid move!');
      }
      this.selectedColumn = null;
    }
  };

})();
