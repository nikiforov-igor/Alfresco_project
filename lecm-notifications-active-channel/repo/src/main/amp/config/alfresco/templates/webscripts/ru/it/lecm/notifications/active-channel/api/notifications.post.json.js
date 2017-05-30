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

	var notificationsList = notificationsActiveChannel.getNotifications(skipItemsCount, loadItemsCount, ignoreNotifications);

	var updated = [];
	for (var j = 0; j < notificationsList.length; j++) {
		var notification = notificationsList[j];
		var template = notification.properties["lecm-notf:from-template"] ? notification.properties["lecm-notf:from-template"] : "";
		var isEnable = template ? notifications.isTemplateEnableForCurrentEmployee(template) : true;
		updated.push({
			item: notification,
			isEnable: isEnable
		})
	}

	model.notifications = updated;

	model.next = true;
})();
