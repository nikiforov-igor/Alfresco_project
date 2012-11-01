<#include "/org/alfresco/include/alfresco-template.ftl" />
<#include "/org/alfresco/include/documentlibrary.inc.ftl" />
<@templateHeader "transitional">
    <@documentLibraryJS />
<script type="text/javascript">//<![CDATA[
new Alfresco.widget.Resizer("Orgstructure");
//]]></script>

    <@script type="text/javascript" src="${url.context}/res/modules/documentlibrary/doclib-actions.js"></@script>
    <@script type="text/javascript" src="${url.context}/js/documentlibrary-actions.js"></@script>

    <#include "/org/alfresco/components/documentlibrary/documentlist.get.head.ftl" />
    <@script type="text/javascript" src="${page.url.context}/res/modules/simple-dialog.js"></@script>
    <@script type="text/javascript" src="${page.url.context}/res/ru/it/lecm/utils/generate-custom-name.js"></@script>
</@>

<@templateBody>
<div id="alf-hd">
    <@region id="header" scope="global"/>
    <@region id="title" scope="template"/>
</div>
<div id="bd">

    <div class="yui-t1" id="alfresco-orgstructure">
        <div id="yui-main">
            <div class="yui-b" id="alf-content">
                <@region id="toolbar" scope="template" />
                <@region id="content" scope="template"/>
            </div>
        </div>
        <div class="yui-b" id="alf-filters">
            <@region id="menu" scope="template"/>
        </div>
    </div>
</div>
</@>

<@templateFooter>
<div id="alf-ft">
    <@region id="footer" scope="global"/>
</div>
</@>