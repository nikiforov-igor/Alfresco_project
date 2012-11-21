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
 * LogicECM StatemachineEditor module namespace.
 *
 * @namespace LogicECM
 * @class LogicECM.module.StatemachineEditor.StatemachineEditor
 */
LogicECM.module.StatemachineEditor = LogicECM.module.StatemachineEditor || {};

/**
 * StatemachineEditor module.
 *
 * @namespace LogicECM.module
 * @class LogicECM.module.StatemachineEditor
 */
(function () {

    var Dom = YAHOO.util.Dom
    var Bubbling = YAHOO.Bubbling;
    LogicECM.module.StatemachineEditor.Menu = function (htmlId) {
        return LogicECM.module.StatemachineEditor.Menu.superclass.constructor.call(
            this,
            "LogicECM.module.StatemachineEditor.Menu",
            htmlId,
            ["button", "container", "connection", "json", "selector"]);
    };

    YAHOO.extend(LogicECM.module.StatemachineEditor.Menu, Alfresco.component.Base, {
        roots:{},
        messages: null,

        setMessages:function (messages) {
            this.messages = messages;
        },

        _draw:function () {
            const structure = "StatemachineEditor";

            function bubbleTable(root) {
                if (root != "undefined" && root != null) {
                    Bubbling.fire("activeGridChanged",
                        {
                            datagridMeta:{
                                itemType:root.itemType,
                                nodeRef:root.nodeRef,
                                custom: {
                                    namePattern:root.namePattern
                                },
                                searchConfig: {
                                    filter:'PARENT:\"' + root.nodeRef + '\"' + ' AND (NOT (ASPECT:"lecm-dic:aspect_active") OR lecm\\-dic:active:true)'
                                }
                            }
                        });
                }
            }

            function getPageName() {
                return window.location.href.slice(window.location.href.indexOf('share/page/') + 11);
            }

            function reloadPage(type) {
                var url = window.location.protocol + "//" + window.location.host + Alfresco.constants.URL_PAGECONTEXT;
                window.location.href = url + (type != null && type != '' ? type : "StatemachineEditor");
            }

            var context = this;

            // Создание кнопок
            var onButtonClick1 = function (e) {
                reloadPage("org-employees");
            };
            this.widgets.employeesButton = Alfresco.util.createYUIButton(this, "employeesBtn", onButtonClick1, {});

            var onButtonClick2 = function (e) {
                reloadPage("staff-list");
            };
            this.widgets.staffButton = Alfresco.util.createYUIButton(this, "staffBtn", onButtonClick2, {});

            var onButtonClick3 = function (e) {
                reloadPage("StatemachineEditor");
            };
            this.widgets.StatemachineEditorButton = Alfresco.util.createYUIButton(this, "StatemachineEditorBtn", onButtonClick3, {});

            var onButtonClick4 = function (e) {
                reloadPage("work-groups");
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
                reloadPage("organization");
            };
            this.widgets.organizationButton = Alfresco.util.createYUIButton(this, "organizationBtn", onButtonClick7, {});

            // начальлная загрузка Грида (на основании текущей странички)
            var type = getPageName();
            if (type == null || type == '') {
                type = structure; // по умолчанию, будем рисовать страницу с подразделениями
            }
            var root = context.roots[type];
            if (root == null){ // введено неверное значение - рисуем страницу с подразделениями
                root =  context.roots[structure];
            }
            bubbleTable(root);
        },

        draw:function draw() {
            var sUrl = Alfresco.constants.PROXY_URI + "lecm/StatemachineEditor/branch";
            var callback = {
                success:function (oResponse) {
                    var oResults = eval("(" + oResponse.responseText + ")");
                    if (oResults != null) {
                        for (var nodeIndex in oResults) {
                            var root = {
                                nodeRef:oResults[nodeIndex].nodeRef,
                                itemType:oResults[nodeIndex].itemType,
                                namePattern:oResults[nodeIndex].namePattern,
                                type:oResults[nodeIndex].type
                            };
                            var namespace = "lecm-orgstr";
                            var rType = root.type;
                            var cType = root.itemType;
                            root.type = namespace + ":" + rType;
                            root.itemType = namespace + ":" + cType;

                            oResponse.argument.context.roots[rType] = root;
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
                timeout:7000
            };
            YAHOO.util.Connect.asyncRequest('GET', sUrl, callback);
        }
    });
})();
