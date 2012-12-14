<#include "/ru/it/lecm/base-share/components/picker.inc.ftl" />

<#assign controlId = fieldHtmlId + "-cntrl">
<#assign optionSeparator="|">
<#assign labelSeparator=":">

<script type="text/javascript">//<![CDATA[
(function () {
	//создаем объект, который будет отлавливать изменения картинки и заменять её
    ImageUpdater = function()
    {
        YAHOO.Bubbling.on("imageUpdated", function(layer,args){
            var imageContainer = YAHOO.util.Dom.get("${controlId}-container");
	        if(imageContainer){
		        // получаем ссылку на текущую картинку
		        var added = YAHOO.util.Dom.get("${controlId}-added");
		        if(added.value == "") { // берем из текущего
			        added = YAHOO.util.Dom.get("${fieldHtmlId}");
		        }
                var imgRef = generateThumbnailUrl(added.value != "" ? added.value != ""  : "${field.value}", false);
                if (imgRef != "") {
                    var ref = added.value;
                    var imageId = ref.slice(ref.lastIndexOf('/') + 1);
                    imageContainer.innerHTML = '<span class="thumbnail">' + '<a href="' + generateThumbnailUrl(added.value, true) +'" target="_blank"><img id="' + imageId + '" src="' + imgRef + '" /></a></span>';
                }
	        }
        }, this);

        return this;
    };

    var imageUpdater = new ImageUpdater();

    function generateThumbnailUrl(ref, view) {
        if (ref != null && ref != undefined && ref.length > 0) {
            var nodeRef = new Alfresco.util.NodeRef(ref);
            if (!view) {
                return Alfresco.constants.PROXY_URI + "api/node/" + nodeRef.uri + "/content/thumbnails/doclib?c=force&ph=true";
            } else {
                return Alfresco.constants.PROXY_URI + "api/node/" + nodeRef.uri + "/content";
            }
        } else {
            return "";
        }
    }
    function init() {
        var imageContainer = YAHOO.util.Dom.get("${controlId}-container");
        var imgRef = generateThumbnailUrl("${field.value}", false);
        if (imgRef != "") {
            var ref = "${field.value}";
            var imageId = ref.slice(ref.lastIndexOf('/') + 1);
            imageContainer.innerHTML = '<span class="thumbnail">' + '<a href="' + generateThumbnailUrl("${field.value}", true) +'" target="_blank"><img id="' + imageId + '" src="' + imgRef + '" /></a></span>';
        }
    }

    function OnElementAvaiable(id) {
        YAHOO.util.Event.onContentReady(id, this.handleOnAvailable, this);
    }
    OnElementAvaiable.prototype.handleOnAvailable = function (me) {
        init();
    };

var obj = new OnElementAvaiable("${controlId}-container");
<@renderPickerJS field "picker" />
    picker.setOptions(
            {
			<#if field.control.params.showTargetLink??>
                showLinkToTarget: ${field.control.params.showTargetLink},
				<#if page?? && page.url.templateArgs.site??>
                    targetLinkTemplate: "${url.context}/page/site/${page.url.templateArgs.site!""}/document-details?nodeRef={nodeRef}",
				<#else>
                    targetLinkTemplate: "${url.context}/page/document-details?nodeRef={nodeRef}",
				</#if>
			</#if>
			<#if field.control.params.allowNavigationToContentChildren??>
                allowNavigationToContentChildren: ${field.control.params.allowNavigationToContentChildren},
			</#if>
                itemType: "${field.endpointType}",
                multipleSelectMode: ${field.endpointMany?string},
                parentNodeRef: "alfresco://company/home",
			<#if field.control.params.rootNode??>
                rootNode: "${field.control.params.rootNode}",
			</#if>
            <#if field.control.params.fireAction?? && field.control.params.fireAction != "">
                fireAction: {
		            <#list field.control.params.fireAction?split(optionSeparator) as typeValue>
			            <#if typeValue?index_of(labelSeparator) != -1>
				            <#assign type=typeValue?split(labelSeparator)>
				            <#if type[0] == "ok">
                                ok: "${type[1]}",
				            </#if>
				            <#if type[0] == "cancel">
                                cancel: "${type[1]}",
				            </#if>
			            </#if>
		            </#list>
                },
            </#if>
                itemFamily: "node",
                displayMode: "${field.control.params.displayMode!"items"}",
                showNotSelectableItems: true
            });
})();
//]]></script>

<div class="form-field">
<div class="yui-dt45-col-thumbnail yui-dt-col-thumbnail" style="width: 100px;">
    <div class="yui-dt-liner" style="width: 100px;" id="${controlId}-container"></div>
</div>
<#if form.mode == "view">
    <div id="${controlId}" class="viewmode-field">
		<#if (field.endpointMandatory!false || field.mandatory!false) && field.value == "">
        <span class="incomplete-warning"><img src="${url.context}/res/components/form/images/warning-16.png" title="${msg("form.field.incomplete")}" /><span>
		</#if>
        <span class="viewmode-label">${field.label?html}:</span>
        <span id="${controlId}-currentValueDisplay" class="viewmode-value current-values"></span>
    </div>
<#else>
    <label for="${controlId}">${field.label?html}:<#if field.endpointMandatory!false || field.mandatory!false><span class="mandatory-indicator">${msg("form.required.fields.marker")}</span></#if></label>

    <div id="${controlId}" class="object-finder">

        <div id="${controlId}-currentValueDisplay" class="current-values"></div>

		<#if field.disabled == false>
            <input type="hidden" id="${fieldHtmlId}" name="-" value="${field.value?html}" />
            <input type="hidden" id="${controlId}-added" name="${field.name}_added" />
            <input type="hidden" id="${controlId}-removed" name="${field.name}_removed" />
            <div id="${controlId}-itemGroupActions" class="show-picker"></div>

			<@renderPickerHTML controlId />
		</#if>
    </div>
</#if>
</div>
