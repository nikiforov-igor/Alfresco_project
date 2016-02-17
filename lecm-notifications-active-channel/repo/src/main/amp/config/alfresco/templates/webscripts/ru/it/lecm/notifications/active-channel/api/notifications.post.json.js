(function () {
	var skipItemsCount = json.has('skipItemsCount') ? json.getInt('skipItemsCount') : 0;
	var loadItemsCount = json.has('loadItemsCount') ? json.getInt('loadItemsCount') : 0;
	var jsonIgnoreNotifications = json.has('ignoreNotifications') ? json.get('ignoreNotifications') : null;
	var ignoreNotifications = [];
	if (jsonIgnoreNotifications) {
		for (var i = 0; i < jsonIgnoreNotifications.length(); i++) {
			ignoreNotifications.push(jsonIgnoreNotifications.get(i));
		}
	}

	model.notifications = notificationsActiveChannel.getNotifications(skipItemsCount, loadItemsCount, ignoreNotifications);
	model.next = true;
})();
