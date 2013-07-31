<#assign id = args.htmlid>
<!--[if IE]>
   <iframe id="yui-history-iframe" src="${url.context}/res/yui/history/assets/blank.html"></iframe> 
<![endif]-->
<input id="yui-history-field" type="hidden" />
<script type="text/javascript">//<![CDATA[
   new IT.component.ModelList('${id}');
//]]></script>
<div id="${id}-body" class="datagrid models">
   
</div>
<div id="${id}-button">Добавить</div>