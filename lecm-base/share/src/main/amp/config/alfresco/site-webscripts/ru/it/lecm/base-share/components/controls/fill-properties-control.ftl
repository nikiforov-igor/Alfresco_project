<script type="text/javascript">//<![CDATA[
(function(){
    var nodeRef;

	function init() {
		LogicECM.module.Base.Util.loadScripts([
			'scripts/lecm-base/components/lecm-fillproperties.js'
		], createControl);
	}

	function createControl() {
		var control = new LogicECM.module.FillProperties("${fieldHtmlId}").setOptions({
			fieldName: "${field.name}",
			properties: "${field.control.params.properties}",
			dateFormat: "${msg("form.control.date-picker.entry.date.format")}"
		}).setMessages(${messages});
        control.onAfterSetItems(nodeRef);
	}

    function loadControl(layer, args) {
        nodeRef = args[1].items;
        init();
    }

    YAHOO.Bubbling.on("afterSetItems", loadControl);

})();

//]]></script>
