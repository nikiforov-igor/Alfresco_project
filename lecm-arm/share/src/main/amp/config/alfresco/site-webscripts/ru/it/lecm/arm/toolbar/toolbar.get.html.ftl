<#include "/org/alfresco/components/component.head.inc">

<@script type="text/javascript" src="${url.context}/res/scripts/lecm-base/components/lecm-toolbar.js"></@script>
<@script type="text/javascript" src="${url.context}/res/scripts/lecm-arm/arm-toolbar.js"></@script>

<#assign id = args.htmlid>
<#assign importFormId = id + "-import-form">
<#assign importInfoFormId = id + "-import-info-form">
<#assign importErrorFormId = id + "-import-error-form">

<script type="text/javascript">//<![CDATA[
    (function(){
        function init() {
            new LogicECM.module.ARM.Toolbar("${id}").setMessages(${messages}).setOptions({
                    showImportXml: true
            });
        }
        YAHOO.util.Event.onDOMReady(init);
    })();
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
	<div class="create-row">
        <span id="${id}-newHtmlNodeButton" class="yui-button yui-push-button">
            <span class="first-child">
                <button type="button">${msg('lecm.arm.html-node.add')}</button>
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
	<div class="arm-export">
        <span id="${id}-exportArmButton" class="yui-button yui-push-button">
            <span class="first-child">
                <button type="button">${msg('lecm.arm.export')}</button>
            </span>
        </span>
    </div>
	<div class="arm-import">
        <span id="${id}-importArmButton" class="yui-button yui-push-button">
            <span class="first-child">
                <button type="button">${msg('lecm.arm.import')}</button>
            </span>
        </span>
    </div>

	<div id="${importInfoFormId}" class="yui-panel import-info">
		<div id="${importInfoFormId}-head" class="hd">${msg("title.import.info")}</div>
		<div id="${importInfoFormId}-body" class="bd">
			<div id="${importInfoFormId}-content" class="import-info-content"></div>
            <div class="bdft">
                <button id="${importFormId}-info-ok" tabindex="0">${msg("button.ok")}</button>
            </div>
        </div>
	</div>

	<div id="${importErrorFormId}" class="yui-panel import-error">
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
            <div class="bdft">
                <button id="${importFormId}-error-ok" tabindex="0">${msg("button.ok")}</button>
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
						<button id="${importFormId}-cancel" tabindex="1">${msg("button.no")}</button>
					</div>
				</form>
			</div>
		</div>
	</div>
</@comp.baseToolbar>
