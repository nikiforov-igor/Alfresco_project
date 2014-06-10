<#include "/org/alfresco/include/alfresco-template.ftl" />
<#include "/org/alfresco/include/documentlibrary.inc.ftl" />

<@templateHeader "transitional">
	<@script type="text/javascript" src="${url.context}/res/jquery/jquery-1.6.2.js"/>
<#--загрузка base-utils.js вынесена в base-share-config-custom.xml-->
	<#--<@script type="text/javascript" src="${url.context}/res/scripts/lecm-base/components/base-utils.js"></@script>-->
        <@script type="text/javascript" src="${url.context}/res/yui/resize/resize.js"></@script>
        <@script type="text/javascript" src="${url.context}/res/scripts/lecm-dictionary/dictionary.js"></@script>
	<#assign plane = false/>
	<#if page.url.args.plane?? && page.url.args.plane == "true">
		<#assign plane = true/>
	</#if>
</@>

<#import "/ru/it/lecm/base/base-page.ftl" as bpage/>
<#import "/ru/it/lecm/base-share/components/2-panels-with-splitter.ftl" as panels/>

<@bpage.basePage showToolbar=false>
<#if isEngineer>
<div class="yui-t1" id="lecm-dictionary">
    <#if plane>
        <div id="yui-main-2">
            <div class="plane-dictionary-content" id="alf-content">
                <@region id="toolbar" scope="template" />
                <@region id="datagrid" scope="template" />
            </div>
        </div>
    <#else>
        <@panels.twoPanels>
                <@region id="toolbar" scope="template" />
                <@region id="datagrid" scope="template" />
        </@panels.twoPanels>
    </#if>
</div>
<#else>
    <@region id="forbidden" scope="template"/>
</#if>
</@bpage.basePage>