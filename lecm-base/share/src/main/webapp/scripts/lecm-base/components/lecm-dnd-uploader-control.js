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
		this.selectedItems = {};

		return this;
	};

	YAHOO.extend(LogicECM.control.DndUploader, Alfresco.component.Base,
		{
			options:{
				disabled: false,

				uploadDirectoryPath: null,

				multipleMode: true,

				directoryName: true,

				autoSubmit: false
			},

			rootNodeRef: null,

			fileUpload: null,

			selectedItems: null,

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
										text: this.msg("message.load.dnd-uploader.failure")
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
						if (this.options.uploadDirectoryPath == "{companyhome}") {
							location = "alfresco://company/home";
						} else if (this.options.uploadDirectoryPath == "{userhome}") {
							location = "alfresco://user/home";
						} else if (this.options.uploadDirectoryPath == "{siteshome}") {
							location = "alfresco://sites/home";
						} else if (this.options.uploadDirectoryPath == "{usertemp}") {
							location = "alfresco://user/temp";
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
				var me = this;

				if (obj.successful != null && obj.successful.length > 0) {
					var elAttachments = Dom.get(this.id + "-attachments");

					for (var i = 0; i < obj.successful.length; i++) {
						var fileName = obj.successful[i].fileName;
						var nodeRef = obj.successful[i].nodeRef;

						if (elAttachments != null) {
							var fileIcon = Alfresco.util.getFileIcon(fileName, "cm:content", 16);
							var fileIconHtml = "<img src='" + Alfresco.constants.URL_RESCONTEXT + "components/images/filetypes/" + fileIcon +"'/>";
                            var iconId = "attachment-" + nodeRef;
                            var removeIcon = "<img id='" + iconId + "' src='" + Alfresco.constants.URL_RESCONTEXT
                                + "components/images/delete-16.png' class='remove-icon'/>";

                            fileName = "<div>" + fileName + "</div>"
							elAttachments.innerHTML += "<li>" + fileIconHtml + fileName + removeIcon + "</li>";


                            Event.onAvailable(iconId, function() {
                                var iconId = arguments[0].iconId;
                                var nodeRef = arguments[0].nodeRef;

                                Event.addListener(iconId, "click", function() {
                                    var li = Dom.getAncestorByTagName(iconId, "li");
                                    var ul = Dom.getAncestorByTagName(li, "ul");

                                    ul.removeChild(li);

	                                delete me.selectedItems[nodeRef];
	                                me.updateFormFields();
                                });
                            },
                            {
	                            iconId: iconId,
	                            nodeRef: nodeRef
                            });
                        }
						this.selectedItems[nodeRef] = nodeRef;
					}

					me.updateFormFields();

					if (this.options.autoSubmit) {
						var formElem  = Dom.get(this.id).form;
						if (formElem != null) {
							var submitButton = Dom.get(formElem.id + "-submit-button");
							if (submitButton != null) {
								submitButton.click();
							}
						}
					}
				}
			},

			updateFormFields: function() {
				var elAdded = Dom.get(this.id + "-added");
				var selectedItems = this.getSelectedItems();

				if (elAdded != null && this.selectedItems != null) {
					elAdded.value = "";
					for (var i = 0; i < selectedItems.length; i++) {
						elAdded.value += (i < selectedItems.length-1 ? selectedItems[i] + ',' : selectedItems[i]);
					}
				}
			},

			getSelectedItems: function () {
				var selectedItems = [];

				for (var item in this.selectedItems) {
					if (this.selectedItems.hasOwnProperty(item)) {
						selectedItems.push(item);
					}
				}
				return selectedItems;
			}
		});
})();