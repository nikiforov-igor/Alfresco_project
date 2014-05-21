<#import "/ru/it/lecm/base-share/components/base-components.ftl" as comp/>
<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign aDateTime = .now>
<#assign id = "template-select-" + aDateTime?iso_utc/>

<#assign templatesGridId ="${id}-templates-grid">
<#assign templatesGridLabel ="templateSelectList-" + aDateTime?iso_utc>

<div class="yui-t1" id="${id}">
    <!-- Grid Start-->
    <div class="reports">
        <div id="${templatesGridId}" class="templates-grid">
        <@grid.datagrid id=templatesGridId showViewForm=false showArchiveCheckBox=false>
            <script type="text/javascript">//<![CDATA[
                LogicECM.module.ReportsEditor.TemplatesGrid = function (containerId) {
                    LogicECM.module.ReportsEditor.TemplatesGrid.superclass.constructor.call(this, containerId);
                    return this;
                };

                YAHOO.lang.extend(LogicECM.module.ReportsEditor.TemplatesGrid, LogicECM.module.Base.DataGrid);

                YAHOO.lang.augmentObject(LogicECM.module.ReportsEditor.TemplatesGrid.prototype, {
                    onActionSelectTemplate: function (item) {
                        // копируем шаблон в отчет
                        var context = this;
                        YAHOO.Bubbling.fire("copyTemplateToReport", {
                            templateId: item.nodeRef
                        });
                    }
                }, true);


                function initTemplateGrid() {
                    var datagrid = new LogicECM.module.ReportsEditor.TemplatesGrid("${templatesGridId}").setOptions(
                            {
                                usePagination: true,
                                showExtendSearchBlock: false,
                                forceSubscribing: true,
                                actions: [
                                    {
                                        type: "datagrid-action-link-${templatesGridLabel}",
                                        id: "onActionSelectTemplate",
                                        permission: "edit",
                                        label: "${msg("actions.select")}"
                                    }
                                ],
                                bubblingLabel: "${templatesGridLabel}",
                                showCheckboxColumn: false
                            }).setMessages(${messages});

                    YAHOO.Bubbling.fire("activeGridChanged", {
                        datagridMeta: {
                            itemType: "lecm-rpeditor:reportTemplate",
                            useChildQuery: true,
                            nodeRef: LogicECM.module.ReportsEditor.SETTINGS.templatesContainer,
                            sort: "cm:name|desc"
                        },
                        bubblingLabel: "${templatesGridLabel}"
                    });
                }

                YAHOO.util.Event.onContentReady('${templatesGridId}', initTemplateGrid);
            //]]></script>
        </@grid.datagrid>
        </div>
    </div>
</div>