<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign id = args.htmlid>
<#assign showSearchBlock = true/>

<script type="text/javascript">//<![CDATA[
(function () {
	new Alfresco.widget.Resizer("OrgStructure").setOptions(
		{
			initialWidth: 300
		}
	);
})();
//]]></script>
<div class="yui-t1" id="orgstructure-grid-with-tree">
	<div id="yui-main">
		<div class="yui-b" id="alf-content">
        <!-- include base datagrid markup-->
		<@grid.datagrid id showSearchBlock>
			<script type="text/javascript">//<![CDATA[
			(function () {
				function init() {
					// EXTEND DATAGRID HERE
					new LogicECM.module.Base.DataGrid('${id}').setOptions(
							{
								usePagination: true,
								showExtendSearchBlock:${showSearchBlock?string}
							}).setMessages(${messages});
				}
				YAHOO.util.Event.onDOMReady(init);
			})();
			//]]></script>
		</@grid.datagrid>
		</div>
	</div>
	<div id="alf-filters">
		<!-- include tree -->
		<#include "/ru/it/lecm/orgstructure/orgstructure-tree.ftl"/>
	</div>
</div>
