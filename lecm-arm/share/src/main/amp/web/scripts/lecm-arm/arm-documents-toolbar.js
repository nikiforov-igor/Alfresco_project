if (typeof LogicECM == "undefined" || !LogicECM) {
    LogicECM = {};
}

LogicECM.module = LogicECM.module || {};

LogicECM.module.ARM = LogicECM.module.ARM|| {};

(function () {
    var Dom = YAHOO.util.Dom;

    LogicECM.module.ARM.DocumentsToolbar = function (htmlId) {
        LogicECM.module.ARM.DocumentsToolbar.superclass.constructor.call(this, "LogicECM.module.ARM.DocumentsToolbar", htmlId);

        this.filtersDialog = null;
        this.columnsDialog = null;
        this.splashScreen = null;
        this.avaiableFilters = [];
        this.currentNode = null;

        this.deferredListPopulation = new Alfresco.util.Deferred(["updateArmFilters", "initDatagrid"],
            {
                fn: this.onToolbarUpdate,
                scope: this
            });

        YAHOO.Bubbling.on("updateArmFilters", this.onUpdateArmFilters, this);
	    YAHOO.Bubbling.on("selectedItemsChanged", this.onCheckDocument, this);
        YAHOO.Bubbling.on('hiddenAssociationFormReady', this._onHiddenAssociationFormReady, this);
        YAHOO.Bubbling.on("objectFinderReady", this._onObjectFinderReady, this);

        YAHOO.Bubbling.on("restoreDefaultColumns", this.onRestoreDefaultColumns, this);
        return this;
    };

    YAHOO.extend(LogicECM.module.ARM.DocumentsToolbar, LogicECM.module.Base.Toolbar);

    YAHOO.lang.augmentObject(LogicECM.module.ARM.DocumentsToolbar.prototype,
        {
            filtersDialog: null,
            columnsDialog: null,
            splashScreen: null,
            gridBubblingLabel: "documents-arm",
	        doubleClickLock: false,

            avaiableFilters:[],
            currentType: null,

            currentNode: null,

            onInitDataGrid: function BaseToolbar_onInitDataGrid(layer, args) {
                LogicECM.module.ARM.DocumentsToolbar.superclass.onInitDataGrid.call(this,layer, args);
                this.deferredListPopulation.fulfil("initDatagrid");
            },

            _renderFilters: function (filters) {
                var filtersDiv = Dom.get(this.id + "-filters-dialog-content");
                var toolbar = this;
                Alfresco.util.Ajax.jsonRequest({
                    method: "POST",
                    url: Alfresco.constants.PROXY_URI + "lecm/arm/draw-filters",
                    dataObj: {
                        htmlId: Alfresco.util.generateDomId(),
                        filters: YAHOO.lang.JSON.stringify(this.avaiableFilters),
                        armCode: LogicECM.module.ARM.SETTINGS.ARM_CODE
                    },
                    successCallback: {
                        fn: function (oResponse) {
                            filtersDiv.innerHTML = oResponse.serverResponse.responseText;
                            if (toolbar.filtersDialog != null) {
                                toolbar.filtersDialog.show();
	                            toolbar.toolbarButtons["defaultActive"].filtersButton.set("disabled", true);
	                            Dom.addClass(toolbar.id + "-filters-button-container", "showed");
                            }
                        }
                    },
                    failureCallback: {
                        fn: function () {
                        }
                    },
                    scope: this,
                    execScripts: true
                });
            },

            _renderColumns: function () {
                var columnsDiv = Dom.get(this.id + "-columns-dialog-content");
                var toolbar = this;
                var ref = this.currentNode.data.nodeRef;
                if (!ref || this.currentNode.data.nodeType != "lecm-arm:node") {
                    ref = this.currentNode.data.armNodeRef;
                }
                Alfresco.util.Ajax.jsonRequest({
                    method: "GET",
                    url: Alfresco.constants.PROXY_URI + "lecm/arm/draw-select-columns",
                    dataObj: {
                        htmlId: Alfresco.util.generateDomId(),
                        nodeRef: ref,
                        columns: this._getNodeColumnsStr(this.currentNode)
                    },
                    successCallback: {
                        fn: function (oResponse) {
                            columnsDiv.innerHTML = oResponse.serverResponse.responseText;
                            if (toolbar.columnsDialog != null) {
                                toolbar.columnsDialog.show();
                                toolbar.toolbarButtons["defaultActive"].columnsButton.set("disabled", true);
                                Dom.addClass(toolbar.id + "-columns-button-container", "showed");
                            }
                        }
                    },
                    failureCallback: {
                        fn: function () {
                        }
                    },
                    scope: this,
                    execScripts: true
                });
            },

            _getNodeColumnsStr: function(node) {
                var columns = [];
                if (node != null) {
                    if (node.data.columns && node.data.columns.length > 0) {
                        for (var i = 0; i < node.data.columns.length; i++) {
                            var column = node.data.columns[i];
                            columns.push(column.id);
                        }
                    }
                }
                return columns.join(";");
            },

		    _drawFiltersPanel: function () {
			    // создаем диалог
			    this.filtersDialog = new YAHOO.widget.Panel(this.id + "-filters-dialog",
				    {
					    fixedcenter: false,
					    close: false,
					    draggable: false,
					    zindex: 4,
					    modal: true,
					    visible: false
				    }
			    );
			    this.filtersDialog.render();

			    var hideMaskCss = document.createElement("style");
			    hideMaskCss.type = "text/css";
			    hideMaskCss.innerHTML = "#" + this.id + "-filters-dialog_mask { opacity: 0; }";
			    document.body.appendChild(hideMaskCss);

			    YAHOO.util.Event.onAvailable(this.id + "-filters-dialog_mask", function () {
				    YAHOO.util.Event.on(this.id + "-filters-dialog_mask", 'click', this.hideFiltersDialog, null, this);
			    }, null, this);
		    },

            _drawColumnsPanel: function () {
                // создаем диалог
                this.columnsDialog = new YAHOO.widget.Panel(this.id + "-columns-dialog",
                    {
                        fixedcenter: false,
                        close: false,
                        draggable: false,
                        zindex: 4,
                        modal: true,
                        visible: false
                    }
                );
                this.columnsDialog.render();

                var hideMaskCss = document.createElement("style");
                hideMaskCss.type = "text/css";
                hideMaskCss.innerHTML = "#" + this.id + "-columns-dialog_mask { opacity: 0; }";
                document.body.appendChild(hideMaskCss);

                YAHOO.util.Event.onAvailable(this.id + "-columns-dialog_mask", function () {
                    YAHOO.util.Event.on(this.id + "-columns-dialog_mask", 'click', this.hideColumnsDialog, null, this);
                }, null, this);
            },

            onFiltersClick: function () {
                this._renderFilters(this.avaiableFilters);
            },

            onColumnsClick: function () {
                this._renderColumns();
            },

	        hideFiltersDialog: function () {
		        this.filtersDialog.hide();
		        this.toolbarButtons["defaultActive"].filtersButton.set("disabled", false);
		        Dom.removeClass(this.id + "-filters-button-container", "showed");
	        },

            hideColumnsDialog: function () {
                this.columnsDialog.hide();
                this.toolbarButtons["defaultActive"].columnsButton.set("disabled", false);
                Dom.removeClass(this.id + "-columns-button-container", "showed");
            },

            onApplyFilterClick: function () {
                //update current filters
                var form = Dom.get('filersForm');
                if (form) {
                    YAHOO.Bubbling.fire ("updateCurrentFilters", {
                        filtersData: this._buildFormData(form)
                    });
                }

	            this.hideFiltersDialog();
            },

	        onCancelFilterClick: function () {
		        this.hideFiltersDialog();
	        },
            onApplyColumnsClick: function () {
                //update current filters
                var form = Dom.get('columnsForm');
                if (form) {
                    YAHOO.Bubbling.fire ("updateCurrentColumns", {
                        selectedColumns: this._buildColumnsData(form)
                    });
                }

                this.hideColumnsDialog();
            },

            onCancelColumnsClick: function () {
                this.hideColumnsDialog();
            },

            _initButtons: function () {
	            this._drawFiltersPanel();
	            this._drawColumnsPanel();
                this.toolbarButtons["defaultActive"].filtersButton = Alfresco.util.createYUIButton(this, "filtersButton", this.onFiltersClick);
                this.toolbarButtons["defaultActive"].columnsButton = Alfresco.util.createYUIButton(this, "columnsButton", this.onColumnsClick);
	            this.widgets.filtersApplyButton = Alfresco.util.createYUIButton(this, "filters-apply-button", this.onApplyFilterClick);
	            this.widgets.filtersCancelButton = Alfresco.util.createYUIButton(this, "filters-cancel-button", this.onCancelFilterClick);
                this.widgets.columnsApplyButton = Alfresco.util.createYUIButton(this, "columns-apply-button", this.onApplyColumnsClick);
                this.widgets.columnsCancelButton = Alfresco.util.createYUIButton(this, "columns-cancel-button", this.onCancelColumnsClick);

                this.toolbarButtons["defaultActive"].groupActionsButton = new YAHOO.widget.Button(
                    this.id + "-groupActionsButton",
                    {
                        type: "menu",
                        menu: [],
                        disabled: false
                    }
                );

                this.toolbarButtons["defaultActive"].groupActionsButton.on("click", this.onCheckDocumentFinished.bind(this));
                this.toolbarButtons["defaultActive"].groupActionsButton.getMenu().cfg.setProperty("classname", "group-actions-dialog");
                this.toolbarButtons["defaultActive"].groupActionsButton.getMenu().subscribe("hide", this.clearOperationsList.bind(this));
                this.toolbarButtons["defaultActive"].groupActionsButton.set("disabled", true);

                this.toolbarButtons["defaultActive"].searchButton = Alfresco.util.createYUIButton(this, "searchButton", this.onSearchClick);

	            this.toolbarButtons["defaultActive"].extendSearchButton = Alfresco.util.createYUIButton(this, "extendSearchButton", this.onExSearchClick,
			            {
				            disabled: true
			            });

                var exportButtonMenu  = [
                            { text: Alfresco.util.message('lecm.arm.lbl.unload.every'), value: "all", onclick: { fn: this.onExportClick.bind(this) } },
                    	    { text: Alfresco.util.message('lecm.arm.lbl.unload.select'), value: "checked", onclick: { fn: this.onExportClick.bind(this) } }
                    	];
                this.toolbarButtons["defaultActive"].exportButton = new YAHOO.widget.Button(
                    this.id + "-exportButton",
                    {
                        type: "menu",
                        menu: exportButtonMenu,
                        disabled: false
                    }
                );
                this.toolbarButtons["defaultActive"].exportButton.getMenu().cfg.setProperty("classname", "group-actions-dialog");

            },

            clearOperationsList: function clearOperationsListFunction() {
                var button = this.toolbarButtons["defaultActive"].groupActionsButton;
                var menu = button.getMenu();
                if (YAHOO.util.Dom.inDocument(menu.element)) {
                    menu.clearContent();
                    menu.render();
                }
            },
            onCheckDocument: function onCheckDocumentFunction() {
                var button = this.toolbarButtons["defaultActive"].groupActionsButton;

	            var buttonName = this.msg("button.group-actions");
                var items = this.modules.dataGrid.getAllSelectedItems();
                if (items.length == 0) {
                    button.set("disabled", true);
                } else {
                    button.set("disabled", false);
	                buttonName += "<span class=\"group-actions-counter\">";
	                buttonName += "(" + items.length + ")";
	                buttonName += "</span>";
                }

	            button.set("label", buttonName);
            },

            onCheckDocumentFinished: function onCheckDocumentFinished_Function() {
                var button = this.toolbarButtons["defaultActive"].groupActionsButton;
                var menu = button.getMenu();
                var items = this.modules.dataGrid.getAllSelectedItems();
                var loadItem = [];
                loadItem.push({
                    text: Alfresco.util.message('lecm.arm.lbl.loading'),
                    disabled: true
                });
                if (YAHOO.util.Dom.inDocument(menu.element)) {
                    menu.clearContent();
                    menu.addItems(loadItem);
                    menu.render();
                } else {
                    menu.itemData = loadItem;
                }
                var me = this;
                Alfresco.util.Ajax.jsonRequest({
                    method: "POST",
                    url: Alfresco.constants.PROXY_URI + "lecm/groupActions/list",
                    dataObj: {
                        items: JSON.stringify(items)
                    },
                    successCallback: {
                        fn: function (oResponse) {
                            var json = oResponse.json;
                            var actionItems = [];
                            var wideActionItems = [];
                            for (var i in json) {
                                if (!json[i].wide) {
                                    actionItems.push({
                                        text: json[i].title,
                                        value: json[i].id,
                                        onclick: {
                                            fn: me.onGroupActionsClick,
                                            obj: {
                                                actionId: json[i].id,
                                                type: json[i].type,
                                                withForm: json[i].withForm,
                                                items: items,
                                                workflowId: json[i].workflowId,
                                                label: json[i].title
                                            },
                                            scope: me
                                        }
                                    });
                                } else {
                                    wideActionItems.push({
                                        text: json[i].title,
                                        value: json[i].id,
                                        onclick: {
                                            fn: me.onGroupActionsClick,
                                            obj: {
                                                actionId: json[i].id,
                                                type: json[i].type,
                                                withForm: json[i].withForm,
                                                items: items,
                                                workflowId: json[i].workflowId,
                                                label: json[i].title
                                            },
                                            scope: me
                                        }
                                    });
                                }
                            }
                            if (actionItems.length == 0 && wideActionItems.length == 0) {
                                actionItems.push({
                                    text: Alfresco.util.message('lecm.arm.lbl.not.available.oper'),
                                    disabled: true
                                });
                            }
                            if (actionItems.length != 0 && wideActionItems.length != 0) {
                                wideActionItems[0].classname = "toplineditem";
                            }
                            if (YAHOO.util.Dom.inDocument(menu.element)) {
                                menu.clearContent();
                                menu.addItems(actionItems);
                                menu.addItems(wideActionItems);
                                menu.render();
                            } else {
                                menu.addItems(actionItems);
                                menu.addItems(wideActionItems);
                            }
                        }
                    },
                    failureCallback: {
                        fn: function () {
                        }
                    },
                    scope: this,
                    execScripts: true
                });
            },

            onGroupActionsClick: function onGroupActionsClick(p_sType, p_aArgs, p_oItem) {
                if (p_oItem.withForm) {
                    this._createScriptForm(p_oItem);
                } else if (p_oItem.type == "lecm-group-actions:script-action") {
                    Alfresco.util.PopupManager.displayPrompt({
                        title: this.msg('lecm.arm.ttl.action.perform'),
                        text: this.msg('lecm.arm.msg.action.confirm') + " \"" + p_oItem.label + "\"",
                        buttons: [
                            {
                                text: this.msg('lecm.arm.lbl.ok'),
                                handler: {
                                    obj: this,
                                    fn: function dlA_onAction_action(event, obj) {
	                                    this.destroy();
	                                    Alfresco.util.Ajax.jsonPost({
                                            url: Alfresco.constants.PROXY_URI + "lecm/groupActions/exec",
                                            dataObj: {
                                                items: p_oItem.items,
                                                actionId: p_oItem.actionId
                                            },
                                            successCallback: {
                                                scope: obj,
                                                fn: function (oResponse) {
                                                    this._actionResponse(p_oItem.label, oResponse);
                                                }
                                            },
                                            failureMessage: obj.msg('message.failure'),
                                            execScripts: true
	                                    });
                                    }
                                }
                            },
                            {
                                text: this.msg('lecm.arm.lbl.cancel'),
                                handler: function dlA_onActionDelete_cancel() {
                                    this.destroy();
                                },
                                isDefault: true
                            }
                        ]
                    });
                } else if (p_oItem.type == "lecm-group-actions:workflow-action") {
                    if (this.doubleClickLock) return;
                    this.doubleClickLock = true;

                    this.options.currentSelectedItems = p_oItem.items;
                    var templateUrl = Alfresco.constants.URL_SERVICECONTEXT;
                    var formWidth = "84em";

                    templateUrl += "lecm/components/form";
                    var templateRequestParams = {
                            itemKind: "workflow",
                            itemId: p_oItem.workflowId,
                            mode: "create",
                            submitType: "json",
                            formId: "workflow-form",
                            showCancelButton: true,
                            showCaption: false
                        };
                    var responseHandler = function(response) {
                            document.location.reload();
                        }
                    var me = this;
                    LogicECM.CurrentModules = {};
                    LogicECM.CurrentModules.WorkflowForm = new Alfresco.module.SimpleDialog("workflow-form").setOptions({
                        width: formWidth,
                        templateUrl: templateUrl,
                        templateRequestParams: templateRequestParams,
                        actionUrl: null,
                        destroyOnHide: true,
                        doBeforeDialogShow: {
                            scope: this,
                            fn: function(p_form, p_dialog) {
                                p_dialog.dialog.setHeader(this.msg("logicecm.workflow.runAction.label", p_oItem.label));
                                var contId = p_dialog.id + "-form-container";
                                Dom.addClass(contId, "metadata-form-edit");
                                Dom.addClass(contId, "no-form-type");

                                this.doubleClickLock = false;

                                p_dialog.dialog.subscribe('destroy', LogicECM.module.Base.Util.formDestructor, {moduleId: p_dialog.id}, this);
                            }
                        },
                        onSuccess: {
                            scope: this,
                            fn: responseHandler
                        }
                    }).show();
                }
            },

            onExportClick: function onExportClick_Function (p_sType, p_aArgs, p_oItem) {
                var value = p_oItem.value;
                if (value === "checked" && this.modules.dataGrid.getSelectedItems().length == 0 ) {
                    Alfresco.util.PopupManager.displayPrompt(
                        {
                            title: Alfresco.util.message('lecm.arm.ttl.unload.items'),
                            text: Alfresco.util.message('lecm.arm.msg.unload.elems.fail'),
                            buttons: [
                                {
                                    text: Alfresco.util.message('lecm.arm.lbl.ok'),
                                    handler: function dlA_onAction_action() {
                                        this.destroy();
                                    }
                                }
                            ]
                        });
                } else {
                    this.modules.dataGrid.exportData(value === "all");
                }
            },

            _onObjectFinderReady: function StartWorkflow_onObjectFinderReady(layer, args) {
                var objectFinder = args[1].eventGroup;
                if (objectFinder.options.field == "assoc_packageItems") {
                    objectFinder.selectItems(this.options.currentSelectedItems.join(","));
                }
            },

            _onHiddenAssociationFormReady: function StartWorkflow_onHiddenAssociationFormReady(layer, args) {
                if (args[1].fieldName == 'assoc_packageItems') {
                    Dom.get(args[1].fieldId + '-added').value = this.options.currentSelectedItems.join(",");
                }
            },

            _openMessageWindow: function openMessageWindowFunction(title, message, reload) {
                Alfresco.util.PopupManager.displayPrompt(
                    {
                        title: Alfresco.util.message('lecm.arm.ttl.oper.res') + " \"" + title + "\"",
                        text: message,
                        noEscape: true,
                        buttons: [
                            {
                                text: Alfresco.util.message('lecm.arm.lbl.ok'),
                                handler: function dlA_onAction_action()
                                {
                                    this.destroy();
                                    if (reload) {
                                        document.location.reload();
                                    }
                                }
                            }]
                    });
            },

            _createScriptForm: function _createScriptFormFunction(item) {
                var me = this;
                var doBeforeDialogShow = function (p_form, p_dialog) {
                    var contId = p_dialog.id + "-form-container";
                    Alfresco.util.populateHTML(
                        [contId + "_h", item.label ]
                    );

                    Dom.addClass(contId, "metadata-form-edit");
                    this.doubleClickLock = false;

	                p_dialog.dialog.subscribe('destroy', LogicECM.module.Base.Util.formDestructor, {moduleId: p_dialog.id}, this);
                };

                var templateUrl = Alfresco.constants.URL_SERVICECONTEXT + "/lecm/components/form/script";
	            var templateRequestParams = {
		            itemKind: "type",
		            itemId: item.actionId,
		            formId: "scriptForm",
		            mode: "create",
		            submitType: "json",
		            items: JSON.stringify(item.items),
					showCaption: false
	            };

                // Using Forms Service, so always create new instance
                var scriptForm = new Alfresco.module.SimpleDialog(this.id + "-scriptForm");
                scriptForm.setOptions(
                    {
                        width: "40em",
                        templateUrl: templateUrl,
	                    templateRequestParams: templateRequestParams,
                        actionUrl: null,
                        destroyOnHide: true,
                        doBeforeDialogShow: {
                            fn: doBeforeDialogShow,
                            scope: this
                        },
                        onSuccess: {
                            fn: function DataGrid_onActionCreate_success(response) {
                                me._actionResponse(item.label, response);
                            },
                            scope: this
                        },
                        onFailure: {
                            fn: function DataGrid_onActionCreate_failure(response) {
                                Alfresco.util.PopupManager.displayMessage(
                                    {
                                        text: this.msg("message.save.failure")
                                    });
                                this.doubleClickLock = false;
                            },
                            scope: this
                        }
                    }).show();
            },

            _actionResponse: function actionResponseFunction(label, response) {
                var json = eval("(" + response.serverResponse.responseText + ")");
                if (json.forCollection) {
                    if (json.redirect != "") {
                        document.location.href = Alfresco.constants.URL_PAGECONTEXT + json.redirect;
                    } else if (json.openWindow) {
                        window.open(Alfresco.constants.URL_PAGECONTEXT + json.openWindow, "", "toolbar=no,location=no,directories=no,status=no,menubar=no,copyhistory=no");
                    } else if (json.withErrors) {
                        this._openMessageWindow(label, Alfresco.util.message('lecm.arm.msg.oper.perform.error') + " \"" + label + "\"", false);
                    } else {
                        document.location.reload();
                    }
                } else {
                    var message = "";
                    for (var i in json.items) {
                        var item = json.items[i];
                        if (item.redirect != "") {
                            document.location.href = Alfresco.constants.URL_PAGECONTEXT + item.redirect;
                        } else if (item.openWindow) {
                            window.open(Alfresco.constants.URL_PAGECONTEXT + item.openWindow, "", "toolbar=no,location=no,directories=no,status=no,menubar=no,copyhistory=no");
                        } else {
                            message += "<div class=\"" + (item.withErrors ? "error-item" : "noerror-item") + "\">" + item.message + "</div>";
                        }
                    }
                    if (message != "") {
                        this._openMessageWindow(label, message, true);
                    }
                }
            },

            onUpdateArmFilters: function (layer, args) {
                var currentNode = args[1].currentNode;
                var isNotGridNode = args[1].isNotGridNode;
                this.currentNode = currentNode;

                if (isNotGridNode) {
                    Dom.setStyle(this.id, "display", "none");
                } else {
                    Dom.setStyle(this.id, "display", "block");
                }

                if (currentNode !== null) {
                    var filters = currentNode.data.filters;
                    var hasFilters = filters != null && filters.length > 0;
                    var hasColumns = currentNode.data.columns != null && currentNode.data.columns.length > 0;
                    var isArmNode = currentNode.data.nodeType == "lecm-arm:node";

                    this.toolbarButtons["defaultActive"].filtersButton.set("disabled", args[1].isNotGridNode || !hasFilters);
                    this.toolbarButtons["defaultActive"].searchButton.set("disabled", args[1].isNotGridNode);
                    this.toolbarButtons["defaultActive"].columnsButton.set("disabled", args[1].isNotGridNode);
                    this.toolbarButtons["defaultActive"].exportButton.set("disabled", args[1].isNotGridNode);

                    var searchInput = Dom.get(this.id + "-full-text-search");
                    if (args[1].isNotGridNode) {
                        searchInput.setAttribute("disabled", true);
                    } else {
                        searchInput.removeAttribute("disabled");
                    }

                    if (hasFilters) {
                        this.avaiableFilters = [];
                        for (var i = 0; i < filters.length; i++) {
                            var filter = filters[i];
                            this.avaiableFilters.push(filter);
                        }
                    }

                    var types = [];
                    if (currentNode.data.types != null) {
                        types = currentNode.data.types.split(",");
                    }
                    this.toolbarButtons["defaultActive"].extendSearchButton.set("disabled", args[1].isNotGridNode ||
                        ((types.length != 1 || types[0].length == 0) && (currentNode.data.searchType == null || currentNode.data.searchType.length == 0)));
                    if (types.length == 1 && types[0].length > 0) {
	                    this.currentType = types[0];
                    } else if (currentNode.data.searchType != null && currentNode.data.searchType.length > 0) {
	                    this.currentType = currentNode.data.searchType;
                    } else {
                        this.currentType = null;
                    }
                }
                if (!this.deferredListPopulation.fulfil("updateArmFilters")){
                    this.onToolbarUpdate();
                }
            },

            onToolbarUpdate: function() {
                if (this.modules.dataGrid != null) {
                    if (this.modules.dataGrid.search != null) {
                        var searchInput = Dom.get(this.id + "-full-text-search");
                        if (searchInput != null) {
                        Dom.get(this.id + "-full-text-search").value = "";
                        YAHOO.Bubbling.fire("hideFullTextSearchLabel");
                        this.checkShowClearSearch();
                    }
                    }
                    this.onCheckDocument();
                }
            },

            _buildPreferencesValue: function () {
                return '';
            },

            _buildColumnsData: function (form) {
                var checkedColumns = [];
                if (form !== null) {
                    for (var i = 0; i < form.elements.length; i++) {
                        var element = form.elements[i];
                        if (element.type !== "checkbox" || element.disabled) {
                            continue;
                        }
                        if (element.checked) {
                            checkedColumns.push(YAHOO.lang.trim(element.value));
                        }
                    }
                }
                return checkedColumns;
            },

            _buildFormData: function (form) {
                var formData = {};
                if (form !== null) {
                    for (var i = 0; i < form.elements.length; i++) {
                        var element = form.elements[i],
                            name = element.name;
                        if (name == "-" || element.disabled || element.type === "button") {
                            continue;
                        }
                        if (name == undefined || name == "") {
                            name = element.id;
                        }
                        var value = YAHOO.lang.trim(element.value);
                        if (name) {
                            // check whether the input element is an array value
                            if ((name.length > 2) && (name.substring(name.length - 2) == '[]')) {
                                name = name.substring(0, name.length - 2);
                                if (formData[name] === undefined) {
                                    formData[name] = [];
                                }
                                formData[name].push(value);
                            }
                            // check whether the input element is an object literal value
                            else if (name.indexOf(".") > 0) {
                                var names = name.split(".");
                                var obj = formData;
                                var index;
                                for (var j = 0, k = names.length - 1; j < k; j++) {
                                    index = names[j];
                                    if (obj[index] === undefined) {
                                        obj[index] = {};
                                    }
                                    obj = obj[index];
                                }
                                obj[names[j]] = value;
                            }
                            else if (!((element.type === "checkbox" || element.type === "radio") && !element.checked)) {
                                if (element.type == "select-multiple") {
                                    for (var j = 0, jj = element.options.length; j < jj; j++) {
                                        if (element.options[j].selected) {
                                            if (formData[name] == undefined) {
                                                formData[name] = [];
                                            }
                                            formData[name].push(element.options[j].value);
                                        }
                                    }
                                }
                                else {
                                    if (formData[name] == undefined) {
                                        formData[name] = value;
                                    } else {
                                        if (YAHOO.lang.isArray(formData[name])) {
                                            formData[name].push(value);
                                        } else {
                                            var valuesArray = [];
                                            valuesArray.push(formData[name]);
                                            valuesArray.push(value);
                                            formData[name] = valuesArray;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                return formData;
            },

	        onExSearchClick: function () {
		        if (this.currentType != null) {
			        var grid = this.modules.dataGrid;
			        var advSearch = grid.search;

			        advSearch.options.searchFormId = Alfresco.util.generateDomId();
			        if (advSearch.currentForm != null && this.currentType != advSearch.currentForm.type) {
			            advSearch.currentForm = null;
			        }

			        advSearch.showDialog({
				        itemType: this.currentType
			        });
		        }
	        },

            onRestoreDefaultColumns: function (layer, args) {
                YAHOO.Bubbling.fire("updateCurrentColumns", {
                    selectedColumns: []
                });
                this.hideColumnsDialog();
            }
        }, true);
})();