<#assign id = args.htmlid>

<#import "/ru/it/lecm/base-share/components/base-components.ftl" as comp/>

<script type="text/javascript">//<![CDATA[
function init() {
    <#--new LogicECM.module.Subscriptions.Toolbar("${id}").setMessages(${messages}).setOptions({-->
	    <#--bubblingLabel: "${bubblingLabel!''}",-->
        <#--searchActive: true-->
    <#--});-->
}
YAHOO.util.Event.onDOMReady(init);
//]]></script>
<@comp.baseToolbar id true false false>
	<div class="new-row">
        <span id="${id}-newFowmButton" class="yui-button yui-push-button">
           <span class="first-child">
              <button type="button" title="${msg("button.new-form")}">${msg("button.new-form")}</button>
           </span>
        </span>
	</div>
	<div class="divider"></div>
	<div class="deploy-forms">
        <span id="${id}-deployFormsButton" class="yui-button yui-push-button">
           <span class="first-child">
              <button type="button" title="${msg("button.deploy-forms")}">${msg("button.deploy-forms")}</button>
           </span>
        </span>
	</div>
</@comp.baseToolbar>