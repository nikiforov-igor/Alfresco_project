<#assign id = args.htmlid>

<#assign buttons = true/>
<#if showButtons??>
	<#assign buttons = showButtons/>
</#if>
<#assign searchBlock = true/>
<#if showSearchBlock??>
	<#assign searchBlock = showSearchBlock/>
</#if>
<#assign exSearch = false/>
<#if showExSearchBtn??>
	<#assign exSearch = showExSearchBtn/>
</#if>

<#assign newRowButtonLabel = "button.new-row"/>
<#if newRowLabel??>
	<#assign newRowButtonLabel = newRowLabel/>
</#if>

<#import "/ru/it/lecm/base-share/components/base-components.ftl" as comp/>

<script type="text/javascript">//<![CDATA[
(function() {
    function createToolbar() {
        var toolbar = new LogicECM.module.BusinessJournal.LoggerToolbar("${id}").setMessages(${messages}).setOptions({
            bubblingLabel:"${bubblingLabel!''}"
        });
    }

    function init() {
        LogicECM.module.Base.Util.loadResources([
            'scripts/lecm-base/components/lecm-toolbar.js',
            'scripts/lecm-business-journal/business-journal-loggertoolbar.js'
        ], [
            'components/data-lists/toolbar.css'
        ], createToolbar);
    }

    YAHOO.util.Event.onDOMReady(init);
})();
//]]></script>

<@comp.baseToolbar id buttons searchBlock exSearch>
<div class="turn-on-logging">
    <span id="${id}-turn-on-logging" class="yui-button yui-push-button">
        <span class="first-child">
            <button type="button" title="${msg('button.turn-on')}">${msg('button.turn-on')}</button>
        </span>
    </span>
</div>
<div class="turn-off-logging">
    <span id="${id}-turn-off-logging" class="yui-button yui-push-button">
        <span class="first-child">
            <button type="button" title="${msg('button.turn-off')}">${msg('button.turn-off')}</button>
        </span>
    </span>
</div>

</@comp.baseToolbar>