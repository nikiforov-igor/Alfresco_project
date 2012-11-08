/**
 * LogicECM root namespace.
 *
 * @namespace LogicECM
 */
// Ensure LogicECM root object exists
if (typeof LogicECM == "undefined" || !LogicECM) {
	var LogicECM = {};
}

/**
 * LogicECM top-level module namespace.
 *
 * @namespace LogicECM
 * @class LogicECM.module
 */
LogicECM.module = LogicECM.module || {};


/**
 * LogicECM Delegation module namespace.
 *
 * @namespace LogicECM
 * @class LogicECM.module.Delegation
 */
LogicECM.module.Delegation = LogicECM.module.Delegation || {};

/**
 * Delegation module.
 *
 * @namespace LogicECM.module
 * @class LogicECM.module.Delegation.Toolbar
 */
(function () {

	LogicECM.module.Delegation.Toolbar = function (containerId) {
		return LogicECM.module.Delegation.Toolbar.superclass.constructor.call(
			this,
			"LogicECM.module.Delegation.Toolbar",
			containerId,
			["button", "container", "connection", "json", "selector"]);
	};

	YAHOO.lang.extend(LogicECM.module.Delegation.Toolbar, Alfresco.component.Base, {

		_createProcuracyBtnClick: function (e, p_obj) {
//		_createProcuracyBtnClick: function (event) {
			Alfresco.util.PopupManager.displayMessage({text: "createProcuracyBtnClick"});

         var datalistMeta = this.modules.dataGrid.datalistMeta,
            destination = datalistMeta.nodeRef,
            itemType = datalistMeta.itemType;

         // Intercept before dialog show
         var doBeforeDialogShow = function DataListToolbar_onNewRow_doBeforeDialogShow(p_form, p_dialog)
         {
            Alfresco.util.populateHTML(
               [ p_dialog.id + "-dialogTitle", this.msg("label.new-row.title") ],
               [ p_dialog.id + "-dialogHeader", this.msg("label.new-row.header") ]
            );
         };

         var templateUrl = YAHOO.lang.substitute(Alfresco.constants.URL_SERVICECONTEXT + "components/form?itemKind={itemKind}&itemId={itemId}&destination={destination}&mode={mode}&submitType={submitType}&showCancelButton=true",
         {
            itemKind: "type",
            itemId: itemType,
            destination: destination,
            mode: "create",
            submitType: "json"
         });

         // Using Forms Service, so always create new instance
         var createRow = new Alfresco.module.SimpleDialog(this.id + "-createRow");

         createRow.setOptions(
         {
            width: "33em",
            templateUrl: templateUrl,
            actionUrl: null,
            destroyOnHide: true,
            doBeforeDialogShow:
            {
               fn: doBeforeDialogShow,
               scope: this
            },
            onSuccess:
            {
               fn: function DataListToolbar_onNewRow_success(response)
               {
                  YAHOO.Bubbling.fire("dataItemCreated",
                  {
                     nodeRef: response.json.persistedObject
                  });

                  Alfresco.util.PopupManager.displayMessage(
                  {
                     text: this.msg("message.new-row.success")
                  });
               },
               scope: this
            },
            onFailure:
            {
               fn: function DataListToolbar_onNewRow_failure(response)
               {
                  Alfresco.util.PopupManager.displayMessage(
                  {
                     text: this.msg("message.new-row.failure")
                  });
               },
               scope: this
            }
         }).show();
		},

		_listProcuraciesBtnClick: function (event) {
			Alfresco.util.PopupManager.displayMessage({text: "listProcuraciesBtnClick"});
			YAHOO.Bubbling.fire("activeDataListChanged", {
				dataList: {itemType: "lecm-ba:procuracy"},
				scrollTo: true
			});
		},

		_onToolbarReady: function () {
			var scope = this;
			var container = YAHOO.util.Dom.get(this.id);
			Alfresco.util.createYUIButton(container, "btnCreateProcuracy", function (e, p_obj) {scope._createProcuracyBtnClick (e, p_obj);}, {label: "создать доверенность"});

			Alfresco.util.createYUIButton(container, "btnListProcuracies", this._listProcuraciesBtnClick, {label: "список доверенностей"});
		},

		onReady: function () {

			Alfresco.logger.info ("A new LogicECM.module.Delegation.Toolbar has been created");

			// Reference to Data Grid component
			this.modules.dataGrid = Alfresco.util.ComponentManager.findFirst("LogicECM.module.Delegation.DataGrid");

			this._onToolbarReady ();
//			YAHOO.util.Event.onContentReady(this.id, this._onToolbarReady);
			YAHOO.util.Dom.setStyle (this.id + "-body", "visibility", "visible");
		}
	});
})();
