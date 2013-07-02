<#import "/ru/it/lecm/base-share/components/base-components.ftl" as comp/>
<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>
<#assign id = "data-source-select"/>
<div class="yui-t1" id="${id}">
    <div id="yui-main">
        <div class="yui-b" id="alf-content">
            <#assign toolbarId = "${id}-columns-toolbar"/>
            <!-- Toolbar Start-->
            <div id="${toolbarId}">
            <@comp.baseToolbar toolbarId true false false>
                <div class="new-row">
                    <span id="${toolbarId}-saveButton" class="yui-button yui-push-button">
                        <span class="first-child">
                            <button type="button"
                                    title="${msg("label.save-source.btn")}">${msg("label.save-source.btn")}</button>
                        </span>
                    </span>
                </div>
                <div class="new-row">
                    <span id="${toolbarId}-saveAsButton" class="yui-button yui-push-button">
                        <span class="first-child">
                            <button type="button"
                                    title="${msg("label.save-as-source.btn")}">${msg("label.save-as-source.btn")}</button>
                        </span>
                    </span>
                </div>
                <div class="prev-page">
                    <span id="${toolbarId}-prevPageButton" class="yui-button yui-push-button">
                       <span class="first-child">
                          <button type="button" title="Назад">${msg("label.prev.btn")}</button>
                       </span>
                    </span>
                </div>
                <div class="next-page">
                    <span id="${toolbarId}-nextPageButton" class="yui-button yui-push-button">
                        <span class="first-child">
                            <button type="button" title="Далее">${msg("label.next.btn")}</button>
                        </span>
                    </span>
                </div>
            </@comp.baseToolbar>
            </div>
            <!-- Toolbar End-->
            <#assign columnsGridId = "${id}-columns-grid">
            <div id="${columnsGridId}" class="columns-right-grid">
                <div id="${columnsGridId}-alf-content">
                    <@grid.datagrid id=columnsGridId showViewForm=false showArchiveCheckBox=false>
                    <script type="text/javascript">//<![CDATA[
                        function createColumnsDatagrid() {
                            var datagrid = new LogicECM.module.ReportsEditor.ColumnsGrid("${columnsGridId}").setOptions(
                                    {
                                        usePagination: true,
                                        useDynamicPagination: false,
                                        showExtendSearchBlock: false,
                                        showActionColumn: true,
                                        overrideSortingWith: false,
                                        actions: [
                                            {
                                                type: "datagrid-action-link-sourceColumns",
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
                                            },
                                            {
                                                type: "datagrid-action-link-sourceColumns",
                                                id: "onActionRemove",
                                                permission: "delete",
                                                label: "${msg("actions.remove")}",
                                                evaluator: function (rowData) {
                                                    // удалить можно только те записи, которые есть в списке выбранных
                                                    var inArray = function(value, array) {
                                                        for (var i = 0; i < array.length; i++) {
                                                            if (array[i].nodeRef == value) return true;
                                                        }
                                                        return false;
                                                    };

                                                    return inArray(rowData.nodeRef, this.activeSourceColumns);
                                                }
                                            }
                                        ],
                                        bubblingLabel: "sourceColumns",
                                        showCheckboxColumn: false
                                    }).setMessages(${messages});

                            YAHOO.util.Event.onContentReady("${columnsGridId}", function () {
                                YAHOO.Bubbling.fire("activeGridChanged", {
                                    datagridMeta: {
                                        datagridFormId: "short-datagrid",
                                        itemType: "lecm-rpeditor:reportDataColumn",
                                        nodeRef: "NOT_LOAD",
                                        sort: "lecm-rpeditor:dataColumnCode|true"
                                    },
                                    bubblingLabel: "sourceColumns"
                                });
                            });
                        }

                        function initColumnsDatagrid() {
                            createColumnsDatagrid();
                            var resizer = new LogicECM.module.Base.Resizer('ReportsEditorAddSourceResizer');
                            resizer.setOptions({
                                initialWidth: 500
                            });
                        }

                        YAHOO.util.Event.onDOMReady(initColumnsDatagrid);
                    //]]></script>
                    </@grid.datagrid>
                </div>
            </div>
        </div>
    </div>
    <div id="alf-filters">
    <#assign toolbarId = "${id}-sources-toolbar"/>
    <!-- Toolbar Start-->
        <div id="${toolbarId}">
            <@comp.baseToolbar toolbarId true false false>
                <div class="new-row">
                    <span id="${toolbarId}-newSourceButton" class="yui-button yui-push-button">
                        <span class="first-child">
                            <button type="button"
                                    title="${msg("label.create-new-source.btn")}">${msg("label.create-new-source.btn")}</button>
                        </span>
                    </span>
                </div>
            </@comp.baseToolbar>
        </div>
    <!-- Toolbar End-->
    <#assign sourceGridId ="${id}-sources-grid">
    <!-- Grid Start-->
        <div id="${sourceGridId}" class="sources-left-grid">
            <@grid.datagrid id=sourceGridId showViewForm=false showArchiveCheckBox=false>
                <script type="text/javascript">//<![CDATA[
                    function createSourcesDatagrid() {
                        var datagrid = new LogicECM.module.ReportsEditor.SourcesGrid("${sourceGridId}").setOptions(
                                {
                                    usePagination: true,
                                    useDynamicPagination: false,
                                    showExtendSearchBlock: false,
                                    showActionColumn: true,
                                    overrideSortingWith: false,
                                    actions: [
                                        {
                                            type: "datagrid-action-link-sourcesList",
                                            id: "onActionSelect",
                                            permission: "edit",
                                            label: "${msg("actions.select")}",
                                            evaluator: function (rowData){
                                                var itemData = rowData.itemData;
                                                return !itemData.selected || itemData.selected == false;
                                            }
                                        }
                                    ],
                                    bubblingLabel: "sourcesList",
                                    showCheckboxColumn: false
                                }).setMessages(${messages});

                        YAHOO.util.Event.onContentReady("${sourceGridId}", function () {
                            YAHOO.Bubbling.fire("activeGridChanged", {
                                datagridMeta: {
                                    itemType: "lecm-rpeditor:reportDataSource",
                                    nodeRef: LogicECM.module.ReportsEditor.SETTINGS.sourcesContainer,
                                    searchConfig: {
                                        filter: 'PATH:"/app:company_home/cm:Business_x0020_platform/cm:LECM/cm:Сервис_x0020_Редактор_x0020_Отчетов/cm:Наборы_x0020_данных//*"' +
                                                ' OR PATH:"' + LogicECM.module.ReportsEditor.SETTINGS.REPORT_PATH + '//*"'
                                    }
                                },
                                bubblingLabel: "sourcesList"
                            });
                        });
                    }

                    function initSources() {
                        createSourcesDatagrid();
                    }

                    YAHOO.util.Event.onDOMReady(initSources);
            //]]></script>
            </@grid.datagrid>
        </div>
    <!-- Grid End-->
    </div>
    <script type="text/javascript">//<![CDATA[

    function init() {
        var editor = new LogicECM.module.ReportsEditor.SelectSourceEditor("${id}").setMessages(${messages});
        editor.setReportId("${page.url.args.reportId}");
        editor.setDataSourceId("${activeSourceId!""}");
        editor.markAsNewSource(!${existInRepo?string});
    }

    YAHOO.util.Event.onDOMReady(init);
    //]]></script>
</div>