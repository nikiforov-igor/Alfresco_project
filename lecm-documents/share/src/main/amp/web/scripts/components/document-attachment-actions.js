// Ensure LogicECM root object exists
if (typeof LogicECM == "undefined" || !LogicECM) {
	LogicECM = {};
}

/**
 * LogicECM top-level module namespace.
 *
 * @namespace LogicECM
 * @class LogicECM.DocumentAttachmentActions
 */
LogicECM.DocumentAttachmentActions = LogicECM.DocumentAttachmentActions || {};

(function () {
	var $siteURL = Alfresco.util.siteURL;

	LogicECM.DocumentAttachmentActions  = function (containerId) {
		YAHOO.Bubbling.on("fileCopied", this.onFileCopiedComplete, this);

		return LogicECM.DocumentAttachmentActions.superclass.constructor.call(this, containerId);
	};

	/**
	 * Extend from LogicECM.module.Base.DataGrid
	 */
	YAHOO.lang.extend(LogicECM.DocumentAttachmentActions , Alfresco.DocumentActions);

	YAHOO.lang.augmentObject(Alfresco.DocumentActions.prototype, {
		onNewVersionUploadCompleteCustom: function (complete)
		{
			// Call the normal callback to post the activity data
			this.onNewVersionUploadComplete.call(this, complete);
			this.recordData.jsNode.setNodeRef(complete.successful[0].nodeRef);
			// Delay page reloading to allow time for async requests to be transmitted
			YAHOO.lang.later(0, this, function()
			{
				window.location.reload();
			});
		},

		_onActionDeleteConfirm: function (asset)
		{
			var path = asset.location.path,
				fileName = asset.fileName,
				displayName = asset.displayName,
				nodeRef = new Alfresco.util.NodeRef(asset.nodeRef);

			var me = this;

			this.modules.actions.genericAction(
				{
					success:
					{
						activity:
						{
							siteId: this.options.siteId,
							activityType: "file-deleted",
							page: "documentlibrary",
							activityData:
							{
								fileName: fileName,
								path: path,
								nodeRef: nodeRef.toString()
							}
						},
						callback:
						{
							fn: function (data)
							{
								window.location = $siteURL("document?nodeRef=" + me.options.documentNodeRef + "&view=attachments");
							}
						}
					},
					failure:
					{
						message: this.msg("message.delete.failure", displayName)
					},
					webscript:
					{
						method: Alfresco.util.Ajax.DELETE,
						name: "file/node/{nodeRef}",
						params:
						{
							nodeRef: nodeRef.uri
						}
					}
				});
		},

		onActionEditOffline: function (asset)
		{
			var displayName = asset.displayName,
				nodeRef = new Alfresco.util.NodeRef(asset.nodeRef);

			this.modules.actions.genericAction(
				{
					success:
					{
						callback:
						{
							fn: function (data)
							{
								var nodeRef = data.json.results[0].nodeRef;
								this.recordData.jsNode.setNodeRef(nodeRef);
								window.location = $siteURL("document-attachment?nodeRef=" + nodeRef + "#editOffline");
							},
							scope: this
						}
					},
					failure:
					{
						message: this.msg("message.edit-offline.failure", displayName)
					},
					webscript:
					{
						method: Alfresco.util.Ajax.POST,
						name: "checkout/node/{nodeRef}",
						params:
						{
							nodeRef: nodeRef.uri
						}
					}
				});
		},

		onActionCancelEditing: function (record)
		{
			var displayName = record.displayName;

			this.modules.actions.genericAction(
				{
					success:
					{
						callback:
						{
							fn: function (data)
							{
								var nodeRef = data.json.results[0].nodeRef;
								this.recordData.jsNode.setNodeRef(nodeRef);
								window.location = $siteURL("document-attachment?nodeRef=" + nodeRef);
							},
							scope: this
						},
						message: this.msg("message.edit-cancel.success", displayName)
					},
					failure:
					{
						message: this.msg("message.edit-cancel.failure", displayName)
					},
					webscript: {
						name: "lecm/unlock?nodeRef={nodeRef}",
						stem: Alfresco.constants.PROXY_URI,
						method: Alfresco.util.Ajax.GET,
						params: {
							nodeRef: file.nodeRef
						}
					}
				});
		},

		onUnlockAction: function onUnlockAction_function(file) {
			if (YAHOO.lang.isFunction(LogicECM.module.UnlockNode.unlock)) {
				LogicECM.module.UnlockNode.unlock(file);
			}
		},

		onActionLECMEditOnline: function onActionLECMEditOnline_function(file) {
			if (YAHOO.lang.isFunction(LogicECM.module.EditOnline.edit)) {
				LogicECM.module.EditOnline.edit(file);
			}
		},

		onFileCopiedComplete: function(layer, args) {
			Alfresco.util.Ajax.jsonGet({
				url: Alfresco.constants.PROXY_URI + "/lecm/document/attachments/api/logCopy",
				dataObj: {
					originalNodeRef: this.options.nodeRef,
					copiedNodeRef: args[1].nodeRef
				},
				successCallback: {
					scope: this,
					fn: function (response) {}
				},
				failureCallback: {
					scope: this,
					fn: function (response) {
						YAHOO.log("Failed to process XHR transaction.", "info", "example");
					}
				}
			});
		}
	}, true);
})();