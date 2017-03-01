if (typeof LogicECM == "undefined" || !LogicECM) {
	LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.WCalendar = LogicECM.module.WCalendar || {};
LogicECM.module.WCalendar.Absence = LogicECM.module.WCalendar.Absence || {};


(function() {

	LogicECM.module.WCalendar.Absence.Toolbar = function(containerId) {
		return  LogicECM.module.WCalendar.Absence.Toolbar.superclass.constructor.call(
				this,
				"LogicECM.module.WCalendar.Absence.Toolbar",
				containerId,
				["button", "container", "connection", "json", "selector"]);
	};

	YAHOO.lang.extend(LogicECM.module.WCalendar.Absence.Toolbar, Alfresco.component.Base, {
		options: {
			pageId: null
		},
		_createNewEmployeeAbsence: function Absence_newEmployeeAbsence(wantedBubblingLabel) {
			var scope = this;
			return function(event, obj) {
				var dataGrid = LogicECM.module.Base.Util.findComponentByBubblingLabel("LogicECM.module.Base.DataGrid", wantedBubblingLabel);
				var datagridMeta = dataGrid.datagridMeta;
				var destination = datagridMeta.nodeRef;
				var itemType = datagridMeta.itemType;

				var doBeforeDialogShow = function DataGrid_onActionEdit_doBeforeDialogShow(p_form, p_dialog) {
					Alfresco.util.populateHTML(
							[p_dialog.id + "-form-container_h", this.msg("label.absence.create-employee-absence.title")]
							);
				};

				var url = "lecm/components/form" +
						"?itemKind={itemKind}" +
						"&itemId={itemId}" +
						"&formId={formId}" +
						"&destination={destination}" +
						"&mode={mode}" +
						"&submitType={submitType}" +
						"&showCancelButton=true" +
						"&showCaption=false";
				var templateUrl = YAHOO.lang.substitute(Alfresco.constants.URL_SERVICECONTEXT + url, {
					itemKind: "type", //The "kind" of item the form is for, the only supported kind currently is "node".
					itemId: itemType, //The identifier of the item the form is for, this will be different for each "kind" of item, for "node" it will be a NodeRef.
					formId: "createNewEmployeeAbsenceForm", //The form configuration to lookup, refers the id attribute of the form element. If omitted the default form i.e. the form element without an id attribute is used.
					destination: destination, //Provides a destination for any new items created by the form, when present a hidden field is generated with a name of alf_destination.
					mode: "create", //The mode the form will be rendered in, valid values are "view", "edit" and "create", the default is "edit".
					submitType: "json" //The "enctype" to use for the form submission, valid values are "multipart", "json" and "urlencoded", the default is "multipart".
				});

				// Using Forms Service, so always create new instance
				var employeeAbsenceForm = new Alfresco.module.SimpleDialog(scope.id + "-createNewEmployeeAbsenceForm");

				employeeAbsenceForm.setOptions({
					width: "50em",
					templateUrl: templateUrl,
					destroyOnHide: true,
					doBeforeDialogShow: {
						fn: doBeforeDialogShow,
						scope: this
					},
					doBeforeFormSubmit: {
						fn: function() {
							var htmlNodeBegin = YAHOO.util.Dom.get(scope.id + "-createNewEmployeeAbsenceForm_prop_lecm-absence_begin"),
								htmlNodeEnd = YAHOO.util.Dom.get(scope.id + "-createNewEmployeeAbsenceForm_prop_lecm-absence_end"),
								htmlNodeUnlimited = YAHOO.util.Dom.get(scope.id + "-createNewEmployeeAbsenceForm_prop_lecm-absence_unlimited"),
								beginDate = LogicECM.module.Base.Util.dateToUTC0(Alfresco.util.fromISO8601(htmlNodeBegin.value)),
								endDate = htmlNodeUnlimited.checked ? beginDate : LogicECM.module.Base.Util.dateToUTC0(Alfresco.util.fromISO8601(htmlNodeEnd.value));

							htmlNodeBegin.value = Alfresco.util.toISO8601(beginDate);
							htmlNodeEnd.value = Alfresco.util.toISO8601(endDate);

						},
						scope: this
					},
					onSuccess: {
						fn: function DataListToolbar_onNewRow_success(response) {
							YAHOO.Bubbling.fire("dataItemCreated", {
								nodeRef: response.json.persistedObject,
								bubblingLabel: LogicECM.module.WCalendar.Absence.ABSENCE_LABEL
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
				employeeAbsenceForm.show();
			};
		},
		_onToolbarReady: function Absence__onToolbarReady() {
			var scope = this;
			Alfresco.util.createYUIButton(this, "btnCreateNewEmployeeAbsence", this._createNewEmployeeAbsence(LogicECM.module.WCalendar.Absence.ABSENCE_LABEL), {
				label: scope.msg("button.new-employee-absence")
			});
		},
		onReady: function Absence_onReady() {

			Alfresco.logger.info("A new LogicECM.module.WCalendar.Absence.ToolbarProfile has been created");

			this._onToolbarReady();
			YAHOO.util.Dom.setStyle(this.id + "-body", "visibility", "visible");
		}
	});

	LogicECM.module.WCalendar.Absence.ChangeFormFieldsNames = function() {
	};
})();
