<#assign htmlId = fieldHtmlId/>

<script type="text/javascript">
    //    <![CDATA[
    function getNodeRef() {
        var param = decodeURIComponent(location.search.substr(1)).split('&');
        for (var i=0; i < param.length; i++) {
            var tmp = param[i].split('=');
            if (tmp[0] == 'nodeRef') {
                YAHOO.util.Dom.get("${htmlId}").setAttribute("value",tmp[1]);
                YAHOO.util.Dom.get("${htmlId}-added").setAttribute("value",tmp[1]);
            }
        }
    }
    YAHOO.util.Event.onContentReady("${fieldHtmlId}", getNodeRef, true);
    //]]>
</script>

<div class="form-field">
    <input type="hidden" id="${fieldHtmlId}-removed" name="${field.name}_removed"/>
    <input type="hidden" id="${fieldHtmlId}-added" name="${field.name}_added"/>
    <input type="hidden" id="${fieldHtmlId}" name="${field.name}" value=""/>
</div>