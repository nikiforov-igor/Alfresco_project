/**
 * LogicECM root namespace.
 *
 * @namespace LogicECM
 */
// Ensure LogicECM root object exists
if (typeof LogicECM == "undefined" || !LogicECM) {
	var LogicECM = {};
}

(function()
{
	var Dom = YAHOO.util.Dom;


	LogicECM.DocumentConnectionsList = function (fieldHtmlId)
	{
		LogicECM.DocumentConnectionsList.superclass.constructor.call(this, "LogicECM.DocumentConnectionsList", fieldHtmlId, [ "container", "datasource"]);

		YAHOO.Bubbling.on("connectionsUpdate", this.onConnectionsUpdate, this);
		return this;
	};

	YAHOO.extend(LogicECM.DocumentConnectionsList, Alfresco.component.Base,
		{
			options: {
				documentNodeRef: null
			},

			connectButton: null,

			rootRef: null,

			onReady: function()
			{
				this.loadConnectionsFolder();
				this.connectButton = Alfresco.util.createYUIButton(this, "addConnection-button", this.onConnect.bind(this));
			},

			loadConnectionsFolder: function() {
				if (this.options.documentNodeRef != null) {
					var sUrl = Alfresco.constants.PROXY_URI + "/lecm/document/connections/api/folder?documentNodeRef=" + this.options.documentNodeRef;
					var me = this;
					var callback = {
						success:function (oResponse) {
							var oResults = eval("(" + oResponse.responseText + ")");
							if (oResults != null && oResults.nodeRef != null) {
								me.rootRef = oResults.nodeRef;
							}
						},
						failure:function (oResponse) {
							YAHOO.log("Failed to process XHR transaction.", "info", "example");
						},
						argument:{
							context:this
						},
						timeout:10000
					};
					YAHOO.util.Connect.asyncRequest('GET', sUrl, callback);
				}
			},

			onConnect: function(e, p_obj) {
				if (this.rootRef != null) {
					var me = this;
					// Intercept before dialog show
					var doBeforeDialogShow = function(p_form, p_dialog) {
						Alfresco.util.populateHTML(
							[ p_dialog.id + "-form-container_h", this.msg("label.connection.add.title") ]
						);

						var primaryDocumentAddedInput = p_dialog.dialog.form['assoc_lecm-connect_primary-document-assoc_added'];
						if (primaryDocumentAddedInput != null) {
							primaryDocumentAddedInput.value = this.options.documentNodeRef;
						}
						var primaryDocumentInput = p_dialog.dialog.form['assoc_lecm-connect_primary-document-assoc'];
						if (primaryDocumentInput != null) {
							primaryDocumentInput.value = this.options.documentNodeRef;
						}
					};

					var templateUrl = YAHOO.lang.substitute(Alfresco.constants.URL_SERVICECONTEXT + "components/form?itemKind={itemKind}&itemId={itemId}&destination={destination}&mode={mode}&submitType={submitType}&formId={formId}&ignoreNodes={ignoreNodes}&showCancelButton=true",
						{
							itemKind:"type",
							itemId:"lecm-connect:connection",
							destination: this.rootRef,
							mode:"create",
							formId: this.id + "-create-form",
							submitType:"json",
							ignoreNodes: this.options.documentNodeRef
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
									YAHOO.Bubbling.fire("connectionsUpdate");

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
				} else {
					Alfresco.util.PopupManager.displayMessage(
						{
							text:this.msg("message.connection.folder.failure")
						});
				}
			},

			onConnectionsUpdate: function DocumentMembers_onMembersUpdate(layer, args) {
				Alfresco.util.Ajax.request(
					{
						url: Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/document/connections-list",
						dataObj: {
							nodeRef: this.options.documentNodeRef,
							htmlid: this.id + "-" + Alfresco.util.generateDomId()
						},
						successCallback: {
							fn:function(response){
								var container = Dom.get(this.id);
								if (container != null) {
									container.innerHTML = response.serverResponse.responseText;
								}
							},
							scope: this
						},
						failureMessage: this.msg("message.failure"),
						scope: this,
						execScripts: true
					});
			}
		});
})();