var result = [];
for (var i = 0; i < json.length(); i++) {
	var q = json.optJSONObject(i);
	var sheduleTime = shedule.getParentSheduleStdTime(q);
	if (sheduleTime == null) {
		sheduleTime = {};
	}
	result.push(sheduleTime);
}

model.data = result.toString();
