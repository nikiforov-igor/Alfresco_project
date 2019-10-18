<#assign id = args.htmlid?js_string>
<#assign formId = "contract-unclosed-stage"/>
<script type="text/javascript">
    //<![CDATA[
    (function() {
        var Dom = YAHOO.util.Dom,
            Event = YAHOO.util.Event;

        var stageViewDialog = null;

        function hideStageDialog() {
            stageViewDialog.hide();
        }

        function openPreview(nodeRef) {
            Alfresco.util.Ajax.request(
                    {
                        url: Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/form",
                        dataObj: {
                            htmlid: nodeRef.replace("workspace://SpacesStore/", "").replace("-", ""),
                            itemKind: "node",
                            itemId: nodeRef,
                            formId: "${formId}",
                            mode: "view",
                            setId: "common"
                        },
                        successCallback: {
                            fn: function (response) {
                                var formEl = Dom.get("${formId}-content");
                                formEl.innerHTML = response.serverResponse.responseText;
                                if (stageViewDialog != null) {
                                    Dom.setStyle("${formId}", "display", "block");
                                    var message = "${msg("logicecm.view")}";
                                    var titleElement = Dom.get("${formId}-head");
                                    if (titleElement) {
                                        titleElement.innerHTML = message;
                                    }
                                    stageViewDialog.show();
                                }
                            }
                        },
                        failureMessage: "message.failure",
                        execScripts: true
                    });
        }

        function drawStages(nodeRef){
            var url = Alfresco.constants.PROXY_URI + "/lecm/contracts/stages?nodeRef=" + nodeRef;
            var callback = {
                success:function (oResponse) {
                    var resultMessage = "";
                    var oResults = eval("(" + oResponse.responseText + ")");
                    if (oResults != null && oResults.length > 0) {
                        resultMessage = "<div style='margin-bottom: 15px;'><b>${msg('message.contract.stages.not.closed')}</b></div>"
                        for (var rowIndex in oResults) {
                            var row = oResults[rowIndex];
                            resultMessage += "<div style='padding-left: 10px; margin-bottom: 5px;'>${msg('label.stage')} № " + row.number + ", <i><a href='#' onclick='return false;' id='preview_" + row.nodeRef + "'>" + row.title + "</a></i></div>"
                        }
                        resultMessage += "<div style='margin-top: 15px;'><b>${msg('message.contract.execution.close.quest')}</b></div>"
                        var container = Dom.get('${id}_container');
                        container.innerHTML = resultMessage;
                        for (var rowIndex in oResults) {
                            var row = oResults[rowIndex];
                            Dom.get('preview_' + row.nodeRef).onclick = function() {
                                openPreview(this.nodeRef);
                            }.bind(row);
                        }
                    } else {
                        resultMessage += "<div>${msg('ru.it.contract.executed')}</div>"
                        var container = Dom.get('${id}_container');
                        container.innerHTML = resultMessage;
                    }

                },
                argument:{
                    parent:this
                },
                timeout: 60000
            };
            YAHOO.util.Connect.asyncRequest('GET', url, callback);
        }

        function init() {
            stageViewDialog = Alfresco.util.createYUIPanel("${formId}",
                    {
                        width: "50em"
                    });
            YAHOO.Bubbling.on("hidePanel", hideStageDialog);
            Alfresco.util.createYUIButton(null, "", hideStageDialog, { label: "${msg("button.close")}", title: "${msg("button.close")}" }, "${formId}-close");
            drawStages("${args.nodeRef}");
        }
        Event.onContentReady("${id}_container", init, true);
    })();
    //]]>
</script>
<div id="${id}_container"></div>
<div id="${formId}" class="yui-panel">
    <div id="${formId}-head" class="hd">${msg("logicecm.view")}</div>
    <div id="${formId}-body" class="bd">
        <div id="${formId}-content"></div>
        <div class="bdft">
            <span id="${formId}-close" class="yui-button yui-push-button">
                <span class="first-child">
                    <button type="button" tabindex="0">${msg("button.close")}</button>
                </span>
            </span>
        </div>
    </div>
</div>
