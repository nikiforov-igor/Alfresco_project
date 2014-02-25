if (typeof LogicECM == "undefined" || !LogicECM) {
    var LogicECM = {};
}

LogicECM.module = LogicECM.module || {};

LogicECM.module.ARM = LogicECM.module.ARM|| {};

(function () {
    var Dom = YAHOO.util.Dom;

    LogicECM.module.ARM.DocumentsToolbar = function (htmlId) {
        LogicECM.module.ARM.DocumentsToolbar.superclass.constructor.call(this, "LogicECM.module.ARM.DocumentsToolbar", htmlId);

        this.filtersDialog = null;
        this.splashScreen = null;
        this.avaiableFilters = [];

	    YAHOO.Bubbling.on("updateArmToolbar", this.onUpdateArmToolbar, this);
	    YAHOO.Bubbling.on("updateArmFilters", this.onUpdateArmFilters, this);
	    YAHOO.Bubbling.on("selectedItemsChanged", this.onCheckDocument, this);
        return this;
    };

    YAHOO.extend(LogicECM.module.ARM.DocumentsToolbar, LogicECM.module.Base.Toolbar);

    YAHOO.lang.augmentObject(LogicECM.module.ARM.DocumentsToolbar.prototype,
        {
            PREFERENCE_KEY: "ru.it.lecm.arm.current-filters",

            filtersDialog: null,
            splashScreen: null,
            gridBubblingLabel: "documents-arm",
	        doubleClickLock: false,

            avaiableFilters:[],
            isNeedUpdate: false,

            reloadActionsUpdateTimer: null,

            _renderFilters: function (filters, updateHtml) {
                if (!updateHtml) {
                    this.filtersDialog.show();
                } else {
                    var filtersDiv = Dom.get("filtersBlock-forms");
                    var toolbar = this;
                    Alfresco.util.Ajax.jsonRequest({
                        method: "POST",
                        url: Alfresco.constants.PROXY_URI + "lecm/arm/draw-filters",
                        dataObj: {
                            htmlId: Alfresco.util.generateDomId(),
                            filters: YAHOO.lang.JSON.stringify(this.avaiableFilters)
                        },
                        successCallback: {
                            fn: function (oResponse) {
                                filtersDiv.innerHTML = oResponse.serverResponse.responseText;
                                toolbar.isNeedUpdate = false;
                                if (toolbar.filtersDialog != null) {
                                    toolbar.filtersDialog.show();
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
                }
            },

            onFiltersClick: function () {
                //отрисовка фильтров в окне
                this._drawFiltersPanel();
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

                this.filtersDialog.hide();
            },

            _initButtons: function () {
	            this.toolbarButtons["defaultActive"].newDocumentButton = new YAHOO.widget.Button(
		            this.id + "-newDocumentButton",
		            {
			            type: "menu",
			            menu: [],
			            disabled: true
		            }
	            );

                this.toolbarButtons["defaultActive"].filtersButton = Alfresco.util.createYUIButton(this, "filtersButton", this.onFiltersClick);

                this.toolbarButtons["defaultActive"].groupActionsButton = new YAHOO.widget.Button(
                    this.id + "-groupActionsButton",
                    {
                        type: "menu",
                        menu: [],
                        disabled: true
                    }
                );
            },

            _drawFiltersPanel: function () {
                if (this.filtersDialog == null) {
                    // создаем диалог
                    this.filtersDialog = Alfresco.util.createYUIPanel("filtersBlock",
                        {
                            width: "400px"
                        });
                    // создаем кнопки
                    this.widgets.searchButton = Alfresco.util.createYUIButton(this, "filtersBlock-apply-button", this.onApplyFilterClick, {}, Dom.get("filtersBlock-apply-button"));
                }
            },

	        onNewRow: function (p_sType, p_aArgs, p_oItem) {
		        var destination = p_oItem.destination,
			        itemType = p_oItem.type;
		        this.showCreateDialog({itemType: itemType, nodeRef: destination});
	        },

	        showCreateDialog: function (meta) {
		        if (this.doubleClickLock) return;
		        this.doubleClickLock = true;
		        // Intercept before dialog show
		        var me = this;
		        var doBeforeDialogShow = function (p_form, p_dialog) {
			        var contId = p_dialog.id + "-form-container";
			        var addMsg = meta.addMessage;
			        var defaultMsg = this.msg("label.create-row.title");
			        Alfresco.util.populateHTML(
				        [contId + "_h", addMsg ? addMsg : defaultMsg ]
			        );

			        Dom.addClass(contId, "metadata-form-edit");
			        this.doubleClickLock = false;
		        };

		        var templateUrl = YAHOO.lang.substitute(Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/form?itemKind={itemKind}&itemId={itemId}&destination={destination}&mode={mode}&submitType={submitType}&formId={formId}&showCancelButton=true",
			        {
				        itemKind: "type",
				        itemId: meta.itemType,
				        destination: meta.nodeRef,
				        mode: "create",
				        formId: meta.createFormId != null ? meta.createFormId : "",
				        submitType: "json"
			        });

		        // Using Forms Service, so always create new instance
		        var createDetails = new Alfresco.module.SimpleDialog(this.id + "-createDetails");
		        createDetails.setOptions(
			        {
				        width: "80em",
				        templateUrl: templateUrl,
				        actionUrl: null,
				        destroyOnHide: true,
				        doBeforeDialogShow: {
					        fn: doBeforeDialogShow,
					        scope: this
				        },
				        onSuccess: {
					        fn: function DataGrid_onActionCreate_success(response) {
						        Alfresco.util.PopupManager.displayMessage(
							        {
								        text: this.msg("message.save.success")
							        });
						        window.location.href = window.location.protocol + "//" + window.location.host +
							        Alfresco.constants.URL_PAGECONTEXT + "document?nodeRef=" + response.json.persistedObject;
						        this.doubleClickLock = false;
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

	        onUpdateArmToolbar: function(layer, args) {
		        var createTypes = args[1].createTypes;
		        var button = this.toolbarButtons["defaultActive"].newDocumentButton;
		        var menu = button.getMenu();
		        var hasCreateTypes = createTypes != null && createTypes.length > 0;
		        if (hasCreateTypes) {
			        var items = [];
			        for (var i = 0; i < createTypes.length; i++) {
				        var type = createTypes[i];
				        items.push({
					        text: type.label,
					        value: type.type,
					        onclick: {
						        fn: this.onNewRow,
						        obj: {
							        type: type.type,
							        destination: type.draftFolder
						        },
						        scope: this
					        }
				        });
			        }
			        if (YAHOO.util.Dom.inDocument(menu.element)) {
				        menu.clearContent();
				        menu.addItems(items);
				        menu.render();
			        } else {
				        menu.itemData = items;
			        }
		        } else if (YAHOO.util.Dom.inDocument(menu.element)) {
			        menu.clearContent();
			        menu.render();
		        }
		        button.set("disabled", !hasCreateTypes);
	        },

            onCheckDocument: function onCheckDocumentOnDataGrid() {
                var button = this.toolbarButtons["defaultActive"].groupActionsButton;
                button.set("disabled", true);
                if (this.reloadActionsUpdateTimer != null) {
                    clearTimeout(this.reloadActionsUpdateTimer);
                }
                this.reloadActionsUpdateTimer = setTimeout(this.onCheckDocumentFinished.bind(this), 500);
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
                            if (YAHOO.util.Dom.inDocument(menu.element)) {
                                menu.clearContent();
                                menu.addItems(actionItems);
                                menu.render();
                            } else {
                                menu.itemData = actionItems;
                            }
                            if (actionItems.length == 0) {
                                button.set("disabled", true);
                            } else {
                                button.set("disabled", false);
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
                } else {
                    Alfresco.util.Ajax.jsonRequest({
                        method: "POST",
                        url: Alfresco.constants.PROXY_URI + "lecm/groupActions/exec",
                        dataObj: {
                            items: p_oItem.items,
                            actionId: p_oItem.actionId
                        },
                        successCallback: {
                            fn: function (oResponse) {
                                alert("Script was executed!!")
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
                                Alfresco.util.PopupManager.displayMessage(
                                    {
                                        text: this.msg("message.save.success")
                                    });
                                window.location.href = window.location.protocol + "//" + window.location.host +
                                    Alfresco.constants.URL_PAGECONTEXT + "document?nodeRef=" + response.json.persistedObject;
                                this.doubleClickLock = false;
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

            onUpdateArmFilters: function(layer, args) {
                var currentNode = args[1].currentNode;
                if (currentNode !== null) {
                    var filters = currentNode.data.filters;
                    var hasFilters= filters != null && filters.length > 0;
                    if (hasFilters) {
                        this.avaiableFilters = [];
                        for (var i = 0; i < filters.length; i++) {
                            var filter = filters[i];
                            this.avaiableFilters.push(filter);
                        }

                        this.isNeedUpdate = true;
                    }
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
            }
        }, true);
})();