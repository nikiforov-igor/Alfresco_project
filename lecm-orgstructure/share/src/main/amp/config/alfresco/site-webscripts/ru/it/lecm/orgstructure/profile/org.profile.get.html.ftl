<#assign  id = args.htmlid/>

<script type="text/javascript">//<![CDATA[
if (typeof LogicECM == "undefined" || !LogicECM) {
	var LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.OrgStructure = LogicECM.module.OrgStructure || {};
LogicECM.module.OrgStructure.PROFILE_SETTINGS =  LogicECM.module.OrgStructure.PROFILE_SETTINGS || {};
(function() {
	var Dom = YAHOO.util.Dom,
    Connect = YAHOO.util.Connect,
    Event = YAHOO.util.Event;
	var organizationRef = LogicECM.module.OrgStructure.PROFILE_SETTINGS.nodeRef.replace(/\//g, "_");

	function drawForm(nodeRef){
	    Alfresco.util.Ajax.request(
	        {
	            url:Alfresco.constants.URL_SERVICECONTEXT + "components/form",
	            dataObj:{
	                htmlid:"OrganizationMetadata-" + nodeRef.replace(/\//g, "_"),
	                itemKind:"node",
	                itemId:nodeRef,
	                formId:"${id}",
	                mode: LogicECM.module.OrgStructure.IS_ENGINEER ? "edit" : "view",
	                submitType:"json",
	                showSubmitButton:LogicECM.module.OrgStructure.IS_ENGINEER ? "true" : "false"
	            },
	            successCallback:{
	                fn:function(response){
	                    var formEl = Dom.get("${id}-content");
	                    formEl.innerHTML = response.serverResponse.responseText;
	                    Dom.setStyle("${id}-footer", "opacity", "1");
	                    if (LogicECM.module.OrgStructure.IS_ENGINEER) {
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
	                }
	            },
	            failureMessage:"message.failure",
	            execScripts:true
	        });
	}
	
	function init() {
	    drawForm(LogicECM.module.OrgStructure.PROFILE_SETTINGS.nodeRef);
	}
	
	Event.onDOMReady(init);
})();
//]]></script>

<div id="${id}">
    <div id="${id}-content"></div>
</div>