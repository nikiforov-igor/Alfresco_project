<@markup id="css" >
	<@link rel="stylesheet" type="text/css" href="${url.context}/res/components/data-lists/toolbar.css" group="toolbar"/>
	<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/lecm-dictionary/dictionary-toolbar.css" group="toolbar"/>
</@>

<@markup id="js">
	<@script type="text/javascript" src="${url.context}/res/scripts/lecm-base/components/lecm-toolbar.js" group="toolbar"/>
	<@script type="text/javascript" src="${url.context}/res/modules/simple-dialog.js" group="toolbar"/>
</@>

<@markup id="widgets">
	<@createWidgets group="toolbar"/>
</@>

<@markup id="html">
	<@uniqueIdDiv>

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

		<#assign exSearch = false/>
		<#if showExSearchBtn??>
			<#assign exSearch = showExSearchBtn/>
		</#if>

		<#assign newRowButtonLabel = "button.new-row"/>
		<#if newRowLabel??>
			<#assign newRowButtonLabel = newRowLabel/>
		</#if>

		<#assign showImportXml = false/>
		<#if showImportXmlBtn??>
			<#assign showImportXml = showImportXmlBtn/>
		</#if>

		<#assign newSpanId = "${id}-newRowButton"/>

		<#import "/ru/it/lecm/base-share/components/base-components.ftl" as comp/>

		<@comp.baseToolbar id buttons searchBlock exSearch>
		    <div class="new-row">
		        <span id="${newSpanId}" class="yui-button yui-push-button">
		           <span class="first-child">
		              <button type="button">${msg(newRowButtonLabel)}</button>
		           </span>
		        </span>
		    </div>

			<#if showImportXml>
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
									<button id="${importFormId}-cancel" tabindex="1">${msg("button.no")}</button>
								</div>
							</form>
						</div>
					</div>
				</div>
			</#if>
		</@comp.baseToolbar>
	</@>
</@>