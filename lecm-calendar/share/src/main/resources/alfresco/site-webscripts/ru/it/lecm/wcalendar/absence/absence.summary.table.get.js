var monthNames = { m0 : "Январь", m1 : "Февраль", m2 :  "Март", m3 : "Апрель", m4 : "Май", m5 : "Июнь",
m6 : "Июль", m7 : "Август", m8 : "Сентябрь", m9 : "Октябрь", m10 : "Ноябрь", m11 : "Декабрь" };

var daysAmount = 30;

var absenceContainer = remote.connect("alfresco").get("/lecm/wcalendar/absence/get/container");
var absenceContainerObj = jsonUtils.toObject(absenceContainer);

var reqBody = {
	params: {
		parent: absenceContainerObj.nodeRef ,
		itemType: absenceContainerObj.itemType,
		searchConfig: "",
		maxResults: 9999,
		fields: "lecm-absence_abscent-employee-assoc,lecm-absence_begin,lecm-absence_end,lecm-absence_unlimited,lecm-absence_abscence-reason-assoc",
		nameSubstituteStrings: ",,,,",
		showInactive: false
	}
};

var dataRaw = remote.connect("alfresco").post("/lecm/wcalendar/absence/get/list/admin", jsonUtils.toJSONString(reqBody), "application/json");

data = eval("(" + dataRaw + ")");

if (data != null && data.totalRecords > 0) {
	var employeesAbsences = {};
	var reasons = {};
	for (i = 0; i < data.items.length; i++) {
		itemData = data.items[i].itemData;
		if (!employeesAbsences[itemData["assoc_lecm-absence_abscent-employee-assoc"].displayValue]) {
			employeesAbsences[itemData["assoc_lecm-absence_abscent-employee-assoc"].displayValue] = [];
		}
		employeesAbsences[itemData["assoc_lecm-absence_abscent-employee-assoc"].displayValue].push({
			begin: DateFromISO8601(itemData["prop_lecm-absence_begin"].value),
			end: DateFromISO8601(itemData["prop_lecm-absence_end"].value),
			reason: itemData["assoc_lecm-absence_abscence-reason-assoc"].displayValue
		});
		reasons[itemData["assoc_lecm-absence_abscence-reason-assoc"].displayValue] = {
			nodeRef: itemData["assoc_lecm-absence_abscence-reason-assoc"].value
		};
	}

	for (key in reasons) {
		var req =  {
			nodeRef: reasons[key].nodeRef
		};
		var responseRaw = remote.connect("alfresco").post("/lecm/wcalendar/absence/get/absenceReasonColor", jsonUtils.toJSONString(req), "application/json");
		var response = eval("(" + responseRaw + ")");
		reasons[key].color = response.color;
	}
	
	
	//model.rawData = "rawData = " + dataRaw.response;
	

	var today = new Date();
	var calendarHeader = {};

	for (i = 0; i < daysAmount; i++) {
		var month = today.getMonth();
		if (!calendarHeader["m"+month.toString()]) {
			calendarHeader["m"+month.toString()] = [];
		}
		calendarHeader["m"+month.toString()].push(today.getDate());
		today.setDate(today.getDate() + 1);
	}
	
	var result = {};

	for (employee in employeesAbsences) {
		if (!result[employee]) {
			result[employee] = []
		}
		today = new Date();
		today.setHours(12, 0, 0, 0);
		for (i = 0; i < daysAmount; i++) {
			var currentDay = new Date(today)
			var color = "";
			for (var j = 0; j < employeesAbsences[employee].length; j++) {
				if (currentDay > employeesAbsences[employee][j].begin && currentDay < employeesAbsences[employee][j].end ) {
					color = reasons[employeesAbsences[employee][j].reason].color;
					break;
				}
			}
			result[employee].push(color);
			today.setDate(today.getDate() + 1);
		}
	}

	model.debug = "<br><br>data = " + jsonUtils.toJSONString(data) + "<br><br>result = " + jsonUtils.toJSONString(result) + "<br><br>reasons = " + jsonUtils.toJSONString(reasons) + "<br><br>monthNames = " + jsonUtils.toJSONString(monthNames) + "<br><br>calendarHeader = " + jsonUtils.toJSONString(calendarHeader);

	today = new Date();
	model.curMonthConst = today.getMonth();
	model.monthNames = monthNames;
	model.calendarHeader = calendarHeader;
	model.reasons = reasons;
	model.result = result;

}

function DateFromISO8601() {
	var fromISOString = function()
	{
  
		var isoRegExp = /^(?:(\d{4})(?:-(\d{2})(?:-(\d{2}))?)?)?(?:T(\d{2}):(\d{2})(?::(\d{2})(.\d+)?)?((?:[+-](\d{2}):(\d{2}))|Z)?)?$/;

		return function(formattedString)
		{
			var match = isoRegExp.exec(formattedString);
			var result = null;

			if (match)
			{
				match.shift();
				if (match[1]){
					match[1]--;
				} // Javascript Date months are 0-based
				if (match[6]){
					match[6] *= 1000;
				} // Javascript Date expects fractional seconds as milliseconds

				result = new Date(match[0]||1970, match[1]||0, match[2]||1, match[3]||0, match[4]||0, match[5]||0, match[6]||0);

				var offset = 0;
				var zoneSign = match[7] && match[7].charAt(0);
				if (zoneSign != 'Z')
				{
					offset = ((match[8] || 0) * 60) + (Number(match[9]) || 0);
					if (zoneSign != '-')
					{
						offset *= -1;
					}
				}
				if (zoneSign)
				{
					offset -= result.getTimezoneOffset();
				}
				if (offset)
				{
					result.setTime(result.getTime() + offset * 60000);
				}
			}

			return result; // Date or null
		};
	}();
   
	return fromISOString.apply(arguments.callee, arguments);
}