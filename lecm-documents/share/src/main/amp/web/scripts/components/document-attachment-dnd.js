/**
 * LogicECM root namespace.
 *
 * @namespace LogicECM
 */
// Ensure LogicECM root object exists
if (typeof LogicECM == "undefined" || !LogicECM) {
	LogicECM = {};
}

/**
 * DocumentAttachmentsDND
 *
 * @namespace LogicECM
 * @class LogicECM.DocumentAttachmentsDND
 */
(function () {
	/**
	 * YUI Library aliases
	 */
	var Dom = YAHOO.util.Dom,
		Event = YAHOO.util.Event;

	/**
	 * DocumentAttachmentsDND constructor.
	 *
	 * @param {String} htmlId The HTML id of the parent element
	 * @return {LogicECM.DocumentAttachmentsDND} The new DocumentAttachmentsDND instance
	 * @constructor
	 */
	LogicECM.DocumentAttachmentsDND = function DocumentAttachmentsDND_constructor(htmlId) {
		LogicECM.DocumentAttachmentsDND.superclass.constructor.call(this, "LogicECM.DocumentAttachmentsDND", htmlId);
		this.dragEventRefCount = 0;

		return this;
	};

	YAHOO.extend(LogicECM.DocumentAttachmentsDND, Alfresco.component.Base);


	YAHOO.lang.augmentObject(LogicECM.DocumentAttachmentsDND.prototype,
		{
			options: {
				nodeRef: null,

				fileName: null,

				versionLabel: null
			},

			onReady: function DocumentAttachmentsDND_onReady() {
				// Add listeners to the HTML5 drag and drop events fired from the entire doc list
				var container = Dom.get(this.id);
				Event.addListener(container, "dragenter", this.onDocumentListDragEnter, this, true);
				Event.addListener(container, "dragover", this.onDocumentListDragOver, this, true);
				Event.addListener(container, "dragleave", this.onDocumentListDragLeave, this, true);
				Event.addListener(container, "drop", this.onDocumentListDrop, this, true);

				YAHOO.Bubbling.on("metadataRefresh", this.onMetadataRefresh, this);
			},

			onDocumentListDragEnter: function (e) {
				var container = Dom.get(this.id);

				if (e.dataTransfer.items.length == 1) {

					// Firefox is a bit buggy with it's enter/leave event matching
					this.dragEventRefCount = Math.min(++this.dragEventRefCount, 2);
					Dom.addClass(container, "dndDocListHighlight");

					e.stopPropagation();
					e.preventDefault();
				}
			},

			onDocumentListDragOver: function (e) {
				e.dataTransfer.dropEffect = Math.floor(YAHOO.env.ua.gecko) === 1 ? "move" : "copy";
				e.stopPropagation();
				e.preventDefault();
			},

			onDocumentListDragLeave: function (e) {
				var container = Dom.get(this.id);
				if (--this.dragEventRefCount === 0)
				{
					Dom.removeClass(container, "dndDocListHighlight");
				}

				e.stopPropagation();
				e.preventDefault();
			},

			onDocumentListDrop: function (e)
			{
				var container = Dom.get(this.id);

				// Only perform a file upload if the user has *actually* dropped some files!
				if (e.dataTransfer.files !== undefined && e.dataTransfer.files !== null && e.dataTransfer.files.length == 1)
				{
					// We need to get the upload progress dialog widget so that we can display it.
					// The function called has been added to file-upload.js and ensures the dialog is a singleton.
					var progressDialog = LogicECM.getDNDUploadProgressInstance();

					var continueWithUpload = false;

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
						// Remove all the highlighting
						Dom.removeClass(container, "dndDocListHighlight");

						var singleUpdateConfig =
						{
							files: e.dataTransfer.files,
							updateNodeRef: this.options.nodeRef,
							updateFilename: this.options.fileName,
							updateVersion: this.options.versionLabel,
							uploadDirectoryName: this.options.fileName,
							overwrite: true,
							filter: [
								{
									extensions: "*"
								}],
							mode: progressDialog.MODE_SINGLE_UPDATE,
							onFileUploadComplete:
							{
								fn: function() {},
								scope: this
							}
						};

						progressDialog.show(singleUpdateConfig);
					}
				}
				else
				{
					Alfresco.logger.debug("DL_onDocumentListDrop: A drop event was detected, but no files were present for upload: ", e.dataTransfer);
				}
				e.stopPropagation();
				e.preventDefault();
			},

			onMetadataRefresh: function() {
				location.reload();
			}
	}, true);
})();