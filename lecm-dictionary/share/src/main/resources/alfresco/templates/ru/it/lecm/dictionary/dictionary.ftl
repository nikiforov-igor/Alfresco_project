<#include "/org/alfresco/include/alfresco-template.ftl" />
<#include "/org/alfresco/include/documentlibrary.inc.ftl" />

<@templateHeader "transitional">
	<#assign plane = false/>
	<#if page.url.args.plane?? && page.url.args.plane == "true">
		<#assign plane = true/>
	</#if>

	<script type="text/javascript">//<![CDATA[
		function init() {
			new LogicECM.module.DictionaryMain().setOptions(
					{
						dictionaryName: "${page.url.args.dic!""}",
						plane: ${plane?string}
					});

            <#if !plane>
                new LogicECM.module.Base.Resizer('DictionaryResizer');
            </#if>
		}

		YAHOO.util.Event.onDOMReady(init);
	//]]></script>

	<@script type="text/javascript" src="${page.url.context}/scripts/lecm-dictionary/dictionary.js"></@script>
</@>

<#import "/ru/it/lecm/base/base-page.ftl" as bpage/>

<@bpage.basePage showToolbar=false>
<div class="yui-t1" id="lecm-dictionary">
    <div id="yui-main-2">
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