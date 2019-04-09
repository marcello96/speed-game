var presenter = {};
var performAction = function(info, id) {
	var dictionary = {
		"action301": presenter.onNodeSelected,
		"action302": presenter.onEdgeVisited,
		"action303": presenter.onLevelComplete,
		"action304": presenter.onGameFinished,

		"error401": presenter.onStartNodeAlreadySelected,
		"error402": presenter.onStartNodeNotSelected,
		"error403": presenter.onEdgeRevisit,
		"error404": presenter.onNonAdjacentVisit,
		"error405": presenter.onNoPossibleMoves
	};
	if (info.statusCode < 400)
		dictionary["action" + info.statusCode](id, info);
	else dictionary["error" + info.statusCode](id, info);
};


var onShapeReady = function(shape) {
	var container = $('#container');
	var svgHTML = '<svg width="800" height="1000">';
	var visitor = {};
	visitor.renderEdge = function(edge) {
		var lineHTML = '<line id="ID" x1="X1" y1="Y1" x2="X2" y2="Y2" />';
		lineHTML = lineHTML.replace('ID', edge.id).replace('X1', edge.startNode.x).replace('Y1', edge.startNode.y).replace('X2', edge.endNode.x).replace('Y2', edge.endNode.y);
		svgHTML += lineHTML;
	};
	visitor.renderNode = function(node) {
		var circleHTML = '<circle id="ID" cx="CX" cy="CY" r="40" stroke-width="3" fill="#0066CC"/>';
		circleHTML = circleHTML.replace('ID', node.id).replace('CX', node.x).replace('CY', node.y);
		svgHTML += circleHTML;
	};
	visitor.showLevel = function(noOfLevels,currentLevel) {
		var progressBar = $('#progress');
		var spanHeight = Math.round(progressBar.height() / noOfLevels);
		var progressHTML = '';
		for (var i = noOfLevels; i >= 1; i--) {
			if(i < currentLevel)
				progressHTML += '<span class="complete">' + i + '</span>';
			else
				progressHTML += '<span>' + i + '</span>';
		};
		progressBar.html(progressHTML);
		$('#progress span').css({'height':spanHeight-2+'px'});
	};
	presenter.game.visit(visitor);
	svgHTML += '</svg>';
	container.html(svgHTML);
};

presenter.onEdgeVisited = function(edgeID, info) {
	$('#' + edgeID).attr('class', 'visited');
	presenter.onNodeSelected(info.nodeId);
};

presenter.onNodeSelected = function(nodeID) {
	var circles = $('circle');
	circles.attr('fill', '#0066CC');
	$('#' + nodeID).attr('fill', '#CC0000');
};

function sendResult(score) {
	console.log("send result " + score);
	var xhr = new XMLHttpRequest();
	xhr.open('POST', '/euler-path/end', true);
	xhr.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
	//send result to server
	xhr.send(JSON.stringify({result: score, group: config.group, nick: config.nick, age: config.age}));
	xhr.onreadystatechange = function() {
	if (xhr.readyState == XMLHttpRequest.DONE) {
		window.location.replace(xhr.responseText);
	}
	}
}

presenter.onLevelComplete = function(edgeID, info) {
	presenter.onEdgeVisited(edgeID, info);
	sendResult(1);
	// setTimeout(function() {
	// 	$('#error').hide();
	// 	$('#container').hide();
	// 	$('#finish').hide();
	// 	$('#level').show();
	// 	$('#instruction').hide();
	// 	$('.resetLevel').hide();
	// }, 500);
};

var showErrorMessage = function(message) {
	var error = $('#error');
	var container = $('#container');
	error.find('h2').text(message);
	$('circle').prop('disabled', true);
	$('line').prop('disabled', true);
	$('.resetLevel').prop('disabled', true);
	error.animate({
		height: "toggle",
		display: 'inline-block'
	}, 1000, function() {});
	error.css({
		display: 'inline-block'
	});

	container.css({
		opacity: 0.5
	});

	setTimeout(function() {
		error.animate({
			height: "toggle"
		}, 1000, function() {
			container.css({
				opacity: 1.0
			});
		});
		$('circle').prop('disabled', false);
		$('line').prop('disabled', false);
		$('.resetLevel').prop('disabled', false);
	}, 3000);
	// Animation complete.
};


presenter.onStartNodeAlreadySelected = function(nodeID) {
	// showErrorMessage('You have to select a edge to continue the game.');
};

presenter.onNonAdjacentVisit = function() {
	// showErrorMessage('You can only select the edge which is adjacent to current node.');
};

presenter.onStartNodeNotSelected = function() {
	// showErrorMessage('You need select a node to start the game.');
};

presenter.onEdgeRevisit = function() {
	// showErrorMessage('This edge has been already visited.');
};

presenter.onNoPossibleMoves = function(edgeID, info) {
	presenter.onEdgeVisited(edgeID, info);
	// setTimeout(function() {
	// 	$('#movesFinished').show();
	// 	$('circle').prop('disabled', true);
	// 	$('line').prop('disabled', true);
	// }, 500);
};

presenter.onGameFinished = function() {
	sendResult(1);
	// $('#container').hide();
	// $('#error').hide();
	// $('#level').hide();
	// $('.resetLevel').hide();
	// $('#finish').show();
	// $('#progress').hide();
};

var init = function() {
	presenter.game = new Game();
	onShapeReady();
	// $('#container').on('click', 'line', function() {
	// 	var edgeID = $(this).attr('id');
	// 	var info = presenter.game.visitEdge(edgeID);
	// 	performAction(info, edgeID);
	// });
	$('#container').on('click', 'circle', function() {
		var nodeID = $(this).attr('id');
		var info = presenter.game.selectNode(nodeID);
		performAction(info, nodeID);
		if(presenter.game.currentNode){
			for(i = 0; i < presenter.game.currentNode.edges.length; i++){
				var edge = presenter.game.currentNode.edges[i];
				if(!edge.visited){
					if(edge.endNode.id == nodeID && edge.startNode.id == presenter.game.currentNode.id){
						console.log(edge.id);
						var info = presenter.game.visitEdge(edge.id);
						performAction(info, edge.id);
					}
					if(edge.endNode.id == presenter.game.currentNode.id && edge.startNode.id == nodeID){
						console.log(edge.id);
						var info = presenter.game.visitEdge(edge.id);
						performAction(info, edge.id);
					}
				}
			}
		}
	});
	$('.resetLevel').click(function() {
		presenter.game.restartLevel();
		onShapeReady();
		$('#movesFinished').hide();
	});
	// $('#changeLevel').click(function() {
	// 	presenter.game.swicthToNextLevel();
	// 	onShapeReady();
	// 	$('#error').hide();
	// 	$('#level').hide();
	// 	$('#finish').hide();
	// 	$('#container').show();
	// 	$('.resetLevel').show();
	// });
};

$(window).load(init);