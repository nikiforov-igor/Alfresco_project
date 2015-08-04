<#assign defaultValue=field.value>
<#if form.mode == "create" && defaultValue?string == "">
    <#if form.arguments[field.name]?has_content>
        <#assign defaultValue=form.arguments[field.name]>
    </#if>
</#if>

<script type="text/javascript">//<![CDATA[
(function () {
	function initBubbling() {
		YAHOO.Bubbling.on("changeRepeatableType", onChangeRepeatableType);
	}
	
	var idArray = ['this-only','all','all-next','all-prev'];
	
	function onChangeRepeatableType(layer, args) {
		var div = Dom.get("${fieldHtmlId}-"+idArray[args[1].id]);
		var warning = Dom.get("${fieldHtmlId}-warning");
		var id = idArray[args[1].id];
		if (id == "all" || id == "all-prev") {
			warning.className = "warning";
		} else {
			warning.className = "hidden";
		}
		warning.parentNode.removeChild(warning);
		div.appendChild(warning);
	}
	
	function init() {
			LogicECM.module.Base.Util.loadResources([], [
				'css/lecm-events/event-repeatable-type.css'
			], initBubbling);
	}

	YAHOO.util.Event.onContentReady("${fieldHtmlId}-container", init);
})();
//]]></script>

<div class="control editmode">
    <div class="label-div">
        <label for="${fieldHtmlId}">
        ${field.label?html}:
        <#if field.mandatory>
            <span class="mandatory-indicator">${msg("form.required.fields.marker")}</span>
        </#if>
        </label>
    </div>
    <div id="${fieldHtmlId}-container" class="container">
        <div class="buttons-div"><@formLib.renderFieldHelp field=field /></div>


        <div class="value-div" id="${fieldHtmlId}-value">
			<span id="${fieldHtmlId}-warning" class="hidden"><br/>${msg("form.event.repeatabable.type.warning")}</span>
			<div id="${fieldHtmlId}-this-only">
				<input  onClick="YAHOO.Bubbling.fire('changeRepeatableType', {id:0})" type="radio" name="${field.name}" value="THIS_ONLY" checked>${msg("form.event.repeatabable.type.this.only")}</input>
			</div>
			<div id="${fieldHtmlId}-all">
				<input  onClick="YAHOO.Bubbling.fire('changeRepeatableType', {id:1})" type="radio" name="${field.name}" value="ALL">${msg("form.event.repeatabable.type.all")}</input>
			</div>
			<div id="${fieldHtmlId}-all-next">
				<input  onClick="YAHOO.Bubbling.fire('changeRepeatableType', {id:2})" type="radio" name="${field.name}" value="ALL_NEXT">${msg("form.event.repeatabable.type.all.next")}</input>
			</div>
			<div id="${fieldHtmlId}-all-prev">
				<input onClick="YAHOO.Bubbling.fire('changeRepeatableType', {id:3})" type="radio" name="${field.name}" value="ALL_PREV">${msg("form.event.repeatabable.type.all.prev")}</input>
			</div>
        </div>
    </div>
</div>
<div class="clear"></div>
