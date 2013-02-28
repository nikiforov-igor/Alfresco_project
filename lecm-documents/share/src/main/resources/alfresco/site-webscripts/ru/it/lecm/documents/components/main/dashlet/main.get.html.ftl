<#assign id = args.htmlid>

<script type="text/javascript">
    //<![CDATA[
    (function() {
        var Dom = YAHOO.util.Dom,
            Event = YAHOO.util.Event,
            Selector = YAHOO.util.Selector;
        var container;

        function drawForm(nodeRef){
            Alfresco.util.Ajax.request(
                    {
                        url:Alfresco.constants.URL_SERVICECONTEXT + "components/form",
                        dataObj:{
                            htmlid:"DocumentMetadata-" + nodeRef,
                            itemKind:"node",
                            itemId:nodeRef,
                            formId:"${id}",
                            mode:"view"
                        },
                        successCallback:{
                            fn:function(response){
                                container.innerHTML = response.serverResponse.responseText;
                            }
                        },
                        failureMessage:"message.failure",
                        execScripts:true
                    });
        }

        function init() {
            new Alfresco.widget.DashletResizer("${id}", "${instance.object.id}");
            new Alfresco.widget.DashletTitleBarActions("${id?html}").setOptions({
                actions: [
                    {
                        cssClass: "help",
                        bubbleOnClick: {
                            message: "${msg("dashlet.help")?js_string}"
                        },
                        tooltip: "${msg("dashlet.help.tooltip")?js_string}"
                    }
                ]
            });

            container = Dom.get('${id}_results');
            drawForm("${nodeRef}");
        }
        Event.onDOMReady(init);
    })();
    //]]>
</script>

<div class="dashlet document">
    <div class="title">${msg("label.title")}</div>
    <div class="body scrollableList" id="${id}_results"></div>
</div>