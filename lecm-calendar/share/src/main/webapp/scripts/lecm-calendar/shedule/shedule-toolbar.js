if (typeof LogicECM == "undefined" || !LogicECM) {
	var LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.WCalendar = LogicECM.module.WCalendar || {};
LogicECM.module.WCalendar.Shedule = LogicECM.module.WCalendar.Shedule || {};


(function() {

	LogicECM.module.WCalendar.Shedule.Toolbar = function(containerId) {
		return  LogicECM.module.WCalendar.Shedule.Toolbar.superclass.constructor.call(
			this,
			"LogicECM.module.WCalendar.Shedule.Toolbar",
			containerId,
			["button", "container", "connection", "json", "selector"]);
	};

	YAHOO.lang.extend(LogicECM.module.WCalendar.Shedule.Toolbar, Alfresco.component.Base, {

		options: {
			pageId: null
		},

		_createNewCommonShedule: function Shedule_newCommonShedule(wantedBubblingLabel) {
			var scope = this;
			return function(event, obj) {
				var dataGrid = LogicECM.module.WCalendar.Utils.findGridByName("LogicECM.module.Base.DataGrid", wantedBubblingLabel);
				var datagridMeta = dataGrid.datagridMeta;
				var destination = datagridMeta.nodeRef;
				var itemType = datagridMeta.itemType;

				var doBeforeDialogShow = function DataGrid_onActionEdit_doBeforeDialogShow(p_form, p_dialog) {
					Alfresco.util.populateHTML(
						[ p_dialog.id + "-form-container_h", this.msg("label.shedule.create-new-common.title") ]
						);
				};

				var url = "components/form"
				+ "?itemKind={itemKind}"
				+ "&itemId={itemId}"
				+ "&formId={formId}"
				+ "&destination={destination}"
				+ "&mode={mode}"
				+ "&submitType={submitType}"
				+ "&showCancelButton=true";
				var templateUrl = YAHOO.lang.substitute(Alfresco.constants.URL_SERVICECONTEXT + url, {
					itemKind: "type", //The "kind" of item the form is for, the only supported kind currently is "node".
					itemId: itemType, //The identifier of the item the form is for, this will be different for each "kind" of item, for "node" it will be a NodeRef.
					formId: "createNewCommonSheduleForm",//The form configuration to lookup, refers the id attribute of the form element. If omitted the default form i.e. the form element without an id attribute is used.
					destination: destination, //Provides a destination for any new items created by the form, when present a hidden field is generated with a name of alf_destination.
					mode: "create", //The mode the form will be rendered in, valid values are "view", "edit" and "create", the default is "edit".
					submitType: "json" //The "enctype" to use for the form submission, valid values are "multipart", "json" and "urlencoded", the default is "multipart".
				});

				// Using Forms Service, so always create new instance
				var commonSheduleForm = new Alfresco.module.SimpleDialog(scope.id + "-commonSheduleForm");

				commonSheduleForm.setOptions({
					width: "50em",
					templateUrl: templateUrl,
					destroyOnHide: true,
					doBeforeDialogShow:{
						fn:doBeforeDialogShow,
						scope:this
					},
					onSuccess: {
						fn: function DataListToolbar_onNewRow_success(response) {
							YAHOO.Bubbling.fire("dataItemCreated", {
								nodeRef: response.json.persistedObject,
								bubblingLabel: LogicECM.module.WCalendar.Shedule.SHEDULE_LABEL
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
				commonSheduleForm.show();
			}
		},
		
		_createNewSpecialShedule: function Shedule_newSpecialShedule(wantedBubblingLabel) {
			var scope = this;
			return function(event, obj) {
				var dataGrid = LogicECM.module.WCalendar.Utils.findGridByName("LogicECM.module.Base.DataGrid", wantedBubblingLabel);
				var datagridMeta = dataGrid.datagridMeta;
				var destination = datagridMeta.nodeRef;
				var itemType = datagridMeta.itemType;

				var doBeforeDialogShow = function DataGrid_onActionEdit_doBeforeDialogShow(p_form, p_dialog) {
					Alfresco.util.populateHTML(
						[ p_dialog.id + "-form-container_h", this.msg("label.shedule.create-new-special.title") ]
						);
				};

				var url = "components/form"
				+ "?itemKind={itemKind}"
				+ "&itemId={itemId}"
				+ "&formId={formId}"
				+ "&destination={destination}"
				+ "&mode={mode}"
				+ "&submitType={submitType}"
				+ "&showCancelButton=true";
				var templateUrl = YAHOO.lang.substitute(Alfresco.constants.URL_SERVICECONTEXT + url, {
					itemKind: "type", //The "kind" of item the form is for, the only supported kind currently is "node".
					itemId: itemType, //The identifier of the item the form is for, this will be different for each "kind" of item, for "node" it will be a NodeRef.
					formId: "createNewSpecialSheduleForm",//The form configuration to lookup, refers the id attribute of the form element. If omitted the default form i.e. the form element without an id attribute is used.
					destination: destination, //Provides a destination for any new items created by the form, when present a hidden field is generated with a name of alf_destination.
					mode: "create", //The mode the form will be rendered in, valid values are "view", "edit" and "create", the default is "edit".
					submitType: "json" //The "enctype" to use for the form submission, valid values are "multipart", "json" and "urlencoded", the default is "multipart".
				});

				// Using Forms Service, so always create new instance
				var commonSheduleForm = new Alfresco.module.SimpleDialog(scope.id + "-specialSheduleForm");

				commonSheduleForm.setOptions({
					actionUrl: Alfresco.constants.PROXY_URI_RELATIVE + "lecm/wcalendar/shedule/set/createSpecialShedule",
					width: "50em",
					templateUrl: templateUrl,
					destroyOnHide: true,
					doBeforeDialogShow:{
						fn:doBeforeDialogShow,
						scope:this
					},
					doBeforeFormSubmit: {
						fn: function() {
							htmlNodeStart = YAHOO.util.Dom.get(scope.id + "-specialSheduleForm_prop_lecm-shed_time-limit-start-cntrl-date");
							if (htmlNodeStart) {
								htmlNodeStart.name = "prop_lecm-shed_time-limit-start";
							}
							htmlNodeEnd = YAHOO.util.Dom.get(scope.id + "-specialSheduleForm_prop_lecm-shed_time-limit-end-cntrl-date");
							if (htmlNodeStart) {
								htmlNodeEnd.name = "prop_lecm-shed_time-limit-end";
							}
						},
						scope: this
					},
					onSuccess: {
						fn: function DataListToolbar_onNewRow_success(response) {
							YAHOO.Bubbling.fire("dataItemCreated", {
								nodeRef: response.json.persistedObject,
								bubblingLabel: LogicECM.module.WCalendar.Shedule.SHEDULE_LABEL
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
				commonSheduleForm.show();
			}
			
		},

		_onToolbarReady: function Shedule__onToolbarReady() {
			var scope = this;
			Alfresco.util.createYUIButton(this, "btnCreateNewCommonShedule", this._createNewCommonShedule(LogicECM.module.WCalendar.Shedule.SHEDULE_LABEL), {
				label: scope.msg("button.new-common-shedule")
			});
			Alfresco.util.createYUIButton(this, "btnCreateNewSpecialShedule", this._createNewSpecialShedule(LogicECM.module.WCalendar.Shedule.SHEDULE_LABEL), {
				label: scope.msg("button.new-special-shedule")
			});
		},

		onReady: function Shedule_onReady() {

			Alfresco.logger.info("A new LogicECM.module.WCalendar.Shedule.Toolbar has been created");

			// Reference to Data Grid component
			//			this.modules.dataGrid = Alfresco.util.ComponentManager.findFirst("LogicECM.module.Base.DataGrid");

			this._onToolbarReady();
			YAHOO.util.Dom.setStyle(this.id + "-body", "visibility", "visible");
		}
	});

	LogicECM.module.WCalendar.Shedule.ChangeFormFieldsNames = function() {};
})();
