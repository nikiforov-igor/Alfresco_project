<#assign  id = args.htmlid/>

<script type="text/javascript">//<![CDATA[
LogicECM.module = LogicECM.module || {};
LogicECM.module.OrgStructure = LogicECM.module.OrgStructure || {};
LogicECM.module.OrgStructure.PROFILE_SETTINGS =  LogicECM.module.OrgStructure.PROFILE_SETTINGS || {};
(function() {
	var Dom = YAHOO.util.Dom,
    Connect = YAHOO.util.Connect,
    Event = YAHOO.util.Event;
	function drawForm(nodeRef){
	    Alfresco.util.Ajax.request(
	        {
	            url:Alfresco.constants.URL_SERVICECONTEXT + "components/form",
	            dataObj:{
	                htmlid:"${id}-OrganizationMetadata",
	                itemKind:"node",
	                itemId:nodeRef,
	                formId:"${id}",
	                mode: LogicECM.module.OrgStructure.IS_ENGINEER ? "edit" : "view",
	                submitType:"json",
	                showSubmitButton:LogicECM.module.OrgStructure.IS_ENGINEER ? "true" : "false",
					showCaption: false
	            },
	            successCallback:{
	                fn:function(response){
	                    var formEl = Dom.get("${id}-content");
	                    formEl.innerHTML = response.serverResponse.responseText;
	                    Dom.setStyle("${id}-footer", "opacity", "1");
	                    if (LogicECM.module.OrgStructure.IS_ENGINEER) {
	                        var forms = Dom.get('${id}-OrganizationMetadata-form');
	                        // Form definition
	                        var form = new Alfresco.forms.Form('${id}-OrganizationMetadata-form');
	                        form.ajaxSubmit = true;
	                        form.setAJAXSubmit(true,
	                                {
	                                    successCallback: {
	                                        fn: function () {
	                                            Alfresco.util.PopupManager.displayMessage(
	                                                    {
	                                                        text:"${msg('message.data.updated')}"
	                                                    });
	                                        },
	                                        scope: this
	                                    },
	                                    failureCallback: {
	                                        fn: function () {
	                                            Alfresco.util.PopupManager.displayMessage(
	                                                    {
	                                                        text:"${msg('message.data.updated.fail')}"
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