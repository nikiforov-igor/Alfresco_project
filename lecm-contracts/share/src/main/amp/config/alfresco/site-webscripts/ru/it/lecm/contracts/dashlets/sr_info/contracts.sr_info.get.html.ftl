<@markup id="css">
	<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/lecm-contracts/contracts-sr.css" />
</@>
<@markup id="js">
	<@script type="text/javascript" src="${url.context}/res/yui/resize/resize.js"></@script>
</@>
<@markup id="html">
	<#assign id = args.htmlid>
	<#assign containerId = id + "-container">
	
	<div class="dashlet contracts-sr-info bordered">
	    <div class="title dashlet-title">
	        <span>${msg("label.title")}</span>
	    </div>
	    <div class="body scrollableList dashlet-body" id="${id}_results">
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
</@>