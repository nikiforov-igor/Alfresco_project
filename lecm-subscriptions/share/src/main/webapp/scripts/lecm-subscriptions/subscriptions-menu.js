/**
 * LogicECM root namespace.
 *
 * @namespace LogicECM
 */
// Ensure LogicECM root object exists
if (typeof LogicECM == "undefined" || !LogicECM) {
    var LogicECM = {};
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
            var subscriptions = "subscr-object";

            function bubbleTable(root) {
                if (root != "undefined" && root != null && root.nodeRef != "NOT_LOAD") {
                    Bubbling.fire("activeGridChanged",
                        {
                            datagridMeta:{
                                itemType:root.itemType,
                                nodeRef:root.nodeRef,
                                actionsConfig:{
                                    fullDelete:root.fullDelete
                                },
                                custom:{
                                    namePattern:root.namePattern
                                }
                            },
                            bubblingLabel:root.bubblingLabel
                        });
                }
            }

            function getPageName() {
                return window.location.href.slice(window.location.href.indexOf('share/page/') + "subscriptions-to-type");
            }

            function reloadPage(type) {
                var url = window.location.protocol + "//" + window.location.host + Alfresco.constants.URL_PAGECONTEXT;
                window.location.href = url + (type != null && type != '' ? type : "subscriptions");
            }

            var context = this;

            // Создание кнопок
            var onButtonClick1 = function (e) {
                reloadPage("subscriptions-to-type");
            };
            this.widgets.subscrTypeButton = Alfresco.util.createYUIButton(this, "typeBtn", onButtonClick1, {});

            var onButtonClick2 = function (e) {
                reloadPage("subscriptions-to-object");
            };
            this.widgets.subscrObjectButton = Alfresco.util.createYUIButton(this, "objectBtn", onButtonClick2, {});

            // начальлная загрузка Грида (на основании текущей странички)
            var type = getPageName();
            if (type == null || type == '') {
                type = subscriptions; // по умолчанию, будем рисовать страницу с сотрудниками
            }
            var root = context.roots[type];
            bubbleTable(root);
        },

        draw:function draw() {
            var sUrl = Alfresco.constants.PROXY_URI + "lecm/subscriptions/roots";
            var callback = {
                success:function (oResponse) {
                    var oResults = eval("(" + oResponse.responseText + ")");
                    if (oResults != null) {
                        for (var nodeIndex in oResults) {
                            var root = {
                                nodeRef:oResults[nodeIndex].nodeRef,
                                itemType:oResults[nodeIndex].itemType,
                                namePattern:oResults[nodeIndex].namePattern,
                                page:oResults[nodeIndex].page,
                                fullDelete:oResults[nodeIndex].fullDelete
                            };
                            var namespace = "lecm-subscr";
                            var page = root.page;
                            var cType = root.itemType;
                            root.itemType = namespace + ":" + cType;
                            root.bubblingLabel = cType;
                            oResponse.argument.context.roots[page] = root;
                        }
                    }
                    oResponse.argument.context._draw();
                },
                failure:function (oResponse) {
                    YAHOO.log("Failed to process XHR transaction.", "info", "example");
                },
                argument:{
                    context:this
                },
                timeout:10000
            };
            YAHOO.util.Connect.asyncRequest('GET', sUrl, callback);
        }
    });
})();
