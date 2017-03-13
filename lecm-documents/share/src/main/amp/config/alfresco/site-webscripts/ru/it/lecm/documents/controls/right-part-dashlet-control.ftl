<#assign params = field.control.params/>
<#assign controlId = fieldHtmlId + "-cntrl">

<div id="${controlId}"></div>

<script type="text/javascript">
    //<![CDATA[
    (function () {
        Event = YAHOO.util.Event;

        function loadDashlet(dashletURI, properties, dashletContainerId) {
            if (dashletURI == null || dashletURI == "") return;
            Alfresco.util.loadWebscript({
                url: Alfresco.constants.URL_SERVICECONTEXT + dashletURI,
                properties: properties,
                target: dashletContainerId
            })
        }

        function init() {
            <#if params.uri??>
                var uri = "${params.uri?trim}";
                var properties = {
                    nodeRef : "${form.arguments.itemId}",
                    view: "view"
                    <#list  params?keys as key>
                        <#if key != "uri">
                            ,${key}: "${params[key]}"
                        </#if>
                    </#list>
                }
                loadDashlet(uri, properties, "${controlId}");
            </#if>
        }

        Event.onContentReady("${controlId}", init, true);
    })();
    //]]>
</script>
