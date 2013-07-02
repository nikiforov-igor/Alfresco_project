if (typeof LogicECM == "undefined" || !LogicECM) {
	var LogicECM = {};
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
				var dataGrid = this.modules.dataGrid,
					datagridMeta = dataGrid.datagridMeta,
					destination = datagridMeta.nodeRef,
					itemType = datagridMeta.itemType,
					commonScheduleForm = new Alfresco.module.SimpleDialog(scope.id + "-commonScheduleForm"),
					ignoreNodes = scope._getIgnoreNodes(datagrid.widgets.dataTable);

				commonScheduleForm.setOptions({
					width: "50em",
					templateUrl: "lecm/components/form",
					templateRequestParams: {
						ignoreNodes: ignoreNodes.join(),
						itemKind: "type", //The "kind" of item the form is for, the only supported kind currently is "node".
						itemId: itemType, //The identifier of the item the form is for, this will be different for each "kind" of item, for "node" it will be a NodeRef.
						formId: "createNewCommonScheduleForm", //The form configuration to lookup, refers the id attribute of the form element. If omitted the default form i.e. the form element without an id attribute is used.
						destination: destination, //Provides a destination for any new items created by the form, when present a hidden field is generated with a name of alf_destination.
						mode: "create", //The mode the form will be rendered in, valid values are "view", "edit" and "create", the default is "edit".
						submitType: "json", //The "enctype" to use for the form submission, valid values are "multipart", "json" and "urlencoded", the default is "multipart".
						showCancelButton: true
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
					ignoreNodes = scope._getIgnoreNodes(datagrid.widgets.dataTable);


				specialScheduleForm.setOptions({
					width: "50em",
					templateUrl: "lecm/components/form",
					templateRequestParams: {
						ignoreNodes: ignoreNodes.join(),
						itemKind: "type", //The "kind" of item the form is for, the only supported kind currently is "node".
						itemId: itemType, //The identifier of the item the form is for, this will be different for each "kind" of item, for "node" it will be a NodeRef.
						formId: "createNewSpecialScheduleForm", //The form configuration to lookup, refers the id attribute of the form element. If omitted the default form i.e. the form element without an id attribute is used.
						destination: destination, //Provides a destination for any new items created by the form, when present a hidden field is generated with a name of alf_destination.
						mode: "create", //The mode the form will be rendered in, valid values are "view", "edit" and "create", the default is "edit".
						submitType: "json", //The "enctype" to use for the form submission, valid values are "multipart", "json" and "urlencoded", the default is "multipart".
						showCancelButton: true
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
