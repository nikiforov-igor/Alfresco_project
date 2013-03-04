<#assign id = args.htmlid>
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
                                    var contain = Dom.get("custom-dashlet");
                                    if (contain != null) {
                                        contain.innerHTML = response.serverResponse.responseText;

                                        Dom.setStyle("main-content-region", "display", "none");
                                        Dom.setStyle("custom-dashlet", "display", "block");
                                        var oButton = new YAHOO.widget.Button({
                                            id:"cancelDocumentButton",
                                            type:"button",
                                            label:"${msg("dashlet.button.cancel")?js_string}",
                                            container:contain
                                        });
                                        oButton.on("click", hiddenViewForm);
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

        function hiddenViewForm(){
            Dom.setStyle("custom-dashlet", "display", "none");
            Dom.setStyle("main-content-region", "display", "block");
        }
        function showViewForm() {
            drawForm("${nodeRef}","DocumentMain","${id}");
        }
        var viewForm = new YAHOO.util.CustomEvent("onDashletConfigure");
        viewForm.subscribe(showViewForm, null, true);
        function init() {
            new Alfresco.widget.DashletResizer("${id}", "${instance.object.id}");
            new Alfresco.widget.DashletTitleBarActions("${id?html}").setOptions({
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