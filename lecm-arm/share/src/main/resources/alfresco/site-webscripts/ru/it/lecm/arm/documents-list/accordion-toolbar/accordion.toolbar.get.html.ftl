<#include "/org/alfresco/components/component.head.inc">
<!-- Data List Toolbar -->
<@script type="text/javascript" src="${page.url.context}/scripts/lecm-arm/arm-accordion-toolbar.js"></@script>

<#assign id = args.htmlid>
<script type="text/javascript">
(function(){
    function init() {
        new LogicECM.module.ARM.AccordionToolbar("${id}").setMessages(${messages});
    }

    YAHOO.util.Event.onDOMReady(init);
})();
//<![CDATA[
//]]></script>

<#import "/ru/it/lecm/base-share/components/base-components.ftl" as comp/>
<div class="accordion-toolbar">
	<@comp.baseToolbar id true false false>
	<div class="new-row">
	    <span id="${id}-newDocumentButton" class="yui-button yui-push-button">
	       <span class="first-child">
	          <button type="button">${msg("button.new-row")}</button>
	       </span>
	    </span>
	</div>
	</@comp.baseToolbar>
</div>
