<#assign id = args.htmlid>
<!--[if IE]>
   <iframe id="yui-history-iframe" src="${url.context}/res/yui/history/assets/blank.html"></iframe>
<![endif]-->
<input id="yui-history-field" type="hidden" />
<script type="text/javascript">//<![CDATA[
   new Alfresco.component.DataGrid('${id}').setOptions(
   {
      siteId: "${page.url.templateArgs.site!""}",
      containerId: "${template.properties.container!"dataLists"}",
      usePagination: ${(args.pagination!false)?string}
   }).setMessages(${messages});
//]]></script>
<div id="${id}-body" class="datagrid">
   <div class="datagrid-meta">
	   <div class="markup">
		   <label for="filter">Filter:</label> <input type="text" id="filter" value="">
	   </div>
	   <form id="uploadForm" enctype="multipart/form-data" method="post" target=uploadFrame
	         action="${url.context}/proxy/alfresco/lecm/dictionary/post/import">
			<p><input type="file" name="f">
			<input type="submit" value="Import"></p>
	   </form>
	   <div id="progressBar" style="display:none;">
	   </div>
	   <iframe id="uploadFrame" name="uploadFrame" src="" style="display:none;"></iframe>
		<h2 id="${id}-title"></h2>
      <div id="${id}-description" class="datagrid-description"></div>
   </div>
   <div id="${id}-datagridBar" class="yui-ge datagrid-bar flat-button">
      <div class="yui-u first align-center">
         <div id="${id}-paginator" class="paginator"></div>
      </div>
      <div class="yui-u align-right">
         <div class="items-per-page" style="visibility: hidden;">
            <button id="${id}-itemsPerPage-button">${msg("menu.items-per-page")}</button>
         </div>
      </div>
   </div>

   <div id="${id}-grid" class="grid"></div>

   <div id="${id}-selectListMessage" class="hidden select-list-message">${msg("message.select-list")}</div>

   <div id="${id}-datagridBarBottom" class="yui-ge datagrid-bar datagrid-bar-bottom flat-button">
      <div class="yui-u first align-center">
         <div class="item-select">&nbsp;</div>
         <div id="${id}-paginatorBottom" class="paginator"></div>
      </div>
   </div>

   <!-- Action Sets -->
   <div style="display:none">
      <!-- Action Set "More..." container -->
      <div id="${args.htmlid}-moreActions">
         <div class="onActionShowMore"><a href="#" class="show-more" title="${msg("actions.more")}"></a></div>
	     <#--<div class="onActionVersion"><a href="#" class="show-more" title="${msg("actions.more")}"></a></div>-->
         <div class="more-actions hidden"></div>
      </div>

      <!-- Action Set Templates -->
      <div id="${args.htmlid}-actionSet" class="action-set simple">
      <#list actionSet as action>
         <div class="${action.id}"><a rel="${action.permission!""}" href="${action.href}" class="${action.type}" title="${msg(action.label)}"><span>${msg(action.label)}</span></a></div>
      </#list>
      </div>
   </div>
</div>