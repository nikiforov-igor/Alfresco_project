<import resource="classpath:/alfresco/site-webscripts/org/alfresco/share/imports/share-header.lib.js">
<import resource="classpath:/alfresco/site-webscripts/ru/it/lecm/documents/utils/permission-utils.js">

(function(){

	//if (hasRole("DA_ARCHIVISTS")) {
		var operativeStorageWidgets = {
			id: "NOMENCLATURE_MENU_ITEM",
			name: "alfresco/header/AlfMenuItem",
			config: {
				id: "NOMENCLATURE_MENU_ITEM",
				label: "АРМ Архивариуса",
				targetUrl: "arm?code=archive"
			}
		};
	//}

	var menuBar = widgetUtils.findObject(model.jsonModel, "id", "LOGIC_ECM_WIDGETS");
	if (menuBar != null) {
		menuBar.config.widgets.push(operativeStorageWidgets);
	}

})();