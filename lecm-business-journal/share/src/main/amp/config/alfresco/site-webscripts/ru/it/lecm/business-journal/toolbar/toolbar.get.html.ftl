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
(function(){
	function createToolbar() {
		new LogicECM.module.BusinessJournal.Toolbar("${id}").setMessages(${messages}).setOptions({
			bubblingLabel:"${bubblingLabel!''}"
		});
	}

    function init() {
        LogicECM.module.Base.Util.loadResources([
            'scripts/lecm-base/components/lecm-toolbar.js',
            'scripts/lecm-business-journal/business-journal-toolbar.js'
        ], [
            'components/data-lists/toolbar.css',
            'css/lecm-business-journal/business-journal-toolbar.css'
        ], createToolbar);
    }

    YAHOO.util.Event.onDOMReady(init);
})();
//]]></script>

<@comp.baseToolbar id buttons searchBlock exSearch>
<div id="toolbar-archivePanel" class="yui-panel form-container hidden1">
    <div id="toolbar-archivePanel-head" class="hd">${msg("business-journal.archive")}</div>
    <div id="toolbar-archivePanel-body" class="bd">
        <div id="toolbar-archivePanel-content" class="form-fields">

            <#--date calendar control-->
            <#assign dateFieldId = "archiveDate">
            <#assign controlId = dateFieldId + "-cntrl">

            <script type="text/javascript">//<![CDATA[
            (function () {
                new Alfresco.DatePicker("${controlId}", "${dateFieldId}").setOptions({
                        currentValue:"${defaultArchiveToDate!''}",
                        showTime: false,
                        mandatory: true
                    }).setMessages(
                ${messages}
                );
            })();
            //]]></script>

            <input id="${dateFieldId}" type="hidden" name="-" value="" />

            <label for="${controlId}-date" class="archive-label">${msg("archive.date")}:</label>
            <input id="${controlId}-date" name="-" type="text" class="date-entry" tabindex="0"/>

            <a id="${controlId}-icon"><img src="${url.context}/res/components/form/images/calendar.png" class="datepicker-icon" tabindex="0"/></a>

            <div id="${controlId}" class="datepicker hidden1"></div>
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

<div class="archive-rows">
	<span id="${id}-archiveByDateButton" class="yui-button yui-push-button">
       <span class="first-child">
          <button type="button" title="${msg("button.archive")}">${msg("button.archive")}</button>
       </span>
    </span>
</div>
<div class="divider"></div>
<div class="exportcsv">
    <span id="${id}-exportCsvButton" class="yui-button yui-push-button">
        <span class="first-child">
            <button type="button" title="${msg('button.export-csv')}">&nbsp;</button>
        </span>
    </span>
</div>
</@comp.baseToolbar>