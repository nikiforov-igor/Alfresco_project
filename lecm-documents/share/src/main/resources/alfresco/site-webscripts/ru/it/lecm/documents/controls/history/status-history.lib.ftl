<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>
<#macro showDialog formId="status-history-form" nodeRef="">
<script type="text/javascript">//<![CDATA[
//    <![CDATA[
var Dom = YAHOO.util.Dom,
        Event = YAHOO.util.Event,
        Bubbling = YAHOO.Bubbling;
var viewStatusDialog,
        showStatusDialog;
function showViewStatusDialog() {
    var id = Alfresco.util.generateDomId();
    var htmlid = "${formId}-" + id;
    Alfresco.util.Ajax.request({
        url: Alfresco.constants.URL_SERVICECONTEXT + "lecm/components/document/history-datagrid",
        dataObj: {
            nodeRef: "${nodeRef}",
            htmlid: htmlid,
            dataSource: "lecm/business-journal/ds/getStatusHistory"
        },
        successCallback: {
            fn: function (response) {
                showStatusDialog = true;
                var text = response.serverResponse.responseText;
                var formEl = Dom.get("${formId}-content");
                formEl.innerHTML = text;
            },
            scope: this
        },
        failureMessage: function () {
            alert("Данные не загружены");
        },
        scope: this,
        execScripts: true
    });
}
function createStatusDialog() {
    viewStatusDialog = Alfresco.util.createYUIPanel("${formId}",
            {
                width: "50em"
            });
    Dom.setStyle("${formId}", "display", "none");
}
function onSearchSuccess() {
    if (showStatusDialog) {
        showStatusDialog = false;
        if (viewStatusDialog != null) {
            Dom.setStyle("${formId}", "display", "block");
            viewStatusDialog.show();
        }
    }
}
function hideViewStatusDialog() {
    if (viewStatusDialog != null) {
        viewStatusDialog.hide();
        Dom.setStyle("${formId}", "display", "none");
    }
}
function init() {
    createStatusDialog();
    Bubbling.on("onSearchSuccess", onSearchSuccess, this);
    Bubbling.on("hidePanel", hideViewStatusDialog);
}

Event.onContentReady("${formId}", init);
//]]>
</script>

<div id="${formId}" class="yui-panel">
    <div id="${formId}-head" class="hd">${msg("form.logicecm.view")}</div>
    <div id="${formId}-body" class="bd">
        <div id="${formId}-content"></div>
        <div class="bdft">
        <span id="${formId}-cancel" class="yui-button yui-push-button">
        <span class="first-child">
        <button type="button" tabindex="0" onclick="hideViewStatusDialog();">${msg("button.close")}</button>
        </span>
        </span>
        </div>
    </div>
</div>

</#macro>


