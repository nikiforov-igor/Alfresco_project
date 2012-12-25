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
            var employees = "org-employees";

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
                return window.location.href.slice(window.location.href.indexOf('share/page/') + 11);
            }

            function reloadPage(type) {
                var url = window.location.protocol + "//" + window.location.host + Alfresco.constants.URL_PAGECONTEXT;
                window.location.href = url + (type != null && type != '' ? type : "orgstructure");
            }

            var context = this;

            // Создание кнопок
            var onButtonClick1 = function (e) {
                reloadPage("subscr-object");
            };
            this.widgets.employeesButton = Alfresco.util.createYUIButton(this, "objectBtn", onButtonClick1, {});

            var onButtonClick2 = function (e) {
                reloadPage("subscr-type");
            };
            this.widgets.staffButton = Alfresco.util.createYUIButton(this, "typeBtn", onButtonClick2, {});

            var onButtonClick3 = function (e) {
                reloadPage("org-structure");
            };
            this.widgets.orgstructureButton = Alfresco.util.createYUIButton(this, "orgstructureBtn", onButtonClick3, {});

            var onButtonClick4 = function (e) {
                reloadPage("org-work-groups");
            };
            this.widgets.workGroupButton = Alfresco.util.createYUIButton(this, "workGroupBtn", onButtonClick4, {});

            var onButtonClick5 = function (e) {
                reloadPage("org-positions");
            };
            this.widgets.positionsButton = Alfresco.util.createYUIButton(this, "positionsBtn", onButtonClick5, {});

            var onButtonClick6 = function (e) {
                reloadPage("org-roles");
            };
            this.widgets.rolesButton = Alfresco.util.createYUIButton(this, "rolesBtn", onButtonClick6, {});

            var onButtonClick7 = function (e) {
                reloadPage("org-profile");
            };
            this.widgets.organizationButton = Alfresco.util.createYUIButton(this, "organizationBtn", onButtonClick7, {});

            var onButtonClick8 = function (e) {
                reloadPage("org-business-roles");
            };
            this.widgets.businessRolesButton = Alfresco.util.createYUIButton(this, "businessRolesBtn", onButtonClick8, {});

            // начальлная загрузка Грида (на основании текущей странички)
            var type = getPageName();
            if (type == null || type == '') {
                type = employees; // по умолчанию, будем рисовать страницу с сотрудниками
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
                            var namespace = "lecm-orgstr";
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
