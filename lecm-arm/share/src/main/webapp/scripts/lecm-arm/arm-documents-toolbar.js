if (typeof LogicECM == "undefined" || !LogicECM) {
    var LogicECM = {};
}

LogicECM.module = LogicECM.module || {};

LogicECM.module.ARM = LogicECM.module.ARM|| {};

(function () {
    var Dom = YAHOO.util.Dom;

    LogicECM.module.ARM.DocumentsToolbar = function (htmlId) {
        LogicECM.module.ARM.DocumentsToolbar.superclass.constructor.call(this, "LogicECM.module.ARM.DocumentsToolbar", htmlId);

        this.splashScreen = null;
        this.avaiableFilters = [];

	    YAHOO.Bubbling.on("armTreeNodeSelect", this.onToolbarUpdate, this);
	    YAHOO.Bubbling.on("selectedItemsChanged", this.onCheckDocument, this);
        return this;
    };

    YAHOO.extend(LogicECM.module.ARM.DocumentsToolbar, LogicECM.module.Base.Toolbar);

    YAHOO.lang.augmentObject(LogicECM.module.ARM.DocumentsToolbar.prototype,
        {
            PREFERENCE_KEY: "ru.it.lecm.arm.current-filters",

            splashScreen: null,
            gridBubblingLabel: "documents-arm",
	        doubleClickLock: false,

            avaiableFilters:[],
            isNeedUpdate: false,
            currentType: null,

            _renderFilters: function (filters, updateHtml) {
	            var filterDialog = Dom.get(this.id + "-filters-dialog");
	            if (filterDialog != null) {
	                if (!updateHtml) {
		                Dom.setStyle(filterDialog, "visibility", "visible");
	                } else {
	                    var toolbar = this;
		                var me = this;
	                    Alfresco.util.Ajax.jsonRequest({
	                        method: "POST",
	                        url: Alfresco.constants.PROXY_URI + "lecm/arm/draw-filters",
	                        dataObj: {
	                            htmlId: Alfresco.util.generateDomId(),
	                            filters: YAHOO.lang.JSON.stringify(this.avaiableFilters)
	                        },
	                        successCallback: {
	                            fn: function (oResponse) {
		                            var filterContainer = Dom.get(me.id + "-filters-dialog-content");
		                            if (filterContainer != null) {
		                                filterContainer.innerHTML = oResponse.serverResponse.responseText;
		                            }
	                                toolbar.isNeedUpdate = false;
		                            Dom.setStyle(filterDialog, "visibility", "visible");
	                            }
	                        },
	                        failureCallback: {
	                            fn: function () {
	                            }
	                        },
	                        scope: this,
	                        execScripts: true
	                    });
	                }
	            }
            },

            onFiltersClick: function () {
                //отрисовка фильтров в окне
                this._renderFilters(this.avaiableFilters, this.isNeedUpdate);
            },

            onApplyFilterClick: function () {
                //update current filters
                var form = Dom.get('filersForm');
                if (form) {
                    YAHOO.Bubbling.fire ("updateCurrentFilters", {
                        filtersData: this._buildFormData(form)
                    });
                }

	            Dom.setStyle(this.id + "-filters-dialog", "visibility", "hidden");
            },

            _initButtons: function () {
                this.toolbarButtons["defaultActive"].filtersButton = Alfresco.util.createYUIButton(this, "filtersButton", this.onFiltersClick);
	            this.widgets.searchButton = Alfresco.util.createYUIButton(this, "filters-apply-button", this.onApplyFilterClick);

                this.toolbarButtons["defaultActive"].groupActionsButton = new YAHOO.widget.Button(
                    this.id + "-groupActionsButton",
                    {
                        type: "menu",
                        menu: [],
                        disabled: false
                    }
                );

	            this.toolbarButtons["defaultActive"].searchButton = Alfresco.util.createYUIButton(this, "searchButton", this.onSearchClick);

	            this.toolbarButtons["defaultActive"].extendSearchButton = Alfresco.util.createYUIButton(this, "extendSearchButton", this.onExSearchClick,
			            {
				            disabled: true
			            });

                this.toolbarButtons["defaultActive"].groupActionsButton.on("click", this.onCheckDocumentFinished.bind(this));
                this.toolbarButtons["defaultActive"].groupActionsButton.getMenu().subscribe("hide", this.clearOperationsList.bind(this));
                this.toolbarButtons["defaultActive"].groupActionsButton.set("disabled", true);

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
                var items = this.modules.dataGrid.getSelectedItems();
                if (items.length == 0) {
                    button.set("disabled", true);
                } else {
                    button.set("disabled", false);
                }

            },

            onCheckDocumentFinished: function onCheckDocumentFinished_Function() {
                var button = this.toolbarButtons["defaultActive"].groupActionsButton;
                var menu = button.getMenu();
                var datagridItems = this.modules.dataGrid.getSelectedItems();
                var items = []
                for (var i in datagridItems) {
                    items.push(datagridItems[i].nodeRef);
                }
                var me = this;
                var splashScreen = Alfresco.util.PopupManager.displayMessage(
                {
                        spanClass: "wait",
                        displayTime: 0
                });
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
                            for (var i in json) {
                                actionItems.push({
                                    text: json[i].id,
                                    value: json[i].id,
                                    onclick: {
                                        fn: me.onGroupActionsClick,
                                        obj: {
                                            actionId: json[i].id,
                                            withForm: json[i].withForm,
                                            items: items
                                        },
                                        scope: me
                                    }
                                });
                            }
                            if (actionItems.length == 0) {
                                actionItems.push({
                                    text: "Нет доступных операций",
                                    disabled: true
                                });
                            }
                            if (YAHOO.util.Dom.inDocument(menu.element)) {
                                menu.clearContent();
                                menu.addItems(actionItems);
                                menu.render();
                            } else {
                                menu.itemData = actionItems;
                            }
                            YAHOO.lang.later(500, splashScreen, splashScreen.destroy);
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
                } else {
                    var me = this;
                    Alfresco.util.PopupManager.displayPrompt(
                        {
                            title: "Выполнение действия",
                            text: "Подтвердите выполнение действия \"" + p_oItem.actionId + "\"",
                            buttons: [
                                {
                                    text: "Ок",
                                    handler: function dlA_onAction_action()
                                    {
                                        this.destroy();
                                        Alfresco.util.Ajax.jsonRequest({
                                            method: "POST",
                                            url: Alfresco.constants.PROXY_URI + "lecm/groupActions/exec",
                                            dataObj: {
                                                items: p_oItem.items,
                                                actionId: p_oItem.actionId
                                            },
                                            successCallback: {
                                                fn: function (oResponse) {
                                                    me._actionResponse(p_oItem.actionId, oResponse);
                                                }
                                            },
                                            failureCallback: {
                                                fn: function () {
                                                }
                                            },
                                            scope: me,
                                            execScripts: true
                                        });

                                    }
                                },
                                {
                                    text: "Отмена",
                                    handler: function dlA_onActionDelete_cancel()
                                    {
                                        this.destroy();
                                    },
                                    isDefault: true
                                }]
                        });
                }
            },

            _openMessageWindow: function openMessageWindowFunction(title, message, reload) {
                Alfresco.util.PopupManager.displayPrompt(
                    {
                        title: "Результат выполнения операции \"" + title + "\"",
                        text: message,
                        noEscape: true,
                        buttons: [
                            {
                                text: "Ок",
                                handler: function dlA_onAction_action()
                                {
                                    this.destroy();
                                    if (reload) {
                                        document.location.href = document.location.href;
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
                        [contId + "_h", item.actionId ]
                    );

                    Dom.addClass(contId, "metadata-form-edit");
                    this.doubleClickLock = false;
                };

                var url = "/lecm/components/form/script" +
                    "?itemKind={itemKind}" +
                    "&itemId={itemId}" +
                    "&formId={formId}" +
                    "&mode={mode}" +
                    "&submitType={submitType}" +
                    "&items={items}";

                var templateUrl = YAHOO.lang.substitute(Alfresco.constants.URL_SERVICECONTEXT + url,
                {
                    itemKind: "type",
                    itemId: item.actionId,
                    formId: "scriptForm",
                    mode: "create",
                    submitType: "json",
                    items: JSON.stringify(item.items)
                });

                // Using Forms Service, so always create new instance
                var scriptForm = new Alfresco.module.SimpleDialog(this.id + "-scriptForm");
                scriptForm.setOptions(
                    {
                        width: "40em",
                        templateUrl: templateUrl,
                        actionUrl: null,
                        destroyOnHide: true,
                        doBeforeDialogShow: {
                            fn: doBeforeDialogShow,
                            scope: this
                        },
                        onSuccess: {
                            fn: function DataGrid_onActionCreate_success(response) {
                                me._actionResponse(item.actionId, response);
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

            _actionResponse: function actionResponseFunction(actionId, response) {
                var json = eval("(" + response.serverResponse.responseText + ")");
                if (json.forCollection) {
                    if (json.redirect != "") {
                        document.location.href = Alfresco.constants.URL_PAGECONTEXT + json.redirect;
                    } else if (json.openWindow) {
                        window.open(Alfresco.constants.URL_PAGECONTEXT + json.openWindow, "", "toolbar=no,location=no,directories=no,status=no,menubar=no,copyhistory=no");
                    } else if (json.withErrors) {
                        this._openMessageWindow(actionId, "Ошибка при выполнении операции \"" + actionId + "\"", false);
                    } else {
                        document.location.href = document.location.href;
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
                        this._openMessageWindow(actionId, message, true);
                    }
                }
            },

            onToolbarUpdate: function(layer, args) {
	            Dom.setStyle(this.id + "-filters-dialog", "visibility", "hidden");
                var currentNode = args[1].currentNode;
                if (currentNode !== null) {
                    var filters = currentNode.data.filters;
                    var hasFilters = filters != null && filters.length > 0;
	                this.toolbarButtons["defaultActive"].filtersButton.set("disabled", args[1].isReportNode || !hasFilters);

	                this.toolbarButtons["defaultActive"].searchButton.set("disabled", args[1].isReportNode);
	                var searchInput = Dom.get(this.id + "-full-text-search");
	                if (args[1].isReportNode) {
		                searchInput.setAttribute("disabled", true);
	                } else {
		                searchInput.removeAttribute("disabled");
	                }
	                if (this.modules.dataGrid != null && this.modules.dataGrid.search != null) {
	                    this.onClearSearch();
	                }

                    if (hasFilters) {
                        this.avaiableFilters = [];
                        for (var i = 0; i < filters.length; i++) {
                            var filter = filters[i];
                            this.avaiableFilters.push(filter);
                        }

                        this.isNeedUpdate = true;
                    }

	                var types = [];
	                if (currentNode.data.types != null) {
		                types = currentNode.data.types.split(",");
	                }
	                this.toolbarButtons["defaultActive"].extendSearchButton.set("disabled", args[1].isReportNode || types.length != 1);
	                this.currentType = types[0];
                }
            },

            _buildPreferencesValue: function () {
                return '';
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
                                    formData[name] = new Array();
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
                                                formData[name] = new Array();
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
	        }
        }, true);
})();