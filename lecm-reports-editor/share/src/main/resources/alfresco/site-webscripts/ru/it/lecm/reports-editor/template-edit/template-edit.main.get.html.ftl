<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>
<#assign id = args.htmlid>
<script type="text/javascript">//<![CDATA[
    LogicECM.module.ReportsEditor.SETTINGS.DESTINATION = LogicECM.module.ReportsEditor.SETTINGS.templatesContainer;
//]]></script>

<#if page.url.args.reportId??>
<div class="reports">
    <div class="title">
        <h3>Текущий шаблон</h3>
    </div>
    <div id="${id}-reportTemplateInfo"></div>
    <div class="title">
        <h3>Выбор шаблона</h3>
    </div>
    <table cellspacing="2" cellpadding="0" style="margin-bottom: 5px; width:100%">
        <tbody>
        <tr>
            <td valign="top">
                <div class="flat-button">
                    <div class="report-editor-panel">
         <span class="align-left yui-button yui-menu-button" id="${id}-reportType">
            <span class="first-child">
               <button type="button" tabindex="0"></button>
            </span>
         </span>
                        <select id="${id}-reportType-menu">
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
                    <div id="${id}-currentTemplate-name"></div>
                    <input type="hidden" id="${id}-currentTemplate-ref" name="currentTemplate-ref"/>
                </div>
            </td>
            <td>
                <div id="re-templates-grid">
                    <div id="yui-main-2">
                        <div class="yui-b" id="alf-content" style="margin-left: 0;">
                            <@grid.datagrid id="re-templates-grid" showViewForm=false>
                                <script type="text/javascript">//<![CDATA[

                                LogicECM.module.ReportsEditor.TemplatesGrid = function (containerId) {
                                    return LogicECM.module.ReportsEditor.TemplatesGrid.superclass.constructor.call(this, containerId);
                                };

                                YAHOO.lang.extend (LogicECM.module.ReportsEditor.TemplatesGrid, LogicECM.module.Base.DataGrid);

                                YAHOO.lang.augmentObject (LogicECM.module.ReportsEditor.TemplatesGrid.prototype, {
                                    onActionSelect: function (item) {
                                        // копируем шаблон в отчет
                                        YAHOO.Bubbling.fire("copyTemplateToReport", {
                                            templateId: item.nodeRef
                                        });
                                    }

                                }, true);

                                function createDatagrid() {
                                    var datagrid = new LogicECM.module.ReportsEditor.TemplatesGrid('re-templates-grid').setOptions(
                                            {
                                                usePagination:true,
                                                showExtendSearchBlock:false,
                                                actions: [
                                                    {
                                                        type:"datagrid-action-link-template-edit",
                                                        id:"onActionSelect",
                                                        permission:"edit",
                                                        label:"${msg("actions.edit")}",
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
                                                bubblingLabel: "template-edit",
                                                showCheckboxColumn: false
                                            }).setMessages(${messages});

                                    YAHOO.util.Event.onContentReady ("${id}-templates-grid", function () {
                                        YAHOO.Bubbling.fire ("activeGridChanged", {
                                            datagridMeta: {
                                                itemType: "lecm-rpeditor:reportTemplate",
                                                nodeRef: "NOT_LOAD",
                                                sort:"cm:name|desc"
                                            },
                                            bubblingLabel: "template-edit"
                                        });
                                    });
                                }

                                function init() {
                                    createDatagrid();
                                }

                                YAHOO.util.Event.onDOMReady(init);
                                //]]></script>
                            </@grid.datagrid>
                        </div>
                    </div>
                </div>
            </td>
        </tr>
        </tbody>
    </table>

    <script type="text/javascript">//<![CDATA[
        function initEditor() {
            var reportsEditor = new LogicECM.module.ReportsEditor.TemplateEditor("${id}");
            reportsEditor.setReportId("${page.url.args.reportId}");
            reportsEditor.setTemplateId("${activeTemplateId!""}");
            reportsEditor.setMessages(${messages});
            reportsEditor.setDefaultFilter({
                name:"${defaultName!""}",
                nodeRef: "${defaultRef!""}"
            });
            reportsEditor.draw();

            var htmlId = "${page.url.args.reportId}".replace("workspace://SpacesStore/","").replace("-","");
            Alfresco.util.Ajax.request(
                    {
                        url:Alfresco.constants.URL_SERVICECONTEXT + "components/form",
                        dataObj:{
                            htmlid:htmlId,
                            itemKind:"node",
                            itemId:"${page.url.args.reportId}",
                            formId:"template-info",
                            mode: "view",
                            submitType:"json",
                            showSubmitButton:"false",
                            showCancelButton:"false"
                        },
                        successCallback:{
                            fn: function (response) {
                                var formEl = Dom.get("${id}-reportTemplateInfo");
                                formEl.innerHTML = response.serverResponse.responseText;
                                Dom.setStyle("${id}-footer", "opacity", "1");
                            }
                        },
                        failureMessage:"message.failure",
                        execScripts:true
                    });
        }
        YAHOO.util.Event.onDOMReady(initEditor);
    </script>
</div>
<#-- Empty results list template -->
<div id="${id}-empty" style="display: none">
    <div class="empty"><h3>${msg("empty.title")}</h3><span>${msg("empty.description")}</span></div>
</div>
<#else>
    <div>Данная страница достпна только для конкретного отчета</div>
</#if>
