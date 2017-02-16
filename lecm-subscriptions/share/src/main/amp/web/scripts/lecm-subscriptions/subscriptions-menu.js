/**
 * LogicECM root namespace.
 *
 * @namespace LogicECM
 */
// Ensure LogicECM root object exists
if (typeof LogicECM == "undefined" || !LogicECM) {
    LogicECM = {};
}

/**
 * LogicECM top-level module namespace.
 *
 * @namespace LogicECM
 * @class LogicECM.module
 */
LogicECM.module = LogicECM.module || {};


/**
 * LogicECM subscriptions module namespace.
 *
 * @namespace LogicECM
 * @class LogicECM.module.Subscriptions
 */
LogicECM.module.Subscriptions = LogicECM.module.Subscriptions || {};

/**
 * OrgStructure module.
 *
 * @namespace LogicECM.module
 * @class LogicECM.module.Subscription
 */
(function () {

    var Dom = YAHOO.util.Dom;
    var Bubbling = YAHOO.Bubbling;
    LogicECM.module.Subscriptions.Menu = function (htmlId) {
        return LogicECM.module.Subscriptions.Menu.superclass.constructor.call(
            this,
            "LogicECM.module.Subscriptions.Menu",
            htmlId,
            ["button", "container", "connection", "json", "selector"]);
    };

    YAHOO.extend(LogicECM.module.Subscriptions.Menu, Alfresco.component.Base, {
        roots:{},
        messages: null,

        setMessages:function (messages) {
            this.messages = messages;
        },

        _draw:function () {
            var subscriptions = "subscriptions-to-type";

            function bubbleTable(root) {
                if (root != "undefined" && root != null && root.nodeRef != "NOT_LOAD") {
                    Bubbling.fire("activeGridChanged",
                        {
                            datagridMeta:{
                                itemType:root.itemType,
                                nodeRef:root.nodeRef,
                                actionsConfig:{
                                    fullDelete:true
                                }
                            },
                            bubblingLabel:root.bubblingLabel
                        });
                }
            }

            function getPageName() {
                return window.location.href.slice(window.location.href.indexOf(Alfresco.constants.PAGECONTEXT) + Alfresco.constants.PAGECONTEXT.length);
            }

            function reloadPage(type) {
                var url = window.location.protocol + "//" + window.location.host + Alfresco.constants.URL_PAGECONTEXT;
                window.location.href = url + (type != null && type != '' ? type : "subscriptions-to-type");
            }

            var context = this;

            // Создание кнопок
            var onButtonClick1 = function (e) {
                reloadPage("subscriptions-to-type");
            };
            this.widgets.subscrTypeButton = Alfresco.util.createYUIButton(this, "typeBtn", onButtonClick1, {
                disabled: !LogicECM.module.Subscriptions.IS_ENGINEER
            });

            var onButtonClick2 = function (e) {
                reloadPage("subscriptions-to-object");
            };
            this.widgets.subscrObjectButton = Alfresco.util.createYUIButton(this, "objectBtn", onButtonClick2, {
                disabled: !LogicECM.module.Subscriptions.IS_ENGINEER
            });

            // начальлная загрузка Грида (на основании текущей странички)
            if (LogicECM.module.Subscriptions.IS_ENGINEER){
                var type = getPageName();
                if (type == null || type == '') {
                    type = subscriptions;
                }
                var root = context.roots[type];
                bubbleTable(root);
            }
        },

        draw:function draw() {
            var sUrl = Alfresco.constants.PROXY_URI + "lecm/subscriptions/roots";
            Alfresco.util.Ajax.jsonGet({
                url: sUrl,
                successCallback: {
                    fn: function (response) {
                        var oResults = response.json;
                        if (oResults) {
                            for (var nodeIndex in oResults) {
                                var root = {
                                    nodeRef: oResults[nodeIndex].nodeRef,
                                    itemType: oResults[nodeIndex].itemType,
                                    page: oResults[nodeIndex].page,
                                    fullDelete: oResults[nodeIndex].fullDelete
                                };
                                var namespace = "lecm-subscr";
                                var page = root.page;
                                var cType = root.itemType;
                                root.itemType = namespace + ":" + cType;
                                root.bubblingLabel = cType;
                                this.roots[page] = root;
                            }
                        }
                        this._draw();
                    },
                    scope: this
                },
                failureCallback: {
                    fn: function (response) {
                        YAHOO.log("Failed to process XHR transaction.", "info", "example");
                    },
                    scope: this
                }
            });
        }
    });
})();
