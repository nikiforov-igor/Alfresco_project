/**
 * Copyright (C) 2005-2013 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * @module alfresco/header/AlfMenuBarPopup
 * @extends module:alfresco/menus/AlfMenuBarPopup
 * @author Dave Draper
 */
define(["dojo/_base/declare",
        "dijit/_WidgetBase",
        "alfresco/menus/AlfMenuBarPopup",
        "dojo/dom-class",
        "dojo/dom-construct",
        "dojo/_base/lang",
        "dojo/_base/array",
        "dojox/layout/ScrollPane"
    ],
    function(declare, _Widget, AlfMenuBarPopup, domClass, domConstruct, lang, array, ScrollPane) {

        var Connect = YAHOO.util.Connect,
            Dom = YAHOO.util.Dom;

        return declare([_Widget, AlfMenuBarPopup], {

            notificationsCounterId: "notificationsCounter",

            refreshCountTime: 60000,

            loadItemsCount: 6,

            skipItemsCount: 0,

            readNotifications: null,

            scrollPane: null,

            wrapper: null,

            /**
             * An array of the CSS files to use with this widget.
             *
             * @instance
             * @type {{cssFile: string, media: string}[]}
             * @default [{cssFile:"./css/AlfMenuBarPopup.css"}]
             */
            cssRequirements: [{
                cssFile: "./css/counter-styles.css",
                mediaType: "screen"
            }],

            /**
             * Used to indicate whether or not to display a down arrow that indicates that this is a drop-down menu.
             * True by default.
             *
             * @instance
             * @type {boolean} showArrow
             * @default true
             */
            showArrow: false,

            widgets: [{
                name: "alfresco/header/AlfMenuItem",
                config: {
                    iconClass: "alf-loading-icon",
                    label: "loading.label"
                }
            }],

            checkVisibleCounter: function(count) {
                Dom.setStyle(this.notificationsCounterId, "display", count > 0 ? "inline-block" : "none");
                if (count > 0) {
                    domClass.add(this.notificationsCounterId, "blink");
                } else {
                    domClass.remove(this.notificationsCounterId, "blink");
                }
            },

            loadNewNotificationsCount: function() {
                var me = this;
                var sUrl = Alfresco.constants.PROXY_URI + "lecm/notifications/active-channel/api/new-count";
                var callback = {
                    success: function(oResponse) {
                        if (oResponse.responseText != null && oResponse.responseText.length > 0) {
                            var oResults = eval("(" + oResponse.responseText + ")");
                            if (oResults && oResults.newCount) {
                                var elem = Dom.get(me.notificationsCounterId);
                                if (elem != null) {
                                    elem.innerHTML = (oResults.newCount > 99) ? "∞" : oResults.newCount;
                                    me.checkVisibleCounter(oResults.newCount);
                                }
                            } else {
                                YAHOO.log("Failed to process XHR transaction.", "info", "example");
                            }
                        } else {
                            YAHOO.log("Failed to process XHR transaction.", "info", "example");
                        }
                    },
                    failure: function(oResponse) {
                        YAHOO.log("Failed to process XHR transaction.", "info", "example");
                    }
                };
                Connect.asyncRequest('GET', sUrl, callback);
            },

            startLoadNewNotifications: function() {
                this.loadNewNotificationsCount();
                var self = this;

                setInterval(function() { // bind() не работает в IE
                    self.loadNewNotificationsCount();
                }, this.refreshCountTime);

                //костыль для инициализации форм, пока живет здесь
                Alfresco.util.Ajax.request(
                    {
                        url:Alfresco.constants.URL_SERVICECONTEXT + "lecm/config/init?reset=false",
                        dataObj:{},
                        successCallback:{
                            fn:function (response) {
                            }
                        },
                        failureMessage:"message.failure",
                        execScripts:true
                    });
            },

            /**
             * Extends the default implementation to create an additional <span> element with the show arrow CSS class to the
             * focusNode of the widget.
             *
             * @instance
             */
            postCreate: function alfresco_header_NotificationsPopup__postCreate() {
                this.inherited(arguments);
                if (this.popup && this.popup.domNode) {
                    // This ensures that we can differentiate between header menu popups and regular menu popups with our CSS selectors
                    domClass.add(this.popup.domNode, "alf-header-menu-bar");
                    domConstruct.place('<span class="counter" id="notificationsCounter"></span>', this.domNode);
                    this.popup.onOpen = dojo.hitch(this, "initNotifications");
                }
                this.startLoadNewNotifications();
                YAHOO.util.Event.addListener(this.popup.id, "scroll", this.onContainerScroll, this);

            },

            onContainerScroll: function(event, scope) {
                var container = event.currentTarget;
                if (container.scrollTop + container.clientHeight == container.scrollHeight) {
                    Dom.setStyle(scope.notificationsWindowId + "-loading", "visibility", "visible");
                    scope.loadNotifications();
                }
            },

            loadNotifications: function() {
                var me = this;
                Alfresco.util.Ajax.jsonPost({
                    url: Alfresco.constants.PROXY_URI + "lecm/notifications/active-channel/api/records",
                    dataObj: {
                        skipItemsCount: me.skipItemsCount,
                        loadItemsCount: me.loadItemsCount,
                        ignoreNotifications: me.readNotifications
                    },
                    successCallback: {
                        fn: function DataGrid_onDataItemCreated_refreshSuccess(response) {
                            var items = response.json.items;
                            var readNewNotifications = [];
                            var container = Dom.get(this.popup.id);
                            Dom.addClass(container, 'main-part');

                            for (var i = 0; i < items.length; i++) {
                                var item = items[i];
                                me.readNotifications.push(item.nodeRef);
                                var div = document.createElement('div');
                                var detail = document.createElement('span');

                                div.setAttribute('class', 'notification-row');
                                if (item.isRead == "false") {
                                    readNewNotifications.push(item);
                                    Dom.addClass(div, 'bold');
                                }

                                detail.innerHTML = item.description;
                                detail.setAttribute('class', 'detail');
                                div.appendChild(detail);
                                div.innerHTML += '<br />' + Alfresco.util.relativeTime(new Date(item.formingDate));
                                container.appendChild(div);
                            }

                            if (readNewNotifications.length > 0) {
                                me.setReadNotifications(readNewNotifications);
                            }
                            //Dom.setStyle(this.notificationsWindowId + "-loading", "visibility", "hidden");
                            me.showNotificationsWindow();
                        },
                        scope: this
                    },
                    failureCallback: {
                        fn: function(response) {
                            Alfresco.util.PopupManager.displayMessage({
                                text: this.msg("message.notifications.load.failure")
                            });
                        },
                        scope: this
                    }
                });
            },

            setReadNotifications: function(p_items) {
                var items = YAHOO.lang.isArray(p_items) ? p_items : [p_items];
                var nodeRefs = [];
                for (var i = 0; i < items.length; ++i) {
                    nodeRefs.push({
                        "nodeRef": items[i].nodeRef
                    });
                }
                Alfresco.util.Ajax.request({
                    method: "POST",
                    url: Alfresco.constants.PROXY_URI_RELATIVE + "lecm/notifications/active-channel/api/set/read",
                    dataObj: nodeRefs,
                    requestContentType: "application/json",
                    successCallback: {
                        fn: function(response) {
                            this.loadNewNotificationsCount();
                        },
                        scope: this
                    },
                    failureMessage: "не удалось установить прочитанные уведомления"
                });
            },

            initNotifications: function() {
                var _this = this;
                array.forEach(this.popup.getChildren(), function(widget, index) {
                    _this.popup.removeChild(widget);
                });
                var container = Dom.get(this.popup.id);
            	container.innerHTML = "";
                this.skipItemsCount = 0;
                this.readNotifications = [];
                this.loadNotifications();
            },

            _notificationsLoaded: function logic_ecm_header_Notifications__loaded() {
                // Remove the loading menu item...
                var _this = this;
                array.forEach(this.popup.getChildren(), function(widget, index) {
                    _this.popup.removeChild(widget);
                });
            }
        });
    });
