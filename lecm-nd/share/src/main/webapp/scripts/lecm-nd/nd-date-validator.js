if (typeof LogicECM == "undefined" || !LogicECM) {
	var LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.ND = LogicECM.module.ND || {};

(function () {
	var loader = new YAHOO.util.YUILoader({
		require: ["errorTooltipStyle"],
		skin: {}
	});
	loader.addModule({
		name: 'errorTooltipStyle',
		type: 'css',
		fullpath: Alfresco.constants.URL_RESCONTEXT + '/css/lecm-nd/error-tooltip.css'
	});
	loader.insert();
})();

LogicECM.module.ND.dateIntervalValidation =
	function ND_dateIntervalValidation(field, args, event, form, silent, message) {
		var toolTip = document.getElementById('error-tooltip');
		if (!toolTip) {
			toolTip = document.createElement('span');
			toolTip.id = 'error-tooltip';
			field.parentNode.appendChild(toolTip);
			toolTip.innerHTML = 'Дата начала не может быть больше, чем дата окончания';

		}

		toolTip.style.visibility = 'hidden';

		var valid = true;

		var myID = field.id;

		var IDElements = myID.split("_");
		var myField = IDElements[IDElements.length - 1];
		IDElements.splice(-1, 1);
		var commonID = IDElements.join("_");

		var beginField, endField;

		if (myField.toString() == "begin-date") {
			beginField = field;
			endField = YAHOO.util.Dom.get(commonID + "_end-date");
		} else if (myField.toString() == "end-date") {
			endField = field;
			beginField = YAHOO.util.Dom.get(commonID + "_begin-date");
		} else {
			return false;
		}

		var beginValue = beginField.value;
		var endValue = endField.value;

		if (beginValue && endValue) {
			var endDate = new Date(endValue);
			var beginDate = new Date(beginValue);

			if (beginDate > endDate) {
				valid = false;
				toolTip.style.visibility = 'visible';
			}

		}
		return valid;
}
