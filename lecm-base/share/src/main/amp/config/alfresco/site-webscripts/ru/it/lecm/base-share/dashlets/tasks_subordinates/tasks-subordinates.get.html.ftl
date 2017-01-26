<@markup id="css" >
	<#-- CSS Dependencies -->
	<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/lecm-base/dashlets/tasks-subordinates.css" group="dashlets"/>
</@>
<@markup id="js">
	<#-- JavaScript Dependencies -->
	<@script type="text/javascript" src="${url.context}/res/components/workflow/workflow-actions.js" group="dashlets"/>
	<@script type="text/javascript" src="${url.context}/res/scripts/lecm-base/dashlets/tasks-subordinates.js" group="dashlets"/>
</@>

<@markup id="widgets">
	<@createWidgets group="dashlets"/>
</@>

<@markup id="html">
	<@uniqueIdDiv>
		<#assign id = args.htmlid>
		<#assign jsid = args.htmlid?js_string>
		<script type="text/javascript">//<![CDATA[
		(function()
		{
		   new LogicECM.dashlet.TasksSubordinates("${jsid}").setOptions(
		   {
			  hiddenTaskTypes: [<#list hiddenTaskTypes as type>"${type}"<#if type_has_next>, </#if></#list>],
			  maxItems: ${maxItems!"50"},
			  filters:
			  {<#list filters as filter>
				 "${filter.type?js_string}": "${filter.parameters?js_string}"<#if filter_has_next>,</#if>
			  </#list>}
		   }).setMessages(${messages});
		   new Alfresco.widget.DashletResizer("${id}", "${instance.object.id}");
		   new Alfresco.widget.DashletTitleBarActions("${args.htmlid}").setOptions(
		   {
			  actions:
			  [
				 {
					cssClass: "help",
					bubbleOnClick:
					{
					   message: "${msg("dashlet.help")?js_string}"
					},
					tooltip: "${msg("dashlet.help.tooltip")?js_string}"
				 }
			  ]
		   });
		})();
		//]]></script>

		<div class="dashlet tasks-subordinates">
		   <div class="title">${msg("header")}</div>
		   <div class="toolbar flat-button">
			  <div class="hidden">
				 <span class="align-left yui-button yui-menu-button" id="${id}-filters">
					<span class="first-child">
					   <button type="button" tabindex="0"></button>
					</span>
				 </span>
				 <select id="${id}-filters-menu">
				 <#list filters as filter>
					<option value="${filter.type?html}">${msg("filter." + filter.type)}</option>
				 </#list>
				 </select>
				 <div class="clear"></div>
			  </div>
		   </div>
		   <div class="toolbar flat-button">
			  <div class="align-left" id="${id}-paginator">&nbsp;</div>
			  <div class="clear"></div>
		   </div>
		   <div class="body scrollableList" <#if args.height??>style="height: ${args.height}px;"</#if>>
			  <div id="${id}-tasks"></div>
		   </div>
		</div>
	</@>
</@>
