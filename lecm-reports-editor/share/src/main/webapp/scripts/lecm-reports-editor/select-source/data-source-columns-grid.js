LogicECM.module.ReportsEditor.ColumnsGrid = function (containerId) {
    LogicECM.module.ReportsEditor.ColumnsGrid.superclass.constructor.call(this, containerId);
    this.activeSourceColumns = [];

    return this;
};

YAHOO.lang.extend(LogicECM.module.ReportsEditor.ColumnsGrid, LogicECM.module.Base.DataGrid);

YAHOO.lang.augmentObject(LogicECM.module.ReportsEditor.ColumnsGrid.prototype, {

    activeSourceColumns: [],

    onActionAdd: function (item) {
        // добавляем столбец в набор данных (копируем в отчет)
        YAHOO.Bubbling.fire("copyColumnToReportSource", {
            sourceId: this.datagridMeta.nodeRef,
            columnId: item.nodeRef
        });
    },

    onActionRemove: function (p_items, owner) {
        this.onDelete(p_items, owner, {fullDelete: true, trash: false}, function () {
            YAHOO.Bubbling.fire("updateReportSourceColumns");
        }, null);
    }
}, true);