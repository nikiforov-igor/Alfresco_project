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
LogicECM.module = LogicECM.module || {};

LogicECM.module.Documents = LogicECM.module.Documents || {};

(function () {
	var Dom = YAHOO.util.Dom,
		Event = YAHOO.util.Event;

	LogicECM.module.Documents.DocumentPreviewControl = function (fieldHtmlId) {
		LogicECM.module.Documents.DocumentPreviewControl.superclass.constructor.call(this, "LogicECM.module.Documents.DocumentPreviewControl", fieldHtmlId, [ "container"]);

		return this;
	};

	YAHOO.extend(LogicECM.module.Documents.DocumentPreviewControl, Alfresco.component.Base,
		{
			options:{
				taskId: ""
			},

			documentNodeRef: null,

			attachmentsSelect: null,

			onReady:function () {
				this.attachmentsSelect = Dom.get(this.id + "-attachment-select");

				if (this.attachmentsSelect != null) {
					this.loadDocument();
				}

				Event.on(this.attachmentsSelect, "change", this.reloadAttachmentPreview, this, true);
			},

			loadDocument: function() {
				if (this.options.taskId != null) {
					Alfresco.util.Ajax.request({
						method: "GET",
						url: Alfresco.constants.PROXY_URI_RELATIVE + "lecm/approval/GetDocumentDataByTaskId?taskID=" + this.options.taskId,
						successCallback: {
							fn: function (response) {
								var result = response.json;
								if (result != null && result.nodeRef != null) {
									this.documentNodeRef = result.nodeRef;
									this.loadDocumentAttachments();
								}
							},
							scope: this
						}
					});
				}
			},

			loadDocumentAttachments: function() {
				if (this.options.taskId != null) {
					Alfresco.util.Ajax.request({
						method: "GET",
						url: Alfresco.constants.PROXY_URI_RELATIVE + "lecm/document/attachments/api/get?documentNodeRef=" + this.documentNodeRef + "&showEmptyCategory=true",
						successCallback: {
							fn: function (response) {
								var result = response.json;
								if (result != null) {
									this.attachmentsSelect.innerHTML = "";

									if (result.items != null && result.items.length > 0) {
										for (var i = 0; i < result.items.length; i++) {
											var item = result.items[i];

											var optionGroup = document.createElement("optgroup");
											if (item.category != null && item.category.name != null) {
												optionGroup.label = item.category.name;
											}

											if (item.attachments != null && item.attachments.length > 0) {
												for (var j = 0; j < item.attachments.length; j++) {
													var attachment = item.attachments[j];

													if (attachment.nodeRef != null) {
														var option = document.createElement("option");
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
							},
							scope: this
						}
					});
				}
			},

			reloadAttachmentPreview: function() {
				Alfresco.util.Ajax.request(
					{
						url: Alfresco.constants.URL_SERVICECONTEXT + "components/preview/web-preview",
						dataObj: {
							nodeRef: this.attachmentsSelect.value,
							htmlid: this.id + "-preview-container"
						},
						successCallback: {
							fn:function(response){
								Dom.get(this.id + "-preview-container").innerHTML = response.serverResponse.responseText;
                                var previewId = this.id + "-preview-container-full-window-div";

								Event.onAvailable(previewId, function() {
									var preview = Dom.get(previewId);
									var container = Dom.get(this.id + "-preview-container-previewer-div");

									container.innerHTML = "";
									container.appendChild(preview);
								}, {}, this);
							},
							scope: this
						},
						failureMessage: this.msg("message.failure"),
						scope: this,
						execScripts: true
					});
			}
		});
})();