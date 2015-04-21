<#assign defaultValue=field.value>
<#if form.mode == "create" && defaultValue?string == "">
    <#if form.arguments[field.name]?has_content>
        <#assign defaultValue=form.arguments[field.name]>
    </#if>
</#if>

<div class="control editmode">
    <div class="label-div">
        <label for="${fieldHtmlId}">
        ${field.label?html}:
        <#if field.mandatory>
            <span class="mandatory-indicator">${msg("form.required.fields.marker")}</span>
        </#if>
        </label>
    </div>
    <div class="container">
        <div class="buttons-div"><@formLib.renderFieldHelp field=field /></div>
        <div class="value-div">
            <input type="radio" name="${fieldHtmlId}-group-type" value="THIS_ONLY" checked>${msg("form.event.repeatabable.type.this.only")}</input><br/>
            <input type="radio" name="${fieldHtmlId}-group-type" value="ALL">${msg("form.event.repeatabable.type.all")}</input><br/>
            <input type="radio" name="${fieldHtmlId}-group-type" value="ALL_NEXT">${msg("form.event.repeatabable.type.all.next")}</input><br/>
            <input type="radio" name="${fieldHtmlId}-group-type" value="ALL_PREV">${msg("form.event.repeatabable.type.all.prev")}</input>
            <input type="hidden" id="${fieldHtmlId}" name="${field.name}" value="THIS_ONLY">
        </div>
    </div>
</div>
<div class="clear"></div>

<script type="text/javascript">//<![CDATA[
(function() {

    function init() {
        var buttonsGroup = document.getElementsByName("${fieldHtmlId}-group-type");
        for (var i in buttonsGroup) {
            YAHOO.util.Event.addListener(buttonsGroup[i], "click", function(event) {
                var result = document.getElementById("${fieldHtmlId}");
                result.value = event.srcElement.value;
            });
        }
    }

    YAHOO.util.Event.onContentReady("${fieldHtmlId}", init);
})();
//]]></script>
