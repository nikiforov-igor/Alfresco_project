<#include "/org/alfresco/include/alfresco-template.ftl" />

<@templateHeader "transitional">
	<#include "/org/alfresco/components/form/form.dependencies.inc">

	<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/lecm-statemachine-editor/editor.css" group="lecm-statemachine-editor"/>
	<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/lecm-statemachine-editor/main.css" group="lecm-statemachine-editor"/>
	<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/lecm-statemachine-editor/menu.css" group="lecm-statemachine-editor"/>

	<@script type="text/javascript" src="${url.context}/res/yui/resize/resize.js" group="lecm-statemachine-editor"/>
	<@script type="text/javascript" src="${url.context}/res/scripts/lecm-statemachine-editor/main.js" group="lecm-statemachine-editor"/>
	<@script type="text/javascript" src="${url.context}/res/scripts/lecm-statemachine-editor/menu.js" group="lecm-statemachine-editor"/>
</@>

<#assign showContent=page.url.args.statemachineId??>
<#assign showMenu=showContent>
<#import "/ru/it/lecm/base/base-page.ftl" as bpage/>

<@bpage.basePage showToolbar=hasRole showMenu=hasRole>
    <#if hasRole>
        <#if showContent>
            <@region id="content" scope="template" />
        <#else>
            <@region id="deploy" scope="template" />
        </#if>
    <#else>
        <@region id="forbidden" scope="template"/>
    </#if>
</@bpage.basePage>
