<#import "/ru/it/lecm/base-share/components/base-components.ftl" as comp/>

<#assign id = args.htmlid>

<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/lecm-base/components/base-menu/base-menu.css"/>
<@link rel="stylesheet" type="text/css" href="${url.context}/res/components/model-editor/model-editor-menu.css"/>

<@comp.baseMenu>
	<@comp.baseMenuButton "modelEditorHome" msg('lecm.modelEditorHome.btn') "modelEditorHome" true true/>
	<@comp.baseMenuButton "stateMachineHome" msg('lecm.stateMachineHome.btn') "stateMachineHome" true true/>
	<@comp.baseMenuButton "formsEditorHome" msg('lecm.formsEditorHome.btn') "formsEditorHome" true true/>
	<@comp.baseMenuButton "controlsEditorHome" msg('lecm.controlsEditorHome.btn') "controlsEditorHome" true true/>
	<@comp.baseMenuButton "modelListHome" msg('lecm.modelListHome.btn') "modelListHome" true true/>
</@comp.baseMenu>

<@script type="text/javascript" src="${url.context}/res/scripts/lecm-forms-editor/lecm-modeleditor-menu.js"/>
<@inlineScript>
(function() {
	var menu = new LogicECM.module.ModelEditor.Menu("menu-buttons");
	menu.setMessages(${messages});
})();
</@>
