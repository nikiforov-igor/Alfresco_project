<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>
<#assign controlId = fieldHtmlId + "-cntrl">
<#assign containerId = fieldHtmlId + "-container">
<#assign viewFormId = fieldHtmlId + "-view">
<#assign value = field.value>
<#assign formId = "form-history-status">

<script type="text/javascript">
    //    <![CDATA[
    var     Dom = YAHOO.util.Dom,
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
                        nodeRef: "${form.arguments.itemId}",
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

<div class="form-field">
    <div class="viewmode-field">
        <span class="viewmode-label">${field.label?html}:</span>
        <span class="viewmode-value">
            <#if field.value == "">
                ${msg("form.control.novalue")}
            <#else>
                <a onclick="showViewStatusDialog();" href="javascript:void(0);" id="${fieldHtmlId}">${field.value}</a>
            </#if>
        </span>
    </div>
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
</div>