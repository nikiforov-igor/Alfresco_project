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

    var $html = Alfresco.util.encodeHTML,
        $siteURL = Alfresco.util.siteURL,
        $isValueSet = Alfresco.util.isValueSet;

    LogicECM.module.Documents.DocumentPreviewControl = function (fieldHtmlId) {
        LogicECM.module.Documents.DocumentPreviewControl.superclass.constructor.call(this, "LogicECM.module.Documents.DocumentPreviewControl", fieldHtmlId, ["container"]);
        return this;
    };

    YAHOO.extend(LogicECM.module.Documents.DocumentPreviewControl, Alfresco.component.Base);

    YAHOO.lang.augmentProto(LogicECM.module.Documents.DocumentPreviewControl, Alfresco.doclib.Actions);

    YAHOO.lang.augmentObject(LogicECM.module.Documents.DocumentPreviewControl.prototype, {
        options: {
            itemId: "",
            forTask: true,
            selectedAttachmentNodeRef: "",
            baseDocAssocName: null,
            resizeable: false,
            categories: null,
            allActions: null,
            readOnlyActions: null
        },

        documentNodeRef: null,
        attachmentsList: null,
        attachmentsSelect: null,
        attachmentActions: null,
        categories: [],
        actionsSelect: null,
        deletedAttachment: null,
        selectedAttachment: null,
        deferredCategoriesLoad: null,
        menu: null,

        onReady: function DocumentPreviewControl_onReady() {
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

            this.loadAttachmentAddMenu();

            Event.on(this.attachmentsSelect, "change", this.reloadAttachmentPreview, this, true);

            this.widgets.versionsMenuButton = new YAHOO.widget.Button(this.id + '-versions-actions-button', {
                type: 'menu',
                label: this.msg('label.versions.menu'),
                menu: [{
                    text: 'label.attachment.no-versions'
                }]
            });
        },

        loadDocument: function DocumentPreviewControl_loadDocument() {
            if (this.options.itemId != null) {
                if (this.options.forTask === true) {
                    Alfresco.util.Ajax.jsonGet({
                        url: Alfresco.constants.PROXY_URI_RELATIVE + "lecm/workflow/GetDocumentDataByTaskId?taskID=" + this.options.itemId,
                        successCallback: {
                            fn: function (response) {
                                var result = response.json;
                                if (result != null && result.nodeRef != null) {
                                    this.documentNodeRef = result.nodeRef;
                                    this.loadAttachments();
                                }
                            },
                            scope: this
                        }
                    });
                } else {
                    this.documentNodeRef = this.options.itemId;
                    this.loadAttachments();
                }
            }
        },

        loadAttachments: function DocumentPreviewControl_loadAttachments() {
            this.attachmentsList = {};
            if (this.options.categories) {
                this.categories = [];
                var nodeRefs = [];
                for (var i = 0; i < this.options.categories.length; i++) {
                    var categoryNodeRef = this.options.categories[i].nodeRef;
                    nodeRefs.push(categoryNodeRef);
                    Alfresco.util.Ajax.jsonGet({
                        url: Alfresco.constants.URL_SERVICECONTEXT + "lecm/document/data/doclistAttachments/documents/node/" + categoryNodeRef.replace(":/", ""),
                        dataObj: {
                            view: "attachment",
                            nodeRef: categoryNodeRef
                        },
                        successCallback: {
                            fn: function (response) {
                                var categoryAttachments = response.json.items;
                                var categoryNodeRef = response.config.dataObj.nodeRef;
                                if (categoryAttachments) {
                                    this.onCategoryAttachmentsLoaded(categoryNodeRef, categoryAttachments);
                                }
                            },
                            scope: this
                        }
                    });
                }
                this.deferredCategoriesLoad = new Alfresco.util.Deferred(nodeRefs, {
                    fn: this.onAttachmentsLoaded,
                    scope: this
                });
            } else {
                if (this.options.itemId != null) {
                    Alfresco.util.Ajax.jsonGet({
                        url: Alfresco.constants.PROXY_URI_RELATIVE + "lecm/document/attachments/api/get",
                        dataObj: {
                            documentNodeRef: this.documentNodeRef,
                            showEmptyCategory: true
                        },
                        successCallback: {
                            fn: function (response) {
                                var data = response.json.items;
                                if (data && data.length) {
                                    data.forEach(function (item) {
                                        if (item) {
                                            var readOnly = item.category.isReadOnly;
                                            var gr = document.createElement('optgroup');
                                            gr.label = item.category.name;
                                            if (item.attachments && item.attachments.length) {
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
                                this.reloadAttachmentPreview();
                            },
                            scope: this
                        }
                    });
                }
            }
        },

        onAttachmentsLoaded: function DocumentPreviewControl_onAttachmentsLoaded() {
            if (this.options.categories) {
                var sortedCategories = [];
                for (var i = 0; i < this.options.categories.length; i++) {
                    for (var ii = 0; ii < this.categories.length; ii++) {
                        if (this.options.categories[i].nodeRef == this.categories[ii].nodeRef) {
                            sortedCategories.push(this.categories[ii]);
                        }
                    }
                }
                this.categories = sortedCategories;

                if (this.attachmentsSelect) {
                    this.populateAttachmentsSelect();
                    this.selectAttachment();

                    var select = this.attachmentsSelect;
                    var button = Dom.get(this.id + "-attachment-actions");

                    var haveAny = false;
                    for (var prop in this.attachmentsList) {
                        haveAny = true;
                    }

                    if (haveAny) {
                        select.removeAttribute('disabled');
                        Dom.removeClass(button, 'hidden');
                    } else {
                        select.innerHTML = '';

                        var opt = document.createElement('option');
                        opt.textContent = this.msg("label.attachment.no-attachments");
                        opt.value = null;
                        select.appendChild(opt);
                        select.setAttribute('disabled', "");
                        Dom.addClass(button, 'hidden');
                    }
                }
            }
            this.reloadAttachmentPreview();
        },

        populateAttachmentsSelect: function DocumentPreviewControl_populateAttachmentsSelect() {
            this.attachmentsSelect.innerHTML = "";
            for (var i = 0; i < this.categories.length; i++) {
                var category = this.categories[i];
                var gr = document.createElement('optgroup');
                gr.label = category.name;
                for (var ii = 0; ii < category.attachments.length; ii++) {
                    var attachment = category.attachments[ii];
                    var opt = document.createElement('option');
                    opt.textContent = attachment.fileName;
                    opt.value = attachment.nodeRef;
                    gr.appendChild(opt);
                    this.attachmentsList[attachment.nodeRef] = attachment;
                    if (this.options.selectedAttachmentNodeRef && this.options.selectedAttachmentNodeRef === attachment.nodeRef) {
                        this.selectedAttachment = attachment;
                    }
                }
                this.attachmentsSelect.appendChild(gr);
            }
            if (this.selectedAttachment && this.attachmentsList.hasOwnProperty(this.selectedAttachment.nodeRef)) {
                this.attachmentsSelect.value = this.selectedAttachment.nodeRef;
            }
        },

        onCategoryAttachmentsLoaded: function DocumentPreviewControl_onCategoryAttachmentsLoaded(categoryNodeRef, categoryAttachments) {
            var category = this.getCategoryByNodeRef(categoryNodeRef);
            category.nodeRef = categoryNodeRef;
            category.attachments = [];
            for (var i = 0; i < categoryAttachments.length; i++) {
                category.attachments.push(categoryAttachments[i]);
            }
            this.categories.push(category);
            this.deferredCategoriesLoad.fulfil(category.nodeRef);
        },

        getCategoryByNodeRef: function DocumentPreviewControl_getCategoryByNodeRef(categoryNodeRef) {
            for (var i = 0; i < this.options.categories.length; i++) {
                if (this.options.categories[i].nodeRef == categoryNodeRef) {
                    return {
                        name: this.options.categories[i].name,
                        isReadOnly: this.options.categories[i].isReadOnly == "true"
                    }
                }
            }
        },

        loadAttachmentAddMenu: function DocumentPreviewControl_loadAttachmentAddMenu() {
            var attachmentAddEl = Dom.get(this.id + "-attachment-add");
            if (attachmentAddEl) {
                var addAttachmentActions = [];
                if (this.widgets.addAttachmentMenuButton) {
                    this.widgets.addAttachmentMenuButton.destroy();
                }
                for (var i = 0; i < this.options.categories.length; i++) {
                    var category = this.options.categories[i];
                    addAttachmentActions.push({
                        value: category.nodeRef,
                        text: category.name,
                        disabled: category.isReadOnly == "true",
                        onclick: {
                            fn: this.attachmentActionAdd,
                            scope: this,
                            obj: {
                                name: category.name,
                                nodeRef: category.nodeRef
                            },
                        }
                    })
                }
                attachmentAddEl.innerHTML = "";
                this.widgets.addAttachmentMenuButton = new YAHOO.widget.Button(attachmentAddEl, {
                    type: "menu",
                    label: this.msg("label.attachment.add"),
                    menu: addAttachmentActions
                });
            }
        },

        reloadAttachmentPreview: function DocumentPreviewControl_reloadAttachmentPreview() {
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

                var actionsEl = Dom.get(this.id + "-actions");
                if (actionsEl) {
                    actionsEl.innerHTML = '';
                    Alfresco.util.Ajax.request(
                        {
                            url: Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/document/attachment/actions",
                            dataObj: {
                                nodeRef: this.attachmentsSelect.value,
                                format: "json"
                            },
                            successCallback: {
                                fn: function (response) {
                                    var container = Dom.get(this.id + "-actions");
                                    if (response.json.item.actions) {
                                        var item = response.json.item;
                                        var node = item.node;
                                        if (node.isLocked) {
                                            var warning = document.createElement("div");
                                            warning.className = "warning";
                                            if (node.properties["cm:lockOwner"] && node.properties["cm:lockOwner"].userName == Alfresco.constants.USERNAME) {
                                                warning.title = this.msg("details.banner.lock-owner");
                                            } else {
                                                warning.title = this.msg("details.banner.locked", node.properties["cm:lockOwner"].displayName);
                                            }
                                            container.appendChild(warning);
                                        }
                                        var urlContext = Alfresco.constants.URL_RESCONTEXT + "components/documentlibrary/actions/";
                                        for (var key in item.actions) {
                                            var action = item.actions[key];
                                            if (action.id == "document-unlock") {
                                                var div = document.createElement("div");
                                                div.className = "action";
                                                div.style.backgroundImage = "url('" + urlContext + action.icon + "-16.png')";
                                                div.title = this.msg(action.label);
                                                container.appendChild(div);
                                                Event.on(div, "click", function () {
                                                    if (YAHOO.lang.isFunction(LogicECM.module.UnlockNode.unlock)) {
                                                        LogicECM.module.UnlockNode.unlock(item, this.reloadAttachmentPreview.bind(this));
                                                    }
                                                }, this, true);
                                            } else if (action.id == "lecm-online-editing") {
                                                var div = document.createElement("div");
                                                div.className = "action";
                                                div.style.backgroundImage = "url('" + urlContext + action.icon + "-16.png')";
                                                div.title = this.msg(action.label);
                                                container.appendChild(div);
                                                Event.on(div, "click", function () {
                                                    if (YAHOO.lang.isFunction(LogicECM.module.EditOnline.edit)) {
                                                        LogicECM.module.EditOnline.edit(item);
                                                    }
                                                }, this, true);
                                            }
                                        }
                                    }
                                },
                                scope: this
                            },
                            failureMessage: this.msg("message.failure"),
                            scope: this,
                            execScripts: true
                        });
                }
            }
        },

        renderActions: function DocumentPreviewControl_renderActions(actions, item) {
            item.jsNode = new Alfresco.util.Node(item.node);

            this.attachmentActions = [];

            for (var i = 0; i < actions.length; i++) {
                var action = actions[i];
                this.attachmentActions.push({
                    value: this.msg(action.label),
                    text: this.msg(action.label),
                    onclick: {
                        fn: this.getActionMenuFunction(action, item),
                        scope: this
                    }
                });
            }

            if (this.widgets.attachmentMenuButton) {
                var menu = this.widgets.attachmentMenuButton.getMenu();
                menu.clearContent();
                menu.itemData = this.attachmentActions;
                menu.render();
            } else {
                this.widgets.attachmentMenuButton = new YAHOO.widget.Button(this.id + "-attachment-actions-button", {
                    type: "menu",
                    label: this.msg("label.attachment.actions"),
                    menu: this.attachmentActions
                });
            }
        },

        showActions: function DocumentPreviewControl_showActions() {
            var actionSet = Dom.get(this.id + "-action-set");
            Dom.removeClass(actionSet, "hidden");
        },

        checkActions: function DocumentPreviewControl_checkActions(record) {
            if (record.meta && record.meta.category) {
                var categoryNodeRef = record.meta.category;
                var category = this.getCategoryByNodeRef(categoryNodeRef);
                var isReadOnly = category.isReadOnly;
            } else {
                isReadOnly = true;
            }
            var showActions = (isReadOnly) ? this.options.readOnlyActions : this.options.allActions;
            var result = [];
            var actions = record.actions;
            if (actions != null) {
                for (var i = 0; i < actions.length; i++) {
                    var action = actions[i];
                    var show = false;
                    for (var j = 0; j < showActions.length; j++) {
                        if (action.id == showActions[j].id &&
                            (!showActions[j].onlyForOwn ||
                            (record.node != null && record.node.properties["cm:creator"] != null
                            && record.node.properties["cm:creator"].userName == Alfresco.constants.USERNAME))) {
                            show = true;
                        }
                    }
                    if (show) {
                        result.push(action);
                    }
                }
            }
            return result;
        },

        getActionMenuFunction: function DocumentPreviewControl_checkActions_(action, item) {
            if (action.type === "link") {
                if (action.params.href) {
                    var href = Alfresco.util.substituteDotNotation(action.params.href, item);
                    var actionUrls = this.getActionUrls(item);
                    var target = action.params.target ? "target=\"" + action.params.target + "\"" : "";
                    var url = YAHOO.lang.substitute(href, actionUrls);
                    return this["openLink"].bind(this, url, target);
                }
                else {
                    Alfresco.logger.warn("Action configuration error: Missing 'href' parameter for actionId: ", action.id);
                }
            }
            else if (action.type === "pagelink") {
                if (action.params.page) {
                    var recordSiteName = $isValueSet(item.location.site) ? item.location.site.name : null;
                    var pageUrl = $siteURL(Alfresco.util.substituteDotNotation(action.params.page, item),
                        {
                            site: recordSiteName
                        });
                    actionUrls = this.getActionUrls(item);
                    url = YAHOO.lang.substitute(pageUrl, actionUrls);
                    return this["goToPage"].bind(this, url);
                }
                else {
                    Alfresco.logger.warn("Action configuration error: Missing 'page' parameter for actionId: ", action.id);
                }
            }
            else if (action.type === "javascript") {
                if (action.params["function"]) {
                    return this[action.params["function"]];
                }
                else {
                    Alfresco.logger.warn("Action configuration error: Missing 'function' parameter for actionId: ", action.id);
                }
            }
        },

        openLink: function DocumentPreviewControl_openLink(url) {
            var urlParts = url.replace("\"", "").split(" ");
            url = urlParts[0];
            window.open(url);
        },

        goToPage: function DocumentPreviewControl_goToPage(url) {
            window.open(url);
        },

        attachmentActionAdd: function DocumentPreviewControl_attachmentActionAdd(layer, event, args) {
            var name = args.name;
            var nodeRef = args.nodeRef;

            var title = name ? this.msg('label.attachment.upload-to-category') + ': "' + name + '"' : this.msg('label.attachment.add-attachment');

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
                    onFileUploadComplete: {
                        fn: function (obj) {
                            if (obj.successful != null && obj.successful.length > 0) {
                                me.selectedAttachment = {};
                                me.selectedAttachment.nodeRef = obj.successful[0].nodeRef;
                            }

                            if (this.options.itemId) {
                                me.loadAttachments();
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

        selectAttachment: function DocumentPreviewControl_selectAttachment() {
            if (this.attachmentsSelect.selectedIndex < 0) {
                return null;
            }
            var selectedOption = this.attachmentsSelect.options[this.attachmentsSelect.selectedIndex];
            var nodeRef = selectedOption.value;
            if (nodeRef != null && nodeRef.length > 0) {
                this.selectedAttachment = this.attachmentsList[nodeRef];
                if (this.selectedAttachment != null) {
                    this._loadVersions(nodeRef);
                }
            }
            var actionsElSelect = Dom.get(this.id + "-attachment-actions");
            if (actionsElSelect) {
                this.updateActions();
            }
        },

        updateActions: function DocumentPreviewControl_selectAttachment() {
            var actions = this.checkActions(this.selectedAttachment);
            this.renderActions(actions, this.selectedAttachment);
        },

        _loadVersions: function DocumentPreviewControl_loadVersions(nodeRef) {
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

        onActionDetails: function DocumentPreviewControl_onActionDetails() {
            if (this.selectedAttachment != null) {
                var scope = this,
                    nodeRef = this.selectedAttachment.nodeRef;

                // Intercept before dialog show
                var doBeforeDialogShow = function (p_form, p_dialog) {
                    // Dialog title
                    var fileSpan = '<span class="light">' + $html(this.selectedAttachment.fileName) + '</span>';

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

        onActionDelete: function DocumentPreviewControl_onActionDelete() {
            if (this.selectedAttachment != null) {
                var me = this,
                    content = "document",
                    displayName = this.selectedAttachment.fileName;

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

        attachmentActionDeleteConfirm: function DocumentPreviewControl_attachmentActionDeleteConfirm(attachment, displayName) {
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

        onAttachmentsDeleteSuccess: function DocumentPreviewControl_onAttachmentsDeleteSuccess() {
            this.loadAttachments();

            Bubbling.fire('removeAttachmentAction', {
                nodeRef: this.deletedAttachment
            });
            this.deletedAttachment = null;
        },

        onActionUploadNewVersion: function DocumentPreviewControl_onActionUploadNewVersion() {
            if (this.selectedAttachment != null) {
                var displayName = this.selectedAttachment.fileName,
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
                                me.loadAttachments();
                            }, 2000);
                        },
                        scope: this
                    },
                    suppressRefreshEvent: true
                };
                this.fileUpload.show(singleUpdateConfig);
            }
        },

        setHeights: function DocumentPreviewControl_setHeights() {
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

                    Dom.setStyle(previewerDiv, 'height', effectiveContainerHeight + "px");
                    Dom.setStyle(previewerDocument, 'height', 'auto');
                    Dom.setStyle(previewerDocument, 'max-height', effectivePreviewHeight + "px");
                }
            }
        },
    }, true);
})();
