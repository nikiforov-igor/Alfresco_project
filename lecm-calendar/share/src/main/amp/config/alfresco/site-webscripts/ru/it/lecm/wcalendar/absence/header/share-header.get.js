var absenceWidget = {
	name: "logic_ecm/absence/AbsenceInstantWidget",
	id: "LOGIC_ECM_ABSENCE_MENU_ITEM",
	config: {
		id: "LOGIC_ECM_ABSENCE_MENU_ITEM",
		label: "Отсутствие",
		iconImage: "/share/res/components/images/header/instant-absence_light.png"
	}
}

var menuBar =
    widgetUtils.findObject(model.jsonModel, "id", "HEADER_USER_MENU");
if (menuBar != null) {
    menuBar.config.widgets.push(absenceWidget);
}
