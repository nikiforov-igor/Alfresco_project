<!-- Comments List -->
<@link rel="stylesheet" type="text/css" href="${url.context}/res/components/comments/comments-list.css" />
<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/components/document-comments.css" />
<@script type="text/javascript" src="${url.context}/res/scripts/components/document-comments-list.js"></@script>

<#if hasViewCommentPerm!false>
    <#assign el=args.htmlid?js_string>
<script type="text/javascript">//<![CDATA[
new LogicECM.CommentsList("${el}").setOptions(
        {
            nodeRef: "${nodeRef?js_string}",
        siteId: <#if site??>"${site?js_string}"<#else>null</#if>,
            maxItems: ${maxItems?js_string},
        activity: <#if activityParameterJSON??>${activityParameterJSON}<#else>null</#if>,
            editorConfig: {
                inline_styles: false,
                convert_fonts_to_spans: false,
                theme: "advanced",
                theme_advanced_buttons1: "bold,italic,underline,|,bullist,numlist,|,forecolor,|,undo,redo,removeformat",
                theme_advanced_toolbar_location: "top",
                theme_advanced_toolbar_align: "left",
                theme_advanced_resizing: true,
                theme_advanced_buttons2: null,
                theme_advanced_buttons3: null,
                theme_advanced_path: false,
                language: "${locale?substring(0, 2)?js_string}"
            },
            permissions: {
            "edit": <#if hasDeleteCommentPerm!false>true<#else>false</#if>,
            "delete": <#if hasDeleteCommentPerm!false>true<#else>false</#if>
            }
        }).setMessages(
${messages}
);
//]]></script>
<script type="text/javascript">
    function hideButton() {
        if(location.hash != "#expanded") {
            YAHOO.util.Dom.setStyle(this, 'display', 'none');
        }
    }
    YAHOO.util.Event.onAvailable("${el}-action-collapse", hideButton);
</script>
<div class="panel-header">
    <div class="panel-title">${msg("header.comments")}</div>
    <div class="lecm-dashlet-actions">
        <a id="${el}-action-collapse" class="collapse" title="${msg("btn.collapse")}"></a>
    </div>
</div>
<div id="${el}" class="comments-list-container">
    <div id="${el}-body" class="comments-list">
        <div id="${el}-add-comment">
            <div id="${el}-add-form-container" class="theme-bg-color-4 hidden"></div>
        </div>

        <div class="comments-list-actions">
            <div class="left">
                <div id="${el}-actions" class="hidden">
                    <#if hasCreateCommentPerm>
                        <button class="alfresco-button" name=".onAddCommentClick">${msg("button.addComment")}</button>
                    </#if>
                </div>
            </div>
            <div class="right">
                <div id="${el}-paginator-top"></div>
            </div>
            <div class="clear"></div>
        </div>

        <hr class="hidden"/>

        <div id="${el}-comments-list"></div>

        <hr class="hidden"/>

        <div class="comments-list-actions">
            <div class="left">
            </div>
            <div class="right">
                <div id="${el}-paginator-bottom"></div>
            </div>
            <div class="clear"></div>
        </div>
    </div>
</div>
</#if>