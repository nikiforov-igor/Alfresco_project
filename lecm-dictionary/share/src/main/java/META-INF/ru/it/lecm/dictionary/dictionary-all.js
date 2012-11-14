(function () {

	function init() {
	YAHOO.Bubbling.fire("activeGridChanged",
		{
			datagridMeta:{
				description:"",
				itemType:"lecm-dic:dictionary"
			},
			scrollTo:true
		});
	}

	YAHOO.util.Event.onDOMReady(init);
})();
