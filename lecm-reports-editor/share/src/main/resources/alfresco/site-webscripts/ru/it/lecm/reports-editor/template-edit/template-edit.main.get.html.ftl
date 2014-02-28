<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>
<#import "/ru/it/lecm/base-share/components/base-components.ftl" as comp/>

<#assign id = args.htmlid>

<#assign toolbarId = "re-template-edit-toolbar-" + id />

<div id="${toolbarId}">
<@comp.baseToolbar toolbarId true false false>
    <div class="new-row">
        <span id="${toolbarId}-newTemplateButton" class="yui-button yui-push-button">
               <span class="first-child">
                  <button type="button" title="Загрузить">Загрузить</button>
               </span>
        </span>
    </div>
    <div class="new-row">
        <span id="${toolbarId}-newTemplateFromSourceButton" class="yui-button yui-push-button">
               <span class="first-child">
                  <button type="button" title="Новый из набора данных">Новый из набора данных</button>
               </span>
        </span>
    </div>
    <div class="new-row">
        <span id="${toolbarId}-newFromDicButton" class="yui-button yui-push-button">
               <span class="first-child">
                  <button type="button" title="Сохранить как...">Выбрать из справочника</button>
               </span>
        </span>
    </div>
</@comp.baseToolbar>
</div>

<script type="text/javascript">//<![CDATA[
    (function() {
    function initToolbar() {
        var reportsEditor = new LogicECM.module.ReportsEditor.TemplateEditToolbar("${toolbarId}");
        reportsEditor.setReportId("${args.reportId}");
        reportsEditor.setMessages(${messages});
    }
    YAHOO.util.Event.onContentReady("${toolbarId}", initToolbar);
    })();
</script>

<#assign gridId = "re-template-edit-grid-" + id/>

<div class="yui-t1" id="${gridId}">
    <div class="yui-b" id="alf-content-${id}" style="margin-left: 0;">
        <@grid.datagrid id="${gridId}" showViewForm=false>
            <script type="text/javascript">//<![CDATA[
            (function() {
                function createTemplatesDatagrid() {
                    var datagrid = new LogicECM.module.ReportsEditor.TemplateEditGrid('${gridId}').setOptions(
                            {
                                usePagination: true,
                                useDynamicPagination: true,
                                showExtendSearchBlock: false,
                                forceSubscribing: true,
                                actions: [
                                    {
                                        type: "datagrid-action-link-templates",
                                        id: "onActionEdit",
                                        permission: "edit",
                                        label: "${msg("actions.edit")}"
                                    },
                                    {
                                        type: "datagrid-action-link-templates",
                                        id: "onActionDelete",
                                        permission: "delete",
                                        label: "${msg("actions.delete-row")}"
                                    },
                                    {
                                        type: "datagrid-action-link-templates",
                                        id: "onActionSave",
                                        permission: "edit",
                                        label: "${msg("button.save-as-template")}"
                                    },
                                    {
                                        type: "datagrid-action-link-templates",
                                        id: "onActionExport",
                                        permission: "delete",
                                        label: "${msg("button.export-template")}"
                                    }
                                ],
                                datagridMeta: {
                                    itemType: "lecm-rpeditor:reportTemplate",
                                    nodeRef: "${args.reportId}",
                                    actionsConfig: {
                                        fullDelete: true,
                                        trash: false
                                    },
                                    sort: "cm:name|true"
                                },
                                bubblingLabel: "templates",
                                showCheckboxColumn: false
                            }).setMessages(${messages});

                    datagrid.draw();
                }
                YAHOO.util.Event.onContentReady('${gridId}', createTemplatesDatagrid);
            })();
            //]]></script>
        </@grid.datagrid>
    </div>
</div>

<script type="text/javascript">//<![CDATA[
(function() {
    function initEditor() {
        var reportsEditor = new LogicECM.module.ReportsEditor.TemplateEditor("${gridId}");
        reportsEditor.setReportId("${args.reportId}");
        reportsEditor.setMessages(${messages});
    }
    YAHOO.util.Event.onDOMReady(initEditor);
})();
</script>
