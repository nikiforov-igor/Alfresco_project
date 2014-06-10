<#include "/org/alfresco/components/form/form.dependencies.inc">
<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/lecm-statemachine-editor/menu.css" />
<@script type="text/javascript" src="${url.context}/res/scripts/lecm-statemachine-editor/menu.js"></@script>

<#if hasRole>
    <#if page.url.args.statemachineId??>
        <#assign id = args.htmlid>
        <#import "/ru/it/lecm/base-share/components/base-components.ftl" as comp/>
        <@comp.baseMenu>
            <@comp.baseMenuButton "home" "На главную" "" true/>
        </@comp.baseMenu>
    </#if>
</#if>