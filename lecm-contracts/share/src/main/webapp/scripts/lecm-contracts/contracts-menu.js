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
 * @class LogicECM.module.Contracts
 */
LogicECM.module.Contracts = LogicECM.module.Contracts || {};

/**
 * Contracts module.
 *
 * @namespace LogicECM.module
 * @class LogicECM.module.Contracts
 */
(function () {

    var Dom = YAHOO.util.Dom;
    var Bubbling = YAHOO.Bubbling;
    LogicECM.module.Contracts.Menu = function (htmlId) {
        return LogicECM.module.Contracts.Menu.superclass.constructor.call(
            this,
            "LogicECM.module.Contracts.Menu",
            htmlId,
            ["button", "container", "connection", "json", "selector"]);
    };

    YAHOO.extend(LogicECM.module.Contracts.Menu, Alfresco.component.Base, {
        roots:{},
        messages: null,

        setMessages:function (messages) {
            this.messages = messages;
        },

        _draw:function () {
            var subscriptions = "contract-documents";

            function bubbleTable(root) {
                if (root != "undefined" && root != null && root.nodeRef != "NOT_LOAD") {
                    Bubbling.fire("activeGridChanged",
                        {
                            datagridMeta: {
                                itemType: "lecm-contract:document",
                                datagridFormId: "all-contracts",
                                createFormId: "",
                                nodeRef: root.nodeRef,
                                actionsConfig: {
                                    fullDelete: "true"
                                }
                            },
//                            datagridMeta:{
//                                itemType:root.itemType,
//                                nodeRef:root.nodeRef,
//                                actionsConfig:{
//                                    fullDelete:true
//                                }
//                            },
                            bubblingLabel:root.bubblingLabel
                        });
                }
            }

            function getPageName() {
                return window.location.href.slice(window.location.href.indexOf('share/page/') + 11);
            }

            function reloadPage(type) {
                var url = window.location.protocol + "//" + window.location.host + Alfresco.constants.URL_PAGECONTEXT;
                window.location.href = url + (type != null && type != '' ? type : "contract-documents");
            }

            var context = this;

            // Создание кнопок
            var onButtonClick1 = function (e) {
                reloadPage("contract-documents");
            };
            this.widgets.subscrTypeButton = Alfresco.util.createYUIButton(this, "contractsBtn", onButtonClick1, {});

            // начальлная загрузка Грида (на основании текущей странички)
            var type = getPageName();
            if (type == null || type == '') {
                type = subscriptions;
            }
            var root = context.roots[type];
            bubbleTable(root);
        },

        draw:function draw() {
            var sUrl = Alfresco.constants.PROXY_URI + "lecm/contracts/draft-root";
            var callback = {
                success:function (oResponse) {
                    var oResults = eval("(" + oResponse.responseText + ")");
                    if (oResults != null) {
                            var root = {
                                nodeRef: oResults.nodeRef
                            };
                        var page = "contract-documents";
                        root.itemType = "lecm-contract:document";
                        root.bubblingLabel = "contracts";
                        oResponse.argument.context.roots[page] = root;
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
