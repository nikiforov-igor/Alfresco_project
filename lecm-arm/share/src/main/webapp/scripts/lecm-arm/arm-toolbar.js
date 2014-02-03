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
 * LogicECM Dictionary module namespace.
 *
 * @namespace LogicECM
 * @class LogicECM.module.Dictionary.Dictionary
 */
LogicECM.module.ARM = LogicECM.module.ARM || {};

/**
 * Data Lists: Toolbar component.
 *
 * Displays a list of Toolbar
 *
 * @namespace Alfresco
 * @class LogicECM.module.Dictionary.Toolbar
 */
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

			_initButtons: function () {
				this.toolbarButtons["defaultActive"].newRowButton = Alfresco.util.createYUIButton(this, "newRowButton", this.onNewRow);

				this.toolbarButtons["defaultActive"].deleteNodeButton = Alfresco.util.createYUIButton(this, "deleteNodeButton", this.onDeleteNode);
			},

			onNewRow: function(){
				if (this.node != null) {
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
						if (me.node.itemType && me.node.itemType != "") {
							Dom.addClass(contId, me.node.itemType.replace(":", "_") + "_edit");
						}
						me.editDialogOpening = false;
					};

					var templateUrl = YAHOO.lang.substitute(Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/form?itemKind={itemKind}&itemId={itemId}&destination={destination}&mode={mode}&submitType={submitType}&formId={formId}&showCancelButton=true",
						{
							itemKind:"type",
							itemId:me.node.itemType,
							destination:me.node.nodeRef,
							mode:"create",
							formId: "",
							submitType:"json"
						});

					// Using Forms Service, so always create new instance
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
									YAHOO.Bubbling.fire("itemsListChanged");
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
								url: Alfresco.constants.PROXY_URI + "lecm/base/action/delete?alf_method=delete",
								dataObj: {
									nodeRefs: [nodeRef]
								},
								responseContentType: Alfresco.util.Ajax.JSON,
								successCallback: {
									fn: function (response) {
										YAHOO.Bubbling.fire("itemsListChanged", {
											refreshParent: true,
											selectParent: true
										});
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

					this.toolbarButtons["defaultActive"].deleteNodeButton.set("disabled", this.node.itemType == "lecm-arm:arm");
				}
			}
		}, true);
})();