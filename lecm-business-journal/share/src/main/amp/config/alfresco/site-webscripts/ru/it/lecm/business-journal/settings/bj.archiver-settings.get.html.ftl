<#assign  id = args.htmlid/>

<script type="text/javascript">//<![CDATA[
var Dom = YAHOO.util.Dom,
    Connect = YAHOO.util.Connect,
    Event = YAHOO.util.Event;
var organizationRef;

function drawForm(nodeRef){
	var nodeRefId = nodeRef.replace("workspace://SpacesStore/","");
    Alfresco.util.Ajax.request(
        {
            url:Alfresco.constants.URL_SERVICECONTEXT + "components/form",
            dataObj:{
                htmlid:"ArchiverSettingsMetadata-" + nodeRefId,
                itemKind:"node",
                itemId:nodeRef,
                formId:"${id}",
                mode:"edit",
                submitType:"json",
                showSubmitButton:"true"
            },
            successCallback:{
                fn:function(response){
                    var formEl = Dom.get("${id}-content");
                    formEl.innerHTML = response.serverResponse.responseText;
                    Dom.setStyle("${id}-footer", "opacity", "1");
                    var forms = Dom.get('ArchiverSettingsMetadata-' + nodeRefId + '-form');
                    // Form definition
                    var form = new Alfresco.forms.Form('ArchiverSettingsMetadata-' + nodeRefId + '-form');
                    form.ajaxSubmit = true;
                    form.setAJAXSubmit(true,
                            {
                                successCallback: {
                                    fn: function () {
                                        Alfresco.util.PopupManager.displayMessage(
                                                {
                                                    text:"Данные обновлены"
                                                });
                                    },
                                    scope: this
                                },
                                failureCallback: {
                                    fn: function () {
                                        Alfresco.util.PopupManager.displayMessage(
                                                {
                                                    text:"Не удалось обновить данные"
                                                });
                                    },
                                    scope: this
                                }
                            });
                    form.setSubmitAsJSON(true);
                    form.setShowSubmitStateDynamically(true, false);
                    // Initialise the form
                    form.init();
                }
            },
            failureMessage:"message.failure",
            execScripts:true
        });
}

function init() {
    drawForm(LogicECM.module.BusinessJournal.ARCHIVER_SETTINGS_REF);
}

function loadResources() {
    LogicECM.module.Base.Util.loadResources([], [
        'css/lecm-business-journal/bj-archiver-settings.css'
    ], init);
}

Event.onDOMReady(loadResources);

//]]></script>

<div id="${id}" class="bj-archiver-settings">
    <div id="${id}-content"></div>
</div>