if (typeof LogicECM == "undefined" || !LogicECM) {
	LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.ND = LogicECM.module.ND || {};

(function () {
    LogicECM.module.Base.Util.loadCSS(['/css/lecm-nd/error-tooltip.css'], null, ['container']);
})();

var YUITooltip = null;

LogicECM.module.ND.dateIntervalValidation =
	function ND_dateIntervalValidation(field, args, event, form, silent, message) {

		var valid = true;

		var myID = field.id;

		var IDElements = myID.split("_");
		var myField = IDElements[IDElements.length - 1];
		IDElements.splice(-1, 1);
		var commonID = IDElements.join("_");

		visibleBeginField = YAHOO.util.Dom.get(commonID + "_begin-date-cntrl-date");
		visibleEndField = YAHOO.util.Dom.get(commonID + "_end-date-cntrl-date");

		var beginField, endField, visibleBeginField, visibleEndField;

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
				visibleBeginField.style.background = 'bisque';
				visibleEndField.style.background = 'bisque';
				YUITooltip = new YAHOO.widget.Tooltip("error-tooltip",
					{
						context:[visibleBeginField, visibleEndField],
						text:'Дата начала не может быть больше, чем дата окончания' 
					});
			} else {
				visibleBeginField.style.background = 'white';
				visibleEndField.style.background = 'white';
				if(YUITooltip != null) {
					YUITooltip.destroy();
					YUITooltip = null;
				}
			}
		}
		return valid;
}
