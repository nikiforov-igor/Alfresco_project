<#assign htmlId = fieldHtmlId/>

<#if field.control.params.parentDocArg?? && form.arguments[field.control.params.parentDocArg]??>
	<#assign parent = form.arguments[field.control.params.parentDocArg]/>
</#if>

<script type="text/javascript">
    //    <![CDATA[
    (function () {
	    function getNodeRef() {
	        YAHOO.util.Dom.get("${htmlId}").setAttribute("value", "${parent!""}");
	        YAHOO.util.Dom.get("${htmlId}-added").setAttribute("value", "${parent!""}");
	    }
	    YAHOO.util.Event.onContentReady("${fieldHtmlId}", getNodeRef, true);
    })();
    //]]>
</script>

<div class="form-field">
    <input type="hidden" id="${fieldHtmlId}-removed" name="${field.name}_removed"/>
    <input type="hidden" id="${fieldHtmlId}-added" name="${field.name}_added"/>
    <input type="hidden" id="${fieldHtmlId}" name="${field.name}" value=""/>
</div>