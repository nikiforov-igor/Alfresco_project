<#if hasRole>
    <#if page.url.args.statemachineId??>
        <#assign id = args.htmlid>
        <#import "/ru/it/lecm/base-share/components/base-components.ftl" as comp/>
        <@comp.baseMenu>
            <@comp.baseMenuButton "home" "На главную" "" true/>
        </@comp.baseMenu>
    </#if>
</#if>
