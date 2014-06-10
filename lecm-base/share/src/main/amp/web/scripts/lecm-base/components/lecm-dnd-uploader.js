/**
 * LogicECM root namespace.
 *
 * @namespace LogicECM
 */
// Ensure LogicECM root object exists
if (typeof LogicECM == "undefined" || !LogicECM) {
	var LogicECM = {};
}


(function () {
	var Dom = YAHOO.util.Dom,
		Event = YAHOO.util.Event;

	LogicECM.DndUploader = function (containerId) {
		LogicECM.DndUploader.superclass.constructor.call(this, "LogicECM.DndUploader", containerId, [ "container"]);

		this.dragEventRefCount = 0;
		return this;
	};

	YAHOO.extend(LogicECM.DndUploader, Alfresco.component.Base,
		{
			config: {
				disabled: false,

				destinationName: null,

				destination: null,

				multipleMode: true,

				uploadNewVersion: false,

				onFileUploadComplete: null
			},

			dragEventRefCount: 0,

			container: null,

			initUploader:function (config) {
				this.config = config;
				this.config = YAHOO.lang.merge(this.config, config);
				this.container = Dom.get(this.id);

				if (!this.config.disabled) {
					LogicECM.LecmUploaderInitializer.initLecmDndUploader();

					Event.addListener(this.container, "dragenter", this.onDragEnter, this, true);
					Event.addListener(this.container, "dragover", this.onDragOver, this, true);
					Event.addListener(this.container, "dragleave", this.onDragLeave, this, true);
					Event.addListener(this.container, "drop", this.onDrop, this, true);
				}
			},

			onDragEnter: function (e) {

//				if (this.config.multipleMode || (e.dataTransfer.files != undefined && e.dataTransfer.files != null && e.dataTransfer.files.length == 1)) {

					// Firefox is a bit buggy with it's enter/leave event matching
					this.dragEventRefCount = Math.min(++this.dragEventRefCount, 2);
					Dom.addClass(this.container, "dndDocListHighlight");

					e.stopPropagation();
					e.preventDefault();
//				}
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
				if (e.dataTransfer.files !== undefined && e.dataTransfer.files !== null &&
					((this.config.multipleMode && e.dataTransfer.files.length > 0) ||  e.dataTransfer.files.length == 1))
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
							var uploadConfig;
							if (this.config.uploadNewVersion) {
								var me = this;
								var files = e.dataTransfer.files;
								Alfresco.util.Ajax.jsonGet(
									{
										url: Alfresco.constants.PROXY_URI_RELATIVE + "lecm/node/version?nodeRef=" + encodeURIComponent(this.config.destination),
										successCallback:
										{
											fn: function (response) {
												var version = "1.0";
												var oResults = response.json;
												if (oResults != null && oResults.version != null && oResults.version.length > 0) {
													version = oResults.version;
												}

												uploadConfig = {
													files: files,
													updateNodeRef: me.config.destination,
													updateFilename: me.config.destinationName,
													updateVersion: version,
													uploadDirectoryName: me.config.destinationName,
													overwrite: true,
													filter: [
														{
															extensions: "*"
														}],
													mode: progressDialog.MODE_SINGLE_UPDATE,
													onFileUploadComplete: me.config.onFileUploadComplete
												};
												progressDialog.show(uploadConfig);
											},
											scope: this
										},
										failureCallback:
										{
											fn: function (oResponse) {
												Alfresco.util.PopupManager.displayPrompt(
													{
														text: this.msg("message.load.dnd-uploader.failure")
													});
											},
											scope: this
										}
									});
							} else {
								uploadConfig = {
									files: e.dataTransfer.files,
									uploadDirectoryName: this.config.destinationName,
									destination: this.config.destination,
									filter: [],
									mode: this.config.multipleMode ? progressDialog.MODE_MULTI_UPLOAD : progressDialog.MODE_SINGLE_UPLOAD,
									thumbnails: "doclib",
									onFileUploadComplete: this.config.onFileUploadComplete
								};
								progressDialog.show(uploadConfig);
							}
						}
					}
				}
				else
				{
					Alfresco.logger.debug("A drop event was detected, but no files were present for upload: ", e.dataTransfer);
				}

				this.onDragLeave(e);
			}
		});
})();