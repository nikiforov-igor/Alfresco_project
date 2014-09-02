(function () {
    /**
     * YUI Library aliases
     */
    var Dom = YAHOO.util.Dom,
        Event = YAHOO.util.Event,
        Connect = YAHOO.util.Connect;
    /**
     * Toolbar constructor.
     *
     * @param htmlId {String} The HTML id of the parent element
     * @return {LogicECM.module.OrgStructure.Toolbar} The new Toolbar instance
     * @constructor
     */
    LogicECM.module.OrgStructure.Toolbar = function (htmlId) {
        return LogicECM.module.OrgStructure.Toolbar.superclass.constructor.call(this, "LogicECM.module.OrgStructure.Toolbar", htmlId);
    };

    /**
     * Extend from Alfresco.component.Base
     */
    YAHOO.extend(LogicECM.module.OrgStructure.Toolbar, LogicECM.module.Base.Toolbar);

    /**
     * Augment prototype with main class implementation, ensuring overwrite is enabled
     */
    YAHOO.lang.augmentObject(LogicECM.module.OrgStructure.Toolbar.prototype,
        {
            _initButtons: function () {
                this.toolbarButtons[this.options.newRowButtonType].push(
                    Alfresco.util.createYUIButton(this, "newRowButton", this.onNewRow,
                        {
                            disabled: this.options.newRowButtonType != 'defaultActive',
                            value: "create"
                        })
                );

                this.toolbarButtons[this.options.searchButtonsType].push(
                    Alfresco.util.createYUIButton(this, "searchButton", this.onSearchClick,
                        {
                            disabled: this.options.searchButtonsType != 'defaultActive'
                        })
                );

                this.toolbarButtons[this.options.searchButtonsType].push(
                    Alfresco.util.createYUIButton(this, "extendSearchButton", this.onExSearchClick,
                        {
                            disabled: this.options.searchButtonsType != 'defaultActive'
                        })
                );

                this.toolbarButtons["defaultActive"].push(
                    Alfresco.util.createYUIButton(this, "structure", this.onStructureClick)
                );

                if (this.options.showImportXml) {
                    this.toolbarButtons[this.options.newRowButtonType].push(
                        Alfresco.util.createYUIButton(this, "exportButton", this.onExport,
                            {
                                disabled: false,
                                value: "create"
                            })
                    );

                    this.importInfoDialog = Alfresco.util.createYUIPanel(this.id + "-import-info-form",
                        {
                            width: "50em"
                        });

                    this.importErrorDialog = Alfresco.util.createYUIPanel(this.id + "-import-error-form",
                        {
                            width: "60em"
                        });

                    this.importFromDialog = Alfresco.util.createYUIPanel(this.id + "-import-form",
                        {
                            width: "50em"
                        });


                    this.toolbarButtons[this.options.searchButtonsType].push(
                        Alfresco.util.createYUIButton(this, "importXmlButton", this.showImportDialog,
                            {
                                disabled: false
                            })
                    );
                    this.importFromSubmitButton = Alfresco.util.createYUIButton(this, "import-form-submit", this.onImportXML,{
                        disabled: true
                    });
                    Alfresco.util.createYUIButton(this, "import-form-cancel", this.hideImportDialog,{});
                    Event.on(this.id + "-import-form-import-file", "change", this.checkImportFile, null, this);
                    Event.on(this.id + "-import-error-form-show-more-link", "click", this.errorFormShowMore, null, this);
                }
            },

            onStructureClick: function BaseToolbar_onStructureClick() {
                window.open(Alfresco.constants.PROXY_URI + "/lecm/orgstructure/diagram", "Структура организации", "top=0,left=0,height=768,width=1024");
            },
            onExport: function onExport_function() {
                var orgMetadata = this.modules.dataGrid.datagridMeta;
                if (orgMetadata != null && orgMetadata.nodeRef.indexOf(":") > 0) {
                    var destination = orgMetadata.nodeRef;
                    document.location.href = Alfresco.constants.PROXY_URI + "lecm/dictionary/get/export?nodeRef=" + destination;
                }
            },

            /**
             * On "submit"-button click.
             */
            onImportXML: function() {
                if (this.modules.dataGrid && this.modules.dataGrid.datagridMeta != null && this.modules.dataGrid.datagridMeta.nodeRef != null) {
                    var me = this;
                    Connect.setForm(this.id + '-import-xml-form', true);
                    var url = Alfresco.constants.URL_CONTEXT + "proxy/alfresco/lecm/orgstructure/import?nodeRef=" + this.modules.dataGrid.datagridMeta.nodeRef;
                    var callback = {
                        upload: function(oResponse){
                            var oResults = YAHOO.lang.JSON.parse(oResponse.responseText);
                            if (oResults[0] != null && oResults[0].text != null) {
                                Dom.get(me.id + "-import-info-form-content").innerHTML = oResults[0].text;
                                me.importInfoDialog.show();
                            } else if (oResults.exception != null) {
                                Dom.get(me.id + "-import-error-form-exception").innerHTML = oResults.exception.replace(/\n/g, '<br>').replace(/\r/g, '<br>');
                                Dom.get(me.id + "-import-error-form-stack-trace").innerHTML = me.getStackTraceString(oResults.callstack);
                                Dom.setStyle(me.id + "-import-error-form-more", "display", "none");
                                me.importErrorDialog.show();
                            }

                            YAHOO.Bubbling.fire("datagridRefresh",
                                {
                                    bubblingLabel: me.options.bubblingLabel
                                });
                        }
                    };
                    this.hideImportDialog();
                    Connect.asyncRequest(Alfresco.util.Ajax.POST, url, callback);
                }
            }
        }, true);
})();