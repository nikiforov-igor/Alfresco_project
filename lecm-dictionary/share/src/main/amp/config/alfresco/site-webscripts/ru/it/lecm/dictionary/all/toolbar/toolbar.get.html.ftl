<!-- Data List Toolbar -->
<@link rel="stylesheet" type="text/css" href="${url.context}/res/components/data-lists/toolbar.css" />
<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/lecm-dictionary/all-dictionary-toolbar.css" />
<@script type="text/javascript" src="${url.context}/res/scripts/lecm-base/components/lecm-toolbar.js"></@script>
<@script type="text/javascript" src="${url.context}/res/scripts/lecm-dictionary/all-dictionary-toolbar.js"></@script>

<#assign id = args.htmlid>
<#assign importFormId = id + "-import-form">
<#assign importInfoFormId = id + "-import-info-form">
<#assign importErrorFormId = id + "-import-error-form">
<#if isEngineer>
<script type="text/javascript">//<![CDATA[
(function(){
	function init() {
		new LogicECM.module.AllDictionary.Toolbar("${id}").setMessages(${messages});
	}
	YAHOO.util.Event.onDOMReady(init);
})();
//]]></script>
<div id="${args.htmlid}-body" class="datalist-toolbar toolbar">
	<div id="${args.htmlid}-headerBar" class="header-bar flat-button">
		<div class="left">
            <div>
                ${msg('logicecm.dictionary.dictionary-list')}
            </div>
            <div class="divider"></div>
            <div>
                <div id="${id}-import-xml" class="import-xml" title="${msg('button.import-xml')}">
                    <span id="${id}-importXmlButton" class="yui-button yui-push-button">
                        <span class="first-child">
                            <button type="button" title="${msg('button.import-xml')}">&nbsp;</button>
                        </span>
                    </span>
                </div>
            </div>
		</div>
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
				<form method="post" id="${id}-import-xml-form" enctype="multipart/form-data"
				      action="${url.context}/proxy/alfresco/lecm/dictionary/post/import">
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
</div></#if>