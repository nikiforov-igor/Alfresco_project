var skipItemsCount = parseInt(args["skipItemsCount"]);
var loadItemsCount = parseInt(args["loadItemsCount"]);
model.notifications = notificationsActiveChannel.getNotifications(skipItemsCount, loadItemsCount);
model.next = notificationsActiveChannel.getNotifications(skipItemsCount + loadItemsCount, 1);