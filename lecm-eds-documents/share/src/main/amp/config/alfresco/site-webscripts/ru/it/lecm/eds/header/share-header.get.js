var armWidget = {
	id: "SED_MENU_ITEM",
	name: "alfresco/menus/AlfMenuItem",
	config: {
		id: "SED_MENU_ITEM",
		label: msg.get("label.SED.menu.item"),
		targetUrl: "arm?code=SED"
	}
}

var menuBar =
    widgetUtils.findObject(model.jsonModel, "id", "HEADER_MAIN_MENU");
if (menuBar != null) {
    menuBar.config.widgets.splice(1, 0, armWidget);
}
