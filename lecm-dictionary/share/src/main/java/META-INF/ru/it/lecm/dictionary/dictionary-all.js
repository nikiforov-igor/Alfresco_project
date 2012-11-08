(function () {

	function init() {
	YAHOO.Bubbling.fire("activeGridChanged",
		{
			datagridMeta:{
				description:"",
				itemType:"lecm-dic:dictionary",
				title:""
			},
			scrollTo:true
		});
	}

	YAHOO.util.Event.onDOMReady(init);
})();
