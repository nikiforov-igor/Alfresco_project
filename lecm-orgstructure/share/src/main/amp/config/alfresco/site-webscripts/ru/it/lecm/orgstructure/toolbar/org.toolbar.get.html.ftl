<@markup id="css">
	<@link rel="stylesheet" type="text/css" href="${url.context}/res/components/data-lists/toolbar.css" />
</@>
<@markup id="js">
	<@script type="text/javascript" src="${url.context}/res/scripts/lecm-base/components/lecm-toolbar.js"></@script>
	<@script type="text/javascript" src="${url.context}/res/scripts/lecm-orgstructure/orgstructure-toolbar.js"></@script>
</@>
<#assign id = args.htmlid>
<#assign importFormId = id + "-import-form">
<#assign importInfoFormId = id + "-import-info-form">
<#assign importErrorFormId = id + "-import-error-form">

<#assign buttons = true/>
<#if showButtons??>
	<#assign buttons = showButtons/>
</#if>
<#assign searchBlock = true/>
<#if showSearchBlock??>
	<#assign searchBlock = showSearchBlock/>
</#if>

<#assign showStructureBlock = false/>
<#if showStructure??>
    <#assign showStructureBlock = showStructure/>
</#if>

<#assign exSearch = false/>
<#if showExSearchBtn??>
	<#assign exSearch = showExSearchBtn/>
</#if>

<#assign newRowBtnType = "defaultActive"/>
<#if newRowButton??>
	<#assign newRowBtnType = newRowButton/>
</#if>

<#assign searchButtonsType = "defaultActive"/>
<#if searchButtons??>
	<#assign searchButtonsType = searchButtons/>
</#if>

<#assign newRowButtonLabel = "button.new-row"/>
<#if newRowLabel??>
	<#assign newRowButtonLabel = newRowLabel/>
</#if>

<#assign newUnitSpanId = "${id}-newRowButton"/>
<#assign exportUnitSpanId = "${id}-exportButton"/>
<#assign structureId = "${id}-structure"/>

<#assign showImportExport = false/>

<#import "/ru/it/lecm/base-share/components/base-components.ftl" as comp/>

<script type="text/javascript">//<![CDATA[
(function() {
	function init() {
		new LogicECM.module.OrgStructure.Toolbar("${id}").setMessages(${messages}).setOptions({
	        searchButtonsType: "${searchButtonsType?string}",
			bubblingLabel:"${bubblingLabel!''}",
	        newRowButtonType: "${newRowBtnType?string}",
            showImportXml: ${showImportExport?string}
		});
	}
	YAHOO.util.Event.onDOMReady(init);
})();
//]]></script>
<@comp.baseToolbar id buttons searchBlock exSearch>
    <#if isOrgEngineer>
    <div class="new-row">
        <span id="${newUnitSpanId}" class="yui-button yui-push-button">
           <span class="first-child">
              <button type="button">${msg(newRowButtonLabel)}</button>
           </span>
        </span>
    </div>
    <#if showImportExport>
            <div class="new-row">
                <span id="${exportUnitSpanId}" class="yui-button yui-push-button">
                   <span class="first-child">
                      <button type="button">${msg("button.export")}</button>
                   </span>
                </span>
            </div>

            <div class="import-xml"  title="${msg('button.import-xml')}">
                                <span id="${id}-importXmlButton" class="yui-button yui-push-button">
                                    <span class="first-child">
                                        <button type="button">${msg('button.import-xml')}</button>
                                    </span>
                                </span>
            </div>

            <div id="${importInfoFormId}" class="yui-panel">
                <div id="${importInfoFormId}-head" class="hd">${msg("title.import.info")}</div>
                <div id="${importInfoFormId}-body" class="bd">
                    <div id="${importInfoFormId}-content" class="import-info-content"></div>
                </div>
            </div>

            <div id="${importErrorFormId}" class="yui-panel">
                <div id="${importErrorFormId}-head" class="hd">${msg("title.import.info")}</div>
                <div id="${importErrorFormId}-body" class="bd">
                    <div id="${importErrorFormId}-content" class="import-info-content">
                        <div class="import-error-header">
                            <h3>${msg("import.failure")}</h3>
                            <a href="javascript:void(0);" id="${importErrorFormId}-show-more-link">${msg("import.failure.showMore")}</a>
                        </div>
                        <div id="${importErrorFormId}-more" class="import-error-more">
                            <div class="import-error-exception">
                            ${msg("import.failure.exception")}:
                                <div class="import-error-exception-content" id="${importErrorFormId}-exception">
                                </div>
                            </div>
                            <div class="import-error-stack-trace">
                            ${msg("import.failure.stack-trace")}:
                                <div class="import-error-stack-trace-content" id="${importErrorFormId}-stack-trace">
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <div id="${importFormId}" class="yui-panel">
                <div id="${importFormId}-head" class="hd">${msg("title.import")}</div>
                <div id="${importFormId}-body" class="bd">
                    <div id="${importFormId}-content">
                        <form method="post" id="${id}-import-xml-form" enctype="multipart/form-data">
                            <ul class="import-form">
                                <li>
                                    <label for="${importFormId}-import-file">${msg("label.import-file")}*</label>
                                    <input id="${importFormId}-import-file" type="file" name="file" accept=".xml,application/xml,text/xml">
                                </li>
                                <li>
                                    <label for="${importFormId}-chbx-ignore">${msg("label.ignore-errors")}</label>
                                    <input id="${importFormId}-chbx-ignore" type="checkbox" name="ignoreErrors" value="true"/>
                                </li>
                            </ul>
                            <div class="bdft">
                                <button id="${importFormId}-submit" disabled="true" tabindex="0">${msg("button.import-xml")}</button>
                                <button id="${importFormId}-cancel" tabindex="1">${msg("button.cancel")}</button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </#if>
    </#if>
    <#if showStructureBlock >
        <div class="print">
            <span id="${structureId}" class="yui-button yui-push-button">
               <span class="first-child">
                  <button type="button">${msg(showStructureLabel)}</button>
               </span>
            </span>
        </div>
    </#if>

</@comp.baseToolbar>