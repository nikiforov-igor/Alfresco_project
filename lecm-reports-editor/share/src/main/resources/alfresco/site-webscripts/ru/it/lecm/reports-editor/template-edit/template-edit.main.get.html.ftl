<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>
<#import "/ru/it/lecm/base-share/components/base-components.ftl" as comp/>

<#assign id = args.htmlid>

<#assign aDateTime = .now>

<#assign toolbarId = "re-template-edit-toolbar-" + id />

<#assign templatesGridLabel ="templates-edit-label-" + aDateTime?iso_utc>

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

<div id="selectTemplatePanel" class="yui-panel" style="display:none">
    <div id="selectTemplatePanel-select-head" class="hd">Выбрать</div>
    <div id="selectTemplatePanel-select-body" class="bd">
        <div id="selectTemplatePanel-select-content">
            <div id="selectTemplatePanel-content" >
                <div id="selectTemplatePanel-form"></div>
            </div>
            <div class="bdft">
            <#-- Кнопка Очистки -->
                <div class="yui-u align-right right">
                    <span id="selectTemplatePanel-close-button" class="yui-button yui-push-button search-icon">
                        <span class="first-child">
                            <button type="button">${msg('button.close')}</button>
                        </span>
                    </span>
                </div>
            </div>
        </div>
    </div>
</div>

<script type="text/javascript">//<![CDATA[
    function initToolbar() {
        var templateEditToolbar = new LogicECM.module.ReportsEditor.TemplateEditToolbar("${toolbarId}");
        templateEditToolbar.setReportId("${args.reportId}");
        templateEditToolbar.setMessages(${messages});
        templateEditToolbar.setLabel("${templatesGridLabel}");
    }
    YAHOO.util.Event.onContentReady("${toolbarId}", initToolbar);
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
                                showExtendSearchBlock: false,
                                forceSubscribing: true,
                                actions: [
                                    {
                                        type: "datagrid-action-link-${templatesGridLabel}",
                                        id: "onActionEdit",
                                        permission: "edit",
                                        label: "${msg("actions.edit")}"
                                    },
                                    {
                                        type: "datagrid-action-link-${templatesGridLabel}",
                                        id: "onActionDelete",
                                        permission: "delete",
                                        label: "${msg("actions.delete-row")}"
                                    },
                                    {
                                        type: "datagrid-action-link-${templatesGridLabel}",
                                        id: "onActionSave",
                                        permission: "edit",
                                        label: "${msg("button.save-as-template")}"
                                    },
                                    {
                                        type: "datagrid-action-link-${templatesGridLabel}",
                                        id: "onActionExport",
                                        permission: "delete",
                                        label: "${msg("button.export-template")}"
                                    }
                                ],
                                bubblingLabel: "${templatesGridLabel}",
                                showCheckboxColumn: false
                            }).setMessages(${messages});

                    YAHOO.Bubbling.fire("activeGridChanged", {
                        datagridMeta: {
                            itemType: "lecm-rpeditor:reportTemplate",
                            nodeRef: "${args.reportId}",
                            useChildQuery: true,
                            actionsConfig: {
                                fullDelete: true,
                                trash: false
                            },
                            sort: "cm:name|true"
                        },
                        bubblingLabel: "${templatesGridLabel}"
                    });
                }
                YAHOO.util.Event.onContentReady('${gridId}', createTemplatesDatagrid);
            })();
            //]]></script>
        </@grid.datagrid>
    </div>
</div>