<#assign params = field.control.params/>

<#assign controlId = fieldHtmlId + "-cntrl">
<#assign params = field.control.params/>

<div class="yui-gd grid columnSize2" style="margin-bottom: -20px;">
    <div id="${controlId}-1" class="yui-u first column1">
    </div>
    <div id="${controlId}-2" class="yui-u column2">
    </div>
</div>

<script type="text/javascript">
    //<![CDATA[
    (function() {
        var Dom = YAHOO.util.Dom,
            Event = YAHOO.util.Event;
        var dashletNumber = 0;

        function loadDashlet(dashletURI, nodeRef, dashletContainerId){
            if (dashletURI == null || dashletURI == "") return;
            dashletNumber++;
            var container = Dom.get(dashletContainerId);
            var childElement = document.createElement("div");
            childElement.id = dashletContainerId + dashletNumber;
            container.appendChild(childElement);
            Alfresco.util.Ajax.request(
                {
                    url:Alfresco.constants.URL_SERVICECONTEXT + dashletURI,
                    dataObj:{
                        nodeRef: nodeRef,
                        view: "view",
                        htmlid: dashletContainerId + dashletNumber
                    },
                    successCallback:{
                        fn:function(response){
                            childElement.innerHTML = response.serverResponse.responseText;
                        }
                    },
                    failureMessage: "message.failure",
                    execScripts: true,
                    htmlId: dashletContainerId + nodeRef + dashletNumber
                });
        }

        function init() {
            <#if params.first??>
                <#list params.first?split(";") as uri>
                    loadDashlet("${uri?trim}", "${form.arguments.itemId}", "${controlId}-1");
                </#list>
            </#if>
            <#if params.second??>
                <#list params.second?split(";") as uri>
                    loadDashlet("${uri?trim}", "${form.arguments.itemId}", "${controlId}-2");
                </#list>
            </#if>
        }
        Event.onContentReady("${controlId}-2", init, true);
    })();
    //]]>
</script>