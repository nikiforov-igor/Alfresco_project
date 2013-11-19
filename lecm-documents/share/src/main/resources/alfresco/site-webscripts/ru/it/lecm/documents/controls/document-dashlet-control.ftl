<#assign params = field.control.params/>

<#assign controlId = fieldHtmlId + "-cntrl">
<#assign params = field.control.params/>

<div class="yui-gd grid columnSize2">
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

        function loadDashlet(dashletURI, nodeRef, dashletContainerId){
            if (dashletURI == null || dashletURI == "") return;
            Alfresco.util.Ajax.request(
                {
                    url:Alfresco.constants.URL_SERVICECONTEXT + dashletURI,
                    dataObj:{
                        nodeRef: nodeRef,
                        view: "view",
                        htmlid: dashletContainerId
                    },
                    successCallback:{
                        fn:function(response){
                            var container = Dom.get(dashletContainerId);
                            if (container != null) {
                                container.innerHTML = response.serverResponse.responseText;
                            }
                        }
                    },
                    failureMessage: "message.failure",
                    execScripts: true,
                    htmlId: dashletContainerId + nodeRef
                });
        }

        function init() {
            loadDashlet("${params.first!""}", "${form.arguments.itemId}", "${controlId}-1");
            loadDashlet("${params.second!""}", "${form.arguments.itemId}", "${controlId}-2");
        }
        Event.onContentReady("${controlId}-2", init, true);
    })();
    //]]>
</script>