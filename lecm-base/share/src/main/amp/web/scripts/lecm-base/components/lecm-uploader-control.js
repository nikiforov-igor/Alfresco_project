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
 * LogicECM top-level module namespace.
 *
 * @namespace LogicECM
 * @class LogicECM.control
 */
LogicECM.control = LogicECM.control || {};

(function() {

	var Dom = YAHOO.util.Dom,
		Event = YAHOO.util.Event,
		Util = LogicECM.module.Base.Util;

	LogicECM.control.Uploader = function (fieldHtmlId)
	{
		LogicECM.control.Uploader.superclass.constructor.call(this, "LogicECM.control.Uploader", fieldHtmlId);
		this.selectedItems = {};
		this.currentValueHtmlId = fieldHtmlId;
		return this;
	};

	YAHOO.extend(LogicECM.control.Uploader, Alfresco.component.Base, {
		fileUpload: null,
		selectedItems: null,
		rootNodeRef: null,

		options: {
			disabled: false,
			controlId: null,
			uploadDirectoryPath: null,
			currentValue: "",

            /**
             * Отображать загруженный контент
             */
            showImage: false,

            /**
             * Загрузка нескольких файлов поочередно
             */
            multiple: false,

            mandatory: false,

			currentValueHtmlId: ""
		},

		onReady: function () {
			this.options.controlId = this.id + '-cntrl';

			if (!this.options.disabled) {
				LogicECM.LecmUploaderInitializer.initLecmUploaders();
				this.loadRootNode();
                this.loadFileName();
				this.widgets.uploadButton =  new YAHOO.widget.Button(
					this.id + "-cntrl-file-upload-button",
					{ onclick: { fn: this.onFileUpload, obj: null, scope: this } }
				);
			}

            if (this.options.showImage){
                var imageContainer = YAHOO.util.Dom.get(this.options.controlId+"-container");
                var imgRef = this.generateThumbnailUrl(this.options.currentValue, false);
                var className = this.options.disabled ? "thumbnail-view" :"thumbnail-edit";

                if (imgRef != "") {
                    var ref = this.options.currentValue;
                    var imageId = ref.slice(ref.lastIndexOf('/') + 1);
                    imageContainer.innerHTML = '<span class="'+ className +'">' + '<a href="' + this.generateThumbnailUrl(ref, true) +'" target="_blank"><img id="' + imageId + '" src="' + imgRef + '" /></a></span>';
                } else {
                    imageContainer.innerHTML = '<span class="'+ className+'-text">' + this.msg('message.upload.not-loaded') + '</span>';
                }
            }
		},

		loadRootNode: function () {
			Alfresco.util.Ajax.jsonGet({
				url: this.generateRootUrlPath(),
				successCallback: {
					fn: function (response) {
						var oResults = response.json;
						if (oResults) {
							this.rootNodeRef = oResults.nodeRef;
						}
					},
					scope: this
				},
				failureCallback: {
					fn: function (response) {
						this.widgets.dataTable.set("MSG_ERROR", response.json.message);
						this.widgets.dataTable.showTableMessage(response.json.message, YAHOO.widget.DataTable.CLASS_ERROR);
					},
					scope: this
				}
			});
		},

        loadFileName: function () {
            if (this.options.currentValue && this.options.currentValue != "" && this.options.currentValue.indexOf(",") < 0) {
                Alfresco.util.Ajax.jsonGet(
                    {
                        url: Alfresco.constants.PROXY_URI + "api/metadata?nodeRef=" + this.options.currentValue,
                        successCallback: {
                            fn: function (response) {
                                var oResults = response.json;
                                if (oResults != null) {
                                    var el = Dom.get(this.options.controlId + "-currentValueDisplay");
                                    el.innerHTML = '';
                                    var displayName = oResults.properties["{http://www.alfresco.org/model/content/1.0}name"];
                                    el.innerHTML += '<div><span>' + displayName + '</span></div>';
                                }
                            },
                            scope: this
                        }
                    });
            }
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

		onFileUpload: function(e, obj) {
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
                    // данный параметр запрещает обновление формы
                    // в false выполняется bubbling metadataRefresh
//                    suppressRefreshEvent: true
				};
				this.fileUpload.show(uploadConfig);
				Event.preventDefault(e);
			}
		},

		getRemoveButtonHTML: function (node, dopId)
		{
			if (!dopId) {
				dopId = "";
			}
			return '<a href="javascript:void(0);" class="remove-item" id="t-' + this.options.controlId + node.nodeRef + dopId + '"></a>';
		},

		fileUploadComplete: function(obj) {
			if (obj.successful != null && obj.successful.length > 0) {
				var fileName = obj.successful[0].fileName;
				var nodeRef = obj.successful[0].nodeRef;

                if (this.options.showImage){
                    this.updateImage(nodeRef);
                }

                if (!this.options.multiple) {
                    this.selectedItems = {};
                }
				this.selectedItems[nodeRef] = obj.successful[0];
				this.updateFormFields();
			}
		},

        updateImage: function(nodeRef){
            var imageContainer = Dom.get(this.options.controlId+"-container");
            var url = this.generateThumbnailUrl(nodeRef, this.options.disabled);
            imageContainer.innerHTML = '<span class="'+ "thumbnail-edit" +'">' + '<a href="' + url +'" target="_blank"><img id="' + nodeRef + '" src="' + url + '" /></a></span>';
        },

        generateThumbnailUrl: function(ref, view) {
            if (ref != null && ref != undefined && ref.length > 0) {
                var nodeRef = new Alfresco.util.NodeRef(ref);
                if (!view) {
                    return Alfresco.constants.PROXY_URI + "api/node/" + nodeRef.uri + "/content/thumbnails/doclib?c=force&ph=true";
                } else {
                    return Alfresco.constants.PROXY_URI + "api/node/" + nodeRef.uri + "/content";
                }
            } else {
                return "";
            }
        },

		// Updates all form fields
		updateFormFields: function ()
		{
			// Just element
			var el;

			el = Dom.get(this.options.controlId + "-currentValueDisplay");
			el.innerHTML = '';
			var num = 0;
			for (var i in this.selectedItems) {
				var displayName = this.selectedItems[i].fileName;

				if(this.options.multiple) {
					el.innerHTML += Util.getCroppedItem(Util.getControlDefaultView(displayName));
				} else {
					el.innerHTML = Util.getCroppedItem(Util.getControlDefaultView(displayName));
				}
			}

			if(!this.options.disabled)
			{
				var addItems = this.getAddedItems();

				// Update added fields in main form to be submitted
				el = Dom.get(this.options.controlId + "-added");
				el.value = '';
				for (i in addItems) {
					el.value += ( i < addItems.length-1 ? addItems[i] + ',' : addItems[i] );
				}

				var removedItems = this.getRemovedItems();

				// Update removed fields in main form to be submitted
				el = Dom.get(this.options.controlId + "-removed");
				el.value = '';
				for (i in removedItems) {
					el.value += (i < removedItems.length-1 ? removedItems[i] + ',' : removedItems[i]);
				}

				var selectedItems = this.getSelectedItems();

				// Update selectedItems fields in main form to pass them between popup and form
				el = Dom.get(this.options.controlId + "-selectedItems");
				el.value = '';
				for (i in selectedItems) {
					el.value += (i < selectedItems.length-1 ? selectedItems[i] + ',' : selectedItems[i]);
				}

				Dom.get(this.currentValueHtmlId).value = selectedItems.toString();

				if (this.options.changeItemsFireAction != null && this.options.changeItemsFireAction != "") {
					YAHOO.Bubbling.fire(this.options.changeItemsFireAction, {
						selectedItems: this.selectedItems
					});
				}

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
			}
		},

		getAddedItems: function AssociationTreeViewer_getAddedItems()
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

		getRemovedItems: function AssociationTreeViewer_getRemovedItems()
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
		},

		getSelectedItems:function AssociationTreeViewer_getSelectedItems() {
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