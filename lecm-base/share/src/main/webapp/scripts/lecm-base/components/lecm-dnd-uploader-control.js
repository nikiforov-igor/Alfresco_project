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

		this.currentValueHtmlId = fieldHtmlId;
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

				autoSubmit: false,

				currentValue: "",

				showUploadNewVersion: false
			},

			currentValueHtmlId: "",

			rootNodeRef: null,

			fileUpload: null,

			selectedItems: null,

			onReady:function () {
				if (!this.options.disabled) {
					LogicECM.LecmUploaderInitializer.initLecmUploaders();
					this.loadRootNode();

					Event.on(this.id + "-uploader-button", "click", this.showUploader, null, this);
				}
				this.loadSelectedItems();
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

			loadSelectedItems: function () {
				var arrItems = this.options.currentValue;

				var onSuccess = function (response)
				{
					var items = response.json.data.items,
						item;
					this.selectedItems = {};

					for (var i = 0, il = items.length; i < il; i++)
					{
						item = items[i];
						this.selectedItems[item.nodeRef] = item;
					}

					this.updateSelectedItems();
					this.updateFormFields();
					this.updateFormUI();
				};

				var onFailure = function (response)
				{
					this.selectedItems = null;
				};

				if (arrItems !== "")
				{
					Alfresco.util.Ajax.jsonRequest(
						{
							url: Alfresco.constants.PROXY_URI + "lecm/forms/picker/items",
							method: "POST",
							dataObj:
							{
								items: arrItems.split(","),
								itemValueType: "nodeRef",
								itemNameSubstituteString: "{cm:name}"
							},
							successCallback:
							{
								fn: onSuccess,
								scope: this
							},
							failureCallback:
							{
								fn: onFailure,
								scope: this
							}
						});
				}
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
					destinationName: this.options.directoryName,
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
					for (var i = 0; i < obj.successful.length; i++) {
						var fileName = obj.successful[i].fileName;
						var nodeRef = obj.successful[i].nodeRef;

						this.selectedItems[nodeRef] = {
							nodeRef: nodeRef,
							name: fileName,
							justUpload: true
						};
					}

					me.updateSelectedItems();
					me.updateFormFields();
					me.updateFormUI();

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

			attachRemoveItemClickListener: function(node) {
				Event.addListener("attachment-remove-" + node.nodeRef, "click", this.removeSelectedElement, node, this);
			},

			removeSelectedElement: function(event, node) {
				delete this.selectedItems[node.nodeRef];
				this.updateSelectedItems();
				this.updateFormFields();
				this.updateFormUI();
			},

			attachUploadNewVersionClickListener: function(node) {
				Event.addListener("attachment-newVersion-" + node.nodeRef, "click", this.uploadNewVersionSelectedElement, node, this);
			},

			uploadNewVersionSelectedElement: function(event, node) {
				var displayName = node.name,
					nodeRef = node.nodeRef;

				Alfresco.util.Ajax.jsonGet(
					{
						url: Alfresco.constants.PROXY_URI_RELATIVE + "lecm/node/version?nodeRef=" + encodeURIComponent(nodeRef),
						successCallback:
						{
							fn: function (response) {
								var version = "1.0";
								var oResults = response.json;
								if (oResults != null && oResults.version != null && oResults.version.length > 0) {
									version = oResults.version;
								}

								if (!this.fileUpload)
								{
									this.fileUpload = Alfresco.getFileUploadInstance();
								}

								var description = this.msg("label.filter-description", displayName),
									extensions = "*";

								if (displayName && new RegExp(/[^\.]+\.[^\.]+/).exec(displayName))
								{
									extensions = "*" + displayName.substring(displayName.lastIndexOf("."));
								}

								var singleUpdateConfig =
								{
									updateNodeRef: nodeRef,
									updateFilename: displayName,
									updateVersion: version,
									overwrite: true,
									filter: [
										{
											description: description,
											extensions: extensions
										}],
									mode: this.fileUpload.MODE_SINGLE_UPDATE
								};

								this.fileUpload.show(singleUpdateConfig);
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

			attachUploadNewVersionDndListener: function(node) {
				var dndUploader = new LogicECM.DndUploader("attachment-" + node.nodeRef);
				dndUploader.initUploader({
					destinationName: node.name,
					destination: node.nodeRef,
					uploadNewVersion: true
				});
			},

			canUploaderShow: function() {
				return this.options.multipleMode || (Object.keys(this.selectedItems).length == 0);
			},

			updateFormUI: function() {
				Dom.setStyle(Dom.get(this.id + "-uploader-block"), "display", this.canUploaderShow() ? "block" : "none");
			},

			updateSelectedItems: function AssociationAutoComplete_updateSelectedItems() {
				var items = this.selectedItems;

				var elAttachments = Dom.get(this.id + "-attachments");
				if (elAttachments != null) {
					elAttachments.innerHTML = '';

					for (var nodeRef in items) {
						var item = items[nodeRef];
						var fileName = item.name;

						if (elAttachments != null) {
							var fileIcon = Alfresco.util.getFileIcon(fileName, "cm:content", 16);
							var fileIconHtml = "<img src='" + Alfresco.constants.URL_RESCONTEXT + "components/images/filetypes/" + fileIcon +"'/>";
							fileName = "<div>" + fileName + "</div>"
							var row = fileIconHtml + fileName;
							if (!item.justUpload) {
								row = "<a href='" + Alfresco.constants.URL_PAGECONTEXT + "document-attachment?nodeRef=" + nodeRef + "'>" + row + "</a>";
							}

							if (!this.options.disabled) {
								if (this.options.showUploadNewVersion) {
									var iconNewVersionId = "attachment-newVersion-" + nodeRef;
									row += "<img id='" + iconNewVersionId + "' src='" + Alfresco.constants.URL_RESCONTEXT
										+ "/components/documentlibrary/actions/document-upload-new-version-16.png' class='newVersion-icon'/>";
									Event.onAvailable(iconNewVersionId, this.attachUploadNewVersionClickListener, item, this);
								}

								var iconRemoveId = "attachment-remove-" + nodeRef;
								row += "<img id='" + iconRemoveId + "' src='" + Alfresco.constants.URL_RESCONTEXT
									+ "components/images/delete-16.png' class='remove-icon'/>";
								Event.onAvailable(iconRemoveId, this.attachRemoveItemClickListener, item, this);
							}

							var rowId = "attachment-" + nodeRef;
							elAttachments.innerHTML += "<li id='" + rowId + "'>" + row + "</li>";
							if (!this.options.disabled && this.options.showUploadNewVersion) {
								Event.onAvailable(rowId, this.attachUploadNewVersionDndListener, item, this);
							}
						}
					}
				}
			},

			updateFormFields: function() {
				var el;
				var addItems = this.getAddedItems();
				el = Dom.get(this.id + "-added");
				el.value = '';
				for (var i in addItems) {
					el.value += ( i < addItems.length-1 ? addItems[i] + ',' : addItems[i] );
				}

				var removedItems = this.getRemovedItems();
				el = Dom.get(this.id + "-removed");
				el.value = '';
				for (i in removedItems) {
					el.value += (i < removedItems.length-1 ? removedItems[i] + ',' : removedItems[i]);
				}

				var selectedItems = this.getSelectedItems();
				Dom.get(this.currentValueHtmlId).value = selectedItems.toString();

				if (this.options.mandatory) {
					YAHOO.Bubbling.fire("mandatoryControlValueUpdated", this);
				}
				YAHOO.Bubbling.fire("formValueChanged",
					{
						eventGroup:this,
						addedItems:addItems,
						removedItems:removedItems,
						selectedItems:selectedItems,
						selectedItemsMetaData:Alfresco.util.deepCopy(this.selectedItems)
					});
			},

			getSelectedItems: function() {
				var selectedItems = [];

				for (var item in this.selectedItems) {
					if (this.selectedItems.hasOwnProperty(item)) {
						selectedItems.push(item);
					}
				}
				return selectedItems;
			},

			getAddedItems: function()
			{
				var addedItems = [],
					currentItems = Alfresco.util.arrayToObject(this.options.currentValue.split(","));

				for (var item in this.selectedItems)
				{
					if (this.selectedItems.hasOwnProperty(item))
					{
						if (!(item in currentItems))
						{
							addedItems.push(item);
						}
					}
				}
				return addedItems;
			},

			getRemovedItems: function()
			{
				var removedItems = [],
					currentItems = Alfresco.util.arrayToObject(this.options.currentValue.split(","));

				for (var item in currentItems)
				{
					if (currentItems.hasOwnProperty(item))
					{
						if (!(item in this.selectedItems))
						{
							removedItems.push(item);
						}
					}
				}
				return removedItems;
			}
		});
})();