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
				itemId: "",
				forTask: true,
                selectedAttachmentNodeRef: "",
				baseDocAssocName: null
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
				if (this.options.itemId != null) {
					if (this.options.forTask===true){
						Alfresco.util.Ajax.request({
							method: "GET",
							url: Alfresco.constants.PROXY_URI_RELATIVE + "lecm/workflow/GetDocumentDataByTaskId?taskID=" + this.options.itemId,
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
					} else {
						this.documentNodeRef = this.options.itemId;
						this.loadDocumentAttachments();
					}
				}
			},

			loadDocumentAttachments: function() {
				if (this.options.itemId != null) {
					Alfresco.util.Ajax.request({
						method: "GET",
						url: Alfresco.constants.PROXY_URI_RELATIVE + "lecm/document/attachments/api/get",
						dataObj: {
							documentNodeRef: this.documentNodeRef,
							showEmptyCategory: true,
							baseDocAssocName: this.options.baseDocAssocName
						},
						successCallback: {
							fn: function (response) {
								var result = response.json;
								if (result != null) {
									this.attachmentsSelect.innerHTML = "";

									if (result.items != null && result.items.length > 0) {
                                        var attachmentsCount = 0;

										for (var i = 0; i < result.items.length; i++) {
											var item = result.items[i];

											var optionGroup = document.createElement("optgroup");
											if (item.category != null && item.category.name != null) {
												optionGroup.label = item.category.name;
											}

											if (item.attachments != null && item.attachments.length > 0) {
                                                attachmentsCount += item.attachments.length;

												for (var j = 0; j < item.attachments.length; j++) {
													var attachment = item.attachments[j];

													if (attachment.nodeRef != null) {
														var option = document.createElement("option");
														option.value = attachment.nodeRef;
                                                        if(attachment.nodeRef == this.options.selectedAttachmentNodeRef) {
	                                                        option.selected = true;
                                                        }
														if (attachment.name != null) {
															option.innerHTML = attachment.name;
														}

														optionGroup.appendChild(option);
													}
												}
											}

											this.attachmentsSelect.appendChild(optionGroup);
										}

                                        if (attachmentsCount > 0) {
                                            Dom.removeClass(this.id + "-attachment-select-container", "hidden1");
                                        } else {
                                            Dom.removeClass(this.id + "-attachment-select-empty-container", "hidden1");
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
                if (this.attachmentsSelect.value) {
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
                                var dialog = LogicECM.module.Base.Util.getLastDialog();
                                if (dialog != null) {
                                    dialog.dialog.center();
                                }
                                Event.onAvailable(previewId, function() {
									var preview = Dom.get(previewId);
									var container = Dom.get(this.id + "-preview-container-previewer-div");

									container.innerHTML = "";
                                    preview.setAttribute("style", "");
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
			}
		});
})();
