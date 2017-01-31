if (typeof LogicECM == "undefined" || !LogicECM) {
	LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.WCalendar = LogicECM.module.WCalendar || {};
LogicECM.module.WCalendar.Calendar = LogicECM.module.WCalendar.Calendar || {};
LogicECM.module.WCalendar.Calendar.Years = LogicECM.module.WCalendar.Calendar.Years || {};

(function() {

	LogicECM.module.WCalendar.Calendar.Years.Toolbar = function(containerId) {
		return  LogicECM.module.WCalendar.Calendar.Years.Toolbar.superclass.constructor.call(
				this,
				"LogicECM.module.WCalendar.Calendar.Years.Toolbar",
				containerId,
				["button", "container", "connection", "json", "selector"]);
	};

	YAHOO.lang.extend(LogicECM.module.WCalendar.Calendar.Years.Toolbar, Alfresco.component.Base, {
		options: {
			pageId: null
		},
		_createNewYear: function(wantedBubblingLabel) {
			var scope = this;
			return function(event, obj) {
				var dataGrid = LogicECM.module.Base.Util.findComponentByBubblingLabel("LogicECM.module.Base.DataGrid", wantedBubblingLabel);
				var datagridMeta = dataGrid.datagridMeta;
				var destination = datagridMeta.nodeRef;
				var itemType = datagridMeta.itemType;

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
					formId: "createNewYearForm", //The form configuration to lookup, refers the id attribute of the form element. If omitted the default form i.e. the form element without an id attribute is used.
					destination: destination, //Provides a destination for any new items created by the form, when present a hidden field is generated with a name of alf_destination.
					mode: "create", //The mode the form will be rendered in, valid values are "view", "edit" and "create", the default is "edit".
					submitType: "json" //The "enctype" to use for the form submission, valid values are "multipart", "json" and "urlencoded", the default is "multipart".
				});

				// Using Forms Service, so always create new instance
				var yearForm = new Alfresco.module.SimpleDialog(scope.id + "-yearForm");

				yearForm.setOptions({
					width: "50em",
					templateUrl: templateUrl,
					destroyOnHide: true,
					onSuccess: {
						fn: function DataListToolbar_onNewRow_success(response) {
							YAHOO.Bubbling.fire("dataItemCreated", {
								nodeRef: response.json.persistedObject,
								bubblingLabel: LogicECM.module.WCalendar.Calendar.YEARS_LABEL
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
				yearForm.show();
			};
		},
		_onToolbarReady: function() {
			var scope = this;
			Alfresco.util.createYUIButton(this, "btnCreateNewYear", this._createNewYear(LogicECM.module.WCalendar.Calendar.YEARS_LABEL), {
				label: scope.msg("button.new-year")
			});
		},
		onReady: function() {
			Alfresco.logger.info("A new LogicECM.module.WCalendar.Calendar.Years.Toolbar has been created");
			this._onToolbarReady();
			YAHOO.util.Dom.setStyle(this.id + "-body", "visibility", "visible");
		}
	});
})();
