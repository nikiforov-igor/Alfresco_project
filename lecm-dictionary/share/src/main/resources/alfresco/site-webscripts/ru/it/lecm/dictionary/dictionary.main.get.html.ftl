<br/>
<div id="dictionary"></div>
<div id="dictionary_tree"></div>

<script type="text/javascript">//<![CDATA[

(function() {

	var  oCurrentTextNode = null;

	function onTriggerContextMenu(p_oEvent) {

		var oTarget = this.contextEventTarget;

		/*
			Get the TextNode instance that that triggered the
			 display of the ContextMenu instance.
		*/

		oCurrentTextNode = tree.getNodeByElement(oTarget);

		if (!oCurrentTextNode) {
			// Cancel the display of the ContextMenu instance.
			this.cancel();
		}

	}

	var doBeforeDialogShow = function dlA_onActionDetails_doBeforeDialogShow(p_form, p_dialog)
	{
		// Dialog title
		var fileSpan = '134124321: <span class="light">Edit Metatdata</span>';

		Alfresco.util.populateHTML(
				[ p_dialog.id + "-form-container_h", fileSpan]
		);

		// Edit metadata link button
		Alfresco.util.createYUIButton(p_dialog, "editMetadata", null,
				{
					type: "link",
					label: "653653"
					//href: $siteURL("edit-metadata?nodeRef=" + nodeRef)
				});
	};

	function init() {

		var Dictionary = new LogicECM.module.Dictionary("dictionary");
		Dictionary.init("dictionary");
		Dictionary.setMessages(${messages});
		Dictionary.draw();
	}

	//once the DOM has loaded, we can go ahead and set up our tree:
	YAHOO.util.Event.onDOMReady(init);

})();
//]]></script>
