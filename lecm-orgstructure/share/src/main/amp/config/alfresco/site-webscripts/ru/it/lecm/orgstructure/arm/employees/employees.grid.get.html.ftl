<#assign id = args.htmlid>

<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<script type="text/javascript">//<![CDATA[
(function(){
    function createDatagrid() {
        var datagrid = new LogicECM.module.Base.DataGrid('${id}').setOptions(
                {
                    usePagination:false,
                    showExtendSearchBlock:false,
                    showActionColumn: false,
                    maxResults:100,
                    loopSize:50,
                    unlimited: true,
                    actions: [],
                    bubblingLabel: "employees",
                    showCheckboxColumn: false,
                    attributeForShow:"lecm-orgstr:employee-last-name",
                    excludeColumns: ["lecm-orgstr:employee-person-login"]
                }).setMessages(${messages});

        YAHOO.util.Event.onContentReady ('${id}', function () {
            YAHOO.Bubbling.fire ("activeGridChanged", {
                datagridMeta: {
                    itemType: LogicECM.module.OrgStructure.EMPLOYEES_SETTINGS.itemType,
                    nodeRef: LogicECM.module.OrgStructure.EMPLOYEES_SETTINGS.nodeRef,
                    datagridFormId:"arm-datagrid"
                },
                bubblingLabel: "employees"
            });
        });
    }

    function init() {
        LogicECM.module.Base.Util.loadResources([
            'modules/simple-dialog.js',
            'scripts/lecm-base/components/advsearch.js',
            'scripts/lecm-base/components/lecm-datagrid.js'
        ], [
            'components/search/search.css',
            'modules/document-details/historic-properties-viewer.css',
            'yui/treeview/assets/skins/sam/treeview.css',
            'css/lecm-orgstructure/orgstructure-tree.css'
        ], createDatagrid);
    }

    YAHOO.util.Event.onDOMReady(init);
})();
//]]></script>


<div class="yui-t1" id="orgstructure-employees-grid">
	<div id="yui-main-2">
		<div class="yui-b body scrollableList datagrid-content" id="alf-content">
			<!-- include base datagrid markup-->
		<@grid.datagrid id=id showViewForm=false showArchiveCheckBox=false/>
		</div>
	</div>
</div>
