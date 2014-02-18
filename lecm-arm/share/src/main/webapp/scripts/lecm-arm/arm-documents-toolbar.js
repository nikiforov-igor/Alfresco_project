(function () {
    var Dom = YAHOO.util.Dom;

    LogicECM.module.ARM.DocumentsToolbar = function (htmlId) {
        LogicECM.module.ARM.DocumentsToolbar.superclass.constructor.call(this, "LogicECM.module.ARM.DocumentsToolbar", htmlId);

        this.filtersDialog = null;
        this.splashScreen = null;

	    YAHOO.Bubbling.on("updateArmToolbar", this.onUpdateArmToolbar, this);
        return this;
    };

    YAHOO.extend(LogicECM.module.ARM.DocumentsToolbar, LogicECM.module.Base.Toolbar);

    YAHOO.lang.augmentObject(LogicECM.module.ARM.DocumentsToolbar.prototype,
        {
            filtersDialog: null,
            splashScreen: null,
            gridBubblingLabel: "documents-arm",
	        doubleClickLock: false,

            _renderFilters: function () {
                var filtersDiv = Dom.get("filtersBlock-forms");
                /*Alfresco.util.Ajax.request(
                    {
                        url: '',
                        dataObj: {},
                        successCallback: {
                            fn: function (response) {
                                filtersDiv.innerHTML = response.serverResponse.responseText;
                                if (this.filtersDialog != null) {
                                    this.filtersDialog.show();
                                }
                            },
                            scope: this
                        },
                        failureMessage: "Could not load form component",
                        scope: this,
                        execScripts: true
                    });*/
            },

            onFiltersClick: function () {
                //отрисовка фильтров в окне
                this._drawFiltersPanel();
                this._renderFilters();
            },

            onApplyFilterClick: function () {
                YAHOO.Bubbling.fire("datagridRefresh",
                    {
                        bubblingLabel: this.gridBubblingLabel
                    });

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

                this._drawFiltersPanel();
            },

            _drawFiltersPanel: function () {
                if (this.filtersDialog == null) {
                    // создаем диалог
                    this.filtersDialog = Alfresco.util.createYUIPanel("filtersBlock",
                        {
                            width: "800px"
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
	        }
        }, true);
})();