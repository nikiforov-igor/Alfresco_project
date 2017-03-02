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
        Event = YAHOO.util.Event,
        Bubbling = YAHOO.Bubbling;
    var $siteURL = Alfresco.util.siteURL;

    LogicECM.module.Documents.DocumentPreviewControl = function (fieldHtmlId) {
        LogicECM.module.Documents.DocumentPreviewControl.superclass.constructor.call(this, "LogicECM.module.Documents.DocumentPreviewControl", fieldHtmlId, ["container"]);

        return this;
    };

    YAHOO.extend(LogicECM.module.Documents.DocumentPreviewControl, Alfresco.component.Base,
        {
            options: {
                itemId: "",
                forTask: true,
                selectedAttachmentNodeRef: "",
                baseDocAssocName: null,
                resizeable: false
            },

            documentNodeRef: null,
            attachmentsList: null,

            attachmentsSelect: null,
            attachmentActions: null,

            actionsSelect: null,
            deletedAttachment: null,
            selectedAttachment: null,
            menu: null,

            onReady: function () {
                this.modules.actions = new LogicECM.module.Base.Actions();
                this.attachmentsSelect = Dom.get(this.id + "-attachment-select");

                if (this.options.resizeable) {
                    Bubbling.on("webPreviewSetupComplete", this.setHeights, this, true);
                    YAHOO.util.Event.on(window, "resize", this.setHeights, this, true);
                }

                Bubbling.on("onAttachmentsDeleteSuccess", this.onAttachmentsDeleteSuccess, this);

                this.loadDocument();

                YAHOO.util.Event.on(this.attachmentsSelect, "change", this.selectAttachment, null, this);

                this.actionsSelect = Dom.get(this.id + "-attachment-actions");
                if (this.actionsSelect) {
                    this.attachmentActions = [
                        {
                            value: this.msg("label.attachment.download"),
                            text: this.msg("label.attachment.download"),
                            onclick: {fn: this.attachmentActionDownload.bind(this)},
                            editmode: false,
                            mimetype: ""
                        },
                        {
                            value: this.msg("label.attachment.view"),
                            text: this.msg("label.attachment.view"),
                            onclick: {fn: this.attachmentActionView.bind(this)},
                            editmode: false,
                            mimetype: ""
                        },
                        {
                            value: this.msg("label.attachment.editProperties"),
                            text: this.msg("label.attachment.editProperties"),
                            onclick: {fn: this.attachmentActionEditProperties.bind(this)},
                            editmode: true,
                            mimetype: ""
                        },
                        {
                            value: this.msg("label.attachment.uploadNewVersion"),
                            text: this.msg("label.attachment.uploadNewVersion"),
                            onclick: {fn: this.attachmentActionUploadNewVersion.bind(this)},
                            editmode: true,
                            mimetype: ""
                        },
                        {
                            value: this.msg("label.attachment.remove"),
                            text: this.msg("label.attachment.remove"),
                            onclick: {fn: this.attachmentActionDelete.bind(this)},
                            editmode: true,
                            mimetype: ""
                        }
                    ];

                    this.widgets.attachmentMenuButton = new YAHOO.widget.Button(this.id + "-attachment-actions-button", {
                        type: "menu",
                        label: this.msg("label.attachment.actions"),
                        menu: this.attachmentActions
                    });

                    this.widgets.versionsMenuButton = new YAHOO.widget.Button(this.id + '-versions-actions-button', {
                        type: 'menu',
                        label: this.msg('label.versions.menu'),
                        menu: [{
                            text: 'Нет версий'
                        }]
                    });
                }

                Event.on(this.attachmentsSelect, "change", this.reloadAttachmentPreview, this, true);
            },

            initAttachments: function (haveany) {
                var select = this.attachmentsSelect;
                var button = Dom.get(this.id + "-attachment-actions");
                var area = Dom.get(this.id + "-preview");

                if (haveany) {
                    select.removeAttribute('disabled');
                    Dom.removeClass(button, 'hidden');
                    Dom.removeClass(area, 'hidden');
                } else {
                    select.innerHTML = '';

                    var opt = document.createElement('option');
                    opt.textContent = "Нет вложений";
                    opt.value = null;
                    select.appendChild(opt);
                    select.setAttribute('disabled', "");
                    Dom.addClass(button, 'hidden');
                    Dom.addClass(area, 'hidden');
                }
            },

            loadDocument: function () {
                if (this.options.itemId != null) {
                    if (this.options.forTask === true) {
                        Alfresco.util.Ajax.jsonGet({
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

            loadDocumentAttachments: function () {
                if (this.options.itemId != null) {
                    Alfresco.util.Ajax.jsonGet({
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
                                    if (this.attachmentsSelect) {
                                        this.attachmentsList = {};
                                        this.attachmentsSelect.innerHTML = "";

                                        this.loadCategories(result.items);
                                        this._processAttachments(result.items);
                                    }
                                    this.reloadAttachmentPreview();
                                }
                            },
                            scope: this
                        }
                    });
                }
            },

            reloadAttachmentPreview: function () {
                if (this.selectedAttachment) {
                    Alfresco.util.Ajax.jsonGet(
                        {
                            url: Alfresco.constants.URL_SERVICECONTEXT + "components/preview/web-preview",
                            dataObj: {
                                nodeRef: this.selectedAttachment.nodeRef,
                                htmlid: this.id + "-preview-container"
                            },
                            successCallback: {
                                fn: function (response) {
                                    Dom.get(this.id + "-preview-container").innerHTML = response.serverResponse.responseText;
                                    var previewId = this.id + "-preview-container-full-window-div";
                                    var dialog = LogicECM.module.Base.Util.getLastDialog();
                                    if (dialog != null) {
                                        dialog.dialog.center();
                                    }
                                    Event.onAvailable(previewId, function () {
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
            },

            _processAttachments: function (data) {
                this.attachmentsSelect.innerHTML = '';
                var hasAttachments = false;
                var attachmentsCount = 0;
                if (data && data.length) {
                    data.forEach(function (item) {
                        if (item) {
                            var readOnly = item.category.isReadOnly;
                            var gr = document.createElement('optgroup');
                            gr.label = item.category.name;
                            if (item.attachments && item.attachments.length) {
                                hasAttachments = true;
                                attachmentsCount += item.attachments.length;
                                item.attachments.forEach(function (attachment) {
                                    var opt = document.createElement('option');
                                    opt.textContent = attachment.name;
                                    opt.value = attachment.nodeRef;
                                    opt.dataset.readonly = readOnly;
                                    gr.appendChild(opt);
                                    this.attachmentsList[attachment.nodeRef] = attachment;
                                    if (this.options.selectedAttachmentNodeRef && this.options.selectedAttachmentNodeRef === attachment.nodeRef) {
                                        this.selectedAttachment = attachment;
                                    }
                                }, this);
                            }

                            this.attachmentsSelect.appendChild(gr);
                        }
                    }, this);

                    if (this.selectedAttachment && this.attachmentsList.hasOwnProperty(this.selectedAttachment.nodeRef)) {
                        this.attachmentsSelect.value = this.selectedAttachment.nodeRef;
                    }
                    this.selectAttachment();
                }
                this.initAttachments(hasAttachments);
            },

            loadCategories: function (data) {
                var addAttachmentActions = [];
                this.attachmentsList = {};

                var me = this;

                if(this.widgets.addAttachmentMenuButton) {
                    this.widgets.addAttachmentMenuButton.destroy();
                }

                this.categories = new Object();
                if (data && data.length) {
                    data.forEach(function (item) {
                        var readonly = (me.MODE !== me.MODE_EDIT || item.category.isReadOnly);
                        me.categories[item.category.name] = {
                            readonly: readonly,
                            nodeRef: item.category.nodeRef
                        };
                        addAttachmentActions.push({
                            value: item.category.nodeRef,
                            text: item.category.name,
                            disabled: ((me.MODE !== me.MODE_EDIT && item.category.name === "Прочее") || (me.MODE === me.MODE_EDIT && item.category.isReadOnly)),
                            onclick: {
                                fn: me.attachmentActionAdd,
                                scope: me,
                                obj: {
                                    name: item.category.name,
                                    nodeRef: item.category.nodeRef
                                }
                            }
                        });
                    }, this);
                }
                var button = Dom.get(this.id + "-attachment-add");
                if (button) {
                    button.innerHTML = "";
                    this.widgets.addAttachmentMenuButton = new YAHOO.widget.Button({
                        container: button,
                        type: "menu",
                        label: this.msg("label.attachment.add"),
                        disabled: (this.hasAddContentRight != null && !this.hasAddContentRight),
                        menu: addAttachmentActions
                    });
                }
            },

            attachmentActionAdd: function(arg1, arg2, arg3) {
                var silent = false;
                if (arguments.length == 2) {
                    var name = arg2.name;
                    var nodeRef = arg2.nodeRef;
                    silent = arg2.silent;
                } else {
                    var name = arg3.name;
                    var nodeRef = arg3.nodeRef;
                }

                var title = name ? 'Загрузить в категорию: "' + name + '"' : 'Добавить вложение';

                if (nodeRef != null) {
                    if (!this.fileUpload) {
                        this.fileUpload = Alfresco.getFileUploadInstance();
                    }
                    var me = this;
                    var uploadConfig =
                    {
                        destination: nodeRef,
                        filter: [],
                        mode: this.fileUpload.MODE_SINGLE_UPLOAD,
                        thumbnails: "doclib",
                        onFileUploadComplete:
                        {
                            fn: function (obj) {
                                if (!silent) {
                                    if (obj.successful != null && obj.successful.length > 0) {
                                        // TODO: Костыль, надо бы переписать логику
                                        me.selectedAttachment = {};
                                        me.selectedAttachment.nodeRef = obj.successful[0].nodeRef;
                                    }

                                    if (this.options.nodeRef) {
                                        me.loadDocumentAttachments(false);
                                    } else {
                                        me.attachmentsList[obj.successful[0].nodeRef] = {
                                            nodeRef: obj.successful[0].nodeRef,
                                            name: obj.successful[0].fileName
                                        };

                                        me.initAttachments(true);
                                        me.manualAttachmentListUpdate();
                                    }
                                    me.reloadAttachmentPreview();
                                }

                            },
                            scope: this
                        },
                        suppressRefreshEvent: true
                    };
                    this.fileUpload.show(uploadConfig);
                    if (this.fileUpload.uploader.titleText) {
                        this.fileUpload.uploader.titleText.innerHTML = title;
                    } else if (this.fileUpload.uploader.widgets && this.fileUpload.uploader.widgets.panel) {
                        this.fileUpload.uploader.widgets.panel.setHeader(title);
                    }
                }
            },

            selectAttachment: function () {
                if (this.attachmentsSelect.selectedIndex < 0) {
                    return null;
                }
                var selectedOption = this.attachmentsSelect.options[this.attachmentsSelect.selectedIndex];
                var nodeRef = selectedOption.value;
                var readonly = selectedOption.dataset.readonly === "true";
                if (nodeRef != null && nodeRef.length > 0) {
                    this.setMenuAttachmentButtonAccess(readonly, nodeRef);
                    this.selectedAttachment = this.attachmentsList[nodeRef];
                    if (this.selectedAttachment != null) {
                        this._loadVersions(nodeRef);
                    }
                }
            },

            attachmentActionDownload: function () {
                if (this.selectedAttachment != null) {
                    document.location = Alfresco.constants.PROXY_URI_RELATIVE + "api/node/content/" + this.selectedAttachment.nodeRef.replace(":/", "") + "/" + this.selectedAttachment.name + "?a=true";
                }
            },

            _loadVersions: function (nodeRef) {
                Alfresco.util.Ajax.jsonGet({
                    url: Alfresco.constants.PROXY_URI_RELATIVE + 'api/version',
                    dataObj: {
                        nodeRef: nodeRef
                    },
                    successCallback: {
                        fn: function (response) {
                            var data = response.json;
                            if (data) {
                                if (data.length > 1) {
                                    var versions = data.map(function (el) {
                                        return {
                                            text: el.label,
                                            url: Alfresco.constants.PROXY_URI + '/api/node/content/' + el.nodeRef.replace(":/", "") + '/' + el.name + '?a=true'
                                        }
                                    });

                                    this.selectedAttachment.version = data[0].label;
                                    Dom.removeClass(this.id + '-versions-actions', 'hidden');

                                    function updateMenu() {
                                        var items = Alfresco.util.deepCopy(menu.getItems());
                                        this.addItems(versions);

                                        items.forEach(function (item) {
                                            this.removeItem(item);
                                        }, this);

                                        this.unsubscribe('render', updateMenu);
                                        this.unsubscribe('show', updateMenu);
                                    }

                                    var menu = this.widgets.versionsMenuButton.getMenu();
                                    if (!menu._rendered) {
                                        menu.subscribe('render', updateMenu);
                                    } else {
                                        menu.subscribe('show', updateMenu);
                                    }
                                } else {
                                    Dom.addClass(this.id + '-versions-actions', 'hidden');
                                }
                            }
                        },
                        scope: this
                    },
                    failureMessage: ""
                });
            },

            setMenuAttachmentButtonAccess: function (readonly, nodeRef) {
                if (this.widgets.attachmentMenuButton != null) {
                    var i;
                    var menu = this.widgets.attachmentMenuButton.getMenu();
                    var items = menu.getItems();

                    function updateItems(readonly) {
                        for (i = 0; i < menu.itemData.length; i++) {
                            var item = menu.getItem(i);
                            // TODO you know what to do
                            var editable = menu.itemData[i].editmode;
                            item.cfg.setProperty('disabled', (editable && readonly && this.MODE !== this.MODE_CREATE) || menu.itemData[i].disabled);
                        }
                    }

                    if (items && items.length) {
                        updateItems(readonly);
                    } else {
                        menu.subscribe('render', updateItems.bind(this, readonly));
                    }
                }
            },

            attachmentActionView: function () {
                if (this.selectedAttachment != null) {
                    window.open(Alfresco.constants.PROXY_URI_RELATIVE + "api/node/content/" + this.selectedAttachment.nodeRef.replace(":/", "") + "/" + this.selectedAttachment.name, '_blank');
                }
            },

            attachmentActionEditProperties: function () {
                if (this.selectedAttachment != null) {
                    var scope = this,
                        nodeRef = this.selectedAttachment.nodeRef;

                    // Intercept before dialog show
                    var doBeforeDialogShow = function (p_form, p_dialog) {
                        // Dialog title
                        var fileSpan = '<span class="light">' + $html(this.selectedAttachment.name) + '</span>';

                        Alfresco.util.populateHTML(
                            [p_dialog.id + "-dialogTitle", scope.msg("edit-details.title", fileSpan)]
                        );

                        // Edit metadata link button
                        this.widgets.editMetadata = Alfresco.util.createYUIButton(p_dialog, "editMetadata", null,
                            {
                                type: "link",
                                label: scope.msg("edit-details.label.edit-metadata"),
                                href: $siteURL("edit-metadata?nodeRef=" + nodeRef)
                            });
                    };

                    var templateUrl = YAHOO.lang.substitute(Alfresco.constants.URL_SERVICECONTEXT + "components/form?itemKind={itemKind}&itemId={itemId}&destination={destination}&mode={mode}&submitType={submitType}&formId={formId}&showCancelButton=true",
                        {
                            itemKind: "node",
                            itemId: nodeRef,
                            mode: "edit",
                            submitType: "json",
                            formId: "doclib-simple-metadata"
                        });

                    // Using Forms Service, so always create new instance
                    var editDetails = new Alfresco.module.SimpleDialog(this.id + "-editDetails-" + Alfresco.util.generateDomId());

                    editDetails.setOptions(
                        {
                            width: "auto",
                            templateUrl: templateUrl,
                            actionUrl: null,
                            destroyOnHide: true,
                            doBeforeDialogShow: {
                                fn: doBeforeDialogShow,
                                scope: this
                            },
                            onSuccess: {
                                fn: function () {
                                    if (this.attachmentsSelect.selectedOptions && this.attachmentsSelect.selectedOptions.length > 0) {
                                        var option = this.attachmentsSelect.selectedOptions[0];
                                        var nodeRef = option.getAttribute("value");

                                        var onSuccess = function refresh_onSuccess(response) {
                                            var items = response.json.data.items;
                                            if (items && items.length > 0) {
                                                option.innerHTML = items[0].name;
                                                this.attachmentsList[nodeRef].name = items[0].name;
                                            }
                                        };

                                        Alfresco.util.Ajax.jsonRequest({
                                            method: 'POST',
                                            url: Alfresco.constants.PROXY_URI_RELATIVE + 'lecm/forms/picker/items',
                                            dataObj: {
                                                items: nodeRef.split(",")
                                            },
                                            successCallback: {
                                                fn: onSuccess,
                                                scope: this
                                            },
                                            failureCallback: {
                                                fn: function () {
                                                    Alfresco.util.PopupManager.displayMessage({
                                                        text: Alfresco.component.Base.prototype.msg("Сбой")
                                                    });
                                                },
                                                scope: this
                                            }
                                        });
                                    }
                                },
                                scope: this
                            },
                            onFailure: {
                                fn: function (response) {
                                    var failureMsg = this.msg("message.details.failure");
                                    if (response.json && response.json.message.indexOf("Failed to persist field 'prop_cm_name'") !== -1) {
                                        failureMsg = this.msg("message.details.failure.name");
                                    }
                                    Alfresco.util.PopupManager.displayMessage(
                                        {
                                            text: failureMsg
                                        });
                                },
                                scope: this
                            }
                        });
                    editDetails.show();
                }
            },

            attachmentActionDelete: function () {
                if (this.selectedAttachment != null) {
                    var me = this,
                        content = "document",
                        displayName = this.selectedAttachment.name;

                    var displayPromptText = this.msg("message.confirm.delete", displayName);

                    Alfresco.util.PopupManager.displayPrompt(
                        {
                            title: this.msg("actions." + content + ".delete"),
                            text: displayPromptText,
                            noEscape: true,
                            buttons: [
                                {
                                    text: this.msg("button.delete"),
                                    handler: function () {
                                        this.destroy();
                                        me.attachmentActionDeleteConfirm.call(me, me.selectedAttachment, displayName);
                                    }
                                },
                                {
                                    text: this.msg("button.cancel"),
                                    handler: function () {
                                        this.destroy();
                                    },
                                    isDefault: true
                                }]
                        });
                }
            },

            attachmentActionDeleteConfirm: function (attachment, displayName) {
                // Весьма глупый способ, но щито поделать
                this.deletedAttachment = attachment.nodeRef;
                this.modules.actions.genericAction(
                    {
                        success: {
                            event: {
                                name: "onAttachmentsDeleteSuccess"
                            },
                            message: this.msg("message.delete.success", displayName)
                        },
                        failure: {
                            message: this.msg("message.delete.failure", displayName)
                        },
                        webscript: {
                            method: Alfresco.util.Ajax.DELETE,
                            name: "delete",
                            queryString: "full=true"
                        },
                        config: {
                            requestContentType: Alfresco.util.Ajax.JSON,
                            dataObj: {
                                nodeRefs: [attachment.nodeRef]
                            }
                        }
                    });
            },

            onAttachmentsDeleteSuccess: function () {
                if (this.options.itemId) {
                    this.loadDocumentAttachments(false);
                } else {
                    delete this.attachmentsList[this.deletedAttachment];
                    this.manualAttachmentListUpdate();

                    function getMapSize(obj) {
                        var size = 0, key;
                        for (key in obj) {
                            if (obj.hasOwnProperty(key)) size++;
                        }
                        return size;
                    }

                    if (getMapSize(this.attachmentsList) == 0) {
                        this.initAttachments(false);
                    }
                }

                Bubbling.fire('removeAttachmentAction', {
                    nodeRef: this.deletedAttachment
                });
                this.deletedAttachment = null;
            },

            attachmentActionUploadNewVersion: function () {
                if (this.selectedAttachment != null) {
                    var displayName = this.selectedAttachment.name,
                        nodeRef = this.selectedAttachment.nodeRef,
                        version = this.selectedAttachment.version;

                    if (!this.fileUpload) {
                        this.fileUpload = Alfresco.getFileUploadInstance();
                    }

                    // Show uploader for multiple files
                    var description = this.msg("label.filter-description", displayName),
                        extensions = "*";

                    if (displayName && new RegExp(/[^\.]+\.[^\.]+/).exec(displayName)) {
                        // Only add a filtering extension if filename contains a name and a suffix
                        extensions = "*" + displayName.substring(displayName.lastIndexOf("."));
                    }

                    var me = this;

                    var singleUpdateConfig =
                    {
                        updateNodeRef: nodeRef.toString(),
                        updateFilename: displayName,
                        updateVersion: version,
                        overwrite: true,
                        filter: [
                            {
                                description: description,
                                extensions: extensions
                            }],
                        mode: this.fileUpload.MODE_SINGLE_UPDATE,
                        onFileUploadComplete: {
                            fn: function (obj) {
                                setTimeout(function () {
                                   if (obj.successful != null && obj.successful.length > 0) {
                                        // TODO: Костыль, надо бы переписать логику
                                        me.selectedAttachment = {};
                                        me.selectedAttachment.nodeRef = obj.successful[0].nodeRef;
                                    }
                                    me.loadDocumentAttachments();
                                }, 2000);
                            },
                            scope: this
                        },
                        suppressRefreshEvent: true
                    };
                    this.fileUpload.show(singleUpdateConfig);
                }
            },

            manualAttachmentListUpdate: function () {
                // Прицельное обновление списка вложений - нужно только для страницы создания
                // Т.к дёргать категрии как обычно бессмыслено - будет создана папка "Основные"
                // и вытянуты все вложения оттуда, даже если они остались там от другого док-а

                this.attachmentsSelect.innerHTML = "";

                for (var nodeRef in this.attachmentsList) {
                    if (this.attachmentsList.hasOwnProperty(nodeRef)) {
                        var attachmentObj = this.attachmentsList[nodeRef];
                        var opt = document.createElement('option');
                        opt.textContent = attachmentObj.name;
                        opt.value = attachmentObj.nodeRef;
                        this.attachmentsSelect.appendChild(opt);
                    }
                }

                if (this.attachmentsSelect.options.length) {
                    this.attachmentsSelect.selectedIndex = this.attachmentsSelect.options.length - 1;
                    this.selectAttachment();
                }
            },

            setHeights: function () {
                var page = Dom.get('doc-bd');
                var footer = Dom.get('alf-ft');

                // UFAS-1303 - Иногда форма приходит очень жирной, что вызывает
                // не совсем верный рассчёт её максимальной высоты.
                // Подозреваю, что дело не совсем в этом, нужно переделать логику установки высоты

                // Dirty hack to get visible part of element
                // Proudly borrowed from http://stackoverflow.com/questions/24768795/get-the-visible-height-of-a-div-with-jquery
                function inViewport($el) {
                    var elH = $el.outerHeight(),
                        H = $(window).height(),
                        r = $el[0].getBoundingClientRect(), t = r.top, b = r.bottom;
                    return Math.max(0, t > 0 ? Math.min(elH, H - t) : (b < H ? b : H ));
                }

                if (page) {
                    var actualPageHeight = inViewport($(page));
                    var actualFooterHeight = footer.getBoundingClientRect().height;

                    var contentBoxHeight = actualPageHeight - actualFooterHeight;
                    var attachActionsContainer = Dom.get(this.id + '-attachments-header');
                    var attachActionsHeight = attachActionsContainer && attachActionsContainer.getBoundingClientRect().height || 0;

                    var previewerContainerEl = Dom.get(this.id + '-preview-container');
                    if (previewerContainerEl) {
                        var previewerDiv = YAHOO.util.Selector.query('.previewer', previewerContainerEl)[0];
                        var previewerControls = YAHOO.util.Selector.query('.controls', previewerContainerEl)[0];
                        var previewerControlsHeight = previewerControls && previewerControls.getBoundingClientRect().height || 0;
                        var previewerDocument = YAHOO.util.Selector.query('.viewer', previewerContainerEl)[0];

                        var effectiveContainerHeight = contentBoxHeight - attachActionsHeight;
                        var effectivePreviewHeight = contentBoxHeight - attachActionsHeight - previewerControlsHeight;

                        Dom.setStyle(previewerDiv, 'min-height', effectiveContainerHeight + "px");
                        Dom.setStyle(previewerDocument, 'height', 'auto');
                        Dom.setStyle(previewerDocument, 'max-height', effectivePreviewHeight + "px");
                    }
                }
            },
        });
})();
