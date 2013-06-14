<#include "/org/alfresco/components/form/controls/common/utils.inc.ftl" />

<#assign fieldValue=field.value!"">
<#assign controlId = fieldHtmlId + "-cntrl">
<#assign showImage = false>
<#assign hideItem = false>

<#if fieldValue?string == "" && field.control.params.defaultValueContextProperty??>
	<#if context.properties[field.control.params.defaultValueContextProperty]??>
		<#assign fieldValue = context.properties[field.control.params.defaultValueContextProperty]>
	<#elseif args[field.control.params.defaultValueContextProperty]??>
		<#assign fieldValue = args[field.control.params.defaultValueContextProperty]>
	</#if>
</#if>
<#assign disabled = form.mode == "view">
<#if field.control.params.showImage?? && field.control.params.showImage=="true">
    <#assign showImage = true>
</#if>
<#if field.control.params.hideItem?? && field.control.params.showImage=="true">
    <#assign hideItem = true>
</#if>

<script type="text/javascript">//<![CDATA[
(function()
{
	var control = new LogicECM.control.Uploader("${fieldHtmlId}").setMessages(${messages});
	control.setOptions(
			{
				<#if disabled>
					disabled: true,
				</#if>
				<#if field.control.params.uploadDirectoryPath??>
					uploadDirectoryPath: "${field.control.params.uploadDirectoryPath}",
				</#if>
				currentValue: "${field.value!''}"
			});


<#if showImage>
    //создаем объект, который будет отлавливать изменения картинки и заменять её
    ImageUpdater = function()
    {
        YAHOO.Bubbling.on("imageUpdated", function(layer,args){
            var imageContainer = YAHOO.util.Dom.get("${controlId}-container");
            var className ="${disabled?string}"=="true" ? "thumbnail-view" :"thumbnail-edit";
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
                    imageContainer.innerHTML = '<span class="'+ className +'">' + '<a href="' + generateThumbnailUrl(added.value, true) +'" target="_blank"><img id="' + imageId + '" src="' + imgRef + '" /></a></span>';
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
        var className = "${disabled?string}"=="true" ? "thumbnail-view" :"thumbnail-edit";

        if (imgRef != "") {
            var ref = "${field.value}";
            var imageId = ref.slice(ref.lastIndexOf('/') + 1);
            imageContainer.innerHTML = '<span class="'+ className +'">' + '<a href="' + generateThumbnailUrl("${field.value}", true) +'" target="_blank"><img id="' + imageId + '" src="' + imgRef + '" /></a></span>';
        } else {
            imageContainer.innerHTML = '<span class="'+ className+'-text">' + "${msg('message.upload.not-loaded')}" + '</span>';
        }

    }

    function OnElementAvaiable(id) {
        YAHOO.util.Event.onContentReady(id, this.handleOnAvailable, this);
    }
    OnElementAvaiable.prototype.handleOnAvailable = function (me) {
        init();
    };

    var obj = new OnElementAvaiable("${controlId}-container");
</#if>
})();
//]]></script>

<div class="form-field">
    <#if showImage>
        <div class="yui-dt45-col-thumbnail yui-dt-col-thumbnail" style="width: 100px;">
            <div class="yui-dt-liner" style="width: 100px;" id="${controlId}-container"></div>
        </div>
    </#if>
	<#if disabled>
        <#if !hideItem>
            <div class="viewmode-field">
                <#if showViewIncompleteWarning?? && showViewIncompleteWarning && field.mandatory && !(fieldValue?is_number) && fieldValue?string == "">
                    <span class="incomplete-warning"><img src="${url.context}/res/components/form/images/warning-16.png" title="${msg("form.field.incomplete")}"/><span>
                </#if>
                <span class="viewmode-label">${field.label?html}:</span>
                <span id="${controlId}-currentValueDisplay" class="viewmode-value"></span>
            </div>
        </#if>
	<#else>
		<label for="${controlId}">${field.label?html}:<#if field.endpointMandatory!false || field.mandatory!false><span class="mandatory-indicator">${msg("form.required.fields.marker")}</span></#if></label>
		<div id="${controlId}" class="object-finder">
			<div id="${controlId}-currentValueDisplay" class="current-values"></div>
			<input type="hidden" id="${controlId}-added" name="${field.name}_added"/>
			<input type="hidden" id="${controlId}-removed" name="${field.name}_removed"/>
			<input type="hidden" id="${controlId}-selectedItems"/>

			<div id="${controlId}-itemGroupActions" class="show-picker">
                <span class="file-upload-button">
                    <input type="button" id="${controlId}-file-upload-button" name="-" value="${msg("lecm.form.upload")}"/>
                </span>
			</div>
			<div class="clear"></div>
		</div>
	</#if>
	<input type="hidden" id="${fieldHtmlId}" name="${field.name}" value="${field.value?html}" />
</div>