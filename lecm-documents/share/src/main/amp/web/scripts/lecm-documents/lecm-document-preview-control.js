/**
 * LogicECM root namespace.
 *
 * @namespace LogicECM
 */
// Ensure LogicECM root object exists
if (typeof LogicECM == 'undefined' || !LogicECM) {
	var LogicECM = {};
}

/**
 * LogicECM top-level control namespace.
 *
 * @namespace LogicECM
 * @class LogicECM.control
 */
LogicECM.module = LogicECM.module || {};

LogicECM.module.Documents = LogicECM.module.Documents || {};

(function() {
	var Dom = YAHOO.util.Dom,
		Event = YAHOO.util.Event;

	LogicECM.module.Documents.DocumentPreviewControl = function(fieldHtmlId) {
		LogicECM.module.Documents.DocumentPreviewControl.superclass.constructor.call(this, 'LogicECM.module.Documents.DocumentPreviewControl', fieldHtmlId, ['container']);

		return this;
	};

	YAHOO.extend(LogicECM.module.Documents.DocumentPreviewControl, Alfresco.component.Base, {
		options: {
			taskId: '',
			height: '',
			viewerHeight: ''
		},

		documentNodeRef: null,

		attachmentsSelect: null,

		onReady: function() {
			this.attachmentsSelect = Dom.get(this.id + '-attachment-select');

			if (this.attachmentsSelect != null) {
				this.loadDocument();
			}

			Event.on(this.attachmentsSelect, 'change', this.reloadAttachmentPreview, null, this);
		},

		loadDocument: function() {
			if (this.options.taskId != null) {
				Alfresco.util.Ajax.request({
					method: 'GET',
					url: Alfresco.constants.PROXY_URI_RELATIVE + 'lecm/workflow/GetDocumentDataByTaskId?taskID=' + this.options.taskId,
					successCallback: {
						scope: this,
						fn: function(response) {
							var result = response.json;
							if (result != null && result.nodeRef != null) {
								this.documentNodeRef = result.nodeRef;
								this.loadDocumentAttachments();
							}
						}
					}
				});
			}
		},

		loadDocumentAttachments: function() {
			if (this.options.taskId != null) {
				Alfresco.util.Ajax.request({
					method: 'GET',
					url: Alfresco.constants.PROXY_URI_RELATIVE + 'lecm/document/attachments/api/get?documentNodeRef=' + this.documentNodeRef + '&showEmptyCategory=true',
					successCallback: {
						scope: this,
						fn: function(response) {
							var result = response.json;
							if (result != null) {
								this.attachmentsSelect.innerHTML = '';

								if (result.items != null && result.items.length > 0) {
									for (var i = 0; i < result.items.length; i++) {
										var item = result.items[i];

										var optionGroup = document.createElement('optgroup');
										if (item.category != null && item.category.name != null) {
											optionGroup.label = item.category.name;
										}

										if (item.attachments != null && item.attachments.length > 0) {
											for (var j = 0; j < item.attachments.length; j++) {
												var attachment = item.attachments[j];

												if (attachment.nodeRef != null) {
													var option = document.createElement('option');
													option.value = attachment.nodeRef;
													if (attachment.name != null) {
														option.innerHTML = attachment.name;
													}
													optionGroup.appendChild(option);
												}
											}
										}
										this.attachmentsSelect.appendChild(optionGroup);
									}
								}
								this.reloadAttachmentPreview();
							}
						}
					}
				});
			}
		},

		reloadAttachmentPreview: function() {
			if (this.attachmentsSelect.value) {
				Alfresco.util.Ajax.request({
					url: Alfresco.constants.URL_SERVICECONTEXT + 'components/preview/web-preview',
					dataObj: {
						nodeRef: this.attachmentsSelect.value,
						htmlid: this.id + '-preview-container'
					},
					successCallback: {
						scope: this,
						fn: this.reloadAttachmentPreviewSuccessCallback
					},
					failureMessage: this.msg('message.failure'),
					scope: this,
					execScripts: true
				});
			}
		},

		reloadAttachmentPreviewSuccessCallback: function(response) {
			var options = {
				fullWindowDiv: this.id + '-preview-container-full-window-div',
				previewerDiv: this.id + '-preview-container-previewer-div',
				viewer: this.id + '-preview-container-viewer'
			}

			Dom.get(this.id + '-preview-container').innerHTML = response.serverResponse.responseText;

			Event.onAvailable(options.fullWindowDiv, this.onFullWindowDivAvaliable, options, this);
			if (this.options.height) {
				Event.onAvailable(options.previewerDiv, this.onPreviewerDivAvaliable, options, this);
				if (this.options.viewerHeight) {
					Event.onContentReady(options.viewer, this.onViewerReady, options, this);
				}
			}
		},

		onFullWindowDivAvaliable: function(obj) {
			var preview = Dom.get(obj.fullWindowDiv);
			var container = Dom.get(obj.previewerDiv);

			container.innerHTML = '';
			preview.setAttribute('style', '');
			container.appendChild(preview);
		},

		onPreviewerDivAvaliable: function(obj) {
			Dom.setStyle(obj.previewerDiv, 'height', this.options.height);
		},

		onViewerReady: function(obj) {

			function onWebPreviewResize(obj) {
				var subscribers = webPreview.plugin.onResize.subscribers;
				var oldSubscribers = YAHOO.lang.merge({subscribers: subscribers});

				webPreview.plugin.onResize.unsubscribeAll();
				Dom.setStyle(obj.previewerDiv, 'height', this.options.height);
				Dom.setStyle(obj.viewer, 'height', this.options.viewerHeight);
				webPreview.plugin.onResize.subscribers = oldSubscribers.subscribers;
				return false;
			}

			var webPreview, components = Alfresco.util.ComponentManager.find({name: 'Alfresco.WebPreview'});

				Dom.setStyle(obj.previewerDiv, 'height', this.options.height);
				Dom.setStyle(obj.viewer, 'height', this.options.viewerHeight);

			if (components && components.length) {
				webPreview = components[0];
				if (webPreview.plugin && webPreview.plugin.onResize) {
					webPreview.plugin.onResize.subscribe(onWebPreviewResize, obj, this);
				}
			}
		}
	});
})();
