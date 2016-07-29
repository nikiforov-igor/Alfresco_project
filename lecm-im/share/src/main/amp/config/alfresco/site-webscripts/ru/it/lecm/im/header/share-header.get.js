<import resource="classpath:/alfresco/site-webscripts/org/alfresco/share/imports/share-header.lib.js">

(function(){

	var imWidgets = {
		id: "IM_MENU_ITEM",
        name: "logic_ecm/im/MessengerPopup",
        config: {
            id: "IM_MENU_ITEM",
            label: msg.get("lecm.im.lbl")
        }
	};
	
	var menuBar = widgetUtils.findObject(model.jsonModel, "id", "HEADER_APP_MENU_BAR");
	if (menuBar != null) {
		menuBar.config.widgets.push(imWidgets);
	}

})();