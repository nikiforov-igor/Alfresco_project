/* global Alfresco */

/**
 * @module logic_ecm/notifications/NotificationsGroup
 * @extends module:alfresco/menus/AlfMenuGroup
 * @param declare
 * @param lang
 * @param array
 * @param event
 * @param string
 * @param domAttr
 * @param domClass
 * @param on
 * @param xhr
 * @param json
 * @param AlfDropDownMenu
 * @param AlfMenuGroup
 * @param template
 * @author LogicECM
 */
define(['dojo/_base/declare',
		'dojo/_base/lang',
		'dojo/_base/array',
		'dojo/_base/event',
		'dojo/string',
		'dojo/dom-attr',
		'dojo/dom-class',
		'dojo/on',
		'dojo/request/xhr',
		'dojo/json',
		'alfresco/menus/AlfDropDownMenu',
		'alfresco/menus/AlfMenuGroup',
		'dojo/text!./templates/NotificationsGroup.html'
	],
	function(declare, lang, array, event, string, domAttr, domClass, on, xhr, json, AlfDropDownMenu, AlfMenuGroup, template) {
		return declare([AlfMenuGroup], {

			templateString: template,

			cssRequirements: [{cssFile: '../../../alfresco/menus/css/AlfMenuGroup.css'}],

			constructor: function() {
				this.templateString = string.substitute(template, {
					ddmTemplateString: AlfDropDownMenu.prototype.templateString
				});
			},

			postCreate: function() {
				this.inherited(arguments);
				domAttr.set(this._clearAllNode, 'title', this.message('message.notifications.clearall.title'));
				on (this._clearAllNode, 'i:click', lang.hitch(this, this._onClearAllClick));
			},

			_clearAllSuccess: function(success) {
				this.params.notificationsPopup.loadNewNotificationsCount();
				array.forEach(this.params.notificationsPopup.rootWidget.getChildren(), function (itemWidget) {
					this.removeChild(itemWidget);
				}, this.params.notificationsPopup.rootWidget);
				this.params.notificationsPopup.rootWidget.processWidgets([{
					name: 'alfresco/header/AlfMenuItem',
					config: {
						i18nScope: 'notificationsPopup',
						label: 'message.notifications.none'
					}
				}], this.params.notificationsPopup.rootWidget.domNode);
			},

			_clearAllFailure: function(failure) {
				Alfresco.util.PopupManager.displayMessage({
					text: this.message('message.notifications.clearall.set.read.failure')
				});
			},

			_clearAll: function() {
				xhr.post(Alfresco.constants.PROXY_URI_RELATIVE + 'lecm/notifications/active-channel/api/set/read/all', {
					handleAs: 'json',
					headers: {'Content-Type': 'application/json'},
					data: '{}'
				}).then(lang.hitch(this, this._clearAllSuccess), lang.hitch(this, this._clearAllFailure));
			},

			_onClearAllClick: function(evt) {
				event.stop(evt);
				Alfresco.util.PopupManager.displayPrompt({
					title: this.message('message.notifications.clearall.title'),
					text:  this.message('message.notifications.clearall.prompt.title'),
					modal: true,
					buttons: [{
						text: this.message('message.notifications.clearall.prompt.yes.title'),
						handler: {
							obj: {
								context: this,
								fn: this._clearAll
							},
							fn: function(event, obj) {
								this.destroy();
								obj.fn.call(obj.context);
							}
						}
					}, {
						text: this.message('message.notifications.clearall.prompt.no.title'),
						handler: function() {
							this.destroy();
						}
					}]
				});
				return false;
			},

			toggle: function(condition) {
				domClass.toggle(this._clearAllContainer, 'hidden', condition);
			}
		});
	}
);
