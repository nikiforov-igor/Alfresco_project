var nodeRef = args['nodeRef'];
var type = args['type'];
var addSubordinatesTask = args['addSubordinatesTask'];

var myTasksLimit = args['myTasksLimit'];
if (myTasksLimit == null) {
    myTasksLimit = 0;
}

var ctx = Packages.org.springframework.web.context.ContextLoader.getCurrentWebApplicationContext();
var stateMachineHelper = ctx.getBean("stateMachineHelper");

model.data = stateMachineHelper.getTasks(nodeRef, type, addSubordinatesTask, myTasksLimit);