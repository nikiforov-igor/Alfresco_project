<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign id = args.htmlid>

<div class="yui-t1" id="contracts-grid">
    <div id="yui-main-2">
        <div class="yui-b" id="alf-content" style="margin-left: 0;">
        <@grid.datagrid id=id showViewForm=true>
            <script type="text/javascript">//<![CDATA[
            function createDatagrid() {
                new LogicECM.module.DocumentsJournal.DataGrid('${id}').setOptions({
                    usePagination: true,
                    pageSize: 10,
                    showExtendSearchBlock: true,
                    actions: [],
                    allowCreate: false,
                    showActionColumn: false,
                    showCheckboxColumn: false,
                    bubblingLabel: "documents-journal",
                    attributeForShow: "lecm-document:present-string"
                }).setMessages(${messages});

                YAHOO.Bubbling.fire("activeGridChanged",
                        {
                            datagridMeta: {
                                itemType: "lecm-document:base",
                                nodeRef: LogicECM.module.DocumentsJournal.SETTINGS.nodeRef,
                                sort: "cm:modified|false",
                                searchConfig: {
                                    filter: '+PATH:"' + LogicECM.module.DocumentsJournal.SETTINGS.draftsPath + '//*"'
                                            + ' OR +PATH:"' + LogicECM.module.DocumentsJournal.SETTINGS.documentsPath + '//*"'
                                }

                            },
                            bubblingLabel: "documents-journal"
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
