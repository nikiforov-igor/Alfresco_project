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
 * LogicECM top-level control namespace.
 *
 * @namespace LogicECM
 * @class LogicECM.control
 */
LogicECM.control = LogicECM.control || {};

(function () {
	var Dom = YAHOO.util.Dom,
		Event = YAHOO.util.Event;

	LogicECM.control.DndUploader = function (fieldHtmlId) {
		LogicECM.control.DndUploader.superclass.constructor.call(this, "LogicECM.control.DndUploader", fieldHtmlId, [ "container"]);

		return this;
	};

	YAHOO.extend(LogicECM.control.DndUploader, Alfresco.component.Base,
		{
			options:{
				disabled: false,

				uploadDirectoryPath: null,

				multipleMode: true,

				directoryName: true
			},

			onReady:function () {
				if (!this.options.disabled) {
					this.loadRootNode();

					YAHOO.Bubbling.on("metadataRefresh", this.onMetadataRefresh, this);
				}
			},

			loadRootNode: function () {
				var sUrl = this.generateRootUrlPath() + this.generateRootUrlParams();

				Alfresco.util.Ajax.jsonGet(
					{
						url: sUrl,
						successCallback:
						{
							fn: function (response) {
								var oResults = response.json;
								if (oResults != null) {
									this.initUploader(oResults.nodeRef);
								}
							},
							scope: this
						},
						failureCallback:
						{
							fn: function (oResponse) {
								Alfresco.util.PopupManager.displayPrompt(
									{
										text: this.msg("message.load-root-node.failure")
									});
							},
							scope: this
						}
					});
			},

			generateRootUrlPath: function ()
			{
				return Alfresco.constants.PROXY_URI + "/lecm/forms/node/search";
			},

			generateRootUrlParams: function ()
			{
				var params = "?titleProperty=" + encodeURIComponent("cm:name");
				if (this.options.uploadDirectoryPath && this.options.uploadDirectoryPath.charAt(0) == "/")
				{
					params += "&xpath=" + encodeURIComponent(this.options.uploadDirectoryPath);
				}

				return params;
			},

			initUploader: function(destination) {
				var uploader = new LogicECM.DndUploader(this.id + "-uploader-block");
				uploader.initUploader({
					disabled: this.options.disabled,
					directoryName: this.options.directoryName,
					destination: destination,
					multipleMode: this.options.multipleMode
				});
			},

			onMetadataRefresh: function(e, obj) {
				if (obj != null) {
					if (obj[1] != null && obj[1].files != null) {
						var files = obj[1].files;

						var elAdded = Dom.get(this.id + "-added");
						var elAttachments = Dom.get(this.id + "-attachments");

						for (var i = 0; i < files.length; i++) {
							if (elAttachments != null) {
								var fileName = files[i].name;
								var icon = Alfresco.util.getFileIcon(fileName, "cm:content", 16);
								var iconHtml = "<img src='" + Alfresco.constants.URL_RESCONTEXT + "components/images/filetypes/" + icon +"'/>";

								elAttachments.innerHTML += "<li>" + iconHtml + fileName + "</li>";
							}
							if (elAdded != null) {
								elAdded.value += ( i < files.length-1 ? files[i].nodeRef + ',' : files[i].nodeRef );
							}
						}
					}
				}
			}
		});
})();