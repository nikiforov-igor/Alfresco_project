<#import "/ru/it/lecm/base-share/components/base-components.ftl" as comp/>

<#assign toolbarId = "controlsToolbar-" + args.htmlid/>
<#assign datagridBubblingLabel = bubblingLabel + "-" + context.pageId + "-" + context.templateId/>

<div id="${toolbarId}">
<@comp.baseToolbar toolbarId true false false>
	<div class="new-row">
		<div id="${toolbarId}-btnCreateNewControl"></div>
	</div>
	<div class="divider"></div>
	<div class="generate-controls">
		<div id="${toolbarId}-btnGenerateControls"></div>
	</div>
	<div class="divider"></div>
	<div class="deploy-controls">
		<div id="${toolbarId}-btnDeployControls"></div>
	</div>
</@>
</div>
<@inlineScript group="lecm-controls-editor">
(function() {
	var controlsEditorToolbar = new LogicECM.module.ControlsEditor.Toolbar("${toolbarId}");
	controlsEditorToolbar.setOptions({
		typename: "${doctype}",
		destination: "${context.page.properties["typeRoot"]}",
		bubblingLabel: "${datagridBubblingLabel}"
	});
	controlsEditorToolbar.setMessages(${messages});
})();
</@>
