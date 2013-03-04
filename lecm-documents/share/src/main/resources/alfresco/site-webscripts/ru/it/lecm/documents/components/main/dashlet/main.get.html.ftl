<#assign id = args.htmlid?js_string>
<script type="text/javascript">
    //<![CDATA[
    (function() {
        var Dom = YAHOO.util.Dom,
            Event = YAHOO.util.Event,
            Selector = YAHOO.util.Selector;
        var container;

        function drawForm(nodeRef, htmlId, formId){
            Alfresco.util.Ajax.request(
                    {
                        url:Alfresco.constants.URL_SERVICECONTEXT + "components/form",
                        dataObj:{
                            htmlid: htmlId + "-" + nodeRef,
                            itemKind: "node",
                            itemId:nodeRef,
                            formId: formId,
                            mode:"view"
                        },
                        successCallback:{
                            fn:function(response){
                                if (arguments[0].config.htmlId == "DocumentMain") {
                                    var contain = Dom.get("custom-dashlet-content");
                                    if (contain != null) {
                                        contain.innerHTML = response.serverResponse.responseText;
                                        Dom.get("custom-dashlet-title").innerHTML = "${msg("label.title")}";
                                        Dom.setStyle("main-region", "display", "none");
                                        Dom.setStyle("custom-dashlet", "display", "block");
                                    }
                                } else {
                                    container.innerHTML = response.serverResponse.responseText;
                                }
                            }
                        },
                        failureMessage:"message.failure",
                        execScripts:true,
                        htmlId:htmlId
                    });
        }

        function showViewForm() {
            drawForm("${nodeRef}","DocumentMain","${id}");
        }

        var viewForm = new YAHOO.util.CustomEvent("onDashletConfigure");
        viewForm.subscribe(showViewForm, null, true);

        function init() {
            new Alfresco.widget.DashletResizer("${id}", "document.main.dashlet");
            new Alfresco.widget.DashletTitleBarActions("${id}").setOptions({
                actions: [
                    {
                        cssClass: "view",
                        <#--linkOnClick:  Alfresco.constants.URL_PAGECONTEXT +"view-metadata?nodeRef=" + "${nodeRef}",-->
                        eventOnClick: viewForm,
                        tooltip: "${msg("dashlet.view.tooltip")?js_string}"
                    },
                    {
                        cssClass: "edit",
                        linkOnClick:  Alfresco.constants.URL_PAGECONTEXT +"edit-metadata?nodeRef=" + "${nodeRef}",
                        tooltip: "${msg("dashlet.edit.tooltip")?js_string}"
                    },
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
            drawForm("${nodeRef}","DocumentMetadata", "document");
        }
        Event.onDOMReady(init);
    })();
    //]]>
</script>

<div class="dashlet document">
    <div class="title">${msg("label.title")}</div>
    <div class="body scrollableList" id="${id}_results"></div>
</div>