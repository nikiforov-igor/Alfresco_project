<#include "/org/alfresco/components/component.head.inc">
<@script type="text/javascript" src="${page.url.context}/scripts/lecm-arm/arm-toolbar.js"></@script>

<#assign id = args.htmlid>
<script type="text/javascript">//<![CDATA[
function init() {
    new LogicECM.module.ARM.Toolbar("${id}").setMessages(${messages});
}

YAHOO.util.Event.onDOMReady(init);
//]]></script>

<#import "/ru/it/lecm/base-share/components/base-components.ftl" as comp/>
<@comp.baseToolbar id true false false>
    <div class="create-row">
        <span id="${id}-newRowButton" class="yui-button yui-push-button">
            <span class="first-child">
                <button type="button">${msg('lecm.arm.add-element')}</button>
            </span>
        </span>
    </div>
	<div class="create-row">
        <span id="${id}-newReportsNodeButton" class="yui-button yui-push-button">
            <span class="first-child">
                <button type="button">${msg('lecm.arm.reports-node.add')}</button>
            </span>
        </span>
    </div>
	<div class="delete-node">
        <span id="${id}-deleteNodeButton" class="yui-button yui-push-button">
            <span class="first-child">
                <button type="button">${msg('lecm.arm.delete-element')}</button>
            </span>
        </span>
    </div>
</@comp.baseToolbar>
