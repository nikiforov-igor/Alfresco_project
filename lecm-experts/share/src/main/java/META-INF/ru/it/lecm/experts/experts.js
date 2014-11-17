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
 * Experts module.
 *
 * @namespace LogicECM.module
 * @class LogicECM.module.Experts
 */
(function () {

    var Dom = YAHOO.util.Dom;

    LogicECM.module.Experts = function (htmlId) {
        return LogicECM.module.Experts.superclass.constructor.call(
            this,
            "LogicECM.module.Experts",
            htmlId,
            ["button", "container", "connection", "json", "selector"]);
    };

    YAHOO.lang.extend(LogicECM.module.Experts, Alfresco.component.Base, {
        loadService:"lecm/experts/get/byContent",
        getService:"lecm/experts/main",
        table:null,
        button:null,
        cDoc:null,
        globalDataCount:0,

        init:function (formId) {
            this.cDoc = formId;

            var parent = Dom.get(this.id);

            var columnDefs = [
                { key:"login", label:this.msg("control.table.column.login.title"), resizeable:true },
                { key:"fullName", label:this.msg("control.table.column.fullname.title"), resizeable:true, sortable:true},
                { key:"linkRef", label:this.msg("control.table.column.link.title"), resizeable:true }
            ];

            var initialSource = new YAHOO.util.DataSource([]);
            initialSource.responseType = YAHOO.util.DataSource.TYPE_JSARRAY;
            initialSource.responseSchema = {fields:["login", "fullName", "linkRef"]};

            this.table = new YAHOO.widget.DataTable(parent, columnDefs, initialSource, {initialLoad:false});

            this._loadTable();
        },

        _draw:function () {
            var context = this;

            this.button = new YAHOO.widget.Button({
                id:"getExperts",
                type:"button",
                label:this.msg("control.button.get.title"),
                container:"buttons"
            });

            var onButtonClick = function (e) {
                context.button.set('disabled', true);
                context.loadExperts();
            };

            this.button.on("click", onButtonClick);
        },

        addExperts:function (experts) {
            if (experts != null) {
                for (var nodeIndex in experts) {
                    var newRow = {
                        login:experts[nodeIndex].lname,
                        fullName:experts[nodeIndex].fname,
                        linkRef:"<a href='" + window.location.protocol + "//" + window.location.host + Alfresco.constants.URL_PAGECONTEXT + "user/" + experts[nodeIndex].lname + "/profile" + "'>" + this.msg("control.table.body.link.profile") + "</a>"
                    };
                    this.table.addRow(newRow, this.globalDataCount);
                    this.globalDataCount++;
                }
                this.table.render();
            }
        },

        loadExperts:function () {
            var templateUrl = Alfresco.constants.PROXY_URI + this.loadService + "?nodeRef={nodeRef}";
            var serviceUrl = YAHOO.lang.substitute(templateUrl, {
                nodeRef:encodeURI(this.cDoc)
            });

            var callback = {
                success:function (oResponse) {
                    var oExperts = eval("(" + oResponse.responseText + ")");
                    oResponse.argument.context.addExperts(oExperts);
                    oResponse.argument.context.button.set('disabled', false);
                },
                failure:function (o) {
                    alert("Failed to get experts. " + "[" + o.statusText + "]");
                    o.argument.context.button.set('disabled', false);
                },
                argument:{
                    context:this
                }
            };
            YAHOO.util.Connect.asyncRequest('GET', serviceUrl, callback);
        },
        _loadTable:function () {
            var sUrl = Alfresco.constants.PROXY_URI + this.getService;
            if (this.cDoc != null) {
                sUrl += "?nodeRef=" + encodeURI(this.cDoc);
            }
            var callback = {
                success:function (oResponse) {
                    var oResults = eval("(" + oResponse.responseText + ")");
                    oResponse.argument.context.addExperts(oResults);
                    oResponse.argument.context._draw();
                },
                failure:function (oResponse) {
                    alert("Failed to load experts. " + "[" + oResponse.statusText + "]");
                },
                argument:{
                    context:this
                }
            };
            YAHOO.util.Connect.asyncRequest('GET', sUrl, callback);
        }
    });
})();