<#if hasRole>
    <#if page.url.args.statemachineId??>
        <#assign id = args.htmlid>
        <#import "/ru/it/lecm/base-share/components/base-components.ftl" as comp/>
        <@comp.baseMenu>
            <@comp.baseMenuButton "home" msg("btn.home") "" true/>
        </@comp.baseMenu>
    </#if>
</#if>
