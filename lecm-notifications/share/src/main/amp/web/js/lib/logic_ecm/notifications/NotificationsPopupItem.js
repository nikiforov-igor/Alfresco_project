/* global Alfresco */

/**
 * @module logic_ecm/notifications/NotificationsPopupItem
 * @extends module:alfresco/header/AlfMenuItem
 * @param declare
 * @param lang
 * @param domAttr
 * @param domClass
 * @param domConstruct
 * @param html
 * @param on
 * @param xhr
 * @param json
 * @param template
 * @param AlfMenuItem
 * @author LogicECM
 */
define(['dojo/_base/declare',
	'dojo/_base/lang',
	'dojo/dom-attr',
	'dojo/dom-class',
	'dojo/dom-construct',
	'dojo/html',
	'dojo/on',
	'dojo/request/xhr',
	'dojo/json',
	'dojo/text!./templates/NotificationsPopupItem.html',
	'alfresco/header/AlfMenuItem'
],
	function (declare, lang, domAttr, domClass, domConstruct, html, on, xhr, json, template, AlfMenuItem) {

		return declare([AlfMenuItem], {

			templateString: template,

			cssRequirements: [{cssFile: './css/AlfMenuItem.css'}],

			_toggleRead: function (isRead) {
				var idx;
				if (isRead) {
					this.params.notificationsPopup.readNotifications.push(this.params.item.nodeRef);
					domAttr.set(this.notificationNode, 'title', this.message('message.notification.item.read.title'));
					domClass.replace(this.notificationNode, 'read', 'unread');
				} else {
					idx = this.params.notificationsPopup.readNotifications.indexOf(this.params.item.nodeRef);
					if (idx > -1) {
						this.params.notificationsPopup.readNotifications.splice(idx, 1);
					}
					domAttr.set(this.notificationNode, 'title', this.message('message.notification.item.unread.title'));
					domClass.replace(this.notificationNode, 'unread', 'read');
				}
			},

			_markAsRead: function() {
				xhr.post(Alfresco.constants.PROXY_URI_RELATIVE + 'lecm/notifications/active-channel/api/toggle/read', {
					handleAs: 'json',
					headers: {'Content-Type': 'application/json'},
					data: json.stringify([{
						nodeRef: this.params.item.nodeRef
					}])
				}).then(lang.hitch(this, function(success) {
					this._toggleRead(success.resp[this.params.item.nodeRef] === 'true');
					this.params.notificationsPopup.loadNewNotificationsCount();
				}), lang.hitch(this, function(failure) {
					Alfresco.util.PopupManager.displayMessage({
						text: this.message('message.notification.item.set.read.failure')
					});
				}));
			},

			postCreate: function () {
				this.inherited(arguments);
				html.set(this.notificationDetailsNode, this.params.item.description);
				domConstruct.place(Alfresco.util.relativeTime(new Date(this.params.item.formingDate)), this.notificationNode, 'last');
				this._toggleRead(this.params.item.isRead === 'true');
				on(this.domNode, 'a:click', lang.hitch(this, this._onHrefClick));
			},

			//TODO: ctrl+click handler, open in new tab handler also should mark notification as read!
			_onHrefClick: function(evt) {
				var href = evt.target.href;
				var pathname = evt.target.pathname;
				var pageUrl = href.slice(href.indexOf(pathname), href.length);
				var url = pageUrl.slice(Alfresco.constants.URL_PAGECONTEXT.length, pageUrl.length);
				evt.preventDefault();
				this.alfPublish('ALF_NAVIGATE_TO_PAGE', {
					url: url,
					type: this.targetUrlType,
					target: this.targetUrlLocation
				});
			},

			_onClick: function (evt) {
				this._setSelected(false);
				this._markAsRead();
			}
		});
	}
);
