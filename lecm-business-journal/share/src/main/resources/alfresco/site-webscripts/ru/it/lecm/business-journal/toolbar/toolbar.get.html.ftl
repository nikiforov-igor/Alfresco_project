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
function init() {
	new LogicECM.module.BusinessJournal.Toolbar("${id}").setMessages(${messages}).setOptions({
		bubblingLabel:"${bubblingLabel!''}"
	});
}
YAHOO.util.Event.onDOMReady(init);
//]]></script>

<@comp.baseToolbar id buttons true true>
<div id="toolbar-archivePanel" class="yui-panel" style="display: none">
    <div id="toolbar-archivePanel-head" class="hd">${msg("business-journal.archive")}</div>
    <div id="toolbar-archivePanel-body" class="bd">
        <div id="toolbar-archivePanel-content" style="padding:20 px; margin:10 px">
            <label for="archiveDate">${msg("archive.date")}</label>
            <input type="text" name="-" id="archiveDate" value="20" maxlength="3"/>
        </div>
        <div class="bdft">
	    <span id="toolbar-archivePanel-archiveButton" class="yui-button yui-push-button">
        <span class="first-child">
            <button type="button" tabindex="0">${msg("button.archive")}</button>
        </span>
        </span>
        <span id="toolbar-archivePanel-cancelButton" class="yui-button yui-push-button">
        <span class="first-child">
            <button type="button" tabindex="1">${msg("button.close")}</button>
        </span>
        </span>
        </div>
    </div>
</div>
<div class="archive-row">
	<span id="${id}-archiveByDateButton" class="yui-button yui-push-button">
       <span class="first-child">
          <button type="button" title="${msg("button.archive")}">&nbsp;</button>
       </span>
    </span>
</div>
<div class="divider"></div>
<div class="archive-row">
	<span id="${id}-archiveButton" class="yui-button yui-push-button">
       <span class="first-child">
          <button type="button" title="${msg("button.archive")}">&nbsp;</button>
       </span>
    </span>
</div>
<div class="exportcsv">
    <span id="${id}-exportCsvButton" class="yui-button yui-push-button">
        <span class="first-child">
            <button type="button" title="${msg('button.export-csv')}">&nbsp;</button>
        </span>
    </span>
</div>
</@comp.baseToolbar>