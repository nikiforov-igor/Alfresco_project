(function(){
	var approvalListNodeRef = args["nodeRef"],
		approvaItemType = args["approvaItemType"];
	model.approvalListNodeRef = approvalListNodeRef;
	model.approvaItemType = approvaItemType;
	model.id = approvalListNodeRef.replace(/:|\//g, '_');
})();
