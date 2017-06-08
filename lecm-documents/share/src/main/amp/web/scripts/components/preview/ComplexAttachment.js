/**
 * Supports at least the following mime types: "complex-attachment".
 *
 * @param wp {Alfresco.WebPreview} The Alfresco.WebPreview instance that decides which plugin to use
 * @param attributes {Object} Arbitrary attributes brought in from the <plugin> element
 */
Alfresco.WebPreview.prototype.Plugins.ComplexAttachment = function (wp, attributes) {
    this.wp = wp;
    this.attributes = YAHOO.lang.merge(Alfresco.util.deepCopy(this.attributes), attributes);
    LogicECM.module.Base.Util.loadCSS(
        ["scripts/components/preview/ComplexAttachment.css"]
    );
    return this;
};

Alfresco.WebPreview.prototype.Plugins.ComplexAttachment.prototype =
{
    /**
     * Attributes
     */
    attributes: {
        /**
         * Decides if the node's content or one of its thumbnails shall be displayed.
         * Leave it as it is if the node's content shall be used.
         * Set to a custom thumbnail definition name if the node's thumbnail contains the image to display.
         *
         * @property src
         * @type String
         * @default null
         */
        src: null,
    },

    toolbar: null,
    id: null,
    workFilesNodes: null,
    uploadDestinationNodeRef: null,
    currentPage: null,
    totalPageNum: null,
    currentPageSpinner: null,
    lockPageSwitch: false,
    complexAttachmentNodeRef: null,
    deferredCreateWorkFilesUploadButton: null,
    hasStateMachine: null,
    categoryReadOnly: null,
    hasAddAttachmentPerm: null,

    /**
     * Tests if the plugin can be used in the users browser.
     *
     * @method report
     * @return {String} Returns nothing if the plugin may be used, otherwise returns a message containing the reason
     *         it cant be used as a string.
     * @public
     */
    report: function ComplexAttachment_report() {
    },

    /**
     * Display the node.
     *
     * @method display
     * @public
     */
    display: function ComplexAttachment_display() {
        this.id = this.wp.id;
        this.complexAttachmentNodeRef = this.wp.options.nodeRef;
        this.loadDocument(this.complexAttachmentNodeRef);
        this.displayComplexAttachment(this.complexAttachmentNodeRef);
    },

    displayComplexAttachment: function (complexAttachmentNodeRef) {
        Alfresco.util.Ajax.request({
            method: 'GET',
            url: Alfresco.constants.PROXY_URI_RELATIVE + "/lecm/document/api/complex-attachments?nodeRef=" + complexAttachmentNodeRef,
            successCallback: {
                fn: onGetComplexAttachmentsFilesSuccess, scope: this
            },
            scope: this,
            execScripts: true
        });

        function onGetComplexAttachmentsFilesSuccess(response) {
            this.totalPageNum = response.json.totalPageNum;
            this.workFilesNodes = response.json.nodes;
            this.uploadDestinationNodeRef = response.json.folderNodeRef;

            if (this.totalPageNum == 0) {
                this.onNoWorkFiles();
            } else {
                this.currentPage = 1;
                this.renderPreview(this.workFilesNodes);
            }
        }
    },

    loadDocument: function (complexAttachmentNodeRef) {
        Alfresco.util.Ajax.request({
            method: "GET",
            url: Alfresco.constants.PROXY_URI_RELATIVE + "lecm/document/attachments/api/getDocumentByAttachment?nodeRef=" + complexAttachmentNodeRef,
            successCallback: {
                fn: function (response) {
                    this.documentNodeRef = response.json.nodeRef;
                },
                scope: this
            }
        })
    },

    onNoWorkFiles: function () {
        var container = Selector.query(".message", this.wp.id, true);
        container.innerHTML = Alfresco.util.message('lecm.complex.not.upload.files');
        this.deferredCreateWorkFilesUploadButton = new Alfresco.util.Deferred(['onCheckHasStatemachine', 'onCheckHasAddAttachmentPerm', 'onCheckCategoryReadOnly'], {
            scope: this,
            fn: function () {
                if (this.hasStatemachine && this.hasAddAttachmentPerm && !this.categoryReadOnly) {
                    var container = Selector.query(".message", this.wp.id, true);
                    container.innerHTML += "<br><br><br><button id='" + this.wp.id + '-upload-work-files' + "'>Загрузить</button>";
                }
                Alfresco.util.createYUIButton(this, 'upload-work-files', this.uploadLargeFiles, {
                        disabled: false,
                        value: "CreateChildren"
                    }
                );
            }
        });
        this.checkPermissionsForUploadWorkFiles();


    },

    renderPreview: function (nodes) {
        Alfresco.util.Ajax.request({
            url: Alfresco.constants.URL_SERVICECONTEXT + "components/preview/web-preview",
            dataObj: {
                nodeRef: nodes[0].nodeRef.toString(), /* отображаем первую страницу */
                htmlid: this.id
            },
            successCallback: {
                fn: function () {
                    this.afterSubPreviewerRendered();
                },
                scope: this
            },
            scope: this,
            execScripts: true
        });
    },

    afterSubPreviewerRendered: function () {
        this.createToolbar();
    },

    createToolbar: function () {
        if (!Dom.get(this.id + '-toolbar')) {
            this.toolbar = document.createElement('div');
            this.toolbar.id = this.id + '-toolbar';
            var wpElement = Dom.get(this.id);
            /* toolbar будет над телом превьювера */
            wpElement.insertBefore(this.toolbar, Dom.get(this.id + '-body'));

            this.toolbar.setAttribute('align', 'center');
            this.toolbar.className = "share-toolbar previewer-toolbar";
            this.toolbar.style.marginBottom = "3px";

            var spinnerSpan = document.createElement('span');
            spinnerSpan.className = "spinner-span";
            this.currentPageSpinner = document.createElement('input');
            spinnerSpan.appendChild(this.currentPageSpinner);
            this.currentPageSpinner.setAttribute("type", "number");
            this.currentPageSpinner.min = 1;
            this.currentPageSpinner.max = this.workFilesNodes.length;
            this.currentPageSpinner.value = 1;
            YAHOO.util.Event.addListener(this.currentPageSpinner, 'change', this.onSpinnerValueChanged.bind(this));

            this.maxPageSpan = document.createElement('span');
            this.maxPageSpan.className = "total-page-span";
            this.maxPageSpan.innerHTML = ' / ' + this.totalPageNum;

            var prevBtn = this.createToolbarButton('prev-btn', Alfresco.util.message('lecm.complex.prev'), this.goPrevPage);
            prevBtn.className += " backLink";
            this.toolbar.appendChild(prevBtn);
            var nextBtn = this.createToolbarButton('next-btn', Alfresco.util.message('lecm.complex.next'), this.goNextPage);
            nextBtn.className += " forwardLink";
            this.toolbar.appendChild(nextBtn);
            this.toolbar.appendChild(spinnerSpan);
            this.toolbar.appendChild(this.maxPageSpan);
            var openInNewTabBtn = this.createToolbarButton('in-new-tab-btn', Alfresco.util.message('lecm.complex.show.in.browser'), this.openCurrentPageInNewTab);
            this.toolbar.appendChild(openInNewTabBtn);
            var openDetailViewBtn = this.createToolbarButton('detail-view-btn', Alfresco.util.message('lecm.complex.open.page'), this.openCurrentPageDetailView);
            this.toolbar.appendChild(openDetailViewBtn);

            this.deferredCreateWorkFilesUploadButton = new Alfresco.util.Deferred(['onCheckHasStatemachine', 'onCheckHasAddAttachmentPerm', 'onCheckCategoryReadOnly'], {
                scope: this,
                fn: function () {
                    if (this.hasStatemachine && this.hasAddAttachmentPerm && !this.categoryReadOnly) {
                        var uploadBtn = this.createToolbarButton('upload-btn', Alfresco.util.message('lecm.complex.upload.files'), this.uploadLargeFiles);
                        this.toolbar.insertBefore(uploadBtn, openInNewTabBtn);
                    }
                }
            });
            this.checkPermissionsForUploadWorkFiles();
        }
    },

    createToolbarButton: function(buttonName, label, clickAction) {
        var button = document.createElement('button');
        button.className = "toolbar-element";
        button.id = this.toolbar.id + '-' + buttonName;
        button.innerText = label;
        YAHOO.util.Event.addListener(button, 'click', clickAction.bind(this));
        return button;
    },

    onSpinnerValueChanged: function(e) {
        var newPage = parseInt(this.currentPageSpinner.value, 10);
        if (newPage > 0 && newPage <= this.totalPageNum && newPage != this.currentPage) {
            this.onPageSelect(newPage);
        }
    },

    setCurrentPage: function(newCurrentPage) {
        if (newCurrentPage === parseInt(newCurrentPage, 10) && newCurrentPage > 0 &&
            newCurrentPage <= this.totalPageNum) {
            this.currentPage = newCurrentPage;
            this.currentPageSpinner.value = newCurrentPage;
            this.totalPageNum = this.workFilesNodes.length;
            this.currentPageSpinner.max = this.totalPageNum;
            this.maxPageSpan.innerHTML = ' / ' + this.totalPageNum;
        }
    },

    goPrevPage: function () {
        if (this.currentPage >= 2) {
            this.onPageSelect(this.currentPage - 1);
        }
    },

    goNextPage: function () {
        if (this.currentPage < this.totalPageNum) {
            this.onPageSelect(this.currentPage + 1);
        }
    },

    onPageSelect: function (page) {
        if (!this.pageSwitchLocked) {
            this.pageSwitchLocked = true;
            Alfresco.util.Ajax.request(
                {
                    url: Alfresco.constants.URL_SERVICECONTEXT + "components/preview/web-preview",
                    dataObj: {
                        nodeRef: this.workFilesNodes[page - 1].nodeRef.toString(),
                        htmlid: this.id
                    },
                    successCallback: {
                        fn: function () {
                            var previewerDiv = Dom.get(this.id + "-previewer-div");
                            previewerDiv.innerHTML = "<div class='message'></div>";
                            previewerDiv.className = "previewer ComplexAttachment";
                            previewerDiv.style = "";
                            this.setCurrentPage(page);
                            this.pageSwitchLocked = false;
                        },
                        scope: this
                    },
                    scope: this,
                    execScripts: true
                });
        }
    },

    uploadLargeFiles: function (e) {
        var uploaderDialogTitle = YAHOO.lang.substitute(Alfresco.util.message('lecm.complex.upload.files'));

        if (this.fileUpload == null) {
            this.fileUpload = Alfresco.getFileUploadInstance();
        }

        var multiUploadConfig = {
            destination: this.uploadDestinationNodeRef,
            filter: [],
            mode: this.fileUpload.MODE_MULTI_UPLOAD,
            thumbnails: "doclib",
            onFileUploadComplete: {
                fn: function (response) {
                    for (var i = 0; i < response.successful.length; i++) {
                        this.workFilesNodes.push(response.successful[i]);
                    }
                    this.totalPageNum = this.workFilesNodes.length;
                    this.workFilesNodes.sort(function (a, b) {
                        var aName = a.fileName || a.properties["cm:name"];
                        var bName = b.fileName || b.properties["cm:name"];

                        if (aName > bName) {
                            return 1;
                        }
                        if (aName < bName) {
                            return -1;
                        }

                        return 0;
                    });
                    var previewerDiv = Dom.get(this.id + "-previewer-div");
                    previewerDiv.innerHTML = "<div class='message'></div>";
                    previewerDiv.className = "previewer ComplexAttachment";
                    previewerDiv.style = "";
                    this.renderPreview(this.workFilesNodes, this.totalPageNum);
                    this.onPageSelect(1);
                },
                scope: this
            },
            suppressRefreshEvent: true
        };
        this.fileUpload.show(multiUploadConfig);
        if (this.fileUpload.uploader.titleText) {
            this.fileUpload.uploader.titleText.innerHTML = uploaderDialogTitle;
        } else if (this.fileUpload.uploader.widgets && this.fileUpload.uploader.widgets.panel) {
            this.fileUpload.uploader.widgets.panel.setHeader(uploaderDialogTitle);
        }
        YAHOO.util.Event.preventDefault(e);
    },

    checkPermissionsForUploadWorkFiles: function () {
        Alfresco.util.Ajax.request({
            method: 'GET',
            url: Alfresco.constants.PROXY_URI_RELATIVE + 'lecm/documents/hasStatemachine?nodeRef=' + this.documentNodeRef,
            successCallback: {
                fn: function (response) {
                    this.hasStatemachine = response.json;
                    this.deferredCreateWorkFilesUploadButton.fulfil('onCheckHasStatemachine');
                },
                scope: this
            }
        });
        Alfresco.util.Ajax.request({
            method: 'GET',
            url: Alfresco.constants.PROXY_URI_RELATIVE +
            'lecm/security/api/getPermission?nodeRef=' + this.documentNodeRef +
            '&permission=' + '_lecmPerm_ContentAdd',
            successCallback: {
                fn: function (response) {
                    this.hasAddAttachmentPerm = response.json;
                    this.deferredCreateWorkFilesUploadButton.fulfil('onCheckHasAddAttachmentPerm');
                },
                scope: this
            }
        });
        Alfresco.util.Ajax.request({
            method: 'GET',
            url: Alfresco.constants.PROXY_URI_RELATIVE +
            'lecm/document/attachments/api/getCategoryByAttachment?nodeRef=' + this.complexAttachmentNodeRef,
            successCallback: {
                fn: function (response) {
                    this.categoryReadOnly = response.json.isReadOnly;
                    this.deferredCreateWorkFilesUploadButton.fulfil('onCheckCategoryReadOnly');
                },
                scope: this
            }
        });
    },

    openCurrentPageInNewTab: function () {
        var currentPageNode = this.workFilesNodes[this.currentPage - 1];
        var fileName = currentPageNode.fileName || currentPageNode.properties['cm:name'];
        var url = Alfresco.constants.PROXY_URI_RELATIVE + "api/node/content/" + currentPageNode.nodeRef.toString().replace(":/", "") + "/" + fileName;
        window.open(url);
    },

    openCurrentPageDetailView: function () {
        var currentPageNode = this.workFilesNodes[this.currentPage - 1];
        var url = Alfresco.constants.URL_PAGECONTEXT + "document-attachment?nodeRef=" + currentPageNode.nodeRef.toString();
        window.open(url);
    }
};
