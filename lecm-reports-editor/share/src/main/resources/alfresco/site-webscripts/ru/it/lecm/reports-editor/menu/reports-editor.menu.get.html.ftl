<#if page.url.args.reportId??>
<#assign id = args.htmlid>
<#assign menuId = "menu-buttons">
<#import "/ru/it/lecm/base-share/components/base-components.ftl" as comp/>
<@comp.baseMenu>
</@comp.baseMenu>
</#if>