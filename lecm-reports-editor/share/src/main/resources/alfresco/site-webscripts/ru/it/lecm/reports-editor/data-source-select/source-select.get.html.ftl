<#import "/ru/it/lecm/base-share/components/base-components.ftl" as comp/>
<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign aDateTime = .now>
<#assign id = "data-source-select-" + aDateTime?iso_utc/>

<#assign sourceListLabel ="sourcesList-" + aDateTime?iso_utc>
<#assign sourceColumnsLabel ="sourceColumns-" + aDateTime?iso_utc>
<#assign parentGridLabel = args.gridLabel!""/>

<div class="yui-t1" id="${id}">
    <div id="yui-main" style="width: 55%;">
        <div id="alf-content">
        <#assign toolbarId = "${id}-columns-toolbar"/>
            <!-- Toolbar Start-->
            <div id="${toolbarId}" class="select-columns-toolbar">
                <@comp.baseToolbar toolbarId true false false>
                    <div class="select-row">
                            <span id="${toolbarId}-selectColumnsBtn" class="yui-button yui-push-button">
                                <span class="first-child">
                                    <button type="button" title="${msg("label.select.btn")}">${msg("label.select.btn")}</button>
                                </span>
                            </span>
                    </div>
                </@comp.baseToolbar>
            </div>
            <!-- Toolbar End-->
            <#assign columnsGridId = "${id}-columns-grid">
            <div class="reports">
                <div id="${columnsGridId}" class="columns-right-grid">
                    <div id="${columnsGridId}-alf-content">
                    <@grid.datagrid id=columnsGridId showViewForm=false showArchiveCheckBox=false>
                        <script type="text/javascript">//<![CDATA[
                        function createColumnsDatagrid() {
                            var datagrid = new LogicECM.module.ReportsEditor.ColumnsGrid("${columnsGridId}").setOptions(
                                    {
                                        usePagination: true,
                                        showExtendSearchBlock: false,
                                        showActionColumn: true,
                                        overrideSortingWith: false,
                                        forceSubscribing: true,
                                        pageSize:10,
                                        actions: [
                                            {
                                                type: "datagrid-action-link-${sourceColumnsLabel}",
                                                id: "onActionAdd",
                                                permission: "edit",
                                                label: "${msg("actions.add")}",
                                                evaluator: function (rowData) {
                                                    // добавить можно только те записи, ключа которых нет в списке
                                                    var inArray = function(value, array) {
                                                        for (var i = 0; i < array.length; i++) {
                                                            if (array[i].code == value) return true;
                                                        }
                                                        return false;
                                                    };

                                                    var code = rowData.itemData["prop_lecm-rpeditor_dataColumnCode"].value;
                                                    return !inArray(code, this.activeSourceColumns);
                                                }
                                            }
                                        ],
                                        bubblingLabel: "${sourceColumnsLabel}",
                                        showCheckboxColumn: true
                                    }).setMessages(${messages});

                            datagrid.mainDataGridLabel  = "${parentGridLabel}";

                            YAHOO.Bubbling.fire("activeGridChanged", {
                                datagridMeta: {
                                    datagridFormId: "short-datagrid",
                                    itemType: "lecm-rpeditor:reportDataColumn",
                                    nodeRef: "NOT_LOAD",
                                    sort: "lecm-rpeditor:dataColumnCode|true"
                                },
                                bubblingLabel: "${sourceColumnsLabel}"
                            });
                        }

                        YAHOO.util.Event.onContentReady("${columnsGridId}", createColumnsDatagrid);
                        //]]></script>
                    </@grid.datagrid>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <div id="alf-filters" style="width:40%">
        <#assign sourceGridId ="${id}-sources-grid">
        <!-- Grid Start-->
            <div class="reports">
            <div id="${sourceGridId}" class="sources-left-grid">
                <@grid.datagrid id=sourceGridId showViewForm=false showArchiveCheckBox=false>
                    <script type="text/javascript">//<![CDATA[
                        function createSourcesDatagrid() {
                            var datagrid = new LogicECM.module.ReportsEditor.SourcesGrid("${sourceGridId}").setOptions(
                                    {
                                        usePagination: true,
                                        showExtendSearchBlock: false,
                                        showActionColumn: true,
                                        overrideSortingWith: false,
                                        forceSubscribing: true,
                                        pageSize: 10,
                                        actions: [
                                            {
                                                type: "datagrid-action-link-${sourceListLabel}",
                                                id: "onActionSelectSource",
                                                permission: "edit",
                                                label: "${msg("actions.select")}"
                                            }
                                        ],
                                        bubblingLabel: "${sourceListLabel}",
                                        showCheckboxColumn: false
                                    }).setMessages(${messages});

                            datagrid.mainDataGridLabel = "${parentGridLabel}";

                            YAHOO.Bubbling.fire("activeGridChanged", {
                                datagridMeta: {
                                    itemType: "lecm-rpeditor:reportDataSource",
                                    nodeRef: LogicECM.module.ReportsEditor.SETTINGS.sourcesContainer
                                },
                                bubblingLabel: "${sourceListLabel}"
                            });
                        }

                    YAHOO.util.Event.onContentReady("${sourceGridId}", createSourcesDatagrid);
                    //]]></script>
                </@grid.datagrid>
            </div>
        </div>
        <!-- Grid End-->
    </div>

    <script type="text/javascript">//<![CDATA[
    (function() {
        function initEditor() {
                var sourceSelectEditor = new LogicECM.module.ReportsEditor.SelectSourceEditor("${id}").setMessages(${messages});
                sourceSelectEditor.setReportId("${args.reportId}");
                sourceSelectEditor.setDataSourceId("${activeSourceId!""}");
                sourceSelectEditor.setColumnsDataGridLabel("${sourceColumnsLabel}");
                sourceSelectEditor.setSourcesDataGridLabel("${sourceListLabel}");
                sourceSelectEditor.setMainDataGridLabel("${parentGridLabel}");
        }

        YAHOO.util.Event.onContentReady("${toolbarId}-body", initEditor);
    })();
    //]]></script>
</div>