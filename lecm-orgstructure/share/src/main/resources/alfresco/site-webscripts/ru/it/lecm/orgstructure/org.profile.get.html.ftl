<#assign  id = args.htmlid/>

<script type="text/javascript">//<![CDATA[
var Dom = YAHOO.util.Dom,
    Connect = YAHOO.util.Connect,
    Event = YAHOO.util.Event;
var organizationRef;

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
                showSubmitButton:"false"
            },
            successCallback:{
                fn:function(response){
                    var formEl = Dom.get("${id}-content");
                    formEl.innerHTML = response.serverResponse.responseText;
                    Dom.setStyle("${id}-footer", "opacity", "1");
                    setActions(formEl);
                    LogicECM.module.Base.Util.setHeight();
                }
            },
            failureMessage:"message.failure",
            execScripts:true
        });
}

function init() {
    var  sUrl = Alfresco.constants.PROXY_URI + "/lecm/orgstructure/api/getOrganization";
    var callback = {
        success:function (oResponse) {
            var oResults = eval("(" + oResponse.responseText + ")");
            if (oResults != null) {
                organizationRef = oResults.nodeRef;
                drawForm(organizationRef);
            }
        },
        failure:function (oResponse) {
            alert("Не удалось загрузить данные об Организации. Попробуйте обновить страницу.");
        },
        argument:{
        }
    };
    YAHOO.util.Connect.asyncRequest('GET', sUrl, callback);
}

function saveOrganization() {
    var nodeRef = new Alfresco.util.NodeRef(organizationRef);
    Connect.setForm('OrganizationMetadata-' + organizationRef + '-form', true);
    var url = Alfresco.constants.URL_CONTEXT + "/proxy/alfresco/api/node/" + nodeRef.uri + "/formprocessor";
    var organizationSaveCallBack = {
        upload:function(o){
            Alfresco.util.PopupManager.displayMessage({
                    text:"Данные обновлены"
                });
        }
    };
    Connect.asyncRequest(Alfresco.util.Ajax.GET, url, organizationSaveCallBack);
}
function setActions(form) {
    Dom.getElementsByClassName('set-bordered-panel', 'div', form, function() {
        var panel = this;
        var header = Dom.getElementsByClassName('set-bordered-panel-heading', 'div', panel);
        var body = Dom.getElementsByClassName('set-bordered-panel-body', 'div', panel);
        var height = Dom.getStyle(body, 'height');

        Dom.setStyle(body, 'height', height);
        Event.addListener(header, 'click', function() {
            var hasClass = Dom.hasClass(body, 'closed');

            if (typeof(hasClass) == 'object') {
                hasClass = hasClass[0] || false;
            }
            if (hasClass) {
                Dom.setStyle(body, 'height', height);
                Dom.removeClass(body, 'closed');
            } else {
                Dom.setStyle(body, 'height', 0);
                Dom.addClass(body, 'closed');
            }
            setTimeout(function() {
                LogicECM.module.Base.Util.setHeight();
            }, 350); // height transition duration = 300
        });
    });
}
YAHOO.util.Event.onDOMReady(init);
//]]></script>

<div id="${id}">
    <div id="${id}-content"></div>
    <div id="${id}-footer" class="org-profile-ft">
        <button id="${id}-save" tabindex="0" onclick="saveOrganization();">${msg("button.save")}</button>
    </div>
</div>