if (typeof LogicECM == "undefined" || !LogicECM) {
	var LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.WCalendar = LogicECM.module.WCalendar || {};
LogicECM.module.WCalendar.Calendar = LogicECM.module.WCalendar.Calendar || {};
LogicECM.module.WCalendar.Calendar.SpecialDays = LogicECM.module.WCalendar.Calendar.SpecialDays || {};

(function() {

	LogicECM.module.WCalendar.Calendar.SpecialDays.Toolbar = function(containerId) {
		return  LogicECM.module.WCalendar.Calendar.SpecialDays.Toolbar.superclass.constructor.call(
				this,
				"LogicECM.module.WCalendar.Calendar.SpecialDays.Toolbar",
				containerId,
				["button", "container", "connection", "json", "selector"]);
	};

	YAHOO.lang.extend(LogicECM.module.WCalendar.Calendar.SpecialDays.Toolbar, Alfresco.component.Base, {
		options: {
			pageId: null
		},
		_createSpecialDay: function WCalendarToolbar_createSpecialDay(wantedBubblingLabel) {
			var scope = this;
			return function(event, obj) {
				var dataGrid = LogicECM.module.Base.Util.findComponentByBubblingLabel("LogicECM.module.Base.DataGrid", wantedBubblingLabel);
				var datagridMeta = dataGrid.datagridMeta;
				var destination = datagridMeta.nodeRef;
				var itemType = datagridMeta.itemType;
				var headerLabel;

				if (wantedBubblingLabel.toString() == LogicECM.module.WCalendar.Calendar.WORKING_DAYS_LABEL.toString()) {
					headerLabel = "label.calendar.create-new-working.title";
				} else if (wantedBubblingLabel.toString() == LogicECM.module.WCalendar.Calendar.NON_WORKING_DAYS_LABEL.toString()) {
					headerLabel = "label.calendar.create-new-non-working.title";
				}

				var doBeforeDialogShow = function DataGrid_onActionEdit_doBeforeDialogShow(p_form, p_dialog) {
					Alfresco.util.populateHTML(
							[p_dialog.id + "-form-container_h", this.msg(headerLabel)]
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
					formId: "createNewSpecialDayForm", //The form configuration to lookup, refers the id attribute of the form element. If omitted the default form i.e. the form element without an id attribute is used.
					destination: destination, //Provides a destination for any new items created by the form, when present a hidden field is generated with a name of alf_destination.
					mode: "create", //The mode the form will be rendered in, valid values are "view", "edit" and "create", the default is "edit".
					submitType: "json" //The "enctype" to use for the form submission, valid values are "multipart", "json" and "urlencoded", the default is "multipart".
				});

				// Using Forms Service, so always create new instance
				var workingDayForm = new Alfresco.module.SimpleDialog(scope.id + "-workingDayForm");

				workingDayForm.setOptions({
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
								bubblingLabel: wantedBubblingLabel
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
				workingDayForm.show();
			};
		},
		_onToolbarReady: function() {
			var scope = this;
			this.widgets.CreateWorkingDayBtn = Alfresco.util.createYUIButton(this, "btnCreateWorkingDay", this._createSpecialDay(LogicECM.module.WCalendar.Calendar.WORKING_DAYS_LABEL), {
				label: scope.msg("button.new-working-day"),
				value: "create",
				disabled: true
			});
			this.widgets.CreateNonWorkingDayBtn = Alfresco.util.createYUIButton(this, "btnCreateNonWorkingDay", this._createSpecialDay(LogicECM.module.WCalendar.Calendar.NON_WORKING_DAYS_LABEL), {
				label: scope.msg("button.new-non-working-day"),
				value: "create",
				disabled: true
			});
			YAHOO.Bubbling.on("enableAddButton", function() {
				this.widgets.CreateWorkingDayBtn.set("disabled", false);
				this.widgets.CreateNonWorkingDayBtn.set("disabled", false);
			}, this);

		},
		onReady: function() {
			Alfresco.logger.info("A new window.LogicECM.module.WCalendar.Calendar.SpecialDays.Toolbar has been created");
			this._onToolbarReady();
			YAHOO.util.Dom.setStyle(this.id + "-body", "visibility", "visible");
		}
	});
})();
