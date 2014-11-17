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
 * LogicECM Connection module namespace.
 *
 * @namespace LogicECM
 * @class LogicECM.module.Connection
 */
LogicECM.module.Connection = LogicECM.module.Connection || {};

(function()
{
    var Dom = YAHOO.util.Dom,
        Bubbling = YAHOO.Bubbling,
        Event = YAHOO.util.Event;


    LogicECM.module.Connection.TempConnection = function LogicECM_module_Connection_TempConnection(fieldHtmlId)
    {
        LogicECM.module.Connection.TempConnection.superclass.constructor.call(this, "LogicECM.module.Connection.TempConnection", fieldHtmlId, [ "container", "datasource"]);
        this.selectItemId = fieldHtmlId + "-added";
        this.removedItemId = fieldHtmlId + "-removed";
        this.controlId = fieldHtmlId;
        this.currentDisplayValueId = fieldHtmlId + "-currentValueDisplay";

        Bubbling.on("datagridVisible", this.init, this);
        return this;
    };

    YAHOO.extend(LogicECM.module.Connection.TempConnection, Alfresco.component.Base,
        {
            options: {
                bubblingId: null,
                datagrid: null
            },

            selectItemId: null,
            removedItemId: null,
            currentDisplayValueId: null,
            controlId: null,

            init: function function_init(layer, args) {
                var datagrid = args[1];
                if (this.options.bubblingId === datagrid.options.bubblingLabel) {
                    this.options.datagrid.widgets.dataTable.subscribe("renderEvent", this.onDatagridChanged.bind(this), this, true);
                }
            },

            onDatagridChanged: function () {
                var records = this.options.datagrid.widgets.dataTable.getRecordSet().getRecords();
                var result = [];
                for (var i in records) {
                    var record = records[i].getData("nodeRef");
                    result.push(record);
                }
                Dom.get(this.selectItemId).value = result.join(",");
            }

        });
})();