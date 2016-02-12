if (typeof LogicECM == "undefined" || !LogicECM) {
	LogicECM = {};
}

LogicECM.module = LogicECM.module || {};

LogicECM.module.Routes = LogicECM.module.Routes || {};

(function () {

	function hasNodeRef(element) {
		return element.getData().nodeRef == this.nodeRef;
	}

	LogicECM.module.Routes.Evaluators = {

		stageItemUp: function (rowData) {
			var recordSet = this.widgets.dataTable.getRecordSet();
			var records = recordSet.getRecords();
			var index = records.findIndex(hasNodeRef, rowData);
			return index > 0;
		},

		stageItemDown: function (rowData) {
			var recordSet = this.widgets.dataTable.getRecordSet();
			var records = recordSet.getRecords();
			var index = records.findIndex(hasNodeRef, rowData);
			return index < records.length - 1;
		},

		stageItemEdit: function (rowData) {
			return true;
		},

		stageItemDelete: function (rowData) {
			return true;
		}
	};
})();
