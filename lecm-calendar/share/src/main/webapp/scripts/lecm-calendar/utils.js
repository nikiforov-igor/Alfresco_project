if (typeof LogicECM == "undefined" || !LogicECM) {
	var LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.WCalendar = LogicECM.module.WCalendar || {};
LogicECM.module.WCalendar.Utils = LogicECM.module.WCalendar.Utils || {};


// функция, возвращающая грид, имеющий тот же bubblingLabel, что и тулбар
LogicECM.module.WCalendar.Utils.findGridByName = function WCalendarToolbar_findGridByName(p_sName, bubblingLabel) {
	var components = [];
	var found = [];
	var bMatch, component;

	components = Alfresco.util.ComponentManager.list();

	for (var i = 0, j = components.length; i < j; i++) {
		component = components[i];
		bMatch = true;
		if (component['name'].search(p_sName) == -1) {
			bMatch = false;
		}
		if (bMatch) {
			found.push(component);
		}
	}
	if (bubblingLabel) {
		for (i = 0, j = found.length; i < j; i++) {
			component = found[i];
			if (typeof component == "object" && component.options.bubblingLabel) {
				if (component.options.bubblingLabel == bubblingLabel) {
					return component;
				}
			}
		}
	} else {
		return (typeof found[0] == "object" ? found[0] : null);
	}
	return null;
};