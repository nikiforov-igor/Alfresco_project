<#include "/org/alfresco/include/alfresco-template.ftl" />
<@templateHeader>
    <link rel="stylesheet" type="text/css" href="${page.url.context}/css/components/document-header.css" />
    <@script type="text/javascript" src="${url.context}/js/documentlibrary-actions.js"></@script>
    <@script type="text/javascript" src="${page.url.context}/yui/resize/resize-min.js"></@script>
    <@link rel="stylesheet" type="text/css" href="${page.url.context}/res/components/documentlibrary/actions.css" />
    <@script type="text/javascript" src="${url.context}/res/modules/documentlibrary/doclib-actions.js"></@script>
    <@link rel="stylesheet" type="text/css" href="${page.url.context}/res/components/document-details/document-details-panel.css" />
    <link rel="stylesheet" type="text/css" href="${page.url.context}/css/dashlet-components.css" />
    <@link rel="stylesheet" type="text/css" href="${page.url.context}/res/components/document-details/document-metadata.css" />
    <@script type="text/javascript" src="${page.url.context}/res/components/document-details/document-metadata.js"></@script>
    <@templateHtmlEditorAssets />
    <script type="text/javascript">
        //<![CDATA[
        Alfresco.constants.DASHLET_RESIZE = true;
        //]]>
    </script>
</@>

<@templateBody>
<div id="alf-hd">
    <@region id="header" scope="global"/>
    <#--<@region id="title" scope="template"/>-->
</div>
<div id="bd">
    <@region id="document-header" scope="template"/>
    <div class="yui-gc">
        <div class="yui-u first">
            <div id="main-region" class="yui-gd grid columnSize2">
                <div class="yui-u first column1">
                    <@region id="main" scope="template"/>
                    <@region id="members" scope="template"/>
                </div>
                <div class="yui-u column2">
                    <@region id="attachments" scope="template"/>
                    <@region id="tasks" scope="template"/>
                </div>
            </div>
            <@region id="custom" scope="template"/>
            <@region id="comments" scope="template"/>
        </div>
        <div class="yui-u">
            <@region id="document-actions" scope="template"/>
            <@region id="document-metadata" scope="template"/>
            <@region id="document-attachments" scope="template"/>
            <@region id="document-workflows" scope="template"/>
            <@region id="document-history" scope="template"/>
            <@region id="document-connections" scope="template"/>
            <@region id="document-tags" scope="template"/>
            <@region id="document-members" scope="template"/>
            <@region id="document-tasks" scope="template"/>
        </div>
    </div>

    <@region id="html-upload" scope="template"/>
    <@region id="flash-upload" scope="template"/>
    <@region id="file-upload" scope="template"/>
</div>
</@>

<@templateFooter>
<div id="alf-ft">
    <@region id="footer" scope="global"/>
</div>
</@>
