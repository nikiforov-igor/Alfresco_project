var nodeRef = args['nodeRef'];
var state = args['state'];
var addSubordinatesTask = args['addSubordinatesTask'];
var addMyTasks = args['addMyTasks'];

var myTasksLimit = args['myTasksLimit'];
if (myTasksLimit == null) {
    myTasksLimit = 0;
}

var node = search.findNode(nodeRef);

model.data = statemachine.getTasks(node, state, addSubordinatesTask == "true", addMyTasks != "false", myTasksLimit);
