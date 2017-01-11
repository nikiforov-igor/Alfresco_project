/**
 * LogicECM root namespace.
 *
 * @namespace LogicECM
 */
// Ensure LogicECM root object exists
if (typeof LogicECM == "undefined" || !LogicECM) {
	LogicECM = {};
}

(function()
{
	var Dom = YAHOO.util.Dom,
		Event = YAHOO.util.Event,
		Bubbling = YAHOO.Bubbling;


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

            doubleClickLock: false,

			graphTreeContainer: null,

			linksContainer: null,

			onReady: function()
			{
				this.loadConnectionsFolder();
				this.connectButton = Alfresco.util.createYUIButton(this, "addConnection-button", this.onConnect.bind(this));

				this.modules.actions = new LogicECM.module.Base.Actions();

				var me = this;
				var fnActionHandler = function DataGrid_fnActionHandler(layer, args)
				{
					var owner = Bubbling.getOwnerByTagName(args[1].anchor, "div");
					if (owner !== null)
					{
						var nodeName = owner.getAttribute('data-name');
						var nodeRef = owner.getAttribute('data-noderef');
						if (typeof me[owner.className] == "function" && nodeName != null && nodeRef != null)
						{
							me[owner.className].call(me, nodeName, nodeRef);
						}
					}
					return true;
				};
				Bubbling.addDefaultAction("list-action-link", fnActionHandler);

				var rows = Dom.getElementsByClassName('detail-list-item');
				for (var i = 0; i < rows.length; i++)
				{
					Event.addListener(rows[i], "mouseover", this.onEventHighlightRow, {row: rows[i]}, this);
					Event.addListener(rows[i], "mouseout", this.onEventUnhighlightRow, {row: rows[i]}, this);
				}

				this.graphTreeContainer = Dom.get(this.id + "-graph-tree");
				this.linksContainer = Dom.get(this.id + "-connections-list-container");

				var viewButtonGroup = new YAHOO.widget.ButtonGroup(this.id + "-view-mode-button-group");
				var buttons = viewButtonGroup.getButtons()
				for (var i = 0; i < buttons.length; i++) {
					buttons[i].addListener("click", this.viewChanged, this, true);
				}
			},

			viewChanged: function(event) {
				if (event.currentTarget.id === this.id + "-view-mode-radiofield-links") {
					this.graphTreeContainer.style.display = "none";
					this.linksContainer.style.display = "block";
				} else if (event.currentTarget.id === this.id + "-view-mode-radiofield-tree") {
					this.linksContainer.style.display = "none";
					this.graphTreeContainer.style.display = "block";
				}
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
                if (this.doubleClickLock) return;
                this.doubleClickLock = true;
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
                        this.doubleClickLock = false;
						p_dialog.dialog.subscribe('destroy', LogicECM.module.Base.Util.formDestructor, {moduleId: p_dialog.id}, this);
					};

					var templateUrl = Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/form";
					var templateRequestParams = {
							itemKind:"type",
							itemId:"lecm-connect:connection",
							destination: this.rootRef,
							mode:"create",
							formId: this.id + "-create-form",
							submitType:"json",
						ignoreNodes: this.options.documentNodeRef,
						showCancelButton: true,
						showCaption: false
					};

					//				// Using Forms Service, so always create new instance
					var createDetails = new Alfresco.module.SimpleDialog(this.id + "-createDetails");
					createDetails.setOptions(
						{
							width:"50em",
							templateUrl:templateUrl,
							templateRequestParams:templateRequestParams,
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
                                    this.doubleClickLock = false;
								},
								scope:this
							},
							onFailure:{
								fn:function (response) {
									Alfresco.util.PopupManager.displayMessage(
										{
											text:this.msg("message.connection.add.failure")
										});
                                    this.doubleClickLock = false;
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
                                    var customRegion = container.parentElement;
                                    customRegion.innerHTML = "";
                                    customRegion.innerHTML = response.serverResponse.responseText;
								}
							},
							scope: this
						},
						failureMessage: this.msg("message.failure"),
						scope: this,
						execScripts: true
					});
			},

			onEventHighlightRow: function(e, itemInfo) {
				Dom.addClass(itemInfo.row, "highlighted");
			},

			onEventUnhighlightRow: function(e, itemInfo) {
				Dom.removeClass(itemInfo.row, "highlighted");
			},

			onActionDelete: function (name, noderef) {
				if (noderef != null && name != null) {
					var me = this;

					var fnActionDeleteConfirm = function DataGridActions__onActionDelete_confirm(nodeRef) {
						this.modules.actions.genericAction(
							{
								success:{
									event:{
										name:"connectionsUpdate"
									},
									message:me.msg("message.delete.success")
								},
								failure:{
									message:me.msg("message.delete.failure")
								},
								webscript:{
									method:Alfresco.util.Ajax.DELETE,
									name:"delete",
									queryString:"full=true"
								},
								config:{
									requestContentType:Alfresco.util.Ajax.JSON,
									dataObj:{
										nodeRefs:[nodeRef]
									}
								}
							});
					};

					Alfresco.util.PopupManager.displayPrompt(
						{
							title:this.msg("message.confirm.delete.title"),
							text: this.msg("message.confirm.delete.description", '"' + name + '"'),
							buttons:[
								{
									text:this.msg("button.delete"),
									handler:function () {
										this.destroy();
										fnActionDeleteConfirm.call(me, noderef);
									}
								},
								{
									text:this.msg("button.cancel"),
									handler:function () {
										this.destroy();
									},
									isDefault:true
								}
							]
						});
				}
			}
		});
})();