if (typeof LogicECM == "undefined" || !LogicECM) {
	var LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.WCalendar = LogicECM.module.WCalendar || {};
LogicECM.module.WCalendar.Schedule = LogicECM.module.WCalendar.Schedule || {};


(function() {

	LogicECM.module.WCalendar.Schedule.Toolbar = function(containerId) {
		return  LogicECM.module.WCalendar.Schedule.Toolbar.superclass.constructor.call(
				this,
				"LogicECM.module.WCalendar.Schedule.Toolbar",
				containerId,
				["button", "container", "connection", "json", "selector"]);
	};

	YAHOO.lang.extend(LogicECM.module.WCalendar.Schedule.Toolbar, Alfresco.component.Base, {
		options: {
			pageId: null
		},
		_createNewCommonSchedule: function Schedule_newCommonSchedule(wantedBubblingLabel) {
			var scope = this;
			return function(event, obj) {
				var dataGrid = LogicECM.module.Base.Util.findComponentByBubblingLabel("LogicECM.module.Base.DataGrid", wantedBubblingLabel);
				var datagridMeta = dataGrid.datagridMeta;
				var destination = datagridMeta.nodeRef;
				var itemType = datagridMeta.itemType;

				var doBeforeDialogShow = function DataGrid_onActionEdit_doBeforeDialogShow(p_form, p_dialog) {
					Alfresco.util.populateHTML(
							[p_dialog.id + "-form-container_h", this.msg("label.schedule.create-new-common.title")]
							);
				};

				var url = "lecm/components/form" +
						"?itemKind={itemKind}" +
						"&itemId={itemId}" +
						"&formId={formId}" +
						"&destination={destination}" +
						"&mode={mode}" +
						"&submitType={submitType}" +
						"&showCancelButton=true";
				var templateUrl = YAHOO.lang.substitute(Alfresco.constants.URL_SERVICECONTEXT + url, {
					itemKind: "type", //The "kind" of item the form is for, the only supported kind currently is "node".
					itemId: itemType, //The identifier of the item the form is for, this will be different for each "kind" of item, for "node" it will be a NodeRef.
					formId: "createNewCommonScheduleForm", //The form configuration to lookup, refers the id attribute of the form element. If omitted the default form i.e. the form element without an id attribute is used.
					destination: destination, //Provides a destination for any new items created by the form, when present a hidden field is generated with a name of alf_destination.
					mode: "create", //The mode the form will be rendered in, valid values are "view", "edit" and "create", the default is "edit".
					submitType: "json" //The "enctype" to use for the form submission, valid values are "multipart", "json" and "urlencoded", the default is "multipart".
				});

				// Using Forms Service, so always create new instance
				var commonScheduleForm = new Alfresco.module.SimpleDialog(scope.id + "-commonScheduleForm");

				commonScheduleForm.setOptions({
					width: "50em",
					templateUrl: templateUrl,
					destroyOnHide: true,
					doBeforeDialogShow: {
						fn: doBeforeDialogShow,
						scope: this
					},
					onSuccess: {
						fn: function DataListToolbar_onNewRow_success(response) {
							YAHOO.Bubbling.fire("dataItemCreated", {
								nodeRef: response.json.persistedObject,
								bubblingLabel: LogicECM.module.WCalendar.Schedule.SCHEDULE_LABEL
							});

							Alfresco.util.PopupManager.displayMessage({
								text: scope.msg("message.new-row.success")
							});
						},
						scope: scope
					},
					onFailure: {
						fn: function DataListToolbar_onNewRow_failure(response) {
							Alfresco.util.PopupManager.displayMessage({
								text: scope.msg("message.new-row.failure")
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
				var dataGrid = LogicECM.module.Base.Util.findComponentByBubblingLabel("LogicECM.module.Base.DataGrid", wantedBubblingLabel);
				var datagridMeta = dataGrid.datagridMeta;
				var destination = datagridMeta.nodeRef;
				var itemType = datagridMeta.itemType;

				var doBeforeDialogShow = function DataGrid_onActionEdit_doBeforeDialogShow(p_form, p_dialog) {
					Alfresco.util.populateHTML(
							[p_dialog.id + "-form-container_h", this.msg("label.schedule.create-new-special.title")]
							);
				};

				var url = "lecm/components/form" +
						"?itemKind={itemKind}" +
						"&itemId={itemId}" +
						"&formId={formId}" +
						"&destination={destination}" +
						"&mode={mode}" +
						"&submitType={submitType}" +
						"&showCancelButton=true";
				var templateUrl = YAHOO.lang.substitute(Alfresco.constants.URL_SERVICECONTEXT + url, {
					itemKind: "type", //The "kind" of item the form is for, the only supported kind currently is "node".
					itemId: itemType, //The identifier of the item the form is for, this will be different for each "kind" of item, for "node" it will be a NodeRef.
					formId: "createNewSpecialScheduleForm", //The form configuration to lookup, refers the id attribute of the form element. If omitted the default form i.e. the form element without an id attribute is used.
					destination: destination, //Provides a destination for any new items created by the form, when present a hidden field is generated with a name of alf_destination.
					mode: "create", //The mode the form will be rendered in, valid values are "view", "edit" and "create", the default is "edit".
					submitType: "json" //The "enctype" to use for the form submission, valid values are "multipart", "json" and "urlencoded", the default is "multipart".
				});

				// Using Forms Service, so always create new instance
				var commonScheduleForm = new Alfresco.module.SimpleDialog(scope.id + "-specialScheduleForm");

				commonScheduleForm.setOptions({
					actionUrl: Alfresco.constants.PROXY_URI_RELATIVE + "lecm/wcalendar/schedule/set/createSpecialSchedule",
					width: "50em",
					templateUrl: templateUrl,
					destroyOnHide: true,
					doBeforeDialogShow: {
						fn: doBeforeDialogShow,
						scope: this
					},
					onSuccess: {
						fn: function DataListToolbar_onNewRow_success(response) {
							YAHOO.Bubbling.fire("dataItemCreated", {
								nodeRef: response.json.persistedObject,
								bubblingLabel: LogicECM.module.WCalendar.Schedule.SCHEDULE_LABEL
							});

							Alfresco.util.PopupManager.displayMessage({
								text: scope.msg("message.new-row.success")
							});
						},
						scope: scope
					},
					onFailure: {
						fn: function DataListToolbar_onNewRow_failure(response) {
							Alfresco.util.PopupManager.displayMessage({
								text: scope.msg("message.new-row.failure")
							});
						},
						scope: scope
					}
				});
				commonScheduleForm.show();
			};
		},
		_onToolbarReady: function Schedule__onToolbarReady() {
			var scope = this;
			Alfresco.util.createYUIButton(this, "btnCreateNewCommonSchedule", this._createNewCommonSchedule(LogicECM.module.WCalendar.Schedule.SCHEDULE_LABEL), {
				label: scope.msg("button.new-common-schedule")
			});
			Alfresco.util.createYUIButton(this, "btnCreateNewSpecialSchedule", this._createNewSpecialSchedule(LogicECM.module.WCalendar.Schedule.SCHEDULE_LABEL), {
				label: scope.msg("button.new-special-schedule")
			});
		},
		onReady: function Schedule_onReady() {
			Alfresco.logger.info("A new LogicECM.module.WCalendar.Schedule.Toolbar has been created");
			this._onToolbarReady();
			YAHOO.util.Dom.setStyle(this.id + "-body", "visibility", "visible");
		}
	});

	LogicECM.module.WCalendar.Schedule.ChangeFormFieldsNames = function() {
	};
})();
