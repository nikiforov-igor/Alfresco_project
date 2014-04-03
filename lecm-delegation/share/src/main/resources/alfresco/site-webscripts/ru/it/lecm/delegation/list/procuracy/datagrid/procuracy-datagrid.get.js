(function(){
	model.nodeRef = args["nodeRef"];
	model.itemType = args["itemType"];
	model.filter = args["filter"];
	model.id = model.nodeRef.replace(/:|\//g, '_');
})();
