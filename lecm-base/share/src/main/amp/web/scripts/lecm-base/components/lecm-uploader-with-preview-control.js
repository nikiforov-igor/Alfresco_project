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
 * LogicECM top-level control namespace.
 *
 * @namespace LogicECM
 * @class LogicECM.control
 */
LogicECM.control = LogicECM.control || {};

(function () {
	var Dom = YAHOO.util.Dom,
		Event = YAHOO.util.Event;

	LogicECM.control.UploaderWithPreviw = function (fieldHtmlId) {
		LogicECM.control.UploaderWithPreviw.superclass.constructor.call(this, "LogicECM.control.UploaderWithPreviw", fieldHtmlId, [ "container"]);

		this.selectedItems = {};

		return this;
	};

	YAHOO.extend(LogicECM.control.UploaderWithPreviw, Alfresco.component.Base,
		{
			options:{
				disabled: false,

				uploadDirectoryPath: null,

				multipleMode: true,

				directoryName: true,

				currentValue: ""
			},

			rootNodeRef: null,

			fileUpload: null,

			selectedItems: null,

			flashUploaderWasShow: true,

			onReady:function () {
				if (!this.options.disabled) {
					LogicECM.LecmUploaderInitializer.initLecmUploaders();
					this.loadRootNode();

					Event.on(this.id + "-uploader-button", "click", this.showUploader, null, this);
					Event.on(this.id + "-uploader-remove-link", "click", this.onRemoveClick, null, this);
				}
				this.loadSelectedItems();
			},

			loadRootNode: function () {
				Alfresco.util.Ajax.jsonGet({
					url: this.generateRootUrlPath(),
					successCallback: {
						fn: function (response) {
							var oResults = response.json;
							if (oResults ) {
								this.rootNodeRef = oResults.nodeRef;
								this.initUploader();
							}
						},
						scope: this
					},
					failureCallback: {
						fn: function (oResponse) {
							Alfresco.util.PopupManager.displayPrompt({
								text: this.msg("message.load.dnd-uploader.failure")
							});
						},
						scope: this
					}
				});
			},

			generateRootUrlPath: function () {
				var sUrl = "";
				var params = "";
				if (this.options.uploadDirectoryPath) {
					if (this.options.uploadDirectoryPath.charAt(0) == "/") {
						sUrl = "lecm/forms/node/search";
						params = "?titleProperty=" + encodeURIComponent("cm:name") + "&xpath=" + encodeURIComponent(this.options.uploadDirectoryPath);
					} else if (this.options.uploadDirectoryPath.charAt(0) == "{") {
						sUrl = "lecm/repository/api/getRootDirectory";
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
							params = "?rootNode=" + encodeURIComponent(location);
						}
					}
				} else {
					sUrl = "lecm/repository/api/getRootDirectory";
					params = "?rootNode=" + encodeURIComponent("alfresco://user/temp");
				}

				return Alfresco.constants.PROXY_URI + sUrl + params;
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
						mode: this.fileUpload.MODE_SINGLE_UPLOAD,
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
					multipleMode: false,
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
				}
			},

			canPreviewShow: function() {
				return Object.keys(this.selectedItems).length > 0;
			},

			updateFormUI: function() {
				Dom.setStyle(Dom.get(this.id + "-uploader-block"), "display", !this.canPreviewShow() ? "block" : "none");
				Dom.setStyle(Dom.get(this.id + "-uploader-preview-container"), "display", this.canPreviewShow() ? "block" : "none");
				Dom.setStyle(Dom.get(this.id + "-uploader-remove"), "display", this.canPreviewShow() ? "block" : "none");
			},

			updateSelectedItems: function AssociationAutoComplete_updateSelectedItems() {
				if (this.selectedItems != null && Object.keys(this.selectedItems).length > 0) {
					var key = Object.keys(this.selectedItems)[0];
					var item = this.selectedItems[key];
					var me = this;

					Alfresco.util.Ajax.request(
						{
							url: Alfresco.constants.URL_SERVICECONTEXT + "components/preview/web-preview",
							dataObj: {
								nodeRef: item.nodeRef,
								htmlid: this.id + "-uploader-preview-container"
							},
							successCallback: {
								fn:function(response){
									Dom.get(me.id + "-uploader-preview-container").innerHTML = response.serverResponse.responseText;
                                    var previewId = me.id + "-uploader-preview-container-full-window-div";

									if (me.flashUploaderWasShow) {
										me.flashUploaderWasShow = false;
										Event.onAvailable(previewId, function() {
											var preview = Dom.get(previewId);
											var container = Dom.get(me.id + "-uploader-preview-container-previewer-div");
											container.innerHTML = "";

											preview.setAttribute("style", "");
											container.appendChild(preview);

											me.flashUploaderWasShow = true;
										}, {}, me);
									}
								},
								scope: this
							},
							failureMessage: this.msg("message.failure"),
							scope: this,
							execScripts: true
						});
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
				Dom.get(this.id).value = selectedItems.toString();

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

			getAddedItems: function() {
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

			getRemovedItems: function() {
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
			},

			onRemoveClick: function () {
				this.selectedItems = [];
				this.updateSelectedItems();
				this.updateFormFields();
				this.updateFormUI();
			}
		});
})();