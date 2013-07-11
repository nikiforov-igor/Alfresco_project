<#assign htmlId = fieldHtmlId/>

<script type="text/javascript">
    //    <![CDATA[
    function getNodeRef() {
        console.log("${htmlId}");
        console.log(location.search.substr(1));
        var param = decodeURIComponent(location.search.substr(1)).split('&');
        for (var i=0; i < param.length; i++) {
            var tmp = param[i].split('=');
            if (tmp[0] == 'nodeRef') {
                console.log(Dom.get("${htmlId}"));
                console.log(YAHOO.util.Dom.get("${htmlId}"));
                console.log(document.getElementById("${htmlId}"));
                YAHOO.util.Dom.get("${htmlId}").setAttribute("value",tmp[1]);
            }
        }
    }
    YAHOO.util.Event.onContentReady("${fieldHtmlId}", getNodeRef, true);
    //]]>
</script>

<div class="form-field">
    <input type="hidden" id="${fieldHtmlId}" name="${field.name}" value=""/>
</div>