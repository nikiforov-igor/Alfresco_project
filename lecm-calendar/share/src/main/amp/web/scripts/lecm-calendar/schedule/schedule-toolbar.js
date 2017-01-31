if (typeof LogicECM == "undefined" || !LogicECM) {
	LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.WCalendar = LogicECM.module.WCalendar || {};
LogicECM.module.WCalendar.Schedule = LogicECM.module.WCalendar.Schedule || {};


(function() {
	"use strict";
	LogicECM.module.WCalendar.Schedule.Toolbar = function(containerId) {
		return  LogicECM.module.WCalendar.Schedule.Toolbar.superclass.constructor.call(this, "LogicECM.module.WCalendar.Schedule.Toolbar", containerId);
	};

	YAHOO.lang.extend(LogicECM.module.WCalendar.Schedule.Toolbar, LogicECM.module.Base.Toolbar);

	YAHOO.lang.augmentObject (LogicECM.module.WCalendar.Schedule.Toolbar.prototype, {
		options: {
			pageId: null
		},

		/**
		 * построение массива NodeRef-ов которые исключаются из набора доступных для выбора, при добавлении нового графика работы
		 * @param {type} dataTable таблица с уже существующими графиками работы
		 * @returns {Array} массив NodeRef которые необходимо исключить из выбора
		 */
		_getIgnoreNodes: function Schedule_get(dataTable) {
			var i = 0,
				records = dataTable.getRecordSet().getRecords(),
				recordsLength = records.length,
				recordData = null,
				ignoreNodes = [];
			// Перебираем все строки датагрида
			for (var i = 0; i < recordsLength; ++i) {
				recordData = records[i].getData("itemData");
				ignoreNodes.push(recordData["assoc_lecm-sched_sched-employee-link-assoc"].value);
			}
			return ignoreNodes;
		},

		_createNewCommonSchedule: function Schedule_newCommonSchedule(wantedBubblingLabel) {
			var scope = this;
			return function(event, obj) {
				var dataGrid = this.modules.dataGrid;
				var	datagridMeta = dataGrid.datagridMeta;
				var	destination = datagridMeta.nodeRef;
				var	itemType = datagridMeta.itemType;
				var	commonScheduleForm = new Alfresco.module.SimpleDialog(scope.id + "-commonScheduleForm");
				var	ignoreNodes = scope._getIgnoreNodes(dataGrid.widgets.dataTable);

				commonScheduleForm.setOptions({
					width: "50em",
					templateUrl: "lecm/components/form",
					templateRequestParams: {
						ignoreNodes: ignoreNodes.join(), // ignoreNodes нужны!
						itemKind: "type",
						itemId: itemType,
						formId: "createNewCommonScheduleForm",
						destination: destination,
						mode: "create",
						submitType: "json",
						showCancelButton: true,
						showCaption: false
					},
					destroyOnHide: true,
					doBeforeDialogShow: {
						fn: function DataGrid_onActionEdit_doBeforeDialogShow(p_form, p_dialog) {
							p_dialog.dialog.setHeader( this.msg("label.schedule.create-new-common.title") );
						},
						scope: scope
					},
					onSuccess: {
						fn: function DataListToolbar_onNewRow_success(response) {
							YAHOO.Bubbling.fire("dataItemCreated", {
								nodeRef: response.json.persistedObject,
								bubblingLabel: LogicECM.module.WCalendar.Schedule.SCHEDULE_LABEL
							});

							Alfresco.util.PopupManager.displayMessage({
								text: this.msg("message.new-row.success")
							});
						},
						scope: scope
					},
					onFailure: {
						fn: function DataListToolbar_onNewRow_failure(response) {
							// Спасаем тонущий popup
							Alfresco.util.PopupManager.zIndex = YAHOO.util.Dom.get(this.id + '-commonScheduleForm-form-container_c').style['z-index'] + 1;
							Alfresco.util.PopupManager.displayMessage({
								text: this.msg("message.new-row.failure")
							});
						},
						scope: scope
					}
				});
				commonScheduleForm.show();
			};
		},

		_createNewSpecialSchedule: function Schedule_newSpecialSchedule(wantedBubblingLabel) {
			var scope = this;
			return function(event, obj) {
				var dataGrid = this.modules.dataGrid,
					datagridMeta = dataGrid.datagridMeta,
					destination = datagridMeta.nodeRef,
					itemType = datagridMeta.itemType,
					specialScheduleForm = new Alfresco.module.SimpleDialog(scope.id + "-specialScheduleForm"),
					ignoreNodes = scope._getIgnoreNodes(dataGrid.widgets.dataTable);


				specialScheduleForm.setOptions({
					width: "50em",
					templateUrl: "lecm/components/form",
					templateRequestParams: {
						ignoreNodes: ignoreNodes.join(), // ignoreNodes нужны!
						itemKind: "type",
						itemId: itemType,
						formId: "createNewSpecialScheduleForm",
						destination: destination,
						mode: "create",
						submitType: "json",
						showCancelButton: true,
						showCaption: false
					},
					actionUrl: Alfresco.constants.PROXY_URI_RELATIVE + "lecm/wcalendar/schedule/set/createSpecialSchedule",
					destroyOnHide: true,
					doBeforeDialogShow: {
						fn: function DataGrid_onActionEdit_doBeforeDialogShow(p_form, p_dialog) {
							p_dialog.dialog.setHeader( this.msg("label.schedule.create-new-special.title") );
						},
						scope: scope
					},
					onSuccess: {
						fn: function DataListToolbar_onNewRow_success(response) {
							YAHOO.Bubbling.fire("dataItemCreated", {
								nodeRef: response.json.persistedObject,
								bubblingLabel: LogicECM.module.WCalendar.Schedule.SCHEDULE_LABEL
							});

							Alfresco.util.PopupManager.displayMessage({
								text: this.msg("message.new-row.success")
							});
						},
						scope: scope
					},
					onFailure: {
						fn: function DataListToolbar_onNewRow_failure(response) {
							// Спасаем тонущий popup
							Alfresco.util.PopupManager.zIndex = YAHOO.util.Dom.get(this.id + '-specialScheduleForm-form-container_c').style['z-index'] + 1;
							Alfresco.util.PopupManager.displayMessage({
								text: this.msg("message.new-row.failure")
							});
						},
						scope: scope
					}
				});
				specialScheduleForm.show();
			};
		},

		_initButtons: function () {
			var scope = this;
			Alfresco.util.createYUIButton(this, "btnCreateNewCommonSchedule", this._createNewCommonSchedule(LogicECM.module.WCalendar.Schedule.SCHEDULE_LABEL), {
				label: scope.msg("button.new-common-schedule")
			});
			Alfresco.util.createYUIButton(this, "btnCreateNewSpecialSchedule", this._createNewSpecialSchedule(LogicECM.module.WCalendar.Schedule.SCHEDULE_LABEL), {
				label: scope.msg("button.new-special-schedule")
			});
		}

	}, true);

	LogicECM.module.WCalendar.Schedule.ChangeFormFieldsNames = function() {
	};
})();
