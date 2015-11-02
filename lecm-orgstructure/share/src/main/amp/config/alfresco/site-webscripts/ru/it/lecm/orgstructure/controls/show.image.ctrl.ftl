<#include "/ru/it/lecm/base-share/components/controls/picker.inc.ftl" />

<#assign aDateTime = .now>
<#assign controlId = fieldHtmlId + "-cntrl-" + aDateTime?iso_utc>
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
    function initContainer() {
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
        initContainer();
    };

	var obj = new OnElementAvaiable("${controlId}-container");
	function init() {
        LogicECM.module.Base.Util.loadResources([
            '/yui/resize/resize-min.js',
            'scripts/lecm-base/components/object-finder/lecm-object-finder.js'
		], [
            'css/lecm-orgstructure/controls/show.image.ctrl.css'
		], createPicker);
	}
	function createPicker(){
		<@renderPickerJS field "picker" />
    	picker.setOptions({
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
                showNotSelectableItems: false,
                showFolderItems: true,
                docType: "img"
		});
	}
	YAHOO.util.Event.onDOMReady(init);
})();
//]]></script>

<div class="yui-dt45-col-thumbnail yui-dt-col-thumbnail show-image-thumbnail">
    <div class="yui-dt-liner" id="${controlId}-container"></div>
</div>
<#if form.mode == "view">
	<div id="${controlId}" class="control show-image viewmode">
		<div class="label-div">
			<#if (field.endpointMandatory!false || field.mandatory!false) && field.value == "">
			<span class="incomplete-warning"><img src="${url.context}/res/components/form/images/warning-16.png" title="${msg("form.field.incomplete")}"/><span>
			</#if>
			<label>${field.label?html}:</label>
		</div>
		<div class="container">
			<div class="value-div">
				<input type="hidden" id="${fieldHtmlId}" name="${field.name}" value="${field.value?html}" />
				<span id="${controlId}-currentValueDisplay" class="mandatory-highlightable"></span>
			</div>
		</div>
	</div>
<#else>
	<div class="control show-image editmode">
		<div class="label-div">
			<label for="${controlId}">
			${field.label?html}:
				<#if field.endpointMandatory!false || field.mandatory!false>
					<span class="mandatory-indicator">${msg("form.required.fields.marker")}</span>
				</#if>
			</label>
		</div>
		<div id="${controlId}" class="container">
			<#if field.disabled == false>
				<input type="hidden" id="${controlId}-added" name="${field.name}_added"/>
				<input type="hidden" id="${controlId}-removed" name="${field.name}_removed"/>
				<input type="hidden" id="${controlId}-selectedItems"/>

				<div id="${controlId}-itemGroupActions" class="buttons-div">
					<div id="${controlId}-itemGroupActions" class="show-picker"></div>
				</div>

				<@renderPickerHTML controlId />
			</#if>

			<div class="value-div">
				<input type="hidden" id="${fieldHtmlId}" name="${field.name}" value="${field.value?html}" />
				<div id="${controlId}-currentValueDisplay" class="control-selected-values mandatory-highlightable"></div>
			</div>

		</div>
	</div>
</#if>
<div class="clear"></div>
