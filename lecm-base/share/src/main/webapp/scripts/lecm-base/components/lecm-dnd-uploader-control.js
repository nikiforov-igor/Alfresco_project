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

			rootNodeRef: null,

			fileUpload: null,

				onReady:function () {
				if (!this.options.disabled) {
					LogicECM.LecmUploaderInitializer.initLecmUploaders();
					this.loadRootNode();

					Event.on(this.id + "-uploader-button", "click", this.showUploader, null, this);
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
									this.rootNodeRef = oResults.nodeRef;
									this.initUploader();
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
				if (this.options.uploadDirectoryPath) {
					if (this.options.uploadDirectoryPath.charAt(0) == "/") {
						params += "&xpath=" + encodeURIComponent(this.options.uploadDirectoryPath);
					} else if (this.options.uploadDirectoryPath.charAt(0) == "{") {
						var location = "";
						if (this.options.uploadDirectoryPath == "{companyhome}")
						{
							location = "alfresco://company/home";
						}
						else if (this.options.uploadDirectoryPath == "{userhome}")
						{
							location = "alfresco://user/home";
						}
						else if (this.options.uploadDirectoryPath == "{siteshome}")
						{
							location = "alfresco://sites/home";
						}
						if (location.length > 0) {
							params += "&rootNode=" + encodeURIComponent(location);
						}
					}
				}

				return params;
			},

			showUploader: function () {
				if (this.rootNodeRef != null) {
					if (this.fileUpload == null)
					{
						this.fileUpload = Alfresco.getFileUploadInstance();
					}

					var uploadConfig =
					{
						destination: this.rootNodeRef,
						filter: [],
						mode: this.options.multipleMode ? this.fileUpload.MODE_MULTI_UPLOAD : this.fileUpload.MODE_SINGLE_UPLOAD,
						thumbnails: "doclib",
						onFileUploadComplete:
						{
							fn: this.fileUploadComplete,
							scope: this
						}
					};
					this.fileUpload.show(uploadConfig);
				}
			},

			initUploader: function() {
				var uploader = new LogicECM.DndUploader(this.id + "-uploader-block");
				uploader.initUploader({
					disabled: this.options.disabled,
					directoryName: this.options.directoryName,
					destination: this.rootNodeRef,
					multipleMode: this.options.multipleMode,
					onFileUploadComplete:
					{
						fn: this.fileUploadComplete,
						scope: this
					}
				});
			},

			fileUploadComplete: function(obj) {
				if (obj.successful != null && obj.successful.length > 0) {
					var elAdded = Dom.get(this.id + "-added");
					var elAttachments = Dom.get(this.id + "-attachments");

					for (var i = 0; i < obj.successful.length; i++) {
						var fileName = obj.successful[i].fileName;
						var nodeRef = obj.successful[i].nodeRef;

						if (elAttachments != null) {
							var icon = Alfresco.util.getFileIcon(fileName, "cm:content", 16);
							var iconHtml = "<img src='" + Alfresco.constants.URL_RESCONTEXT + "components/images/filetypes/" + icon +"'/>";

							elAttachments.innerHTML += "<li>" + iconHtml + fileName + "</li>";
						}
						if (elAdded != null) {
							if (elAdded.value.length > 0) {
								elAdded.value += ',';
							}
							elAdded.value += nodeRef;
						}
					}
				}
			}
		});
})();