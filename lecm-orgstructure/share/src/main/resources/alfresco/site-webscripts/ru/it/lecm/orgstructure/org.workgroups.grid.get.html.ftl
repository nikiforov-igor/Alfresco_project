<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign id = args.htmlid>

<div id="orgstructure-workgroup-grid">
	<div id="yui-main-3">
		<div id="${id}-alf-content">
			<!-- include base datagrid markup-->
		<@grid.datagrid id=id showViewForm=false>
			<script type="text/javascript">//<![CDATA[
			function createWorkgroupDatagrid() {
				new LogicECM.module.Base.DataGrid('${id}').setOptions(
						{
							bubblingLabel:"workGroup",
							usePagination:true,
							showExtendSearchBlock:false
						}).setMessages(${messages});
			}

			function init() {
                createWorkgroupDatagrid();
			}

			YAHOO.util.Event.onDOMReady(init);
			//]]></script>
		</@grid.datagrid>
		</div>
	</div>
</div>
