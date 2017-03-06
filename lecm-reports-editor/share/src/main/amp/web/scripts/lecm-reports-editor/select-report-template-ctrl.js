/**
 * Module Namespaces
 */
if (typeof LogicECM == "undefined" || !LogicECM) {
    LogicECM = {};
}
LogicECM.module = LogicECM.module || {};
LogicECM.module.ReportsEditor = LogicECM.module.ReportsEditor|| {};

(function () {
    var Dom = YAHOO.util.Dom;
    var $combine = Alfresco.util.combinePaths;

    LogicECM.module.ReportsEditor.SelectReportTemplateCtrl = function LogicECM_module_AssociationSelectOne(fieldHtmlId) {
        LogicECM.module.ReportsEditor.SelectReportTemplateCtrl.superclass.constructor.call(this, "LogicECM.module.ReportsEditor.SelectReportTemplateCtrl", fieldHtmlId, [ "container", "resize", "datasource"]);
        this.selectItemId = fieldHtmlId;
        this.removedItemId = fieldHtmlId + "-removed";
        this.addedItemId = fieldHtmlId + "-added";
        this.controlId = fieldHtmlId;
        this.currentDisplayValueId = fieldHtmlId + "-currentValueDisplay";

        return this;
    };

    YAHOO.extend(LogicECM.module.ReportsEditor.SelectReportTemplateCtrl, Alfresco.component.Base,
        {
            options: {
                reportNodeRef: null,
                fromParent: false,
                itemType: "lecm-rpeditor:reportTemplate",
                ctrlValue: "nodeRef",
                mandatory: false,
                selectedValue: "",
                oldValue: "",
                maxSearchResults: 30,
                nameSubstituteString: "{cm:name} ({lecm-rpeditor:templateCode})",
                fieldId: null,
                notSelectedOptionShow: false,
                notSelectedText: ""
            },

            rootNode: null,

            controlId: null,

            selectItemId: null,

            removedItemId: null,

            addedItemId: null,

            currentDisplayValueId: null,

            selectItem: null,

            currentDisplayValueElement: null,

            dataSource: null,

            doubleClickLock: false,

            onReady: function () {
                this.selectItem = Dom.get(this.selectItemId);
                if (this.selectItem) {
                    this.populateSelect();
                }
                YAHOO.util.Event.on(this.selectItemId, "change", this.onSelectChange, this, true);

                this.currentDisplayValueElement = Dom.get(this.currentDisplayValueId);
                if (this.currentDisplayValueElement) {
                    this.populateCurrentValue();
                }
            },

            onSelectChange: function () {
                var selectValue = this.selectItem.value;
                var addedItem = "";
                var removedItem = "";

                if (selectValue != this.options.oldValue) {
                    removedItem = this.options.oldValue;
                    addedItem = selectValue;
                }
                Dom.get(this.removedItemId).value = removedItem;
                Dom.get(this.addedItemId).value = addedItem;

                if (this.options.mandatory) {
                    YAHOO.Bubbling.fire("mandatoryControlValueUpdated", this);
                }

                YAHOO.Bubbling.fire("formValueChanged",
                    {
                        eventGroup: this,
                        addedItems: addedItem,
                        removedItems: removedItem,
                        selectedItems: selectValue,
                        selectedItemsMetaData: Alfresco.util.deepCopy(this.selectItem.value)
                    });
            },


            destroy: function () {
                LogicECM.module.ReportsEditor.SelectReportTemplateCtrl.superclass.destroy.call(this);
            },

            loadDefaultValue: function () {
                this.fillContent();
            },

            fillContent: function () {
                var successHandler = function (sRequest, oResponse, oPayload) {
                    if (this.options.notSelectedOptionShow) {
                        var emptyOption = this.selectItem.options[0];
                        var emptOpt = document.createElement('option');
                        emptOpt.innerHTML = emptyOption.innerHTML;
                        emptOpt.value = emptyOption.value;

                        this.selectItem.innerHTML = "";
                        this.selectItem.appendChild(emptOpt);
                    }

                    var results = oResponse.results;
                    for (var i = 0; i < results.length; i++) {
                        var node = results[i];
                        var opt = document.createElement('option');
                        opt.innerHTML = node.templateName;
                        opt.value = this.options.ctrlValue == "nodeRef" ? node.nodeRef : node[this.options.ctrlValue];
                        if (opt.value == this.options.selectedValue) {
                            opt.selected = true;
                        }
                        this.selectItem.appendChild(opt);
                    }

                    this.onSelectChange();
                }.bind(this);

                var failureHandler = function (sRequest, oResponse) {
                    if (oResponse.status == 401) {
                        // Our session has likely timed-out, so refresh to offer the login page
                        window.location.reload();
                    }
                    else {
                        //todo show failure message
                    }
                }.bind(this);

                this.dataSource.sendRequest("",
                    {
                        success: successHandler,
                        failure: failureHandler,
                        scope: this
                    });
            },

            populateSelect: function () {
                this._createDataSource();
                this.loadDefaultValue();
            },

            populateCurrentValue: function () {
                if (this.options.selectedValue != null && this.options.selectedValue.length > 0 && this.options.ctrlValue == "nodeRef") {
                    Alfresco.util.Ajax.jsonGet(
                        {
                            url: Alfresco.constants.PROXY_URI + "slingshot/doclib2/node/" + this.options.selectedValue.replace("://", "/"),
                            successCallback: {
                                fn: function (response) {
                                    var properties = response.json.item.node.properties;
                                    var name = this.options.nameSubstituteString;
                                    for (var prop in properties) {
                                        var propSubstName = "{" + prop + "}";
                                        if (name.indexOf(propSubstName) != -1) {
                                            name = name.replace(propSubstName, properties[prop]);
                                        }
                                    }
                                    this.currentDisplayValueElement.innerHTML = name;
                                },
                                scope: this
                            },
                            failureCallback: {
                                fn: function (response) {
                                    //todo show error message
                                },
                                scope: this
                            }
                        });
                } else if (this.options.notSelectedOptionShow) {
                    this.currentDisplayValueElement.innerHTML = this.options.notSelectedText;
                }
            },

            _createDataSource: function () {
                var me = this;

                var pickerChildrenUrl = Alfresco.constants.PROXY_URI + "lecm/reports-editor/report-templates?reportId=" + this.options.reportNodeRef + "&fromParent=" + this.options.fromParent;
                this.dataSource = new YAHOO.util.DataSource(pickerChildrenUrl,
                    {
                        responseType: YAHOO.util.DataSource.TYPE_JSON,
                        connXhrMode: "queueRequests",
                        responseSchema: {
                            resultsList: "templates"
                        }
                    });

                this.dataSource.doBeforeParseData = function (oRequest, oFullResponse) {
                    var updatedResponse = oFullResponse;

                    if (oFullResponse) {
                        var templates = oFullResponse.data.templates;

                        if (me.options.maxSearchResults > -1 && templates.length > me.options.maxSearchResults) {
                            templates = templates.slice(0, me.options.maxSearchResults - 1);
                        }

                        updatedResponse =
                        {
                            parent: oFullResponse.data.report,
                            templates: templates
                        };
                    }

                    return updatedResponse;
                };
            }
        });
})();