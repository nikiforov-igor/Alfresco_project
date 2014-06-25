<#assign controlId = fieldHtmlId + "-cntrl">

<#assign resultListType = field.control.params.resultListType!'lecm-workflow-result:workflow-result-list'>

<script type="text/javascript">//<![CDATA[
(function () {
    function init() {

        LogicECM.module.Base.Util.loadScripts([
            'scripts/lecm-base/components/object-finder/lecm-object-finder.js',
            'scripts/lecm-workflow/lecm-package-items-finder.js'
        ], createPackageFinder);
    }

    function createPackageFinder() {

        var picker = new LogicECM.module.PackageItemsFinder("${controlId}", "${fieldHtmlId}").setOptions(
                {
                    disabled: true,
                    field: "assoc_packageItems",
                    compactMode: false,
                    mandatory: false,
                    currentValue: "${field.value}",
                    selectActionLabel: "${field.control.params.selectActionLabel!msg("button.select")}",
                    minSearchTermLength: 1,
                    maxSearchResults: 1000
                }).setMessages(${messages});

        picker.setOptions(
                {
                    resultListType: "${resultListType}",
                    showActions: false,
                    additionalProperties: "lecm-document:list-present-string",
                    itemType: "cm:content",
                    multipleSelectMode: ${field.endpointMany?string},
                    parentNodeRef: "alfresco://company/home",
                <#if field.control.params.rootNode??>
                    rootNode: "${field.control.params.rootNode}",
                </#if>
                    itemFamily: "node",
                <#if field.control.params.nameSubstituteString??>
                    nameSubstituteString: "${field.control.params.nameSubstituteString}",
                </#if>
                <#if field.control.params.substituteParent?? && field.control.params.substituteParent == "true">
                    substituteParent: "${form.arguments.itemId!""}",
                </#if>
                    displayMode: "list",
                    showLinkToTarget: true,
                    targetLinkTemplate: function(item) {
                        return Alfresco.util.siteURL("document?nodeRef=" + item.nodeRef);
                    }
            });
        picker.onReady();
    }

    YAHOO.util.Event.onDOMReady(init);

})();
//]]></script>
<#-- Лютый костыль для правки отображения формы как поля (иначе отображается с рамкой и выползает) -->
<style type="text/css">
    div#lecm-result-list-span div.form-fields {
        border: none !important;
        padding-left: 0 !important;
        padding-right: 0 !important;
    }
</style>

<div class="form-field">
    <div id="${controlId}" class="viewmode-field">
        <span class="viewmode-label">${msg("label.attachments")}:</span>
        <div id="lecm-package-items-span">
            <span id="${controlId}-currentValueDisplay" class="viewmode-value current-values"></span>
        </div>
    </div>
    <div id="${controlId}-list" class="viewmode-field">
        <span class="viewmode-label result-list-label">${msg("label.resultList")}:</span>
        <div id="lecm-result-list-span">
            <span id="${controlId}-currentListValueDisplay" class="viewmode-value"></span>
        </div>
    </div>
</div>


