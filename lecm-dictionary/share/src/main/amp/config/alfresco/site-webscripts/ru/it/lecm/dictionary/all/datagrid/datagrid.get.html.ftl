
<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign id = args.htmlid>
<#assign bubblingLabel = "dictionaries-all-datagrid">

<@grid.datagrid id=id showViewForm=false>
<script type="text/javascript">//<![CDATA[
(function(){

    function createDatagrid(rootNodeRef) {
        var $html = Alfresco.util.encodeHTML,
            $links = Alfresco.util.activateLinks,
            $userProfile = Alfresco.util.userProfileLink;

        LogicECM.module.Base.DataGrid.prototype.getCellFormatter = function () {
            var scope = this;

            /**
             * Data Type custom formatter
             *
             * @method renderCellDataType
             * @param elCell {object}
             * @param oRecord {object}
             * @param oColumn {object}
             * @param oData {object|string}
             */
            return function DataGrid_renderCellDataType(elCell, oRecord, oColumn, oData)
            {
                var html = "";

                // Populate potentially missing parameters
                if (!oRecord)
                {
                    oRecord = this.getRecord(elCell);
                }
                if (!oColumn)
                {
                    oColumn = this.getColumn(elCell.parentNode.cellIndex);
                }

                if (oRecord && oColumn)
                {
                    if (!oData)
                    {
                        oData = oRecord.getData("itemData")[oColumn.field];
                    }

                    if (oData)
                    {
                        var datalistColumn = scope.datagridColumns[oColumn.key];
                        if (datalistColumn)
                        {
                            oData = YAHOO.lang.isArray(oData) ? oData : [oData];
                            for (var i = 0, ii = oData.length, data; i < ii; i++)
                            {
                                data = oData[i];

                                if (datalistColumn.name == "lecm-dic:plane")  {
                                    html += data.displayValue ? "${msg('logicecm.dictionary.plane')}" : "${msg('logicecm.dictionary.hierarchical')}";
                                } else {
                                    switch (datalistColumn.dataType.toLowerCase())
                                    {
                                        case "cm:person":
                                            html += '<span class="person">' + $userProfile(data.metadata, data.displayValue) + '</span>';
                                            break;

                                        case "datetime":
                                            html += Alfresco.util.formatDate(Alfresco.util.fromISO8601(data.value), scope.msg("lecm.date-format.defaultDateOnly"));
                                            break;

                                        case "date":
                                            html += Alfresco.util.formatDate(Alfresco.util.fromISO8601(data.value), scope.msg("lecm.date-format.defaultDateOnly"));
                                            break;

                                        case "text":
                                            html += $links($html(data.displayValue));
                                            break;

                                        default:
                                            if (datalistColumn.type == "association")
                                            {
                                                html += $html(data.displayValue);
                                            }
                                            else
                                            {
                                                html += $links($html(data.displayValue));
                                            }
                                            break;
                                    }
                                }

                                if (i < ii - 1)
                                {
                                    html += "<br />";
                                }
                            }
                        }
                    }
                }

                elCell.innerHTML = html;
            };
        };

        LogicECM.module.Base.DataGrid.prototype.getDataTableColumnDefinitions = function () {
            // YUI DataTable column definitions
            var columnDefinitions =	[];

            var column;
            for (var i = 0, ii = this.datagridColumns.length; i < ii; i++)
            {
                column = this.datagridColumns[i];
                var label = column.label;
                if (column.name == "lecm-dic:plane") {
                    label = "${msg('logicecm.dictionary.type')}";
                }
                columnDefinitions.push(
                        {
                            key: this.dataResponseFields[i],
                            label: label,
                            sortable: true,
                            sortOptions:
                            {
                                field: column.formsName,
                                sortFunction: this.getSortFunction()
                            },
                            formatter: this.getCellFormatter(column.dataType)
                        });
            }

            // Add actions as last column
            columnDefinitions.push(
                    { key: "actions", label: this.msg("label.column.actions"), sortable: false, formatter: this.fnRenderCellActions(), width: 80 }
            );
            return columnDefinitions;
        };

        /* Экспорт в XML.
                *
        * @method onActionExportXML
        * @param items {Object} Object literal representing the Data Item to be actioned
        */
        LogicECM.module.Base.DataGrid.prototype.onActionExportXML = function (item) {
            var fields = "";
            var dUrl = Alfresco.constants.PROXY_URI + "/lecm/dictionary/api/getDictionary?dicName=" + encodeURIComponent(item.itemData.prop_cm_name.value);

            Alfresco.util.Ajax.jsonGet({
                url:dUrl,
                successCallback:{
                    fn:function (response) {
                        var oResults = response.json;
                        var itemType = oResults["itemType"];
                        var sUrl = Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/datagrid/config/columns?itemType=" + encodeURIComponent(itemType) + "&formId=export-fields";
                        Alfresco.util.Ajax.jsonGet({
                            url:sUrl,
                            successCallback:{
                                fn:function (response) {
                                    var datagridColumns = response.json.columns;
                                    for (var nodeIndex in datagridColumns) {
                                        fields += "field=" + datagridColumns[nodeIndex].name + "&";
                                    }
                                    document.location.href = Alfresco.constants.PROXY_URI + "lecm/dictionary/get/export"
                                            + "?" + fields
                                            + "nodeRef=" + item.nodeRef;
                                },
                                scope:this
                            },
                            failureMessage: "${msg('message.dictionary.load.failed')}"
                        });
                    },
                    scope:this
                },
                failureMessage: "${msg('message.dictionary.load.failed')}"
            });
        };

        var datagrid = new LogicECM.module.Base.DataGrid('${id}').setOptions(
                {
                    bubblingLabel:"${bubblingLabel}",
                    usePagination: true,
                    showExtendSearchBlock:false,
                    showCheckboxColumn:false,
                    allowCreate: true,
                    createItemBtnMsg: "${msg('button.message.add.dictionary')}",
                    createFormTitleMsg: "${msg('button.message.new.dictionary')}",
                    actions: [
                        {
                            type:"datagrid-action-link-${bubblingLabel}",
                            id:"onActionEdit",
                            permission:"edit",
                            label:"${msg("actions.edit")}"
                        },
                        {
                            type:"datagrid-action-link-${bubblingLabel}",
                            id:"onActionExportXML",
                            permission:"edit",
                            label:"${msg("actions.export-xml")}"
                        }
                    ],
                    datagridMeta:{
                        //createFormId: '',
                        useFilterByOrg: false,
                        itemType: "lecm-dic:dictionary",
                        nodeRef: rootNodeRef
                    }
                }).setMessages(${messages});
        datagrid.draw();
    }

    function loadRootNode() {
        var sUrl = Alfresco.constants.PROXY_URI + "lecm/dictionary/get/folder";
        var callback = {
            success:function (oResponse) {
                var oResults = eval("(" + oResponse.responseText + ")");
                if (oResults != null && oResults.nodeRef != null) {
                    createDatagrid(oResults.nodeRef);
                }
            }
        };

        YAHOO.util.Connect.asyncRequest('GET', sUrl, callback);
    }

    function init() {
        LogicECM.module.Base.Util.loadResources([
            'modules/simple-dialog.js',
            'scripts/lecm-base/components/lecm-datagrid.js',
            'scripts/lecm-base/components/advsearch.js',
            'components/form/date-range.js',
            'components/form/number-range.js',
            'scripts/lecm-base/components/versions.js'
        ], [
            'components/search/search.css',
            'modules/document-details/historic-properties-viewer.css'
        ], loadRootNode);
    }

    YAHOO.util.Event.onDOMReady(init);

})();
//]]></script>
</@grid.datagrid>
