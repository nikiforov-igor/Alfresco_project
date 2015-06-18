<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign id = args.htmlid/>
<#assign showViewForm = false/>

<script type="text/javascript">//<![CDATA[
	(function () {
		"use strict";

        function createDatagrid() {
            var datagrid = new LogicECM.module.Delegation.DelegationList.Grid('${id}');
            datagrid.setOptions({
                bubblingLabel: "delegation-list-datagrid",
                usePagination:true,
                showExtendSearchBlock:true,
                showCheckboxColumn: false,
                searchShowInactive: true,
                attributeForShow: 'lecm-d8n:delegation-opts-owner-assoc',
                expandable: true,
                expandDataSource: 'lecm/delegation/procuraciesDatagrid',
                datagridMeta:{
                    useFilterByOrg: false,
                    itemType: LogicECM.module.Delegation.Const.itemType,
                    nodeRef: LogicECM.module.Delegation.Const.nodeRef
                },
                expandDataObj: {
                    itemType: 'lecm-d8n:procuracy',
                    filter: ' AND @lecm\-dic\:active:true'
                },
                actions: [
                    {
                        type: "datagrid-action-link-delegation-list-datagrid",
                        id: "onActionEdit",
                        permission: "edit",
                        label: "${msg("btn.delegate")}"
                    }
                ]
            });
            datagrid.setMessages(${messages});
            datagrid.draw();
        }

        function init() {
            LogicECM.module.Base.Util.loadResources([
                'scripts/lecm-delegation/delegation-menu.js',
                'scripts/lecm-base/components/lecm-toolbar.js',
                'scripts/lecm-delegation/list/delegation-list-toolbar.js',
                'modules/simple-dialog.js',
                'scripts/lecm-base/components/advsearch.js',
                'scripts/lecm-base/components/lecm-datagrid.js',
                'scripts/lecm-delegation/list/delegation-list.js'
            ], [
                'css/lecm-base/components/base-menu/base-menu.css',
                'css/lecm-delegation/delegation-menu.css',
                'components/data-lists/toolbar.css',
                'css/lecm-delegation/list/delegation-list.css'
            ], createDatagrid);
        }

        YAHOO.util.Event.onDOMReady(init);
	})();
//]]>
</script>
<div id="${id}-delegation-settings"></div>
<@grid.datagrid id showViewForm/>
