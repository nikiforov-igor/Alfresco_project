(function(){
	var approvalListNodeRef = args["nodeRef"],
		approvalItemType = args["approvalItemType"];
	model.approvalListNodeRef = approvalListNodeRef;
	model.approvalItemType = approvalItemType;
	model.id = approvalListNodeRef.replace(/:|\//g, '_');
})();
