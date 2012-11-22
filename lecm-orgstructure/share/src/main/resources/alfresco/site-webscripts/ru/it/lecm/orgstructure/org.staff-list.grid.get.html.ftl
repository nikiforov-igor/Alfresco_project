<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>
<#import "/ru/it/lecm/orgstructure/orgstructure-tree.ftl" as orgTree/>
<#assign id = args.htmlid>
<#assign showSearchBlock = true/>

<div class="yui-t1" id="orgstructure-staff-grid-with-tree">
	<div id="yui-main-2">
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
		<@orgTree.tree nodeType="lecm-orgstr:organization-unit" itemType="lecm-orgstr:staff-list"
						nodePattern="lecm-orgstr_element-full-name">
		</@orgTree.tree>
	</div>
</div>
