// Ensure LogicECM root object exists
if (typeof LogicECM == "undefined" || !LogicECM) {
	var LogicECM = {};
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

		/**
		 * Delete Asset confirmed.
		 *
		 * @override
		 * @method _onActionDeleteConfirm
		 * @param asset {object} Object literal representing file or folder to be actioned
		 * @private
		 */
		_onActionDeleteConfirm: function DocumentActions__onActionDeleteConfirm(asset)
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
							fn: function DocumentActions_oADC_success(data)
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
		}
	}, true);
})();