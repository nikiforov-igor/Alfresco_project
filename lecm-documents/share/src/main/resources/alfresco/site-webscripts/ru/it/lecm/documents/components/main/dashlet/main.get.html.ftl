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
                            htmlid: htmlId,
                            itemKind: "node",
                            itemId:nodeRef,
                            formId: formId,
                            mode:"view"
                        },
                        successCallback:{
                            fn:function(response){
                                if (container != null) {
                                    container.innerHTML = response.serverResponse.responseText;
                                }
                            }
                        },
                        failureMessage:"message.failure",
                        execScripts:true,
                        htmlId:htmlId
                    });
        }

        var viewForm = new YAHOO.util.CustomEvent("onDashletConfigure");
        viewForm.subscribe(expandDashlet, null, true);

        var documentComponentBase = new LogicECM.DocumentComponentBase("${id}").setOptions({
            title:"${msg('label.title')}"
        });

        function expandDashlet() {
            documentComponentBase.expandView(container.innerHTML);
        }

        function init() {
            new Alfresco.widget.DashletResizer("${id}", "document.main.dashlet");
            new Alfresco.widget.DashletTitleBarActions("${id}").setOptions({
                actions: [
                    {
                        cssClass: "view",
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
            drawForm("${nodeRef}",'${id}_results', "document");
        }
        Event.onDOMReady(init);
    })();
    //]]>
</script>

<div class="dashlet document">
    <div class="title">${msg("label.title")}</div>
    <div class="body scrollableList" id="${id}_results"></div>
</div>