if (typeof LogicECM == "undefined" || !LogicECM) {
	LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.Calendar = LogicECM.module.Calendar || {};

(function() {
	var Dom = YAHOO.util.Dom,
		Event = YAHOO.util.Event,
		Util = LogicECM.module.Base.Util,
		KeyListener = YAHOO.util.KeyListener;

	LogicECM.module.Calendar.Edit = function(htmlId) {
		LogicECM.module.Calendar.Edit.superclass.constructor.call(this, htmlId);
		return this;
	};

	YAHOO.extend(LogicECM.module.Calendar.Edit, LogicECM.module.Documents.Edit);

	YAHOO.lang.augmentObject(LogicECM.module.Calendar.Edit.prototype, {
		allAttachments: [],
		attachmentsRootNode: null,

		onReady: function () {
			this.loadForm();
			this.loadPermissions();
		},

		onFormSubmitSuccess: function (response) {
			var repeatable = Dom.get(this.runtimeForm.formId)["prop_lecm-events_repeatable"];
			if (repeatable.value == "true") {
				var updateRepeatableFormId = this.id + "-update-repeated-form";
				var dialog = Alfresco.util.createYUIPanel(updateRepeatableFormId,
					{
						width: "50em",
						close: false
					});
				Alfresco.util.createYUIButton(this, "update-repeated-form-ok", this.onUpdateEvent);
				Dom.setStyle(updateRepeatableFormId, "display", "block");
				dialog.show();

				this._hideSplash();
			} else {
				this.onUpdateEvent();
			}
		},

		onUpdateEvent: function() {
			var radio = document.getElementsByName('events-update-repeated');
			var updateRepeatedValue = "THIS";
			for(var i = 0; i < radio.length; i++){
				if(radio[i].checked){
					updateRepeatedValue = radio[i].value;
				}
			}

			var me = this;
			Alfresco.util.Ajax.request(
				{
					url: Alfresco.constants.PROXY_URI + "lecm/events/afterUpdate",
					dataObj: {
						eventNodeRef: this.options.nodeRef,
						updateRepeated: updateRepeatedValue
					},
					successCallback: {
						fn: function (o) {
							window.location.href = Alfresco.constants.URL_PAGECONTEXT + "event?nodeRef=" + me.options.nodeRef;
						},
						scope: this
					},
					failureMessage: this.msg("load.fail")
				});
		},

		onBeforeFormRuntimeInit: function(layer, args) {
			this.runtimeForm = args[1].runtime;
			var submitElement = this.runtimeForm.submitElements[0];
			var originalSubmitFunction = submitElement.submitForm;

			submitElement.submitForm = this.onSubmit.bind(this, originalSubmitFunction, submitElement);

			var actionSubmit = Dom.get(this.id + "-event-action-save");
			if (actionSubmit != null) {
				YAHOO.util.Event.addListener(actionSubmit, "click", this.onSubmit.bind(this, originalSubmitFunction, submitElement));
			}

			var actionCancel = Dom.get(this.id + "-event-action-cancel");
			if (actionCancel != null) {
				YAHOO.util.Event.addListener(actionCancel, "click", this.onCancelButtonClick, null, this);
			}

			args[1].runtime.setAJAXSubmit(true,
				{
					successCallback:
					{
						fn: this.onFormSubmitSuccess,
						scope: this
					},
					failureCallback:
					{
						fn: this.onFormSubmitFailure,
						scope: this
					}
				});
			YAHOO.Bubbling.unsubscribe("beforeFormRuntimeInit", this.onBeforeFormRuntimeInit, this);
		},

		onSubmit: function(fn, scope) {
			if (this.runtimeForm.validate()) {
				this._showSplash();

				var fromDate = Dom.get(this.runtimeForm.formId)["prop_lecm-events_from-date"];
				var toDate = Dom.get(this.runtimeForm.formId)["prop_lecm-events_to-date"];
				var allDay = Dom.get(this.runtimeForm.formId)["prop_lecm-events_all-day"];
				var location = Dom.get(this.runtimeForm.formId)["assoc_lecm-events_location-assoc"];
				var timezoneOffset = new Date().getTimezoneOffset();

				Alfresco.util.Ajax.jsonPost({
					url: Alfresco.constants.PROXY_URI + "lecm/events/event/checkAvailable",
					dataObj: {
						"fromDate": fromDate.value,
						"toDate": toDate.value,
						"allDay": allDay.value,
						"clientTimezoneOffset": timezoneOffset,
						"location": location.value,
						"event": this.options.nodeRef
					},
					successCallback: {
						fn: function refreshSuccess(response) {
							var json = response.json;

							if (json.locationAvailable) {
								var notAvailableEmployees = "";
								if (json.members != null) {
									for (var i = 0; i < json.members.length; i++) {
										if (!json.members[i].available) {
											if (notAvailableEmployees.length > 0) {
												notAvailableEmployees += ", "
											}
											notAvailableEmployees += json.members[i].name;
										}
									}
								}

								if (notAvailableEmployees.length > 0)  {
									this._hideSplash();

									Alfresco.util.PopupManager.displayPrompt(
										{
											title: this.msg("title.confirm.event.employees.notAvailable"),
											text: this.msg("message.event.employees.notAvailable", notAvailableEmployees),
											buttons:[
												{
													text:this.msg("button.ok"),
													handler:function () {
														this.destroy();
														if (YAHOO.lang.isFunction(fn) && scope) {
															fn.call(scope);
														}
													}
												},
												{
													text:this.msg("button.cancel"),
													handler:function () {
														this.destroy();
													},
													isDefault:true
												}
											]
										});

								} else {
									if (YAHOO.lang.isFunction(fn) && scope) {
										fn.call(scope);
									}
								}
							} else {
								this._hideSplash();
								Alfresco.util.PopupManager.displayMessage(
									{
										text: this.msg("message.event.location.notAvailable")
									});
							}
						},
						scope: this
					},
					failureCallback: {
						fn: function refreshFailure(response) {
							console.log(response);
						},
						scope: this
					}
				});
			} else {
				if (YAHOO.lang.isFunction(fn) && scope) {
					fn.call(scope);
				}
			}
		},

		initAttachments: function() {
			this.loadAttachments();

			var uploaderButton = this.id + "-uploader-button";
			Event.on(uploaderButton, "click", this.showUploader, null, this);
			new KeyListener(uploaderButton,
				{
					keys: KeyListener.KEY.ENTER
				},
				{
					fn: this.showUploader,
					scope: this,
					correctScope: true
				}, KeyListener.KEYDOWN).enable();
			this.widgets.uploaderButton = uploaderButton;
		},

		loadAttachments: function() {
			this.allAttachments = [];
			Alfresco.util.Ajax.request({
				method: "GET",
				url: Alfresco.constants.PROXY_URI_RELATIVE + "lecm/document/attachments/api/get?documentNodeRef=" + this.options.nodeRef + "&showEmptyCategory=true",
				successCallback: {
					fn: function (response) {
						var result = response.json;
						if (result != null) {
							if (result.items != null && result.items.length > 0) {
								for (var i = 0; i < result.items.length; i++) {
									var item = result.items[i];
									if (item.attachments != null) {
										for (var j  = 0; j < item.attachments.length; j++) {
											this.allAttachments.push(item.attachments[j]);
										}
									}
									if (item.category != null && !item.category.isReadOnly) {
										this.attachmentsRootNode = item.category.nodeRef;
									}
								}
							}
						}

						this.initDndUploader();
						this.updateAttachmentsView();
					},
					scope: this
				}
			});
		},

		initDndUploader: function() {
			if (this.attachmentsRootNode != null) {
				var uploader = new LogicECM.DndUploader(this.id + "-uploader-block");
				uploader.initUploader({
					disabled: false,
					destinationName: this.msg("title.event.upload.attachments.directoryName"),
					destination: this.attachmentsRootNode,
					multipleMode: true,
					onFileUploadComplete: {
						fn: this.fileUploadComplete,
						scope: this
					}
				});
			}
		},

		updateAttachmentsView: function () {
			var items = this.allAttachments;
			var elAttachments = Dom.get(this.id + "-attachments");

			if (elAttachments) {
				elAttachments.innerHTML = '';

				for (var i = 0; i < items.length; i++) {
					var item = items[i];
					var fileName = item.name;
					var nodeRef = item.nodeRef;

					var fileIcon = Alfresco.util.getFileIcon(fileName, "cm:content", 16);
					var fileIconHtml = "<img src='" + Alfresco.constants.URL_RESCONTEXT + "components/images/filetypes/" + fileIcon + "' width='16' height='16'/>";
					fileName = "<span>" + fileName + "</span>";
					var leftPart = fileIconHtml + fileName;
					leftPart = "<a href='" + Alfresco.constants.URL_PAGECONTEXT + "document-attachment?nodeRef=" + nodeRef + "'>" + leftPart + "</a>";

					var rightPart = "";
					if (this.hasDeleteContentRight) {
						var iconRemoveId = "attachment-remove-" + nodeRef;
						rightPart += "<img id='" + iconRemoveId + "' src='" + Alfresco.constants.URL_RESCONTEXT
							+ "components/images/delete-16.png' class='remove-icon'/>";
						Event.onAvailable(iconRemoveId, this.attachRemoveItemClickListener, item, this);
					}

					var rowId = "attachment-" + nodeRef.replace(/:|\//g, '_');
					elAttachments.innerHTML += "<li id='" + rowId + "'>" + Util.getCroppedItem(leftPart, rightPart) + "</li>";
				}
			}
		},

		attachRemoveItemClickListener: function (node) {
			Event.addListener("attachment-remove-" + node.nodeRef, "click", this.removeSelectedElement, node, this);
		},

		removeSelectedElement: function (event, node) {
			Alfresco.util.Ajax.request({
				method: "DELETE",
				url: Alfresco.constants.PROXY_URI + "slingshot/doclib/action/file/node/" + node.nodeRef.replace(":/", ""),
				successCallback: {
					fn: function (response) {
						Alfresco.util.PopupManager.displayMessage(
							{
								text: Alfresco.util.message("message.delete.success")
							});
						this.loadAttachments();
					},
					scope: this
				}
			});
		},

		showUploader: function() {
			if (this.attachmentsRootNode != null) {
				if (this.fileUpload == null)
				{
					this.fileUpload = Alfresco.getFileUploadInstance();
				}

				var uploadConfig =
				{
					uploadDirectoryName: this.msg("title.event.upload.attachments.directoryName"),
					destination: this.attachmentsRootNode,
					filter: [],
					mode: this.fileUpload.MODE_MULTI_UPLOAD,
					thumbnails: "doclib",
					onFileUploadComplete:
					{
						fn: this.fileUploadComplete,
						scope: this
					},
					suppressRefreshEvent: this.options.suppressRefreshEvent
				};
				this.fileUpload.show(uploadConfig);
			}
		},

		fileUploadComplete: function(obj) {
			var me = this;
			if (obj.successful != null && obj.successful.length > 0) {
				for (var i = 0; i < obj.successful.length; i++) {
					var fileName = obj.successful[i].fileName;
					var nodeRef = obj.successful[i].nodeRef;

					this.allAttachments.push({
						nodeRef: nodeRef,
						name: fileName,
						justUpload: true
					});
				}

				me.updateAttachmentsView();
			}
		},

		loadPermissions: function() {
			Alfresco.util.Ajax.jsonGet({
				url: Alfresco.constants.PROXY_URI_RELATIVE + "lecm/security/api/getPermissions",
				dataObj: {
					nodeRef: this.options.nodeRef,
					permissions: "_lecmPerm_ContentDelete"
				},
				successCallback: {
					scope: this,
					fn: function (response) {
						var oResults = response.json;
						if (oResults) {
							this.hasDeleteContentRight = response.json[0];
						}

						this.initAttachments();
					}
				},
				failureMessage: this.msg("message.failure")
			});
		}
	}, true);
})();