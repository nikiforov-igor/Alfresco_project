var skipItemsCount = parseInt(json.get("skipItemsCount"));
var loadItemsCount = parseInt(json.get("loadItemsCount"));
var jsonIgnoreNotifications = json.get("ignoreNotifications");
var ignoreNotifications = [];
if (jsonIgnoreNotifications != null) {
	for (var i = 0; i < jsonIgnoreNotifications.length(); i++) {
		ignoreNotifications.push(jsonIgnoreNotifications.get(i));
	}
}

model.notifications = notificationsActiveChannel.getNotifications(skipItemsCount, loadItemsCount, ignoreNotifications);
model.next = true;