if (typeof LogicECM == "undefined" || !LogicECM) {
    LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.EDS = LogicECM.module.EDS || {};

(function () {
    function getRecordIndex(array, element) {
        for (var i = 0; i < array.length; i++) {
            if (array[i].getData().nodeRef == element.nodeRef) {
                return i;
            }
        }
        return -1;
    }
    LogicECM.module.EDS.Evaluators = {

        documentDataTableItemUp: function (rowData) {
                var recordSet = this.widgets.dataTable.getRecordSet();
                var index = getRecordIndex(recordSet.getRecords(), rowData);
                return index > 0;
            },

        documentDataTableItemDown: function (rowData) {
                var recordSet = this.widgets.dataTable.getRecordSet();
                var index = getRecordIndex(recordSet.getRecords(), rowData);
                return index < recordSet.getRecords().length - 1;
            }
    };
})();