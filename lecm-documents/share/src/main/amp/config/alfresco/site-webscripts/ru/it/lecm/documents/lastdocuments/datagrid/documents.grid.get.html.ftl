<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign id = "last-documents-datagrid">

<div class="yui-t1" id="contracts-grid">
    <div id="yui-main-2">
        <div class="yui-b datagrid-content" id="alf-content">
        <@grid.datagrid id=id showViewForm=false>
            <script type="text/javascript">//<![CDATA[
			(function(){
	            function createDatagrid() {
	                var datagrid = new LogicECM.module.Base.DataGrid('${id}').setOptions({
	                    usePagination: true,

                        disableDynamicPagination: true,
	                    pageSize: 20,
	                    showExtendSearchBlock: false,
	                    actions: [],
	                    allowCreate: false,
	                    showActionColumn: false,
	                    showCheckboxColumn: false,
	                    bubblingLabel: "documents-last",
	                    attributeForOpen: "lecm-document:present-string",
                        dataSource: "/lecm/documents/last/datasource",
                        datagridMeta: {
                            itemType: "lecm-document:base",
                            useChildQuery: false,
                            datagridFormId: "last-documents-list"
                        }
	                }).setMessages(${messages});
                    datagrid.draw();
	            }
	
	            function init() {
                    LogicECM.module.Base.Util.loadScripts([
                        'scripts/lecm-base/components/advsearch.js',
                        'scripts/lecm-base/components/lecm-datagrid.js'
                    ], createDatagrid);
	            }
	
	            YAHOO.util.Event.onDOMReady(init);
			})();
            //]]></script>
        </@grid.datagrid>
        </div>
    </div>
</div>
