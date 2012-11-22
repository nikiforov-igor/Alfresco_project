<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign id = args.htmlid>

<div  id="orgstructure-workforces-grid">
	<div id="yui-main-2">
		<div id="${id}-alf-content">
			<!-- include base datagrid markup-->
		<@grid.datagrid id=id showViewForm=false>
			<script type="text/javascript">//<![CDATA[
			function createWorkforceDatagrid() {
				new LogicECM.module.Base.DataGrid('${id}').setOptions(
						{
							bubblingLabel: "workForce",
							usePagination:true,
							showExtendSearchBlock:false
						}).setMessages(${messages});
			}

			function init() {
                createWorkforceDatagrid();
			}

			YAHOO.util.Event.onDOMReady(init);
			//]]></script>
		</@grid.datagrid>
		</div>
	</div>
</div>
