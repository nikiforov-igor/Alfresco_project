if (typeof LogicECM == "undefined" || !LogicECM) {
    LogicECM = {};
}

LogicECM.module = LogicECM.module || {};

LogicECM.module.ARM = LogicECM.module.ARM|| {};

(function()
{

	LogicECM.module.ARM.Toolbar = function(htmlId)
	{
		LogicECM.module.ARM.Toolbar.superclass.constructor.call(this, "LogicECM.module.ARM.Toolbar", htmlId);

		YAHOO.Bubbling.on("activeGridChanged", this.onSelectTreeNode, this);
		return this;
	};

	/**
	 * Extend from Alfresco.component.Base
	 */
	YAHOO.extend(LogicECM.module.ARM.Toolbar, LogicECM.module.Base.Toolbar);

	/**
	 * Augment prototype with main class implementation, ensuring overwrite is enabled
	 */
	YAHOO.lang.augmentObject(LogicECM.module.ARM.Toolbar.prototype,
		{
			node: null,
			editDialogOpening: false,

			importFromDialog: null,
			importInfoDialog: null,
			importErrorDialog: null,

			_initButtons: function () {
				this.toolbarButtons["defaultActive"].newRowButton = Alfresco.util.createYUIButton(this, "newRowButton", this.onNewRow);

				this.toolbarButtons["defaultActive"].newReportsNode = Alfresco.util.createYUIButton(this, "newReportsNodeButton", this.onNewReportNode);

				this.toolbarButtons["defaultActive"].newHtmlNode = Alfresco.util.createYUIButton(this, "newHtmlNodeButton", this.onNewHtmlNode);

				this.toolbarButtons["defaultActive"].deleteNodeButton = Alfresco.util.createYUIButton(this, "deleteNodeButton", this.onDeleteNode);

				this.toolbarButtons["defaultActive"].exportButton = Alfresco.util.createYUIButton(this, "exportArmButton", this.onExport);

				this.toolbarButtons["defaultActive"].importButton = Alfresco.util.createYUIButton(this, "importArmButton", this.showImportDialog,
						{
							disabled: this.options.searchButtonsType != 'defaultActive'
						});
				this.importFromSubmitButton = Alfresco.util.createYUIButton(this, "import-form-submit", this.onImportXML,{
					disabled: true
				});
				Alfresco.util.createYUIButton(this, "import-form-cancel", this.hideImportDialog,{});
				Alfresco.util.createYUIButton(this, "import-form-info-ok", function() {this.importInfoDialog.hide();},{});
				Alfresco.util.createYUIButton(this, "import-form-error-ok", function() {this.importErrorDialog.hide();},{});
				YAHOO.util.Event.on(this.id + "-import-form-import-file", "change", this.checkImportFile, null, this);
				YAHOO.util.Event.on(this.id + "-import-error-form-show-more-link", "click", this.errorFormShowMore, null, this);
			},

			onNewReportNode: function() {
				this.onNewRow(null, null, "lecm-arm:reports-node");
			},

			onNewHtmlNode: function() {
				this.onNewRow(null, null, "lecm-arm:html-node");
			},

			onNewRow: function(e, target, itemType){
				if (this.node != null) {
					if (itemType == null) {
						itemType = this.node.itemType;
					}
					if (this.editDialogOpening) {
						return;
					}
					this.editDialogOpening = true;
					var me = this;
					// Intercept before dialog show
					var doBeforeDialogShow = function (p_form, p_dialog) {
						var contId = p_dialog.id + "-form-container";
						Alfresco.util.populateHTML(
							[contId + "_h", this.msg("label.create-row.title") ]
						);
						if (itemType && itemType != "") {
							Dom.addClass(contId, itemType.replace(":", "_") + "_edit");
						}
						me.editDialogOpening = false;
						p_dialog.dialog.subscribe('destroy', LogicECM.module.Base.Util.formDestructor, {moduleId: p_dialog.id}, this);
					};

					var templateUrl = Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/form";
					var templateRequestParams = {
						itemKind:"type",
						itemId:itemType,
						destination:me.node.nodeRef,
						mode:"create",
						formId: "",
						submitType:"json",
						showCancelButton: true,
						showCaption: false
					};

					// Using Forms Service, so always create new instance
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
									YAHOO.Bubbling.fire("addTreeItem", {
										nodeRef: response.json.persistedObject
									});
									Alfresco.util.PopupManager.displayMessage(
										{
											text: this.msg( "message.save.success")
										});
									me.editDialogOpening = false;
								},
								scope:this
							},
							onFailure:{
								fn:function (response) {
									Alfresco.util.PopupManager.displayMessage(
										{
											text: this.msg("message.save.failure")
										});
									me.editDialogOpening = false;
								},
								scope:this
							}
						}).show();
				}
			},

			onDeleteNode: function() {
				if (this.node != null) {
					var me = this;

					var fnActionDeleteConfirm = function DataGridActions__onActionDelete_confirm(nodeRef) {
						Alfresco.util.Ajax.jsonRequest(
							{
								method: Alfresco.util.Ajax.POST,
								url: Alfresco.constants.PROXY_URI + "lecm/base/action/delete?full=true&trash=false&alf_method=delete",
								dataObj: {
									nodeRefs: [nodeRef]
								},
								responseContentType: Alfresco.util.Ajax.JSON,
								successCallback: {
									fn: function (response) {
										YAHOO.Bubbling.fire("deleteSelectedTreeItem");
										Alfresco.util.PopupManager.displayMessage(
											{
												text: me.msg( "message.delete.success")
											});
									}
								},
								failureMessage: "message.delete.failure",
								execScripts: true
							});
					};

					Alfresco.util.PopupManager.displayPrompt(
						{
							title:this.msg("message.confirm.delete.title"),
							text: this.msg("message.confirm.delete.description"),
							buttons:[
								{
									text:this.msg("button.delete"),
									handler:function () {
										this.destroy();
										fnActionDeleteConfirm.call(me, me.node.nodeRef);
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
			},

			onSelectTreeNode: function(layer, args) {
				if (args[1].datagridMeta != null) {
					this.node = args[1].datagridMeta;

					this.toolbarButtons["defaultActive"].newRowButton.set("label", Alfresco.util.message('lecm.arm.lbl.add') + " " + this.getTypeName(this.node.itemType));
					this.toolbarButtons["defaultActive"].deleteNodeButton.set("label", Alfresco.util.message('lecm.arm.lbl.delete.selected') + " " + this.getTypeName(this.node.currentItemType));

					this.toolbarButtons["defaultActive"].deleteNodeButton.set("disabled", this.node.itemType == "lecm-arm:arm");
					this.toolbarButtons["defaultActive"].newReportsNode.set("disabled", this.node.itemType != "lecm-arm:node" && this.node.itemType != "lecm-arm:reports-node" && this.node.itemType != "lecm-arm:html-node" );
					this.toolbarButtons["defaultActive"].newHtmlNode.set("disabled", this.node.itemType == "lecm-arm:arm" || this.node.itemType == "lecm-arm:accordion");

					this.toolbarButtons["defaultActive"].exportButton.set("disabled", this.node.currentItemType != "lecm-arm:arm");
					this.toolbarButtons["defaultActive"].importButton.set("disabled", this.node.currentItemType != "lecm-dic:dictionary");
				}
			},

			getTypeName: function (type) {
				if (type == "lecm-arm:arm") {
					return Alfresco.util.message('page.title.arm');
				} else if (type == "lecm-arm:accordion"){
					return Alfresco.util.message('lecm.arm.lbl.section');
				} else if (type == "lecm-arm:node") {
					return Alfresco.util.message('lecm.arm.lbl.node');
				}
				return Alfresco.util.message('lecm.arm.lbl.element');
			},

			onExport: function() {
				if (this.node != null) {
					document.location.href = Alfresco.constants.PROXY_URI + "lecm/dictionary/get/export?nodeRef=" + this.node.nodeRef;
				}
			},

			onImportXML: function() {
				var me = this;
				YAHOO.util.Connect.setForm(this.id + '-import-xml-form', true);
				var url = Alfresco.constants.URL_CONTEXT + "proxy/alfresco/lecm/dictionary/post/import?nodeRef=" + this.node.nodeRef;
				var callback = {
					upload: function(oResponse){
						var oResults = YAHOO.lang.JSON.parse(oResponse.responseText);
						YAHOO.Bubbling.fire("itemsListChanged");
						if (oResults[0] != null && oResults[0].text != null) {
							Dom.get(me.id + "-import-info-form-content").innerHTML = oResults[0].text;
							me.importInfoDialog.show();
						} else if (oResults.exception != null) {
							Dom.get(me.id + "-import-error-form-exception").innerHTML = oResults.exception.replace(/\n/g, '<br>').replace(/\r/g, '<br>');
							Dom.get(me.id + "-import-error-form-stack-trace").innerHTML = me.getStackTraceString(oResults.callstack);
							Dom.setStyle(me.id + "-import-error-form-more", "display", "none");
							me.importErrorDialog.show();
						}
					}
				};
				this.hideImportDialog();
				YAHOO.util.Connect.asyncRequest(Alfresco.util.Ajax.POST, url, callback);
			}
		}, true);
})();