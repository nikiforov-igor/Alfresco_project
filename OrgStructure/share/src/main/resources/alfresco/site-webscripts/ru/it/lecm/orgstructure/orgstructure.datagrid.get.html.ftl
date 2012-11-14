<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>

<#assign id = args.htmlid>
<#assign showTree = true>
<#assign showSearchBlock = true/>

<#if page.url.args.type?? && (page.url.args.type == "employees"|| page.url.args.type == "workGroups")>
	<#assign showTree = false>
</#if>

<script type="text/javascript">//<![CDATA[
(function () {
<#if showTree>
	new Alfresco.widget.Resizer("OrgStructure").setOptions(
		{
			initialWidth: 300
		}
	);
</#if>
})();
//]]></script>
<div class="yui-t1" id="orgstructure-grid-with-tree">
	<div id="yui-main">
		<div class="yui-b" id="alf-content" <#if !showTree>style="margin-left: 0;"</#if>>
        <!-- include base datagrid markup-->
		<@grid.datagrid id showSearchBlock false "">
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
<#if showTree>
	<div id="alf-filters">
		<!-- include tree -->
		<#include "/ru/it/lecm/orgstructure/orgstructure-tree.ftl"/>
	</div>
</#if>
</div>
