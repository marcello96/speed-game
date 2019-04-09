Sudoku.filter('column_modulo_3', function() {
        return function(input) {
        	return input%3==1 ? 'margin_left_60' : '';
        };
    }
);
    
Sudoku.filter('row_modulo_3', function() {
        return function(input) {
        	return (input%3==0 && input<9) ? 'margin_bottom_60' : '';
        };
    }
);
    
Sudoku.filter('check_number', function() {
        return function(input) {
        	if (input >= 1 && input <= 9)    		
        		return input;
        };
    }
);

Sudoku.filter('column_modulo_3_style', function(){
        return function(input) {
            return (input%3==1) ? 'padding-left:3px;' : '';
        };
    }
);

Sudoku.filter('row_modulo_3_style', function(){
        return function(input) {
            return (input%3==0 ) ? 'padding-bottom:3px;' : '';
        };
    }
);


Sudoku.filter('if_poss_gt0', function(){
        return function(input) {
            return (typeof input !== 'undefined' && input.length != 0 ) ? "Possibilities:" : '';
        };
    }
);

Sudoku.filter('if_poss_eq0', function(){
        return function(input) {
            return (typeof input === 'undefined' ||  input.length == 0 ) ? "" : 'display: none;';
        };
    }
);


Sudoku.filter('if_correct_disable', function(){
    return function(input){
        return input == "correct" ? "true" : "false"; 
    }
});


