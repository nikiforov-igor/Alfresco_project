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
 * LogicECM Orgstructure module namespace.
 *
 * @namespace LogicECM
 * @class LogicECM.module.OrgStructure.OrgStructure
 */
LogicECM.module.OrgStructure = LogicECM.module.OrgStructure || {};

/**
 * OrgStructure module.
 *
 * @namespace LogicECM.module
 * @class LogicECM.module.OrgStructure
 */
(function () {

    var Dom = YAHOO.util.Dom
    var Bubbling = YAHOO.Bubbling;
    LogicECM.module.OrgStructure.Menu = function (htmlId) {
        return LogicECM.module.OrgStructure.Menu.superclass.constructor.call(
            this,
            "LogicECM.module.OrgStructure.Menu",
            htmlId,
            ["button", "container", "connection", "json", "selector"]);
    };

    YAHOO.extend(LogicECM.module.OrgStructure.Menu, Alfresco.component.Base, {
        roots:{},
        messages: null,

        setMessages:function (messages) {
            this.messages = messages;
        },

        _draw:function () {
            const structure = "orgstructure";

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
                var url = window.location.protocol + "//" + window.location.host + "/share/page/";
                window.location.href = url + (type != null && type != '' ? type : "orgstructure");
            }

            var context = this;

            var type = getPageName();
            if (type == null || type == '') {
                type = structure; // по умолчанию, будем рисовать страницу с подразделениями
            }
            var root = context.roots[type];
            if (root == null){ // введено неверное значение - рисуем страницу с подразделениями
                root =  context.roots[structure];
            }

            // Создание кнопок
            var button1 = new YAHOO.widget.Button({
                id:"employees",
                type:"button",
                label:context.messages["lecm.orgstructure.employees.btn"],
                container:"employees",
                width:140
            });
            var onButtonClick1 = function (e) {
                reloadPage("org-employees");
            };
            button1.on("click", onButtonClick1);

            var button2 = new YAHOO.widget.Button({
                id:"staff-list",
                type:"button",
                label:context.messages["lecm.orgstructure.staff-list.btn"],
                container:"staff-list"
            });
            var onButtonClick2 = function (e) {
                reloadPage("staff-list");
            };
            button2.on("click", onButtonClick2);

            var button3 = new YAHOO.widget.Button({
                id:"orgstructure",
                type:"button",
                label:context.messages["lecm.orgstructure.orgstructure.btn"],
                container:"orgstructure"
            });
            var onButtonClick3 = function (e) {
                reloadPage("orgstructure");
            };
            button3.on("click", onButtonClick3);

            var button4 = new YAHOO.widget.Button({
                id:"work-groups",
                type:"button",
                label:context.messages["lecm.orgstructure.work-groups.btn"],
                container:"work-groups"
            });

            var onButtonClick4 = function (e) {
                reloadPage("work-groups");
            };
            button4.on("click", onButtonClick4);

            var button5 = new YAHOO.widget.Button({
                id:"positions",
                type:"button",
                label:context.messages["lecm.orgstructure.positions.btn"],
                container:"positions"
            });
            var onButtonClick5 = function (e) {
                reloadPage("org-positions");
            };
            button5.on("click", onButtonClick5);

            var button6 = new YAHOO.widget.Button({
                id:"roles",
                type:"button",
                label:context.messages["lecm.orgstructure.roles.btn"],
                container:"roles"
            });

            var onButtonClick6 = function (e) {
                reloadPage("org-roles");
            };
            button6.on("click", onButtonClick6);

            var button7 = new YAHOO.widget.Button({
                id:"organization",
                type:"button",
                label:context.messages["lecm.orgstructure.organization.btn"],
                container:"organization"
            });
            var onButtonClick7 = function (e) {
                reloadPage("organization");
            };
            button7.on("click", onButtonClick7);

            // начальлная загрузка Грида (на основании текущей странички)
            bubbleTable(root);
        },

        draw:function draw() {
            var sUrl = Alfresco.constants.PROXY_URI + "lecm/orgstructure/branch";
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
