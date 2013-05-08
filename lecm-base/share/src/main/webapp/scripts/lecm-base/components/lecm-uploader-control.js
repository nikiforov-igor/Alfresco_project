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
 * LogicECM top-level module namespace.
 *
 * @namespace LogicECM
 * @class LogicECM.control
 */
LogicECM.control = LogicECM.control || {};

(function() {

	var Dom = YAHOO.util.Dom,
		Event = YAHOO.util.Event;

	LogicECM.control.Uploader = function (fieldHtmlId)
	{
		LogicECM.control.Uploader.superclass.constructor.call(this, "LogicECM.control.Uploader", fieldHtmlId);
		this.selectedItems = {};
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
			currentValue: ""
		},

		onReady: function () {
			this.options.controlId = this.id + '-cntrl';

			if (!this.options.disabled) {
				this.setUploaders();
				this.loadRootNode();

				this.widgets.uploadButton =  new YAHOO.widget.Button(
					this.id + "-cntrl-file-upload-button",
					{ onclick: { fn: this.onFileUpload, obj: null, scope: this } }
				);
			}
		},

		setUploaders: function() {
			this.setUploader("components/upload/html-upload", this.options.controlId + "-html-uploader", false);
			this.setUploader("components/upload/flash-upload", this.options.controlId + "-flash-uploader", true);
			this.setUploader("components/upload/file-upload", this.options.controlId + "-file-uploader", false);
		},

		setUploader: function(url, containerId, createWrap) {
			var htmlId = this.id + Alfresco.util.generateDomId();

			Alfresco.util.Ajax.request(
				{
					url: Alfresco.constants.URL_SERVICECONTEXT + url,
					dataObj: {
						htmlid: htmlId
					},
					successCallback: {
						fn:function(response){
							var text = response.serverResponse.responseText;
							if (createWrap) {
								Dom.get(containerId).innerHTML =  "<div id='" + htmlId + "'>" + text + "</div>";
							} else {
								Dom.get(containerId).innerHTML =  text;
							}
						},
						scope: this
					},
					failureMessage: this.msg("message.failure"),
					scope: this,
					execScripts: true
				});
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
							var response = YAHOO.lang.JSON.parse(oResponse.responseText);
							this.widgets.dataTable.set("MSG_ERROR", response.message);
							this.widgets.dataTable.showTableMessage(response.message, YAHOO.widget.DataTable.CLASS_ERROR);
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

				this.selectedItems[nodeRef] = obj.successful[0];
				this.updateFormFields();
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


				var divClass = (num++) % 2 > 0 ? "association-auto-complete-selected-item-even" : "association-auto-complete-selected-item";
				if(this.options.disabled) {
					el.innerHTML += '<div class="' + divClass + '"> ' + displayName + ' ' + '</div>';
				} else {
					el.innerHTML
						+= '<div class="' + divClass + '"> ' + displayName + '</div>';
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

				Dom.get(this.eventGroup).value = selectedItems.toString();

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