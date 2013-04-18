<#assign id = args.htmlid>
<#assign containerId = id + "-container">

<script type="text/javascript">
    //<![CDATA[
    (function() {
        function init() {
            new Alfresco.widget.DashletResizer("${id}", "${instance.object.id}");
        }
        YAHOO.util.Event.onDOMReady(init);
    })();
    //]]>
</script>


<div class="dashlet contracts bordered">
    <div class="title dashlet-title">
        <span>${msg("label.title")}</span>
    </div>
    <div class="body scrollableList dashlet-body" id="${id}_results" <#if args.height??>style="height: ${args.height}px;"</#if>>
	    <#if data??>
		    <ul class="sr-list">
			    <#list data as value>
			        <li>
				        <a href=" ${value.item.node.properties["lecm-contract-dic:reference-data-link"]!''}"> ${value.item.node.properties["cm:name"]!''}</a>
			        </li>
			    </#list>
		    </ul>
	    </#if>
    </div>
</div>