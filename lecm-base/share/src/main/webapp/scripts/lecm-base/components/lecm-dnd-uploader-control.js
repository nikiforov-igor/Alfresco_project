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

		this.controlId = fieldHtmlId + "-cntrl";
		this.dragEventRefCount = 0;
		return this;
	};

	YAHOO.extend(LogicECM.control.DndUploader, Alfresco.component.Base,
		{
			options:{
				disabled: false,

				uploadDirectoryPath: null,

				multipleMode: true
			},

			dragEventRefCount: 0,

			controlId: "",

			container: null,

			rootNodeRef: null,

			onReady:function () {
				// Add listeners to the HTML5 drag and drop events fired from the entire doc list
				this.container = Dom.get(this.controlId + "-uploader-block");

				if (!this.options.disabled) {
					this.setDndUploader();
					this.loadRootNode();

					Event.addListener(this.container, "dragenter", this.onDragEnter, this, true);
					Event.addListener(this.container, "dragover", this.onDragOver, this, true);
					Event.addListener(this.container, "dragleave", this.onDragLeave, this, true);
					Event.addListener(this.container, "drop", this.onDrop, this, true);

					YAHOO.Bubbling.on("metadataRefresh", this.onMetadataRefresh, this);
				}
			},

			setDndUploader: function() {
				var uploaderContainerId = "lecm-controls-dnd-uploader";
				var dndUploaderContainer = Dom.get(uploaderContainerId);
				if (dndUploaderContainer == null) {
					dndUploaderContainer = document.createElement("div");
					dndUploaderContainer.id = uploaderContainerId;
					document.body.appendChild(dndUploaderContainer);

					Alfresco.util.Ajax.request(
						{
							url: Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/dnd-upload",
							dataObj: {
								htmlid: uploaderContainerId
							},
							successCallback: {
								fn:function(response){
									dndUploaderContainer.innerHTML = response.serverResponse.responseText;
								},
								scope: this
							},
							failureMessage: this.msg("message.failure"),
							scope: this,
							execScripts: true
						});
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

			onDragEnter: function (e) {

				if (this.options.multipleMode || e.dataTransfer.items.length == 1) {

					// Firefox is a bit buggy with it's enter/leave event matching
					this.dragEventRefCount = Math.min(++this.dragEventRefCount, 2);
					Dom.addClass(this.container, "dndDocListHighlight");

					e.stopPropagation();
					e.preventDefault();
				}
			},

			onDragOver: function (e) {
				e.dataTransfer.dropEffect = Math.floor(YAHOO.env.ua.gecko) === 1 ? "move" : "copy";
				e.stopPropagation();
				e.preventDefault();
			},

			onDragLeave: function (e) {
				if (--this.dragEventRefCount === 0)
				{
					Dom.removeClass(this.container, "dndDocListHighlight");
				}

				e.stopPropagation();
				e.preventDefault();
			},

			onDrop: function (e)
			{
				// Only perform a file upload if the user has *actually* dropped some files!
				if (this.rootNodeRef != null && e.dataTransfer.files !== undefined && e.dataTransfer.files !== null &&
					((this.options.multipleMode && e.dataTransfer.files.length > 0) ||  e.dataTransfer.files.length == 1))
				{
					// We need to get the upload progress dialog widget so that we can display it.
					// The function called has been added to file-upload.js and ensures the dialog is a singleton.
					var progressDialog = LogicECM.getDNDUploadProgressInstance();

					if (progressDialog != null) {

						var continueWithUpload = false;
	//
						// Check that at least one file with some data has been dropped...
						var zeroByteFiles = "", i, j;

						j = e.dataTransfer.files.length;
						for (i = 0; i < j; i++)
						{
							if (e.dataTransfer.files[i].size > 0)
							{
								continueWithUpload = true;
							}
							else
							{
								zeroByteFiles += '"' + e.dataTransfer.files[i].name + '", ';
							}
						}

						if (!continueWithUpload)
						{
							zeroByteFiles = zeroByteFiles.substring(0, zeroByteFiles.lastIndexOf(", "));
							Alfresco.util.PopupManager.displayMessage(
								{
									text: progressDialog.msg("message.zeroByteFiles", zeroByteFiles)
								});
						}

						// Perform some checks on based on the browser and selected files to ensure that we will
						// support the upload request.
						if (continueWithUpload && progressDialog.uploadMethod === progressDialog.INMEMORY_UPLOAD)
						{
							// Add up the total size of all selected files to see if they exceed the maximum allowed.
							// If the user has requested to upload too large a file or too many files in one operation
							// then generate an error dialog and abort the upload...
							var totalRequestedUploadSize = 0;

							j = e.dataTransfer.files.length;
							for (i = 0; i < j; i++)
							{
								totalRequestedUploadSize += e.dataTransfer.files[i].size;
							}
							if (totalRequestedUploadSize > progressDialog.getInMemoryLimit())
							{
								continueWithUpload = false;
								Alfresco.util.PopupManager.displayPrompt(
									{
										text: progressDialog.msg("inmemory.uploadsize.exceeded", Alfresco.util.formatFileSize(progressDialog.getInMemoryLimit()))
									});
							}
						}

						// If all tests are passed...
						if (continueWithUpload)
						{
							var directory = this.options.uploadDirectoryPath,
								directoryName = directory.substring(directory.lastIndexOf("/") + 1),
								destination = this.rootNodeRef,
								mode = this.options.multipleMode ? progressDialog.MODE_MULTI_UPLOAD : progressDialog.MODE_SINGLE_UPDATE;

							var uploadConfig =
							{
								files: e.dataTransfer.files,
								uploadDirectoryName: directoryName,
								destination: destination,
								filter: [],
								mode: mode,
								thumbnails: "doclib"
							};

							progressDialog.show(uploadConfig);
						}
					}
				}
				else
				{
					Alfresco.logger.debug("A drop event was detected, but no files were present for upload: ", e.dataTransfer);
				}

				this.onDragLeave(e);
			},

			onMetadataRefresh: function(e, obj) {
				if (obj != null) {
					if (obj[1] != null && obj[1].files != null) {
						var files = obj[1].files;

						var elAdded = Dom.get(this.controlId + "-added");
						var elAttachments = Dom.get(this.controlId + "-attachments");

						for (var i = 0; i < files.length; i++) {
							if (elAttachments != null) {
								elAttachments.innerHTML += "<li>" + files[i].name + "</li>";
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