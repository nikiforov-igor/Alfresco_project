<#include "/org/alfresco/include/alfresco-template.ftl" />
<#include "/org/alfresco/include/documentlibrary.inc.ftl" />

<@templateHeader "transitional">
	<@documentLibraryJS />

	<#assign plane = false/>
	<#if page.url.args.plane?? && page.url.args.plane == "true">
		<#assign plane = true/>
	</#if>

	<script type="text/javascript">//<![CDATA[
		(function () {
			<#if !plane>
				new Alfresco.widget.Resizer("DocumentLibrary");
			</#if>

			function init() {
				new LogicECM.module.DictionaryMain().setOptions(
						{
							dictionaryName: "${page.url.args.dic!""}",
							plane: ${plane?string}
						});
			}

			YAHOO.util.Event.onDOMReady(init);
		})();
	//]]></script>

	<#include "/org/alfresco/components/form/form.get.head.ftl">
	<@script type="text/javascript" src="${url.context}/res/modules/documentlibrary/doclib-actions.js"></@script>
	<@script type="text/javascript" src="${url.context}/js/documentlibrary-actions.js"></@script>
	<#include "/org/alfresco/components/documentlibrary/documentlist.get.head.ftl" />
	<@script type="text/javascript" src="${page.url.context}/res/modules/simple-dialog.js"></@script>
	<@script type="text/javascript" src="${page.url.context}/res/ru/it/lecm/utils/generate-custom-name.js"></@script>

	<@script type="text/javascript" src="${page.url.context}/scripts/lecm-dictionary/dictionary.js"></@script>
</@>

<@templateBody>
<div id="alf-hd">
	<@region id="header" scope="global"/>
    <@region id="title" scope="template"/>
</div>
<div id="bd">
    <div class="yui-t1" id="lecm-dictionary">
        <div id="yui-main">
            <div class="yui-b" id="alf-content" <#if plane>style="margin-left: 0;"</#if>>
                <@region id="toolbar" scope="template" />
                <@region id="datagrid" scope="template" />
            </div>
        </div>
		<#if !plane>
	        <div class="yui-b" id="alf-filters">
		        <@region id="tree" scope="template"/>
	        </div>
		</#if>
    </div>
</div>
</@>

<@templateFooter>
<div id="alf-ft">
	<@region id="footer" scope="global"/>
</div>
</@>