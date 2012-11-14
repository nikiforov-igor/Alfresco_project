var nodeRef = args["nodeRef"];

var status = search.findNode(nodeRef);

if (status != null) {
	//Удаление всех ссылок на статус в действиях

	//Удаление статуса
	status.remove();
}
