<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>
<#import "/ru/it/lecm/base-share/components/base-components.ftl" as comp/>

<#assign id = args.htmlid>
<#assign aDateTime = .now>

<#assign toolbarId = "columns-toolbar-${id}-"  + aDateTime?iso_utc/>
<#assign editSourceColumnsLabel ="editSourceColumns-" + aDateTime?iso_utc>

<div id="${toolbarId}">
    <@comp.baseToolbar toolbarId true false false>
        <div class="new-row">
            <span id="${toolbarId}-newColumnButton" class="yui-button yui-push-button">
                <span class="first-child">
                    <button type="button"
                            title="${msg("label.new-column.btn")}">${msg("label.new-column.btn")}</button>
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
        <div class="select-source">
            <span id="${toolbarId}-selectSource" class="yui-button yui-push-button">
                <span class="first-child">
                    <button type="button"
                            title="${msg("label.select-source.btn")}">${msg("label.select-source.btn")}</button>
                </span>
            </span>
        </div>
        <div class="divider"></div>
        <div class="delete-row">
            <span id="${toolbarId}-deleteColumnsBtn" class="yui-button yui-push-button">
                <span class="first-child">
                    <button type="button"
                            title="${msg("label.delete.btn")}">&nbsp;</button>
                </span>
            </span>
        </div>
    </@comp.baseToolbar>
</div>
<div id="selectSourcePanel" class="yui-panel" style="display:none">
    <div id="selectSourcePanel-select-head" class="hd">Выбрать</div>
    <div id="selectSourcePanel-select-body" class="bd">
        <div id="selectSourcePanel-select-content">
            <div id="selectSourcePanel-content" >
                <div id="selectSourcePanel-form"></div>
            </div>
            <div class="bdft">
            <#-- Кнопка Очистки -->
                <div class="yui-u align-right right">
                    <span id="selectSourcePanel-close-button" class="yui-button yui-push-button search-icon">
                        <span class="first-child">
                            <button type="button">${msg('button.close')}</button>
                        </span>
                    </span>
                </div>
            </div>
        </div>
    </div>
</div>

<script type="text/javascript">
    (function () {
        LogicECM.module.ReportsEditor.REPORT_SETTINGS =
        <#if reportSettings?? >
        ${reportSettings}
        <#else>
        {}
        </#if>;

        function initEditToolbar() {
            var toolbar = new LogicECM.module.ReportsEditor.EditSourceToolbar("${toolbarId}").setMessages(${messages});
            toolbar.setReportId("${args.reportId}");
            toolbar.setBubblingLabel("${editSourceColumnsLabel}");
        }

        YAHOO.util.Event.onContentReady("${toolbarId}", initEditToolbar);
    })();
</script>

<#assign gridId = "re-source-edit-grid-${id}"/>
<div class="reports">
    <!-- Grid Start -->
    <div class="yui-t1" id="${gridId}">
        <div id="yui-main-2">
            <div class="yui-b" id="alf-content-${id}" style="margin-left: 0;">
            <@grid.datagrid id=gridId showViewForm=false>
                <script type="text/javascript">//<![CDATA[
                YAHOO.util.Event.onContentReady('${gridId}', function () {
                    var datagrid = new LogicECM.module.ReportsEditor.ColumnsGrid('${gridId}').setOptions(
                            {
                                usePagination: true,
                                showExtendSearchBlock: false,
                                overrideSortingWith: false,
                                forceSubscribing: true,
                                editForm: (LogicECM.module.ReportsEditor.REPORT_SETTINGS && LogicECM.module.ReportsEditor.REPORT_SETTINGS.isSQLReport == "true")
                                        ? "sql-provider-column" : "",
                                actions: [
                                    {
                                        type: "datagrid-action-link-${editSourceColumnsLabel}",
                                        id: "onActionEdit",
                                        permission: "edit",
                                        label: "${msg("actions.edit")}"
                                    },
                                    {
                                        type: "datagrid-action-link-${editSourceColumnsLabel}",
                                        id: "onActionDelete",
                                        permission: "delete",
                                        label: "${msg("actions.delete-row")}"
                                    }
                                ],
                                bubblingLabel: "${editSourceColumnsLabel}",
                                showCheckboxColumn: true
                            }).setMessages(${messages});

                    YAHOO.Bubbling.fire("activeGridChanged", {
                        datagridMeta: {
                            itemType: "lecm-rpeditor:reportDataColumn",
                            nodeRef: "${activeSourceId!"NOT_LOAD"}",
                            actionsConfig: {
                                fullDelete: true,
                                trash: false
                            },
                            sort: "lecm-rpeditor:dataColumnCode|true"
                        },
                        bubblingLabel: "${editSourceColumnsLabel}"
                    });
                });
                //]]></script>
            </@grid.datagrid>
            </div>
        </div>
    </div>
</div>