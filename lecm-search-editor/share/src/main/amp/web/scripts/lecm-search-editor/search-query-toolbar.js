if (typeof LogicECM == "undefined" || !LogicECM) {
    LogicECM = {};
}

LogicECM.module = LogicECM.module || {};

LogicECM.module.SearchQueries = LogicECM.module.SearchQueries || {};

(function () {
    var Dom = YAHOO.util.Dom;
    
    LogicECM.module.SearchQueries.Toolbar = function (name, htmlId) {
        LogicECM.module.SearchQueries.Toolbar.superclass.constructor.call(this, name ? name : "LogicECM.module.SearchQueries.Toolbar", htmlId);

        YAHOO.Bubbling.unsubscribe("hiddenAssociationFormReady", this._onHiddenAssociationFormReady, this);
        YAHOO.Bubbling.unsubscribe("selectedItemsChanged", this.onCheckDocument, this);
        YAHOO.Bubbling.unsubscribe("objectFinderReady", this._onObjectFinderReady, this);
        YAHOO.Bubbling.unsubscribe("updateArmFilters", this.onUpdateArmFilters, this);
        YAHOO.Bubbling.unsubscribe("restoreDefaultColumns", this.onRestoreDefaultColumns, this);
        return this;
    };

    YAHOO.extend(LogicECM.module.SearchQueries.Toolbar, LogicECM.module.ARM.DocumentsToolbar);

    YAHOO.lang.augmentObject(LogicECM.module.SearchQueries.Toolbar.prototype,
        {
            options: {
                queryNodeRef: null,
                bubblingLabel: null,
                editPath: null,
                deletePath:null,
                expired: 60,
                panelId: null
            },

            queryConfig: null,

            
            PREFERENCE_KEY: "ru.it.lecm.search-editor.state.",

            _initButtons: function () {
                this.toolbarButtons['defaultActive'].push(
                    Alfresco.util.createYUIButton(this, "editQueryButton", this.onEditQueryButton,
                        {
                            disabled: false,
                            value: "edit"
                        })
                );
                this.toolbarButtons['defaultActive'].push(
                    Alfresco.util.createYUIButton(this, "columnsButton", this.onColumnsButton,
                        {
                            disabled: false,
                            value: "edit"
                        })
                );
                this.toolbarButtons['defaultActive'].push(
                    Alfresco.util.createYUIButton(this, "deleteQueryButton", this.onDeleteQueryButton,
                        {
                            disabled: false,
                            value: "edit"
                        })
                );
            },

            onReady: function BaseToolbar_onReady() {
                this._initButtons();

                Alfresco.util.Ajax.jsonRequest({
                    method: 'GET',
                    url: Alfresco.constants.PROXY_URI + 'api/metadata?nodeRef=' + this.options.queryNodeRef + "&shortQNames",
                    successCallback: {
                        scope: this,
                        fn: function(response) {
                            var props = response.json.properties;
                            this.queryConfig = YAHOO.lang.JSON.parse(props["lecm-search-queries:query-setting"]);
                            this.queryConfig.queryNodeRef = this.options.queryNodeRef;
                        }
                    },
                    failureMessage: this.msg('message.failure'),
                    scope: this,
                    execScripts: true
                });
                
                Dom.setStyle(this.id + "-body", "visibility", "visible");
            },

            onEditQueryButton: function (e, p_obj) {
                var dataGrid = this.modules.dataGrid;
                if (dataGrid) {
                    this.queryConfig.queryNodeRef = this.options.queryNodeRef;

                    LogicECM.module.Base.Util.setCookie(this.PREFERENCE_KEY  + Alfresco.constants.USERNAME,
                        typeof  this.queryConfig == "object" ? YAHOO.lang.JSON.stringify(this.queryConfig) : this.queryConfig, {});

                    this._reloadPage(this.options.editPath);
                } else {
                    Alfresco.util.PopupManager.displayMessage(
                        {
                            text:me.msg("msg.editor.toolbar.grid.not.found")
                        });
                }
            },
            onDeleteQueryButton: function (e, p_obj) {
                var dataGrid = this.modules.dataGrid;
                if (dataGrid) {
                    var me = this;
                    var fn = "onActionDelete";
                    if (fn && (typeof dataGrid[fn] == "function")) {
                        dataGrid[fn].call(dataGrid, [{nodeRef:this.options.queryNodeRef}], null, {fullDelete: true}, function () {
                            YAHOO.Bubbling.fire("armRefreshParentSelectedTreeNode");
                            YAHOO.Bubbling.fire("selectedParentCurrentNode");
                        });
                    }
                } else {
                    Alfresco.util.PopupManager.displayMessage(
                        {
                            text:me.msg("msg.editor.toolbar.grid.not.found")
                        });
                }
            },

            onColumnsButton: function () {
                if(this.queryConfig == null || this.queryConfig == '{}') {
                    Alfresco.util.PopupManager.displayMessage(
                        {
                            text:this.msg("msg.editor.not_select_query")
                        });
                    return;
                }
                this.showDialog();
            },

            showDialog: function () {
                var me = this;
                if (this.preferencesDialog == null) {
                    // создаем диалог
                    this.preferencesDialog = Alfresco.util.createYUIPanel(this.options.panelId + "-preferencesBlock",
                        {
                            width: "400px",
                            buttons: [
                                {
                                    text: Alfresco.util.message('label.button.save'),
                                    handler: function(e){
                                        me.onSaveClick();
                                    }

                                },
                                {
                                    text: Alfresco.util.message('label.button.reset'),
                                    handler: function(e){
                                        me.onRollbackClick();
                                    }

                                }
                            ]

                        });
                }

                Alfresco.util.Ajax.jsonGet(
                    {
                        url: Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/datagrid/config/columns?itemType=" + this.queryConfig.docType + "&formId=searchQueryGrid",
                        successCallback: {
                            fn: function (response) {
                                var formDiv = Dom.get("preferencesBlock-forms");
                                while (formDiv.firstChild) {
                                    formDiv.removeChild(formDiv.firstChild);
                                }

                                var columns = response.json.columns;

                                var prefColumns = [];
                                var prefs =
                                    LogicECM.module.Base.Util.getCookie(this._buildColumnsKey());
                                if (prefs != null) {
                                    try {
                                        prefColumns = JSON.parse(prefs);
                                    } catch (e) {
                                    }
                                }

                                var button, div;
                                for (var i = 0, ii = columns.length; i < ii; i++) {
                                    div = document.createElement("div");
                                    div.className = "column-pref-div";

                                    button = document.createElement('input');
                                    button.setAttribute('id', this.options.panelId + '-' + columns[i].formsName);
                                    button.setAttribute('type', 'checkbox');
                                    button.setAttribute('name', columns[i].formsName);
                                    button.value = columns[i].name;
                                    button.checked = (prefColumns.length == 0) || this._inArray(columns[i].name, prefColumns);
                                    button.className = "column-pref-cb-btn";

                                    div.appendChild(button);

                                    var label = document.createElement('label');
                                    label.className = "column-pref-lbl";
                                    label.appendChild(document.createTextNode(columns[i].label));

                                    div.appendChild(label);
                                    formDiv.appendChild(div);
                                }
                                this.preferencesDialog.show();
                            },
                            scope: this
                        },
                        failureCallback: {
                            fn: function () {
                                Alfresco.util.PopupManager.displayMessage(
                                    {
                                        text: this.msg("message.error")
                                    });
                            },
                            scope: this
                        }
                    });
            },

            onSaveClick: function () {
                var result = this._retrivePrefColumns();

                var date = new Date;
                date.setDate(date.getDate() + this.options.expired);
                LogicECM.module.Base.Util.setCookie(this._buildColumnsKey(), YAHOO.lang.JSON.stringify(result), {expires: date});

                this.preferencesDialog.hide();
                YAHOO.Bubbling.fire("populateDataGrid",
                    {
                        bubblingLabel: this.options.bubblingLabel
                    });
            },

            onRollbackClick: function () {
                var blockChildren = YAHOO.util.Dom.getChildren('preferencesBlock-forms');
                for (var i = 0; i < blockChildren.length; i++) {
                    if (blockChildren[i].children.length > 0) {
                        for (var j = 0; j < blockChildren[i].children.length; j++) {
                            var input = blockChildren[i].children[j];
                            if (input.id != "") {
                                input.checked = true;
                            }
                        }
                    }
                }
            },

            _retrivePrefColumns: function() {
                var columns = [];
                var blockChildren = YAHOO.util.Dom.getChildren('preferencesBlock-forms');
                for (var i = 0; i < blockChildren.length; i++) {
                    if (blockChildren[i].children.length > 0) {
                        for (var j = 0; j < blockChildren[i].children.length; j++) {
                            var input = blockChildren[i].children[j];
                            if (input.id != "") {
                                if (input.checked) {
                                    columns.push(input.value);
                                }
                            }
                        }
                    }
                }
                return columns;
            },

            _buildColumnsKey: function () {
                var id = new Alfresco.util.NodeRef(this.options.queryNodeRef).id;
                return this.PREFERENCE_KEY + "columnPref." + Alfresco.constants.USERNAME + "." + id;
            },

            _reloadPage: function (path) {
                if (path && path != '')  {
                    // переход на страницу редактирования
                    window.location.href = window.location.protocol + "//" + window.location.host +
                        Alfresco.constants.URL_PAGECONTEXT + "arm?code=SED&path=" + encodeURIComponent(path);
                } else {
                    window.location.reload();
                }
            },

            _inArray: function(value, array) {
                for (var i = 0; i < array.length; i++) {
                    if (array[i] == value) return true;
                }
                return false;
            }
        }, true);
})();