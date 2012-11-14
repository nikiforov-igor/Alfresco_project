var nodeRef = args["nodeRef"];

var action = search.findNode(nodeRef);

if (action != null) {
	//Удаление действия
	action.remove();
}
