<#import "/ru/it/lecm/base-share/components/base-components.ftl" as comp/>

<#assign id = args.htmlid>
<#assign nodeType = context.page.properties["nodeType"] ! "document">
<#assign fileName = (context.page.properties["fileName"] ! "")?html>

<#if context.page.properties["nodeRef"]??>
	<#assign nodeRef = context.page.properties["nodeRef"]?js_string>
<#elseif page.url.args["nodeRef"]??>
	<#assign nodeRef = page.url.args["nodeRef"]?js_string>
<#else>
	<#assign nodeRef = "">
</#if>

<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/lecm-base/components/base-menu/base-menu.css"/>
<@link rel="stylesheet" type="text/css" href="${url.context}/res/components/model-editor/model-editor-menu.css"/>

<@comp.baseMenu>
	<@comp.baseMenuButton "modelEditorHome" msg('lecm.modelEditorHome.btn') "modelEditorHome" true true/>
	<@comp.baseMenuButton "formsEditorHome" msg('lecm.formsEditorHome.btn') "formsEditorHome" true true/>
	<@comp.baseMenuButton "controlsEditorHome" msg('lecm.controlsEditorHome.btn') "controlsEditorHome" true true/>
	<@comp.baseMenuButton "modelListHome" msg('lecm.modelListHome.btn') "modelListHome" true true/>
</@comp.baseMenu>

<@script type="text/javascript" src="${url.context}/res/scripts/lecm-forms-editor/lecm-modeleditor-menu2.js"/>
<@inlineScript>
(function() {
	var menu = new LogicECM.module.ModelEditor.Menu2("menu-buttons");
	menu.setMessages(${messages});
	menu.setOptions({
		<#if modelItem??>
		modelItem: {
			<#if modelItem.nodeRef??>nodeRef: '${modelItem.nodeRef}',</#if>
			<#if modelItem.isDocumentModel??>isDocumentModel: ${modelItem.isDocumentModel?string},</#if>
			<#if modelItem.isModelActive??>isModelActive: ${modelItem.isModelActive?string},</#if>
			<#if modelItem.typeName??>typeName: '${modelItem.typeName}'</#if>
		},
		</#if>
	<#if nodeRef??>nodeRef: '${nodeRef}',</#if>
	<#if nodeType??>nodeType: '${nodeType}',</#if>
	<#if fileName??>fileName: '${fileName}'</#if>
});
})();
</@>
