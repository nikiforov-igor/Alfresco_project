var lecmProfile = {
	    id: "LECM_PROFILE_MENU_ITEM",
	    name: "alfresco/header/AlfMenuItem",
	    config: {
	        id: "LECM_PROFILE_MENU_ITEM",
	        label: msg.get("label.ECM.my_profile"),
	        targetUrl: "my-profile",
	        iconImage: url.context + "/res/components/images/header/user-profile_light.png"

	    }
};

var menuBar =
    widgetUtils.findObject(model.jsonModel, "id", "HEADER_USER_MENU");
if (menuBar != null) {
    menuBar.config.widgets.splice(1, 0, lecmProfile);
}
