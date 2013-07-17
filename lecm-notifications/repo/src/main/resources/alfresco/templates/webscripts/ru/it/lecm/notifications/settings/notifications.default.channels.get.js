var defaultChannels = notifications.getCurrentUserDefaultNotificationTypes();
if (defaultChannels != null) {
	model.channels = defaultChannels;
}