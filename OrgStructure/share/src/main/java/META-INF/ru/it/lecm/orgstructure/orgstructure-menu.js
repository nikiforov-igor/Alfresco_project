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
 * OrgStructure module.
 *
 * @namespace LogicECM.module
 * @class LogicECM.module.OrgStructure
 */
(function () {

    var Dom = YAHOO.util.Dom
    var Bubbling = YAHOO.Bubbling;
    LogicECM.module.Menu = function (htmlId) {
        return LogicECM.module.Menu.superclass.constructor.call(
            this,
            "LogicECM.module.Menu",
            htmlId,
            ["button", "container", "connection", "json", "selector"]);
    };

    YAHOO.extend(LogicECM.module.Menu, Alfresco.component.Base, {
        menu:null,
        elements:[],
        roots: new Object(),
        selectedElement:null,
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
                Bubbling.fire("orgElementSelected",
                    {
                        orgstructureElement:{
                            description:"",
                            type:root.type,
                            itemType:root.childType,
                            name:root.type,
                            nodeRef:root.nodeRef,
                            dataSourceUri:root.dsUri,
                            permissions:{
                                'delete':false,
                                'edit':false
                            },
                            title:root.label
                        },
                        scrollTo:true
                    });
            }

            var context = this;
            var button1 = new YAHOO.widget.Button({
                id:"structure",
                type:"button",
                label:"Structure",
                container:"button1"
            });
            var onButtonClick1 = function (e) {
                Dom.setStyle(Dom.get("orgstructure-tree"), "display","block");
                var root = context.roots["lecm-orgstr:organization-structure"];
                bubbleTable(root);
            };
            button1.on("click", onButtonClick1);
            this.elements.push(button1);

            var button2 = new YAHOO.widget.Button({
                id:"projects",
                type:"button",
                label:"Projects Register",
                container:"button2"
            });
            var onButtonClick2 = function (e) {
                Dom.setStyle(Dom.get("orgstructure-tree"), "display","none");
                var root = context.roots["lecm-orgstr:project-register"];
                bubbleTable(root);
            };
            button2.on("click", onButtonClick2);
            this.elements.push(button2);

            var button3 = new YAHOO.widget.Button({
                id:"employees",
                type:"button",
                label:"Employees",
                container:"button3"
            });
            var onButtonClick3 = function (e) {
                Dom.setStyle(Dom.get("orgstructure-tree"), "display","none");
                var root = context.roots["lecm-orgstr:employee-container"];
                bubbleTable(root);
            };
            button3.on("click", onButtonClick3);
            this.elements.push(button3);

            var button4 = new YAHOO.widget.Button({
                id:"stafflist",
                type:"button",
                label:"Staff List",
                container:"button4"
            });

            var onButtonClick4 = function (e) {
                Dom.setStyle(Dom.get("orgstructure-tree"), "display","none");
                var root = context.roots["lecm-orgstr:staff-list"];
                bubbleTable(root);
            };
            button4.on("click", onButtonClick4);
            this.elements.push(button4);

            Dom.setStyle(Dom.get("orgstructure-tree"), "display","none");
        },
        _loadRoots: function loadRoots(fnLoadComplete)  {
            var sUrl = Alfresco.constants.PROXY_URI + "lecm/orgstructure/branch";
            var callback = {
                success: function(oResponse) {
                    var oResults = eval("(" + oResponse.responseText + ")");
                    if (oResults != null) {
                        for (var nodeIndex in oResults) {
                            var root = {
                                label: oResults[nodeIndex].title,
                                nodeRef: oResults[nodeIndex].nodeRef,
                                isLeaf: oResults[nodeIndex].isLeaf,
                                type: oResults[nodeIndex].type,
                                dsUri:oResults[nodeIndex].dsUri,
                                childType:oResults[nodeIndex].childType
                            };
                            oResponse.argument.roots[root.type] = root;
                        }
                    }
                    if (oResponse.argument.fnLoadComplete != null) {
                        oResponse.argument.fnLoadComplete();
                    }
                },
                failure: function(oResponse) {
                    YAHOO.log("Failed to process XHR transaction.", "info", "example");
                    if (oResponse.argument.fnLoadComplete != null) {
                        oResponse.argument.fnLoadComplete();
                    }
                },
                argument: {
                    roots: this.roots,
                    fnLoadComplete: fnLoadComplete
                },
                timeout: 7000
            };
            YAHOO.util.Connect.asyncRequest('GET', sUrl, callback);
        }/*,
        _loadContent: function loadContent(full, type, fnLoadComplete)  {
            var root = this.roots[type];
            var sUrl = Alfresco.constants.URL_PAGECONTEXT + "lecm/orgstructure/content";
            if (full != null) {
                sUrl += "?full=" + full;
            }

            var callback = {
                success: function(oResponse) {
                    var content = Dom.get("orgstructure-content");
                    if (content != null) {
                        content.innerHTML = oResponse.responseText;
                    }
                    var root = oResponse.argument.root;
                    Bubbling.fire("orgElementSelected",
                        {
                            orgstructureElement: {
                                description: "",
                                type:root.type,
                                itemType: root.childType,
                                name: root.type,
                                nodeRef: root.nodeRef,
                                dataSourceUri:root.dsUri,
                                permissions: {
                                    'delete': false,
                                    'edit': false
                                },
                                title: root.label
                            },
                            scrollTo: true
                        });
                },
                failure: function(oResponse) {
                    YAHOO.log("Failed to process XHR transaction.", "info", "example");
                    if (oResponse.argument.fnLoadComplete != null) {
                        oResponse.argument.fnLoadComplete();
                    }
                },
                argument: {
                    root:root,
                    type:type,
                    fnLoadComplete: fnLoadComplete
                },
                timeout: 7000
            };
            YAHOO.util.Connect.asyncRequest('GET', sUrl, callback);
        }*/
    });
})();
(function () {
    /**
     * YUI Library aliases
     */
    var Bubbling = YAHOO.Bubbling;

    /**
     * Alfresco.service.DataListActions implementation
     */
    Alfresco.service.DataListActions = {};
    Alfresco.service.DataListActions.prototype =
    {

    };
})();

(function () {
    Alfresco.module.DataListActions = function () {
        return null;
    };

    Alfresco.module.DataListActions.prototype =
    {

    };
})();
