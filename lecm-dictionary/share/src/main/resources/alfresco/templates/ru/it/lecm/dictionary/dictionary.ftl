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

	<@script type="text/javascript" src="${url.context}/res/modules/documentlibrary/doclib-actions.js"></@script>
	<@script type="text/javascript" src="${url.context}/js/documentlibrary-actions.js"></@script>
	<#include "/org/alfresco/components/documentlibrary/documentlist.get.head.ftl" />
	<@script type="text/javascript" src="${page.url.context}/res/modules/simple-dialog.js"></@script>
	<@script type="text/javascript" src="${page.url.context}/scripts/lecm-base/components/base-utils.js"></@script>

	<@script type="text/javascript" src="${page.url.context}/scripts/lecm-dictionary/dictionary.js"></@script>
</@>

<#import "/ru/it/lecm/base/base-page.ftl" as bpage/>

<@bpage.basePage showToolbar=false>
	<div class="yui-t1" id="lecm-dictionary">
		<div id="yui-main">
			<div class="<#if !plane>yui-b<#else>plane-dictionary-content</#if>" id="alf-content">
				<@region id="toolbar" scope="template" />
	            <@region id="datagrid" scope="template" />
			</div>
		</div>
		<#if !plane>
			<div id="alf-filters">
				<@region id="tree" scope="template"/>
			</div>
		</#if>
	</div>
</@bpage.basePage>