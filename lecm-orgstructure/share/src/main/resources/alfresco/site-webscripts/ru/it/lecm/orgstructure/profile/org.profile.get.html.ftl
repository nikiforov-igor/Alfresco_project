<#assign  id = args.htmlid/>

<script type="text/javascript">//<![CDATA[
var Dom = YAHOO.util.Dom,
    Connect = YAHOO.util.Connect,
    Event = YAHOO.util.Event;
var organizationRef = LogicECM.module.OrgStructure.PROFILE_SETTINGS.nodeRef;

function drawForm(nodeRef){
    Alfresco.util.Ajax.request(
        {
            url:Alfresco.constants.URL_SERVICECONTEXT + "components/form",
            dataObj:{
                htmlid:"OrganizationMetadata-" + nodeRef,
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
                    var forms = Dom.get('OrganizationMetadata-' + organizationRef + '-form');
                    // Form definition
                    var form = new Alfresco.forms.Form('OrganizationMetadata-' + organizationRef + '-form');
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
    /*var  sUrl = Alfresco.constants.PROXY_URI + "/lecm/orgstructure/api/getOrganization";
    var callback = {
        success:function (oResponse) {
            var oResults = eval("(" + oResponse.responseText + ")");
            if (oResults != null) {
                organizationRef = oResults.nodeRef;
                drawForm(organizationRef);
            }
        },
        failure:function (oResponse) {
            Alfresco.util.PopupManager.displayMessage(
                    {
                        text:"Не удалось загрузить данные об Организации. Попробуйте обновить страницу."
                    });
        },
        argument:{
        }
    };
    YAHOO.util.Connect.asyncRequest('GET', sUrl, callback);*/
    drawForm(LogicECM.module.OrgStructure.PROFILE_SETTINGS.nodeRef);
}

Event.onDOMReady(init);
//]]></script>

<div id="${id}">
    <div id="${id}-content"></div>
</div>