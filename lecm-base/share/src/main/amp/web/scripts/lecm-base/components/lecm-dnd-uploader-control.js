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
		Event = YAHOO.util.Event,
		Util = LogicECM.module.Base.Util,
        Selector = YAHOO.util.Selector,
        KeyListener = YAHOO.util.KeyListener;

	LogicECM.control.DndUploader = function (fieldHtmlId) {
		LogicECM.control.DndUploader.superclass.constructor.call(this, "LogicECM.control.DndUploader", fieldHtmlId, [ "container"]);

		this.currentValueHtmlId = fieldHtmlId;
		this.selectedItems = {};

		return this;
	};

	YAHOO.extend(LogicECM.control.DndUploader, Alfresco.component.Base,
		{
			disabled: false,
            options:{
				disabled: false,
				uploadDirectoryPath: null,
				multipleMode: true,
				directoryName: true,
				autoSubmit: false,
				currentValue: "",
				defaultValue: "",
				showUploadNewVersion: false,
				checkRights: false,
				itemNodeRef: false,
                suppressRefreshEvent: false,
                useDnD: true,
                showPreview: true,
	            defaultSelectedShowPreviewButton: false
			},

			currentValueHtmlId: "",
			rootNodeRef: null,
			fileUpload: null,
			selectedItems: null,

			hasViewContentRight: null,
			hasAddContentRight: null,
			hasDeleteContentRight: null,
			hasNewVersionContentRight: null,
			selectedPreviewFile: null,

			onReady:function () {
				if (!this.options.disabled) {
                    var me = this;
                    LogicECM.LecmUploaderInitializer.initLecmUploaders();
					this.loadRootNode();

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
                    me.widgets.uploaderButton = uploaderButton;

                    var updPreviewBtnId = this.id + "-show-preview-button";
					if (Dom.get(updPreviewBtnId) != null) {
                        this.widgets.showPreviewButton = new YAHOO.widget.Button(
                            updPreviewBtnId,
                            {
                                type: "checkbox",
                                onclick: {
                                    fn: this.updatePreview,
                                    obj: null,
                                    scope: this
                                },
                                checked: this.options.defaultSelectedShowPreviewButton,
                                disabled: true
                            }
                        );
                        new KeyListener(updPreviewBtnId, {keys: KeyListener.KEY.ENTER},
                            {
                                fn: function(layer, args) {
                                    var e = args[1];

                                    me.widgets.showPreviewButton.set("checked", !(me.widgets.showPreviewButton.get("checked")));
                                    me.updatePreview();

                                    e.preventDefault();
                                    e.stopPropagation();
                                },
                                scope: me,
                                correctScope: true
                            }, KeyListener.KEYDOWN).enable();

                    }

                    var list = Dom.get(this.id + '-attachments');
                    if (list) {
                        Event.on(list, "focusin", function (e) {
                            var rows = Selector.query("li", e.target);
                            if (rows && rows.length > 0 && me.selectedPreviewFile == null) {
                                me.selectPreviewFile(null, rows[0].id.substring("attachment-".length));
                                e.target.scrollTop = 0;
                            }
                        });
                        new KeyListener(list, {keys: KeyListener.KEY.ENTER},
                            {
                                fn: function() {
                                    me.widgets.showPreviewButton.set("checked", true);
                                    me.updatePreview();
                                },
                                scope: me,
                                correctScope: true
                            }, KeyListener.KEYDOWN).enable();
                        new KeyListener(list, {keys: KeyListener.KEY.DELETE},
                            {
                                fn: function (layer, args) {
                                    me.removeSelectedElement(args[1], {nodeRef: this.selectedPreviewFile});
                                },
                                scope: me,
                                correctScope: true
                            }, KeyListener.KEYDOWN).enable();
                        new KeyListener(list, {keys: KeyListener.KEY.DOWN},
                            {
                                fn: function (layer, args) {
                                    var e = args[1];
                                    if (me.selectedItems && !me.selectedItems.isEmpty) {
                                        if (this.selectedPreviewFile != null) {
                                            var selectedId = "attachment-" + this.selectedPreviewFile;
                                            var next = Dom.getNextSibling(selectedId);
                                            if (next) {
                                                Dom.removeClass(selectedId, "selected");
                                                Dom.addClass(next.id, "selected");
                                                this.selectedPreviewFile = next.id.substring("attachment-".length);
                                            }
                                        } else {
                                            var firstRow = Dom.getFirstChild(list);
                                            Dom.addClass(firstRow, "selected");
                                            this.selectedPreviewFile = firstRow.id.substring("attachment-".length);
                                        }
                                    }
                                    e.preventDefault();
                                    e.stopPropagation();
                                },
                                scope: me,
                                correctScope: true
                            }, KeyListener.KEYDOWN).enable();
                        new KeyListener(list, {keys: KeyListener.KEY.UP},
                            {
                                fn: function (layer, args) {
                                    var e = args[1];
                                    if (me.selectedItems && !me.selectedItems.isEmpty) {
                                        if (this.selectedPreviewFile != null) {
                                            var selectedId = "attachment-" + this.selectedPreviewFile;
                                            var previous = Dom.getPreviousSibling(selectedId);
                                            if (previous) {
                                                Dom.removeClass(selectedId, "selected");
                                                Dom.addClass(previous.id, "selected");
                                                this.selectedPreviewFile = previous.id.substring("attachment-".length);
                                            }
                                        } else {
                                            var lastRow = Dom.getLastChild(list);
                                            Dom.addClass(lastRow, "selected");
                                            this.selectedPreviewFile = lastRow.id.substring("attachment-".length);
                                        }
                                    }
                                    e.preventDefault();
                                    e.stopPropagation();
                                },
                                scope: me,
                                correctScope: true
                            }, KeyListener.KEYDOWN).enable();
                    }

                    YAHOO.Bubbling.on("hidePanel", function(layer, args) {
                        var panel = args[1].panel;
                        if (panel && panel.id.indexOf("dnd-upload") >= 0 && me.widgets.uploaderButton) {
                            Dom.get(me.widgets.uploaderButton).focus();
                        }
                    });
				}
				if (this.options.checkRights) {
					this.loadPermissions();
				} else {
					this.loadSelectedItems();
				}
			},

			loadPermissions: function() {
				Alfresco.util.Ajax.jsonGet({
					url: Alfresco.constants.PROXY_URI_RELATIVE + "lecm/security/api/getPermissions?nodeRef=" + encodeURIComponent(this.options.itemNodeRef) + "&permissions=" + encodeURIComponent("_lecmPerm_ContentList,_lecmPerm_ContentAddVer,_lecmPerm_ContentAdd,_lecmPerm_ContentDelete"),
					successCallback: {
						fn: function (response) {
							var oResults = response.json;
							if (oResults && oResults.length == 4) {
								this.hasViewContentRight = response.json[0];
								this.hasNewVersionContentRight = response.json[1];
								this.hasAddContentRight = response.json[2];
								this.hasDeleteContentRight = response.json[3];

								if (this.hasViewContentRight) {
									this.loadSelectedItems();
								}

								if (this.hasAddContentRight) {
									Dom.removeClass(this.id + "-uploader-block", "hidden");
								}
							}
						},
						scope: this
					},
					failureMessage: this.msg("message.failure")
				});
			},

			loadRootNode: function () {
				Alfresco.util.Ajax.jsonGet({
					url: this.generateRootUrlPath(),
					successCallback: {
						fn: function (response) {
							var oResults = response.json;
							if (oResults) {
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

				if (arrItems == "" && this.options.defaultValue != null) {
					arrItems += this.options.defaultValue;
				}

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
					if (items.length > 0) {
						this.selectPreviewFile(null, items[0].nodeRef);
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
                if (this.disabled) return;
				if (this.rootNodeRef != null) {
					if (this.fileUpload == null)
					{
						this.fileUpload = Alfresco.getFileUploadInstance();
					}

					var uploadConfig =
					{
						uploadDirectoryName: this.options.directoryName,
						destination: this.rootNodeRef,
						filter: [],
						mode: this.options.multipleMode ? this.fileUpload.MODE_MULTI_UPLOAD : this.fileUpload.MODE_SINGLE_UPLOAD,
						thumbnails: "doclib",
						onFileUploadComplete:
						{
							fn: this.fileUploadComplete,
							scope: this
						},
                        suppressRefreshEvent: this.options.suppressRefreshEvent
					};
					this.fileUpload.show(uploadConfig);

                    this.setTabbingInUploader();
				}
			},

            setTabbingInUploader: function() {
                var fileSelectionInput = this.fileUpload.uploader.fileSelectionInput;
                var fileSelectionButton = Dom.getPreviousSibling(fileSelectionInput);
                if (fileSelectionButton) {
                    // по Enter'у на кнопке открывать диалог выбора
                    new KeyListener(fileSelectionButton, {keys: KeyListener.KEY.ENTER},
                        {
                            fn: function(layer, args) {
                                fileSelectionInput.click();
                            },
                            scope: this,
                            correctScope: true
                        }, KeyListener.KEYDOWN).enable();
                    fileSelectionButton.focus();
                }
            },

			initUploader: function() {
                if (!this.options.useDnD) return;
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

					me.selectPreviewFile(null, obj.successful[obj.successful.length - 1].nodeRef);

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
                if (this.disabled) return;
				delete this.selectedItems[node.nodeRef];

				if (node.nodeRef == this.selectedPreviewFile) {
					var selectedItems = this.getSelectedItems();
					if (selectedItems.length > 0) {
						this.selectPreviewFile(null, selectedItems[0]);
					} else {
						this.selectedPreviewFile = null;
						this.updatePreview();
					}
				}

				this.updateSelectedItems();
				this.updateFormFields();
				this.updateFormUI();
			},

			attachShowPreviewClickListener: function(node) {
				Event.addListener("attachment-show-preview-" + node.nodeRef, "click", this.selectPreviewFile, node.nodeRef, this);
			},

			selectPreviewFile: function(e, nodeRef) {
				if (this.selectedPreviewFile != null) {
					Dom.removeClass("attachment-" + this.selectedPreviewFile, "selected");
				}
				Dom.addClass("attachment-" + nodeRef, "selected");
				this.selectedPreviewFile = nodeRef;
				this.updatePreview();
			},

			updatePreview: function() {
				if (this.widgets.showPreviewButton != null) {
					this.widgets.showPreviewButton.set("disabled", this.selectedPreviewFile == null);
				}
                if (this.options.showPreview) {
                    if (this.selectedPreviewFile != null && (this.widgets.showPreviewButton == null || this.widgets.showPreviewButton.get("checked"))) {
                        YAHOO.Bubbling.fire("showPreview", {
                            nodeRef: this.selectedPreviewFile
                        });
                    } else {
                        YAHOO.Bubbling.fire("hidePreview");
                    }
                }
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
									mode: this.fileUpload.MODE_SINGLE_UPDATE,
                                    suppressRefreshEvent: this.options.suppressRefreshEvent
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
							var fileIconHtml = "<img src='" + Alfresco.constants.URL_RESCONTEXT + "components/images/filetypes/" + fileIcon +"' width='16' height='16'/>";
							fileName = "<span>" + fileName + "</span>";
							var leftPart = fileIconHtml + fileName;
							if (!item.justUpload && !this.options.showPreview) {
								leftPart = "<a href='" + Alfresco.constants.URL_PAGECONTEXT + "document-attachment?nodeRef=" + nodeRef + "'>" + leftPart + "</a>";
							} else {
								var linkId = "attachment-show-preview-" + nodeRef;
								leftPart = "<a href='javascript:void(0);' id='" + linkId + "'>" + leftPart + "</a>";
								Event.onAvailable(linkId, this.attachShowPreviewClickListener, item, this);
							}

							var reghtPart = "";
							if (!this.options.disabled) {
								if (this.options.showUploadNewVersion && (!this.options.checkRights || this.hasNewVersionContentRight)) {
									var iconNewVersionId = "attachment-newVersion-" + nodeRef;
									reghtPart += "<img id='" + iconNewVersionId + "' src='" + Alfresco.constants.URL_RESCONTEXT
										+ "/components/documentlibrary/actions/document-upload-new-version-16.png' class='newVersion-icon'/>";
									Event.onAvailable(iconNewVersionId, this.attachUploadNewVersionClickListener, item, this);
								}

								if (!this.options.checkRights || this.hasDeleteContentRight) {
									var iconRemoveId = "attachment-remove-" + nodeRef;
									reghtPart += "<img id='" + iconRemoveId + "' src='" + Alfresco.constants.URL_RESCONTEXT
										+ "components/images/delete-16.png' class='remove-icon'/>";
									Event.onAvailable(iconRemoveId, this.attachRemoveItemClickListener, item, this);
								}
							}

							var rowId = "attachment-" + nodeRef;
							elAttachments.innerHTML += "<li id='" + rowId + "'>" + Util.getCroppedItem(leftPart, reghtPart) + "</li>";
							if (!this.options.disabled && this.options.showUploadNewVersion) {
								Event.onAvailable(rowId, this.attachUploadNewVersionDndListener, item, this);
							}
						}
					}
				}

				if (this.selectedPreviewFile != null) {
					Dom.addClass("attachment-" + this.selectedPreviewFile, "selected");
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
                el.click();

				var removedItems = this.getRemovedItems();
				el = Dom.get(this.id + "-removed");
				el.value = '';
				for (i in removedItems) {
					el.value += (i < removedItems.length-1 ? removedItems[i] + ',' : removedItems[i]);
				}
                el.click();

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
			},

            enable: function enable_function() {
                this.disabled = false;
            },

            disable: function disable_function() {
                this.disabled = true;
            }
		});
})();