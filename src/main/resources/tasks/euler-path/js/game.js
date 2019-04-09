var Node = function(x, y, id) {
	this.x = x;
	this.y = y;
	this.id = 'node' + id;
	this.edges = [];
	this.isEqualTo = function(node) {
		return this.x === node.x && this.y === node.y;
	};
	this.addEdge = function(edge) {
		this.edges.push(edge);
	};
	this.isEdgeAdjacent = function(edge) {
		return this.edges.some(function(edge1) {
			return edge.id === edge1.id;
		});
	}
	this.areAllEdgesVisited = function(){
		return this.edges.every(function(edge){
			return edge.visited;
		});
	}
};

var Edge = function(startNode, endNode, id) {
	this.startNode = startNode;
	this.endNode = endNode;
	this.id = 'edge' + id;
	this.visited = false;
	this.isEqualTo = function(edge) {
		return this.startNode.isEqualTo(edge.startNode) && this.endNode.isEqualTo(edge.endNode);
	};
	this.getOtherNode = function(node) {
		if (node.isEqualTo(this.startNode))
			return this.endNode;
		return this.startNode;
	}
};

var createShape = function(game) {
	var shapeData = game.shapesData[game.level - 1];
	game.noOfEdgeVisited = 0;
	game.nodes = shapeData.nodesData.map(function(nodeData, index) {
		return new Node(nodeData[0], nodeData[1], index);
	});
	game.edges = shapeData.edgesData.map(function(edgeData, index) {
		var start = game.nodes.filter(function(node) {
			return node.isEqualTo({
				x: edgeData[0],
				y: edgeData[1]
			});
		})[0];
		var end = game.nodes.filter(function(node) {
			return node.isEqualTo({
				x: edgeData[2],
				y: edgeData[3]
			});
		})[0];
		var edge = new Edge(start, end, index);
		start.addEdge(edge);
		end.addEdge(edge);
		return edge;
	});
};

function distance(obj1, obj2){
	return Math.sqrt(Math.pow(obj1[0] - obj2[0], 2) + Math.pow(obj1[1] - obj2[1], 2));
}

function genGraph(){
	var numberOfNodes = getNumberOfNodes();
	var nodes = genNodes(numberOfNodes);
	var edges = [];
	var currentNode = Math.floor(Math.random() * nodes.length);
	var nodesNotUsed = [];
	for(var i = 0; i < nodes.length; i++){
		if(currentNode != i){
			nodesNotUsed.push(i);
		}
	}

	for(var i = 0; i < getNumberOfEdges(numberOfNodes); i++){
		var newNode = Math.floor(Math.random() * nodes.length);
		if(currentNode != newNode){
			var add = true;
			for(var j = 0; j < edges.length; j++){
				if((edges[j][0] == currentNode && edges[j][1] == newNode) || (edges[j][1] == currentNode && edges[j][0] == newNode)){
					add = false;
					i--;
					break;
				}
			}
			if(!add){
				continue;
			}
			edges.push([currentNode, newNode]);
			currentNode = newNode;
			if(nodesNotUsed.indexOf(newNode) > -1){
				nodesNotUsed.splice(nodesNotUsed.indexOf(newNode), 1);
			}
		}
	}

	for(var i = 0; i < nodesNotUsed.length; i++){
		edges.push([currentNode, nodesNotUsed[i]]);
		currentNode = nodesNotUsed[i];
	}

	console.log("not used");
	console.log(nodesNotUsed);
	console.log(edges);
	var graph = {"nodesData": nodes, "edgesData": []};
	for(var i = 0; i < edges.length; i++){
		graph.edgesData.push([nodes[edges[i][0]][0], nodes[edges[i][0]][1], nodes[edges[i][1]][0], nodes[edges[i][1]][1]]);
	}
	return [graph];
}

function getNumberOfNodes(){
	var age = config.age;
	if(age < 7){
		return random(3, 5);
	}
	if(age < 11){
		return random(4, 6);
	}
	if(age < 15){
		return random(5, 7);
	}
	if(age < 19){
		return random(6, 8);
	}
	return random(7, 9);
}

function getNumberOfEdges(numberOfNodes){
	var age = config.age;
	if(age < 7){
		return Math.floor(numberOfNodes * 1.5);
	}
	if(age < 11){
		return Math.floor(numberOfNodes * 1.75);
	}
	if(age < 15){
		return Math.floor(numberOfNodes * 2);
	}
	if(age < 19){
		return Math.floor(numberOfNodes * 2.25);
	}
	return Math.floor(numberOfNodes * 2.5);
}

function random(a, b){
	return Math.floor(Math.random() * (b - a + 1) + a);
}

function genNodes(numberOfNodes){
	var width = 800;
	var height = 1000;
	var radius = 40;
	var nodes = [];
	for(var i = 0; i < numberOfNodes; i++){
		var nodeAdded = false;
		while(!nodeAdded){
			var newNode = [Math.floor(Math.random() * (width - 2*radius) + radius), Math.floor(Math.random() * (height - 2*radius) + radius)];
			nodeAdded = true;
			for(var j = 0; j < nodes.length; j++){
				if(distance(newNode, nodes[j]) < 6 * radius){
					nodeAdded = false;
					break;
				}
			}
		}
		nodes.push(newNode);
	}
	return nodes;
}

var Game = function(shapesData) {
	// var shapesData = [];
	console.log(config);
	this.level = 1;
	if(config.age < 6){
		shape = x[0];
	} else if (config.age < 11){
		shape = x[1];
	} else if (config.age < 16){
		shape = x[2];
	} else if (config.age < 21){
		shape = x[3];
	} else {
		shape = x[4];
	}
	
	// shape = [{"nodesData": genNodes(), "edgesData": []}]
	shape = genGraph();
	console.log(shape);

	this.shapesData = shapesData || shape; // || [shapeData1, shapeData2, shapeData3];
	this.shapesData = [this.shapesData[Math.floor(Math.random() * this.shapesData.length)]]
	this.noOfLevels = this.shapesData.length;
	createShape(this);
};

Game.prototype.getEdgeById = function(edgeID) {
	return this.edges.filter(function(edge) {
		return edge.id === edgeID;
	})[0];
};

Game.prototype.isLevelComplete = function() {
	return this.noOfEdgeVisited === this.edges.length;
};

Game.prototype.getNodeById = function(nodeIdToFind) {
	return this.nodes.filter(function(node) {
		return node.id === nodeIdToFind;
	})[0];
}

Game.prototype.visit = function(visitor) {
	this.edges.forEach(visitor.renderEdge);
	this.nodes.forEach(visitor.renderNode);
	visitor.showLevel(this.noOfLevels,this.level);
};

Game.prototype.selectNode = function(nodeId) {
	if (!this.currentNode) {
		this.currentNode = this.getNodeById(nodeId);
		return {
			statusCode: 301
		};
	}
	// Select edge
	
	return {
		statusCode: 401
	};
};

Game.prototype.visitEdge = function(edgeID) {
	var edge = this.getEdgeById(edgeID);
	if (!this.currentNode) {
		return {
			statusCode: 402 
		}; //current node is not selected;
	}
	if (edge.visited) {
		return {
			statusCode: 403
		}; // edge is being revisited
	}
	if (!this.currentNode.isEdgeAdjacent(edge)) {
		return {
			statusCode: 404
		}; // non-adjacent node visit
	}
	edge.visited = true;
	this.noOfEdgeVisited++;
	this.currentNode = edge.getOtherNode(this.currentNode);

	if (this.isLevelComplete()) {
		if (this.level === this.noOfLevels) {
			return {
				statusCode: 304
			}; //game finished
		}
		return {
			statusCode: 303,
			nodeId: this.currentNode.id
		}; //level finished
	}

	if(this.currentNode.areAllEdgesVisited()){
		return {
			statusCode : 405,
			nodeId:this.currentNode.id
		}; //no possible moves
	}

	return {
		statusCode: 302,
		nodeId: this.currentNode.id
	};
};

Game.prototype.restartLevel = function() {
	createShape(this);
	this.currentNode = undefined;
};

Game.prototype.swicthToNextLevel = function() {
	this.level++;
	this.currentNode = undefined;
	createShape(this);
};