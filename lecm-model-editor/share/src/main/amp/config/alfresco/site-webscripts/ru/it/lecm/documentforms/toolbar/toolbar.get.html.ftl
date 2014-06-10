<@markup id="css">
  <@link rel="stylesheet" type="text/css" href="${url.context}/res/components/data-lists/toolbar.css" />
  <@link rel="stylesheet" type="text/css" href="${url.context}/res/css/lecm-forms-editor/form-editor-toolbar.css" />
  <@link rel="stylesheet" type="text/css" href="${url.context}/res/components/data-lists/toolbar.css" />
</@>
<@markup id="js">
  <@script type="text/javascript" src="${url.context}/res/scripts/lecm-base/components/lecm-toolbar.js"></@script>
  <@script type="text/javascript" src="${url.context}/res/scripts/lecm-forms-editor/forms-editor-toolbar.js"></@script>
</@>
<#assign id = args.htmlid>

<#import "/ru/it/lecm/base-share/components/base-components.ftl" as comp/>

<script type="text/javascript">//<![CDATA[
function init() {
    new LogicECM.module.FormsEditor.Toolbar("${id}").setMessages(${messages}).setOptions({
	    bubblingLabel: "${bubblingLabel!''}",
	    doctype: "${doctype!''}",
        searchActive: true
    });
}
YAHOO.util.Event.onDOMReady(init);
//]]></script>
<@comp.baseToolbar id true false false>
	<div class="new-row">
        <span id="${id}-newFormButton" class="yui-button yui-push-button">
           <span class="first-child">
              <button type="button" title="${msg("button.new-form")}">${msg("button.new-form")}</button>
           </span>
        </span>
	</div>
	<div class="divider"></div>
	<div class="generate-forms">
        <span id="${id}-generateFormsButton" class="yui-button yui-push-button">
           <span class="first-child">
              <button type="button" title="${msg("button.generate-forms")}">${msg("button.generate-forms")}</button>
           </span>
        </span>
	</div>
	<div class="divider"></div>
	<div class="deploy-forms">
        <span id="${id}-deployFormsButton" class="yui-button yui-push-button">
           <span class="first-child">
              <button type="button" title="${msg("button.deploy-forms")}">${msg("button.deploy-forms")}</button>
           </span>
        </span>
	</div>
	<div class="divider"></div>
	<div class="download-config">
        <span id="${id}-downloadConfigButton" class="yui-button yui-push-button">
           <span class="first-child">
              <button type="button" title="${msg("button.download-config")}">${msg("button.download-config")}</button>
           </span>
        </span>
	</div>
	<div class="divider"></div>
	<div class="upload-config">
        <span id="${id}-uploadConfigButton" class="yui-button yui-push-button">
           <span class="first-child">
              <button type="button" title="${msg("button.upload-config")}">${msg("button.upload-config")}</button>
           </span>
        </span>
	</div>
</@comp.baseToolbar>