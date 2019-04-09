Sudoku.controller('SudokuController', function SudokuController($scope, data) {
	'use strict';

	$scope.rows = angular.copy(data);		

    /**
     * Creates an empty grid.
     */
	function createEmptyRows() {
		var rows = angular.copy(data);
		for (var l=0; l<9; l++)
			for(var c=0; c<9; c++){
				rows[l].columns[c].value = "";
				rows[l].columns[c].class = "";
			}
		return rows;
	}

    /**
     * Checks if the current grid is solved.
     */
	function isSolved(rows)
	{
		for(var i = 0; i < 9; i++)
			for(var k=0; k<9; k++){
				if(rows[i].columns[k].value === "")
					return false;
            }
		return true;
	}

    /**
     * Checks and changes the class of the current cell.
     */
	function changeClass(old_class, new_class) {
        if (old_class === "correct")
        	return old_class;
        else
        	return new_class;        
    }

    /**
     * Returns the max and min values of row/column based on coordinates.
     *
     *  e.g. rowId = 5, columnId = 4
     *  x x x | x x x | x x x
     *  x x x | x x x | x x x
     *  x x x | x x x | x x x
     *  - - -   - - -   - - -
     *  x x x | x x x | x x x
     *  x x x | x x x | x x x
     *  x x x | x 0 x | x x x
     *  - - -   - - -   - - -
     *  x x x | x x x | x x x
     *  x x x | x x x | x x x
     *  x x x | x x x | x x x
     *
     *  returns {(3, 6), (3, 6)}
     */
    function getCaseEdgesByCoords(rowId, columnId){

    	var _edges = function(id){
    		var rest = Math.floor(id/3);
    		return {min: rest * 3, max: rest * 3 + 3};
    	};

		return {
			row: _edges(rowId),
			column: _edges(columnId)
		};
    }


    function removePossibilities(possibilities, indices) {
        for (var i = 0; i < indices.length; i++){
                var j = (i == 0) ? indices[i] : indices[i]-i;
                possibilities.splice(j,1);
            }
    }

    /**
     * Returns the possibilities for a cell.
     */
    function getPossibilities(rows, rowId, columnId){
		var pos = [1,2,3,4,5,6,7,8,9];

        linePossibilities(rows, 'row', rowId, pos);
        linePossibilities(rows, 'column', columnId, pos);

        var caseEdges = getCaseEdgesByCoords(rowId, columnId);
        casePossibilities(rows, caseEdges, pos);

	    return pos;	    	       
    }

    /**
     * Returns possibilities of a cell, based on a vertical/horizontal line.
     */
    function linePossibilities(rows, direction, id, pos){
        pos = (typeof pos === 'undefined') ? [1,2,3,4,5,6,7,8,9] : pos;
    	var indices = [];

        for(var i = 0; i < pos.length; i++){
            for(var j = 0; j < 9; j++){
                var positionValue;

                if (direction === 'row')
                    positionValue = rows[id].columns[j].value;
                if (direction === 'column')
                    positionValue = rows[j].columns[id].value;

                if (pos[i] == positionValue){
                    indices.push(i);
                    j = 9;
                }
            }
        }

		removePossibilities(pos, indices);

		return pos;
    }

    /**
     * Returns the possibilities based on a case.
     */
    function casePossibilities(rows, edges, pos){
        pos = (typeof pos === 'undefined') ? [1,2,3,4,5,6,7,8,9] : pos;
    	var indices = [];
    	for(var p=0; p<9; p++) 	    
		    for(var j = edges.row.min; j < edges.row.max; j++)
	            for(var k = edges.column.min; k < edges.column.max; k++)
	            {
	                if (pos[p] == rows[j].columns[k].value){
						indices.push(p);
						j = 9;
						k=9;
					}
	            }	
		removePossibilities(pos, indices);
		return pos;
    }

	$scope.getValue = function(value, rowId, columnId) {
        rowId -= 1;
        columnId -= 1;
		if ($scope.rows[rowId].columns[columnId].class == "correct")

        if (!(value >= 1 && value <= 9)){
			return "";		
		}
		return value;		
	};


	$scope.check = function(rowId, columnId) {
		rowId = rowId - 1;
		columnId = columnId - 1;
		var value = $scope.rows[rowId].columns[columnId].value;

		if ($scope.rows[rowId].columns[columnId].class == "correct")
			return;
		
		if (!(!isNaN(parseFloat(value)) && isFinite(value))){
			$scope.rows[rowId].columns[columnId].class = changeClass($scope.rows[rowId].columns[columnId].class, "error");
			$scope.rows[rowId].columns[columnId].value = '';
			return 
		}
			
		$scope.rows[rowId].columns[columnId].class =  changeClass($scope.rows[rowId].columns[columnId].class,"valide");
		for(var j = 0; j < 9; j++)
	    {
	        if((value == $scope.rows[rowId].columns[j].value) && columnId != j){
	            $scope.rows[rowId].columns[columnId].class = changeClass($scope.rows[rowId].columns[columnId].class,"error");
	            $scope.rows[rowId].columns[j].class = changeClass($scope.rows[rowId].columns[j].class, "error");
	           }
	    }
	    for(var j = 0; j < 9; j++)
	    {
	    	if((value == $scope.rows[j].columns[columnId].value) && rowId != j){
	            $scope.rows[rowId].columns[columnId].class = changeClass($scope.rows[rowId].columns[columnId].class, "error");
	            $scope.rows[j].columns[columnId].class = changeClass("error", $scope.rows[j].columns[columnId].class);
	           }
	    }
	    var edges = getCaseEdgesByCoords(rowId, columnId);
	    		
        for(var j = edges.row.min; j < edges.row.max; j++)
            for(var k=edges.column.min; k<edges.column.max; k++)
            {
                if((value == $scope.rows[j].columns[k].value) && j != rowId && k != columnId){
					$scope.rows[rowId].columns[columnId].class = changeClass($scope.rows[rowId].columns[columnId].class,"error");
	            	$scope.rows[j].columns[k].class = changeClass($scope.rows[j].columns[k].class, "error");
	            }
            }	    	   		
   	};
    
    function reloadPossibilities(row_id, column_id){
        var pos = getPossibilities($scope.rows, row_id, column_id);
        $scope.rows[row_id].columns[column_id].possibilities = angular.copy(pos);
        $scope.currentPossibilities = angular.copy(pos);

    }

    function changeCurrentlyClickedColorByCoords(color, hardcodedColor, notHardcodedColor, hardcodedFontColor, notHardcodedFontColor){
        if(typeof $scope.currently_clicked === 'undefined') return
        var row_id = $scope.currently_clicked[0]
        var column_id = $scope.currently_clicked[1]
        for (var i = 0; i < 9; i++) {
            if(i != row_id) {
                document.getElementsByName("cell-"+(i+1) +"" + (column_id+1))[0].style.background=
                    $scope.rows[i].columns[column_id].class == "correct" ? hardcodedColor: notHardcodedColor;
                document.getElementsByName("cell-"+(i+1) +"" + (column_id+1))[0].style.color=
                    $scope.rows[i].columns[column_id].class == "correct" ? hardcodedFontColor: notHardcodedFontColor;
            }
            if(i != column_id){
                document.getElementsByName("cell-"+(row_id+1) +"" + (i+1))[0].style.background=
                    $scope.rows[row_id].columns[i].class == "correct" ? hardcodedColor: notHardcodedColor;  
                document.getElementsByName("cell-"+(row_id+1) +"" + (i+1))[0].style.color=
                    $scope.rows[row_id].columns[i].class == "correct" ? hardcodedFontColor: notHardcodedFontColor;                 
            }
        }
        var edges = getCaseEdgesByCoords(row_id, column_id);
                
        for(var j = edges.row.min; j < edges.row.max; j++)
            for(var k=edges.column.min; k<edges.column.max; k++)
            {
                if(j != row_id && k != column_id){
                    document.getElementsByName("cell-"+(j+1) +"" + (k+1))[0].style.background=
                        $scope.rows[j].columns[k].class == "correct" ? hardcodedColor: notHardcodedColor;  
                    document.getElementsByName("cell-"+(j+1) +"" + (k+1))[0].style.color=
                        $scope.rows[j].columns[k].class == "correct" ? hardcodedFontColor: notHardcodedFontColor; 
                }
            }    
        document.getElementsByName("cell-"+(row_id+1) +"" + (column_id+1))[0].style.background=color;
    }

	$scope.possibilities = function(row_id, column_id) {
		row_id = row_id - 1;
		column_id = column_id - 1;

        changeCurrentlyClickedColorByCoords("rgb(221, 221, 221)", "pink", "rgb(221, 221, 221)", "black", "rgb(0, 0, 0)")
        $scope.currently_clicked = [row_id, column_id];
        changeCurrentlyClickedColorByCoords("rgb(102, 153, 153)", "rgb(132, 125, 75)", "rgb(214, 194, 42)", "rgb(255, 255, 255)", "rgb(0, 0, 0)")

        reloadPossibilities(row_id, column_id);
	};
	
    $scope.onPossibilityClicked = function(possibility){
        var row_id = $scope.currently_clicked[0] 
        var column_id = $scope.currently_clicked[1] 

        $scope.rows[row_id].columns[column_id].value = possibility

        reloadPossibilities(row_id, column_id)

        if(isSolved($scope.rows))
            sendScoreAndReturnControl(1)

    }


    
    $scope.deleteNumberAtSelectedField = function(){
        if(typeof $scope.currently_clicked === 'undefined') return;

        var row_id = $scope.currently_clicked[0]
        var column_id = $scope.currently_clicked[1]
        $scope.rows[row_id].columns[column_id].value = ""
        reloadPossibilities(row_id, column_id)
    }

    $scope.disableIfNothingSelected = function(){
        var deleteNumberBtn = document.getElementsByName("deleteNumberBtn")[0];
        if ( typeof $scope.currently_clicked === 'undefined' ) {
            deleteNumberBtn.style.display = "none";
            return; 
        }
        var row_id = $scope.currently_clicked[0]
        var column_id = $scope.currently_clicked[1]
        deleteNumberBtn.style.display =  $scope.rows[row_id].columns[column_id].value == ""  ? "none" : "";
    }

    $scope.sendScore = function(){
        sendScoreAndReturnControl(0);
    }

    $scope.generate = function(){
        var board = generateGameBoard();
        printBoard(board)
        generateView(board)
    }

    function printBoard(sudoku){
        for(var i = 0; i < 9; i ++){
            var line = "";
            for (var j = 0; j < 9; j++){
                line += sudoku[i * 9 + j] + " ";
            }
            console.log(line);
        }
    }


    function getUniqueIndeces(N){
        var indices = [];
        for (var i = 0; i < 81; i++) {
            indices.push(i);
        }
        var sample = indices
          .map(x => ({ x, r: Math.random() }))
          .sort((a, b) => a.r - b.r)
          .map(a => a.x)
          .slice(0, N);
        return sample;
    }


    /**
     * Generates a new random grid respecting the age of player.
     */
    function generateView (sudoku) {    

        var rows = createEmptyRows();
        
        for (var l=0; l<9; l++){
            for(var c=0; c<9; c++){             
                rows[l].columns[c].class = "correct"; 
                rows[l].columns[c].value = sudoku[l*9+c];              
            }
        }

        var erasedNumber = ageToErasedNumber() //adapter method
        var uniqueIndeces = getUniqueIndeces(erasedNumber)
        for ( var k = 0; k < erasedNumber; k++ ){
            var i = Math.floor(uniqueIndeces[k] / 9);
            var j = uniqueIndeces[k]  % 9;
            rows[i].columns[j].class = "";
            rows[i].columns[j].value = "";
        } 

        $scope.rows = angular.copy(rows);
    };


    function generateGameBoard(){
        var sudoku = new Array(0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0);
        var saved = new Array();
        var savedSudoku = new Array();
        var i=0;
        var nextMove;
        var whatToTry;
        var attempt;
        while (!isSolvedSudoku(sudoku)) {
            i++;
            nextMove = scanSudokuForUnique(sudoku);
            if (nextMove == false) {
                nextMove = saved.pop();
                sudoku = savedSudoku.pop();
            }
            whatToTry = nextRandom(nextMove);
            attempt = determineRandomPossibleValue(nextMove,whatToTry);
            if (nextMove[whatToTry].length>1) {
                nextMove[whatToTry] = removeAttempt(nextMove[whatToTry],attempt);
                saved.push(nextMove.slice());
                savedSudoku.push(sudoku.slice());
            }
            sudoku[whatToTry] = attempt;
        }
        return sudoku
    }

    // given a sudoku cell, returns the row
    function returnRow(cell) {
        return Math.floor(cell / 9);
    }

    // given a sudoku cell, returns the column
    function returnCol(cell) {
        return cell % 9;
    }

    // given a sudoku cell, returns the 3x3 block
    function returnBlock(cell) {
        return Math.floor(returnRow(cell) / 3) * 3 + Math.floor(returnCol(cell) / 3);
    }

    // given a number, a row and a sudoku, returns true if the number can be placed in the row
    function isPossibleRow(number,row,sudoku) {
        for (var i=0; i<=8; i++) {
            if (sudoku[row*9+i] == number) {
                return false;
            }
        }
        return true;
    }

    // given a number, a column and a sudoku, returns true if the number can be placed in the column
    function isPossibleCol(number,col,sudoku) {
        for (var i=0; i<=8; i++) {
            if (sudoku[col+9*i] == number) {
                return false;
            }
        }
        return true;
    }

    // given a number, a 3x3 block and a sudoku, returns true if the number can be placed in the block
    function isPossibleBlock(number,block,sudoku) {
        for (var i=0; i<=8; i++) {
            if (sudoku[Math.floor(block/3)*27+i%3+9*Math.floor(i/3)+3*(block%3)] == number) {
                return false;
            }
        }
        return true;
    }

    // given a cell, a number and a sudoku, returns true if the number can be placed in the cell
    function isPossibleNumber(cell,number,sudoku) {
        var row = returnRow(cell);
        var col = returnCol(cell);
        var block = returnBlock(cell);
        return isPossibleRow(number,row,sudoku) && isPossibleCol(number,col,sudoku) && isPossibleBlock(number,block,sudoku);
    }

    // given a row and a sudoku, returns true if it's a legal row
    function isCorrectRow(row,sudoku) {
        var rightSequence = new Array(1,2,3,4,5,6,7,8,9);
        var rowTemp= new Array();
        for (var i=0; i<=8; i++) {
            rowTemp[i] = sudoku[row*9+i];
        }
        rowTemp.sort();
        return rowTemp.join() == rightSequence.join();
    }

    // given a column and a sudoku, returns true if it's a legal column
    function isCorrectCol(col,sudoku) {
        var rightSequence = new Array(1,2,3,4,5,6,7,8,9);
        var colTemp= new Array();
        for (var i=0; i<=8; i++) {
            colTemp[i] = sudoku[col+i*9];
        }
        colTemp.sort();
        return colTemp.join() == rightSequence.join();
    }

    // given a 3x3 block and a sudoku, returns true if it's a legal block 
    function isCorrectBlock(block,sudoku) {
        var rightSequence = new Array(1,2,3,4,5,6,7,8,9);
        var blockTemp= new Array();
        for (var i=0; i<=8; i++) {
            blockTemp[i] = sudoku[Math.floor(block/3)*27+i%3+9*Math.floor(i/3)+3*(block%3)];
        }
        blockTemp.sort();
        return blockTemp.join() == rightSequence.join();
    }

    // given a sudoku, returns true if the sudoku is solved
    function isSolvedSudoku(sudoku) {
        for (var i=0; i<=8; i++) {
            if (!isCorrectBlock(i,sudoku) || !isCorrectRow(i,sudoku) || !isCorrectCol(i,sudoku)) {
                return false;
            }
        }
        return true;
    }

    // given a cell and a sudoku, returns an array with all possible values we can write in the cell
    function determinePossibleValues(cell,sudoku) {
        var possible = new Array();
        for (var i=1; i<=9; i++) {
            if (isPossibleNumber(cell,i,sudoku)) {
                possible.unshift(i);
            }
        }
        return possible;
    }

    // given an array of possible values assignable to a cell, returns a random value picked from the array
    function determineRandomPossibleValue(possible,cell) {
        var randomPicked = Math.floor(Math.random() * possible[cell].length);
        return possible[cell][randomPicked];
    }

    // given a sudoku, returns a two dimension array with all possible values 
    function scanSudokuForUnique(sudoku) {
        var possible = new Array();
        for (var i=0; i<=80; i++) {
            if (sudoku[i] == 0) {
                possible[i] = new Array();
                possible[i] = determinePossibleValues(i,sudoku);
                if (possible[i].length==0) {
                    return false;
                }
            }
        }
        return possible;
    }

    // given an array and a number, removes the number from the array
    function removeAttempt(attemptArray,number) {
        var newArray = new Array();
        for (var i=0; i<attemptArray.length; i++) {
            if (attemptArray[i] != number) {
                newArray.unshift(attemptArray[i]);
            }
        }
        return newArray;
    }

    // given a two dimension array of possible values, returns the index of a cell where there are the less possible numbers to choose from
    function nextRandom(possible) {
        var max = 9;
        var minChoices = 0;
        for (var i=0; i<=80; i++) {
            if (possible[i]!=undefined) {
                if ((possible[i].length<=max) && (possible[i].length>0)) {
                    max = possible[i].length;
                    minChoices = i;
                }
            }
        }
        return minChoices;
    }


    $scope.generate();

}); 
