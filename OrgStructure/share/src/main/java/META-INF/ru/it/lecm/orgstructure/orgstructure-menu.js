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
        menu:null,
        elements:[],
        roots:new Object(),
        messages:null,
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

        setMessages:function (messages) {
            this.messages = messages;
        },

        draw:function () {
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
            var button1 = new YAHOO.widget.Button({
                id:"structure",
                type:"button",
                label:context.messages["lecm.orgstructure.structure.btn"],
                container:"button1",
                width:140
            });
            var onButtonClick1 = function (e) {
                reloadPage("structure");
            };
            button1.on("click", onButtonClick1);
            this.elements.push(button1);

            var button2 = new YAHOO.widget.Button({
                id:"employees",
                type:"button",
                label:context.messages["lecm.orgstructure.employees.btn"],
                container:"button2"
            });
            var onButtonClick2 = function (e) {
                reloadPage("employees");
            };
            button2.on("click", onButtonClick2);
            this.elements.push(button2);

            var type = getUrlVars()["type"];
            var root;
            if (type != null) {
                root = context.roots[type];
            } else {
                root = context.roots["structure"];
            }
            bubbleTable(root);
        },
        _loadRoots:function loadRoots() {
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
                            root.itemType = namespace + ":"+ cType;

                            oResponse.argument.context.roots[rType] = root;
                        }
                    }
                    oResponse.argument.context.draw();
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
