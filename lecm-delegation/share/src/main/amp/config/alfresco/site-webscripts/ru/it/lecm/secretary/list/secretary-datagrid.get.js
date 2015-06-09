(function(){
	model.nodeRef = args["itemId"];
	model.itemType = args["itemType"];
	model.filter = args["filter"];
	model.id = model.nodeRef.replace(/:|\//g, '_');
})();