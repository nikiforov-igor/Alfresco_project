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
 * LogicECM Connection module namespace.
 *
 * @namespace LogicECM
 * @class LogicECM.module.Connection
 */
LogicECM.module.Connection = LogicECM.module.Connection || {};

(function()
{
	var Dom = YAHOO.util.Dom,
		Event = YAHOO.util.Event;


	LogicECM.module.Connection.ConnectedDocuments = function (fieldHtmlId)
	{
		LogicECM.module.Connection.ConnectedDocuments.superclass.constructor.call(this, "LogicECM.module.Connection.ConnectedDocuments", fieldHtmlId, [ "container", "datasource"]);
		this.id = fieldHtmlId;
		this.controlId = fieldHtmlId + "-cntrl";
		return this;
	};

	YAHOO.extend(LogicECM.module.Connection.ConnectedDocuments, Alfresco.component.Base,
		{
			options: {
				primaryDocumentNodeRef: null,

				datagridBublingLabel: null
			},

			id: null,

			controlId: null,

			connectButton: null,

			rootRef: "",

			onReady: function()
			{
				this.connectButton = Alfresco.util.createYUIButton(this, this.controlId + "-add-connection-button", this.onConnect.bind(this), {}, Dom.get(this.controlId + "-add-connection-button"));
			},

			onConnect: function(e, p_obj) {
				var me = this;
				// Intercept before dialog show
				var doBeforeDialogShow = function(p_form, p_dialog) {
					Alfresco.util.populateHTML(
						[ p_dialog.id + "-form-container_h", this.msg("label.connection.add.title") ]
					);

					var primaryDocumentAddedInput = p_dialog.dialog.form['assoc_lecm-connect_primary-document-assoc_added'];
					if (primaryDocumentAddedInput != null) {
						primaryDocumentAddedInput.value = this.options.primaryDocumentNodeRef;
					}
					var primaryDocumentInput = p_dialog.dialog.form['assoc_lecm-connect_primary-document-assoc'];
					if (primaryDocumentInput != null) {
						primaryDocumentInput.value = this.options.primaryDocumentNodeRef;
					}
				};

				var templateUrl = YAHOO.lang.substitute(Alfresco.constants.URL_SERVICECONTEXT + "components/form?itemKind={itemKind}&itemId={itemId}&destination={destination}&mode={mode}&submitType={submitType}&formId={formId}&ignoreNodes={ignoreNodes}&showCancelButton=true",
					{
						itemKind:"type",
						itemId:"lecm-connect:connection",
						destination: this.options.primaryDocumentNodeRef,
						mode:"create",
						formId: this.id + "-create-form",
						submitType:"json",
                        ignoreNodes: this.options.primaryDocumentNodeRef
					});

//				// Using Forms Service, so always create new instance
				var createDetails = new Alfresco.module.SimpleDialog(this.id + "-createDetails");
				createDetails.setOptions(
					{
						width:"50em",
						templateUrl:templateUrl,
						actionUrl:null,
						destroyOnHide:true,
						doBeforeDialogShow:{
							fn:doBeforeDialogShow,
							scope:this
						},
						onSuccess:{
							fn:function (response) {
								if (me.options.datagridBublingLabel != null) {
									YAHOO.Bubbling.fire("dataItemCreated", // обновить данные в гриде
										{
											nodeRef:response.json.persistedObject,
											bubblingLabel:me.options.datagridBublingLabel
										});
								}

								Alfresco.util.PopupManager.displayMessage(
									{
										text:this.msg("message.connection.add.success")
									});
							},
							scope:this
						},
						onFailure:{
							fn:function (response) {
								Alfresco.util.PopupManager.displayMessage(
									{
										text:this.msg("message.connection.add.failure")
									});
							},
							scope:this
						}
					}).show();
			}
		});
})();