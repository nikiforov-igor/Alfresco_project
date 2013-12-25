<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>
<#import "/ru/it/lecm/base-share/components/base-components.ftl" as comp/>

<#assign id = args.htmlid>
<#if page.url.args.reportId??>
    <div id="${id}-form" class="reports">
        <div class="title">
            <h3>${msg("label.current-template")}</h3>
        </div>
        <div id="${id}-reportTemplateInfo"></div>
        <div class="title">
            <h3>${msg("label.select-template")}</h3>
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
                    </div>
                </td>
                <td>
                    <div id="${id}-templates-grid">
                            <div class="yui-b" id="alf-content" style="margin-left: 0;">
                                <@grid.datagrid id="${id}-templates-grid" showViewForm=false>
                                    <script type="text/javascript">//<![CDATA[

                                    LogicECM.module.ReportsEditor.TemplatesGrid = function (containerId) {
                                        LogicECM.module.ReportsEditor.TemplatesGrid.superclass.constructor.call(this, containerId);
                                        return this;
                                    };

                                    YAHOO.lang.extend (LogicECM.module.ReportsEditor.TemplatesGrid, LogicECM.module.Base.DataGrid);

                                    YAHOO.lang.augmentObject (LogicECM.module.ReportsEditor.TemplatesGrid.prototype, {
                                        onActionSelectTemplate: function (item) {
                                            // копируем шаблон в отчет
                                            YAHOO.Bubbling.fire("copyTemplateToReport", {
                                                templateId: item.nodeRef
                                            });
                                        }
                                    }, true);


                                    function init() {
                                        var datagrid = new LogicECM.module.ReportsEditor.TemplatesGrid("${id}-templates-grid").setOptions(
                                                {
                                                    usePagination:true,
                                                    showExtendSearchBlock:false,
                                                    actions: [
                                                        {
                                                            type:"datagrid-action-link-template-edit",
                                                            id:"onActionSelectTemplate",
                                                            permission:"edit",
                                                            label:"${msg("actions.select")}",
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

                                    YAHOO.util.Event.onDOMReady(init);
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
            var reportsEditor = new LogicECM.module.ReportsEditor.TemplateEditor("${id}");
            reportsEditor.setReportId("${page.url.args.reportId}");
            reportsEditor.setMessages(${messages});
            reportsEditor.setDefaultFilter({
                name:"${defaultName!""}",
                nodeRef: "${defaultRef!""}"
            });

            reportsEditor._onRefreshTemplate();
        }
        YAHOO.util.Event.onDOMReady(initEditor);
    </script>
<#else>
    <div>${msg("label.unavaiable-page")}</div>
</#if>
