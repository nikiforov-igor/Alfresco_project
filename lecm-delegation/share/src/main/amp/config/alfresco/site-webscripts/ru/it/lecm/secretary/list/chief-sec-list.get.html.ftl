<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign id = args.htmlid/>
<#assign showViewForm = false/>

<script type="text/javascript">//<![CDATA[
	(function () {

        function createDatagrid() {
			var datagrid,
                nestedGridDS;
			if('secretary' == '${type}') {
				datagrid = new LogicECM.module.Secretary.SecretaryList.Grid('${id}');
                nestedGridDS = 'lecm/secretary/secretary-datagrid';
			} else if('deputy' == '${type}') {
                datagrid = new LogicECM.module.Deputy.Grid('${id}');
                nestedGridDS = 'lecm/deputy/deputy-datagrid';
            }

            datagrid.setOptions({
                bubblingLabel: "chief-secretary-list-datagrid",
                usePagination:true,
                disableDynamicPagination: true,
                showExtendSearchBlock:true,
                showCheckboxColumn: false,
                attributeForShow: 'lecm-orgstr:employee-last-name',
                expandable: true,
                expandDataSource: nestedGridDS,
				searchForm: ('secretary' == '${type}') ? 'secretary-search-form' : 'deputy-search-form',
                datagridMeta:{
                    itemType: LogicECM.module.Secretary.Const.itemType,
                    nodeRef: LogicECM.module.Secretary.Const.nodeRef,
                    datagridFormId: "chief-sec-datagrid",
                    useOnlyInSameOrg: true,
                    useFilterByOrg: true,
                    sort: 'lecm-orgstr:employee-last-name|true'
                },
                expandDataObj: {
                    itemType: 'lecm-orgstr:employee'
                },
                actions: [
                    {
                        type: "datagrid-action-link-chief-secretary-list-datagrid",
                        id: "onActionEdit",
                        permission: "edit",
                        label: "${msg("actions.edit")}"
                    }
                ]
            });
            datagrid.setMessages(${messages});
            datagrid.draw();
        }

        function init() {
            LogicECM.module.Base.Util.loadResources([
                'scripts/lecm-base/components/lecm-toolbar.js',
                'modules/simple-dialog.js',
                'scripts/lecm-base/components/advsearch.js',
                'scripts/lecm-base/components/lecm-datagrid.js',
                'scripts/lecm-secretary/secretary-list.js',
                'scripts/lecm-deputy/deputy-datagrid.js'
            ], [
				'css/lecm-secretary/secretary-datagrid.css'
            ], createDatagrid);
        }

        YAHOO.util.Event.onDOMReady(init);
	})();
//]]>
</script>
<div id="${id}-delegation-settings"></div>
<@grid.datagrid id showViewForm/>
