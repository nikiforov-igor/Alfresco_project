<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>
<#import "/ru/it/lecm/base-share/components/base-components.ftl" as comp/>

<#include "/org/alfresco/components/component.head.inc">
<@script type="text/javascript" src="${url.context}/res/scripts/lecm-base/components/advsearch.js"></@script>
<@script type="text/javascript" src="${url.context}/res/scripts/lecm-base/components/lecm-datagrid.js"/>
<@script type="text/javascript" src="${url.context}/res/scripts/lecm-base/components/lecm-association-search.js"/>
<@script type="text/javascript" src="${url.context}/res/scripts/lecm-arm/arm-documents-datagrid.js"></@script>
<@script type="text/javascript" src="${url.context}/res/components/form/date-range.js"></@script>
<@script type="text/javascript" src="${url.context}/res/components/form/number-range.js"></@script>
<@script type="text/javascript" src="${url.context}/res/scripts/lecm-documents/utils/search-queries.js"></@script>

<#assign id = args.htmlid>

<div class="yui-t1" id="arm-grid">
	<div id="yui-main-2">
		<div class="yui-b" id="alf-content">
        <@grid.datagrid id=id showViewForm=true>
            <script type="text/javascript">
                (function () {
                    new LogicECM.module.ARM.DataGrid('${id}').setOptions({
                        usePagination: true,
                        pageSize: 20,
                        showExtendSearchBlock: true,
                        searchShowInactive: true,
                        actions: [
                            {
                                type: "datagrid-action-link-documents-arm",
                                id: "onActionViewDocument",
                                permission: "",
                                label: "${msg("actions.edit")}"
                            }
                        ],
                        allowCreate: false,
                        showActionColumn: true,
                        showCheckboxColumn: true,
                        bubblingLabel: "documents-arm",
                        expandable: true,
                        expandDataSource: {
                            context: Alfresco.constants.PROXY_URI,
                            uri: "lecm/document/connections/api/armPresentation"
                        },
                        datagridMeta: {
                            itemType: "lecm-document:base",
                            datagridFormId: "datagrid-arm",
                            nodeRef: null,
                            actionsConfig: {
                                fullDelete: true,
                                trash: false
                            },
                            sort: "cm:modified|false"
                        }
                    }).setMessages(${messages});
                })();
                //<![CDATA[
                //]]></script>
        </@grid.datagrid>
		</div>
	</div>
</div>
