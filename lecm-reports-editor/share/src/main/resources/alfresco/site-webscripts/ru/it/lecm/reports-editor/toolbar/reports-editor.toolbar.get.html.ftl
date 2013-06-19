<#assign id = args.htmlid>

<#assign searchBlock = false/>
<#assign exSearch = false/>
<#assign buttons = true/>

<#import "/ru/it/lecm/base-share/components/base-components.ftl" as comp/>

<script type="text/javascript">//<![CDATA[
function init() {
    new LogicECM.module.ReportsEditor.Toolbar("${id}").setMessages(${messages}).setOptions({
        bubblingLabel: "reports",
        itemType: "lecm-rpeditor:reportDescriptor",
        destination:LogicECM.module.ReportsEditor.SETTINGS.reportsContainer,
        newRowDialogTitle:"label.create-report-dialog"
    });
}
YAHOO.util.Event.onDOMReady(init);
//]]></script>
<@comp.baseToolbar id buttons searchBlock exSearch>
<div class="new-row">
        <span id="${id}-newReportButton" class="yui-button yui-push-button">
           <span class="first-child">
              <button type="button" title="${msg("button.new-row")}">${msg("button.new-row")}</button>
           </span>
        </span>
</div>
</@comp.baseToolbar>