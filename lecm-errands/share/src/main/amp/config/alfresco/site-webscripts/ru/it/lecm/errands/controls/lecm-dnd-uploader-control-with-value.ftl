<#include "/org/alfresco/components/form/controls/common/utils.inc.ftl" />
<#assign params = field.control.params/>

<div class="control dnd-uploader editmode">
    <input id="${fieldHtmlId}" type="hidden" class="autocomplete-input" name="${field.name}" value="${field.value}"/>
    <input type="hidden" id="${fieldHtmlId}-removed" name="${field.name}_removed"/>
    <input type="hidden" id="${fieldHtmlId}-added" name="${field.name}_added"/>

<#assign showAttsLabel = true/>
<#assign showAttsList = true/>
<#if params.showAttsLabel?? && params.showAttsLabel == "false">
    <#assign showAttsLabel = false/>
</#if>
<#assign suppressRefreshEvent = "false"/>
<#if params.suppressRefreshEvent?? && params.suppressRefreshEvent == "true">
    <#assign suppressRefreshEvent = "true"/>
</#if>
<#assign showPreview = "false"/>
<#if params.showPreview?? && params.showPreview == "true">
    <#assign showPreview = "true"/>
</#if>
<#if params.showAttsList?? && params.showAttsList == "false">
    <#assign showAttsList = false/>
</#if>
<#if showAttsLabel>
    <div class="label-div">
        <label>
        ${field.label?html}:
            <#if field.mandatory><span class="mandatory-indicator">${msg("form.required.fields.marker")}</span></#if>
        </label>
    </div>
</#if>
    <div class="container">
        <div class="buttons-div">
        <@renderDndUploaderContainerHTML fieldHtmlId field form suppressRefreshEvent showPreview/>
        </div>
        <div class="value-div">
        <#if showAttsList>
            <ul id="${fieldHtmlId}-attachments" class="attachments-list"></ul>
        </#if>
        </div>
    </div>
</div>
<div class="clear"></div>

<#macro renderDndUploaderContainerHTML fieldHtmlId field form suppressRefreshEvent="false" showPreview="true">
    <#assign params = field.control.params/>
    <#assign disabled = form.mode == "view">

    <#assign autoSubmit = false/>
    <#if params.autoSubmit?? && params.autoSubmit == "true">
        <#assign autoSubmit = true/>
    </#if>

    <#assign showUploadNewVersion = false/>
    <#if params.showUploadNewVersion?? && params.showUploadNewVersion == "true">
        <#assign showUploadNewVersion = true/>
    </#if>

    <#assign checkRights = false/>
    <#if params.checkRights?? && params.checkRights == "true">
        <#assign checkRights = true/>
    </#if>

    <#assign defaultValue=""/>
    <#assign fieldValue=field.value!"">
    <#if form.mode == "create" && fieldValue?? && fieldValue?string == "" && form.arguments[field.name]?has_content>
        <#assign defaultValue = form.arguments[field.name]/>
    </#if>
    <#if params.currentValueFormArg??>
        <#if form.arguments[params.currentValueFormArg]??>
            <#if (defaultValue?length > 0)>
                <#assign defaultValue = defaultValue + ","/>
            </#if>
            <#assign defaultValue = defaultValue + form.arguments[params.currentValueFormArg]/>
        </#if>
    </#if>
    <#assign defaultSelectedShowPreviewButton = true>
    <#if params.defaultSelectedShowPreviewButton?? && params.defaultSelectedShowPreviewButton == "false">
        <#assign defaultSelectedShowPreviewButton = false/>
    </#if>

<script type="text/javascript">//<![CDATA[
(function() {
    function init() {
        LogicECM.module.Base.Util.loadScripts([
            'scripts/lecm-base/components/lecm-dnd-uploader.js',
            'scripts/lecm-base/components/lecm-uploader-initializer.js',
            'scripts/lecm-base/components/lecm-dnd-uploader-control.js'
        ], createDndUploader);
    }
    function createDndUploader() {
        var control = new LogicECM.control.DndUploader("${fieldHtmlId}").setMessages(${messages});
        control.setOptions(
                {
                    uploadDirectoryPath: "${params.uploadDirectoryPath}",
                    disabled: ${disabled?string},
                    multipleMode: ${field.endpointMany?string},
                    autoSubmit: ${autoSubmit?string},
                    showUploadNewVersion: ${showUploadNewVersion?string},
                    directoryName: "${msg(params.directoryNameCode)}",
                    checkRights: ${checkRights?string},
                    itemNodeRef: "${form.arguments.itemId}",
                    currentValue: "${field.value!""}",
                    <#if defaultValue?has_content>
                        defaultValue: "${defaultValue?string}",
                    </#if>
                    suppressRefreshEvent: ${suppressRefreshEvent?string},
                    defaultSelectedShowPreviewButton: ${defaultSelectedShowPreviewButton?string},
                    showPreview: ${showPreview?string}
                });
    }
    YAHOO.util.Event.onDOMReady(init);
})();
//]]></script>

    <#if !disabled>
    <div id="${fieldHtmlId}-uploader-block" class="uploader-block <#if checkRights>hidden</#if>">
        <fieldset>
            <legend>${msg("label.add-file")}</legend>
            <img id="${fieldHtmlId}-uploader-button" src="/share/res/images/lecm-base/components/plus.png" alt="" class="uploader-button">  <br/>
            <span class="drag-tip">${msg("label.drag-file")}</span>
        </fieldset>
    </div>
    </#if>
</#macro>