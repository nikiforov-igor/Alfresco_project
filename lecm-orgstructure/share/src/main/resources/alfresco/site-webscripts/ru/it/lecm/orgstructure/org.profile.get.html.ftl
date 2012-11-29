<#assign  id = args.htmlid/>

<script type="text/javascript">//<![CDATA[
var Dom = YAHOO.util.Dom,
    Connect = YAHOO.util.Connect,
    Event = YAHOO.util.Event,
    Selector = YAHOO.util.Selector;
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
    var tabs = new YAHOO.widget.TabView(Dom.getElementsByClassName('yui-navset', 'div', form)[0]);
    var ul = Dom.getElementsByClassName('yui-nav', 'ul', form)[0];
    var links = Selector.query('a', ul, false);

    Event.addListener(links, 'click', function() {
        setTimeout(function() {
            LogicECM.module.Base.Util.setHeight();
        }, 10);
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