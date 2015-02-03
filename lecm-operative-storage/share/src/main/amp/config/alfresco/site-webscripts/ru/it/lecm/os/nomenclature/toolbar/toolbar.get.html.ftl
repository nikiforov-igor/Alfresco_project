<#assign id = args.htmlid>
<#assign bubblingId = "nomenclature">
<#assign importFormId = id + "-import-form">
<#assign importInfoFormId = id + "-import-info-form">
<#assign importErrorFormId = id + "-import-error-form">

<script type="text/javascript">
(function() {
	var nomenclatureDatagrid = null;

	function createToolbar() {
		var control = new LogicECM.module.Nomenclature.Toolbar("${id}").setMessages(${messages}).setOptions({
			<#if page.url.args.armSelectedNodeRef?? && page.url.args.armSelectedNodeRef != "">
				armSelectedNodeRef: "${page.url.args.armSelectedNodeRef}",
			</#if>
			<#if page.url.args.root?? && page.url.args.root != "">
				isRoot: ${page.url.args.root},
			</#if>
			bubblingLabel: "${bubblingId}",
			showImportXml: true
		});
		if (nomenclatureDatagrid != null) {
			control.modules.dataGrid = nomenclatureDatagrid;
		}
		LogicECM.CurrentModules = LogicECM.CurrentModules || {};
		LogicECM.CurrentModules["${id}"] = control;
	}

	function init() {
		YAHOO.Bubbling.on("initDatagrid", function (layer, args) {
			nomenclatureDatagrid = args[1].datagrid;
		}, this);

		LogicECM.module.Base.Util.loadResources([
			'scripts/lecm-base/components/lecm-toolbar.js',
			'scripts/lecm-os/nomenclature/nomenclature-toolbar.js'
		], [
			'components/data-lists/toolbar.css',
			'css/lecm-dictionary/dictionary-toolbar.css',
			'css/lecm-nomenclature/nomenclature-toolbar.css'
		], createToolbar);
	}

	YAHOO.util.Event.onDOMReady(init);
})();
</script>

<#import "/ru/it/lecm/base-share/components/base-components.ftl" as comp/>
<@comp.baseToolbar id true false false>
	<div class="create-row">
		<span id="${id}-newRowButton" class="yui-button yui-push-button">
			<span class="first-child">
				<button type="button">${msg('lecm.os.nomenclature.add-element')}</button>
			</span>
		</span>
		<span id="${id}-newRowButtonAdditional" class="yui-button yui-push-button" style="display:none">
			<span class="first-child">
				<button type="button">${msg('lecm.os.nomenclature.add-element')}</button>
			</span>
		</span>
	</div>
	<div class="group-actiona">
		<span id="${id}-groupActionsButton" class="yui-button yui-push-button">
			<span class="first-child">
				<button type="button">${msg('lecm.os.nomenclature.group-actions')}</button>
			</span>
		</span>
	</div>
	<div class="delete-node">
		<span id="${id}-deleteNodeButton" class="yui-button yui-push-button">
			<span class="first-child">
				<button type="button">${msg('lecm.os.nomenclature.delete-element')}</button>
			</span>
		</span>
	</div>
	<div class="export">
		<span id="${id}-exportButton" class="yui-button yui-push-button">
			<span class="first-child">
				<button type="button">${msg('lecm.os.nomenclature.export')}</button>
			</span>
		</span>
	</div>
	<div class="import">
		<span id="${id}-importButton" class="yui-button yui-push-button">
			<span class="first-child">
				<button type="button">${msg('lecm.os.nomenclature.import')}</button>
			</span>
		</span>
	</div>

	<div id="${importInfoFormId}" class="yui-panel hidden1">
		<div id="${importInfoFormId}-head" class="hd">${msg('title.import.info')}</div>
		<div id="${importInfoFormId}-body" class="bd">
			<div id="${importInfoFormId}-content" class="import-info-content"></div>
		</div>
	</div>

	<div id="${importErrorFormId}" class="yui-panel hidden1">
		<div id="${importErrorFormId}-head" class="hd">${msg('title.import.info')}</div>
		<div id="${importErrorFormId}-body" class="bd">
			<div id="${importErrorFormId}-content" class="import-info-content">
				<div class="import-error-header">
					<h3>${msg('import.failure')}</h3>
					<a href="javascript:void(0);" id="${importErrorFormId}-show-more-link">${msg('import.failure.showMore')}</a>
				</div>
				<div id="${importErrorFormId}-more" class="import-error-more">
					<div class="import-error-exception">
					${msg('import.failure.exception')}:
						<div class="import-error-exception-content" id="${importErrorFormId}-exception">
						</div>
					</div>
					<div class="import-error-stack-trace">
					${msg('import.failure.stack-trace')}:
						<div class="import-error-stack-trace-content" id="${importErrorFormId}-stack-trace">
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>

	<div id="${importFormId}" class="yui-panel hidden1">
		<div id="${importFormId}-head" class="hd">${msg('title.import')}</div>
		<div id="${importFormId}-body" class="bd">
			<div id="${importFormId}-content">
				<form method="post" id="${id}-import-xml-form" enctype="multipart/form-data">
					<ul class="import-form">
						<li>
							<label for="${importFormId}-import-file">${msg('label.import-file')}*</label>
							<input id="${importFormId}-import-file" type="file" name="file" accept=".xml,application/xml,text/xml">
						</li>
						<li>
							<label for="${importFormId}-chbx-ignore">${msg('label.ignore-errors')}</label>
							<input id="${importFormId}-chbx-ignore" type="checkbox" name="ignoreErrors" value="true"/>
						</li>
					</ul>
					<div class="bdft">
						<button id="${importFormId}-submit" disabled="true" tabindex="0">${msg('button.import-xml')}</button>
						<button id="${importFormId}-cancel" tabindex="1">${msg('button.cancel')}</button>
					</div>
				</form>
			</div>
		</div>
	</div>
</@comp.baseToolbar>
