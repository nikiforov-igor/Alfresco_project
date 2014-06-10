<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>
<#macro showDialog formId="status-history-form">

<script type="text/javascript">//<![CDATA[
(function() {
	function init() {
        LogicECM.module.Base.Util.loadScripts([
            'scripts/lecm-documents/document-status-dialog-history.js'
		], createStatusHistory);
	}
	function createStatusHistory(){
		new LogicECM.module.DocumentStatusHistory("${formId}").setMessages(${messages});
	}
	YAHOO.util.Event.onDOMReady(init);
})();
//]]></script>

<div id="${formId}">
	<div id="${formId}-panel" class="yui-panel hidden1">
	    <div id="${formId}-panel-head" class="hd">${msg("form.logicecm.view")}</div>
	    <div id="${formId}-panel-body" class="bd">
	        <div id="${formId}-panel-content" class="status-history"></div>
        <div class="bdft">
		        <span id="${formId}-panel-cancel" class="yui-button yui-push-button">
        <span class="first-child">
			            <button type="button" tabindex="0">${msg("button.close")}</button>
        </span>
        </span>
        </div>
    </div>
</div>
</div>
</#macro>


