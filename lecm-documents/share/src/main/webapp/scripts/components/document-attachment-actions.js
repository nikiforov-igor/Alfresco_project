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
		}
	}, true);
})();