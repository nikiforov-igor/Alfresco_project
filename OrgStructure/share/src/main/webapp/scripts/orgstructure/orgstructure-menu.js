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
        options:{
            templateUrl:null,
            actionUrl:null,
            firstFocus:null,
            onSuccess:{
                fn:null,
                obj:null,
                scope:window
            }
        },

        _draw:function () {
            function bubbleTable(root) {
                if (root != "undefined" && root != null) {
                    Bubbling.fire("activeGridChanged",
                        {
                            datagridMeta:{
                                itemType:root.itemType,
                                name:root.type,
                                nodeRef:root.nodeRef, // used in toolbar
                                namePattern:root.namePattern, // used on save in toolbar and tree
                                title:root.label
                            }
                        });
                }
            }

            function getUrlVars() {
                var vars = [], hash;
                var hashes = window.location.href.slice(window.location.href.indexOf('?') + 1).split('&');
                for (var i = 0; i < hashes.length; i++) {
                    hash = hashes[i].split('=');
                    vars.push(hash[0]);
                    vars[hash[0]] = hash[1];
                }
                return vars;
            }

            function reloadPage(type) {
                var url = window.location.protocol + "//" + window.location.host + window.location.pathname;
                window.location.href = url + "?type=" + type;
            }

            var context = this;

            var type = getUrlVars()["type"];
            if (type == null) {
                type = "structure";
            }
            var root = context.roots[type];

            var radio1 = Dom.get("structure");
            radio1.onclick = function (e) {
                reloadPage("structure");
            };
            radio1.checked = (type == "structure");

            var radio2 = Dom.get("employees");
            radio2.onclick = function (e) {
                reloadPage("employees");
            };
            radio2.checked = (type == "employees");

            var radio3 = Dom.get("workGroups");
            radio3.onclick = function (e) {
                reloadPage("workGroups");
            };
            radio3.checked = (type == "workGroups");

            var radio4 = Dom.get("staffLists");
            radio4.onclick = function (e) {
                reloadPage("staffLists");
            };
            radio4.checked = (type == "staffLists");

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
                                label:oResults[nodeIndex].title,
                                nodeRef:oResults[nodeIndex].nodeRef,
                                isLeaf:oResults[nodeIndex].isLeaf,
                                type:oResults[nodeIndex].type,
                                itemType:oResults[nodeIndex].itemType,
                                namePattern:oResults[nodeIndex].namePattern
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
