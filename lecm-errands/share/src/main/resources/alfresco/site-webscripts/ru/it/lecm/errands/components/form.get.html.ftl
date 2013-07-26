<#assign id = args.htmlid?js_string>
<script type="text/javascript">
    //<![CDATA[
    (function () {
        var Dom = YAHOO.util.Dom,
                Event = YAHOO.util.Event;
        var container;

        function drawForm(nodeRef, formId, htmlId) {
            Alfresco.util.Ajax.request(
                    {
                        url: Alfresco.constants.URL_SERVICECONTEXT + "components/form",
                        dataObj: {
                            htmlid: htmlId,
                            itemKind: "node",
                            itemId: nodeRef,
                            formId: (formId ? formId : ""),
                            mode: "view"
                        },
                        successCallback: {
                            fn: function (response) {
                                container = Dom.get('${id}_container');
                                if (container != null) {
                                    container.innerHTML = response.serverResponse.responseText;
                                }
                            }
                        },
                        failureMessage: "message.failure",
                        execScripts: true,
                        htmlId: htmlId + nodeRef
                    });
        }

        function init() {
            drawForm("${nodeRef}", "${formId!""}", '${id}');
        }

        Event.onDOMReady(init);
    })();
    //]]>
</script>

<div class="metadata-form" id="${id}_metadata">
    <div id="${id}_container"></div>
</div>
