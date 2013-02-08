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

	<@script type="text/javascript" src="${page.url.context}/res/modules/simple-dialog.js"></@script>
	<@script type="text/javascript" src="${page.url.context}/scripts/lecm-dictionary/dictionary.js"></@script>
</@>

<#import "/ru/it/lecm/base/base-page.ftl" as bpage/>

<@bpage.basePage showToolbar=false>
	<@region id="dictionary-tree" scope="template" />
</@bpage.basePage>