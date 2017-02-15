<@script type="text/javascript" src="${url.context}/res/scripts/lecm-errands/lecm-document-errands.js"></@script>
<@script type="text/javascript" src="${url.context}/res/scripts/lecm-errands/document-errands.js"></@script>
<@script type="text/javascript" src="${url.context}/res/scripts/lecm-errands/lecm-errands-dashlet.js"></@script>
<@script type="text/javascript" src="${url.context}/res/scripts/lecm-errands/lecm-document-errands.js"></@script>
<@script type="text/javascript" src="${url.context}/res/scripts/lecm-errands/errands-links.js"></@script>

<!-- Document Metadata Header -->
<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/lecm-errands/errands-metadata.css" />
<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/lecm-errands/errands-form.css" />
<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/components/document-metadata-form-edit.css" />
<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/lecm-errands/errands-main-form.css" />
<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/lecm-errands/document-errands.css" />

<#assign id=args.htmlid/>

<script type="text/javascript">
    //<![CDATA[
    (function () {
        function init() {
            new LogicECM.module.Document.Ajax.Content("${id}-results").setOptions(
                    {
                        contentURL: Alfresco.constants.URL_PAGECONTEXT + "lecm/components/document/document-errands/content",
                        requestParams: {
                            nodeRef: "${args.nodeRef}",
                            state: "all",
                            errandsLimit: 5,
                            containerHtmlId: "${id}"
                        },
                        containerId: "${id}-results"
                    }).setMessages(${messages});
        }

        YAHOO.util.Event.onContentReady("${id}-results", init);
    })();
    //]]>
</script>

<div class="widget-bordered-panel">
    <div class="document-metadata-header document-components-panel" id="${id}-results">
        <h2 id="${id}-heading" class="dark">
        ${msg("heading")}
            <span class="alfresco-twister-actions">
                    <a id="${id}-action-expand" href="javascript:void(0);" onclick="" class="expand errands-expand"
                       title="${msg("label.expand")}">&nbsp;</a>
                </span>
        </h2>

        <div id="${id}-formContainer"></div>
    </div>
</div>
