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
        <span id="${toolbarId}-newTemplateSaveButton" class="yui-button yui-push-button">
               <span class="first-child">
                  <button type="button" title="Сохранить как...">Сохранить как...</button>
               </span>
        </span>
    </div>
    <div class="divider"></div>
    <div>
        <div id="${toolbarId}-export-template" class="export-template" title="${msg('button.export-template')}">
            <span id="${toolbarId}-exportTemplateButton" class="yui-button yui-push-button">
                <span class="first-child">
                    <button type="button" title="${msg('button.export-template')}">&nbsp;</button>
                </span>
            </span>
        </div>
    </div>
</div>
</@comp.baseToolbar>

<script type="text/javascript">//<![CDATA[
    function initToolbar() {
        var reportsEditor = new LogicECM.module.ReportsEditor.TemplateEditToolbar("${toolbarId}");
        reportsEditor.setReportId("${args.reportId}");
        reportsEditor.setTemplateId("${activeTemplateId!""}");
        reportsEditor.setMessages(${messages});
    }
    YAHOO.util.Event.onContentReady("${toolbarId}", initToolbar);
</script>

<#assign gridId = "re-template-edit-grid-" + id/>

<div id="${gridId}-form" class="reports">
    <div class="title">
        <h3>${msg("label.current-template")}</h3>
    </div>
    <div id="${gridId}-reportTemplateInfo"></div>
    <div class="title">
        <h3>${msg("label.select-template")}</h3>
    </div>
    <table cellspacing="2" cellpadding="0" style="margin-bottom: 5px; width:100%">
        <tbody>
        <tr>
            <td valign="top">
                <div class="flat-button">
                    <div class="report-editor-panel">
                             <span class="align-left yui-button yui-menu-button" id="${gridId}-reportType">
                                <span class="first-child">
                                   <button type="button" tabindex="0"></button>
                                </span>
                             </span>
                        <select id="${gridId}-reportType-menu">
                        <#assign count = 0/>
                        <#list reportTypes as reportType>
                            <#if count == 0>
                                <#assign defaultRef = reportType.nodeRef?html/>
                                <#assign defaultName = reportType.name?html/>
                            </#if>
                            <option value="${reportType.nodeRef?html}">${reportType.name?html}</option>
                            <#assign count = (count + 1)/>
                        </#list>
                        </select>
                    </div>
                </div>
            </td>
            <td>
                <div id="${gridId}">
                    <div class="yui-b" id="alf-content" style="margin-left: 0;">
                    <@grid.datagrid id= gridId showViewForm=false>
                        <script type="text/javascript">//<![CDATA[

                        LogicECM.module.ReportsEditor.TemplatesGrid = function (containerId) {
                            LogicECM.module.ReportsEditor.TemplatesGrid.superclass.constructor.call(this, containerId);
                            return this;
                        };

                        YAHOO.lang.extend(LogicECM.module.ReportsEditor.TemplatesGrid, LogicECM.module.Base.DataGrid);

                        YAHOO.lang.augmentObject(LogicECM.module.ReportsEditor.TemplatesGrid.prototype, {
                            onActionSelectTemplate: function (item) {
                                // копируем шаблон в отчет
                                YAHOO.Bubbling.fire("copyTemplateToReport", {
                                    templateId: item.nodeRef
                                });
                            }
                        }, true);


                        function initTemplateGrid() {
                            var datagrid = new LogicECM.module.ReportsEditor.TemplatesGrid("${gridId}").setOptions(
                                    {
                                        usePagination: true,
                                        showExtendSearchBlock: false,
                                        forceSubscribing: true,
                                        actions: [
                                            {
                                                type: "datagrid-action-link-template-edit",
                                                id: "onActionSelectTemplate",
                                                permission: "edit",
                                                label: "${msg("actions.select")}",
                                                evaluator: function (rowData) {
                                                    if (rowData) {
                                                        var itemData = rowData.itemData;
                                                        //проверяем имя уже выбранного шаблона
                                                        var template = Dom.getElementsByClassName("itemtype-lecm-rpeditor:reportTemplate");
                                                        var currentTemplateName = (template[0] && template[0].children[1]) ? template[0].children[1].innerHTML : null;
                                                        var selectedTemplateName = itemData["prop_cm_name"].value;
                                                        return currentTemplateName != selectedTemplateName;
                                                    }
                                                    return false;
                                                }
                                            }
                                        ],
                                        datagridMeta: {
                                            itemType: "lecm-rpeditor:reportTemplate",
                                            nodeRef: "NOT_LOAD",
                                            sort: "cm:name|desc"
                                        },
                                        bubblingLabel: "template-edit",
                                        showCheckboxColumn: false
                                    }).setMessages(${messages});

                            datagrid.draw();
                        }

                        YAHOO.util.Event.onContentReady('${gridId}', initTemplateGrid);
                        //]]></script>
                    </@grid.datagrid>
                    </div>
                </div>
            </td>
        </tr>
        </tbody>
    </table>
</div>

<script type="text/javascript">//<![CDATA[
    function initEditor() {
        var reportsEditor = new LogicECM.module.ReportsEditor.TemplateEditor("${gridId}");
        reportsEditor.setReportId("${args.reportId}");
        reportsEditor.setMessages(${messages});
        reportsEditor.setDefaultFilter({
            name: "${defaultName!""}",
            nodeRef: "${defaultRef!""}"
        });

        reportsEditor._onRefreshTemplate();
    }
    YAHOO.util.Event.onDOMReady(initEditor);
</script>
