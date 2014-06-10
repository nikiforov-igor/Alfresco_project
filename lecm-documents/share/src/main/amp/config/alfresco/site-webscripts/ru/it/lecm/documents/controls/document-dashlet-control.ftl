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
        var dashletNumber = 0;

        function loadDashlet(dashletURI, nodeRef, dashletContainerId){
        	if (dashletURI == null || dashletURI == "") return;
			Alfresco.util.loadWebscript({
				url:Alfresco.constants.URL_SERVICECONTEXT + dashletURI,
				properties:{
					nodeRef: nodeRef,
					view: "view",
				},
				target:dashletContainerId,
			});
            //dashletNumber++;
            //var container = Dom.get(dashletContainerId);
            //var childElement = document.createElement("div");
            //childElement.id = dashletContainerId + dashletNumber;
            //container.appendChild(childElement);
            //Alfresco.util.Ajax.request(
            //    {
            //        url:Alfresco.constants.URL_SERVICECONTEXT + dashletURI,
            //        dataObj:{
            //            nodeRef: nodeRef,
            //            view: "view",
            //            htmlid: dashletContainerId + dashletNumber
            //        },
            //        successCallback:{
            //            fn:function(response){
            //                childElement.innerHTML = response.serverResponse.responseText;
            //            }
            //        },
            //        failureMessage: "message.failure",
            //        execScripts: true,
            //        htmlId: dashletContainerId + nodeRef + dashletNumber
            //    });
        }

        function init() {
            <#if params.first??>
                <#list params.first?split(";") as uri>
					<#assign concreteDashDivId = controlId + "-1" + "-" + uri_index/>
					var commonDashDiv = Dom.get("${controlId}-1");
					var concreteDashDiv = document.createElement('div');
					concreteDashDiv.innerHTML = '<div id="${concreteDashDivId}"></div>';
					commonDashDiv.appendChild(concreteDashDiv);

                    loadDashlet("${uri?trim}", "${form.arguments.itemId}", "${concreteDashDivId}");
                </#list>
            </#if>
            <#if params.second??>
                <#list params.second?split(";") as uri>
					<#assign concreteDashDivId = controlId + "-2" + "-" + uri_index/>
					var commonDashDiv = Dom.get("${controlId}-2");
					var concreteDashDiv = document.createElement('div');
					concreteDashDiv.innerHTML = '<div id="${concreteDashDivId}"></div>';
					commonDashDiv.appendChild(concreteDashDiv);

                    loadDashlet("${uri?trim}", "${form.arguments.itemId}", "${concreteDashDivId}");
                </#list>
            </#if>
        }
        Event.onContentReady("${controlId}-2", init, true);
    })();
    //]]>
</script>