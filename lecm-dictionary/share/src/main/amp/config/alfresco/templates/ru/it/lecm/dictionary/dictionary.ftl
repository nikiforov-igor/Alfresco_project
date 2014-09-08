<#include "/org/alfresco/include/alfresco-template.ftl" />
<#include "/org/alfresco/include/documentlibrary.inc.ftl" />
<#import "/ru/it/lecm/base/base-page.ftl" as bpage/>
<#import "/ru/it/lecm/base-share/components/2-panels-with-splitter.ftl" as panels/>

<@bpage.basePageSimple showToolbar=false>
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
        <@panels.twoPanels leftPanelId="left-panel-dictionary" rightPanelId="right-panel-dictionary">
                <@region id="toolbar" scope="template" />
                <@region id="datagrid" scope="template" />
        </@panels.twoPanels>
    </#if>
</div>
<#else>
    <@region id="forbidden" scope="template"/>
</#if>
</@bpage.basePageSimple>