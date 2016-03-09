var res = notificationsActiveChannel.toggleReadNotifications(json);
model.resp = {};
for (var i = 0; i < json.length(); ++i) {
	var obj = json.get(i);
	var nodeRef = obj.get('nodeRef');
	model.resp[nodeRef] = res[i];
}
