/* global notificationsActiveChannel, orgstructure */

(function () {
	notificationsActiveChannel.setReadAllNotifications(orgstructure.getCurrentEmployee());
})();
