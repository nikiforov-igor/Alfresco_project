<#assign id = args.htmlid>

<div class="dashlet contracts-sr-info bordered">
	<div class="title dashlet-title">
		<span>${msg("label.title")}</span>
	</div>
	<div class="body scrollableList dashlet-body" id="${id}_results">
		<#if data??>
			<ul class="sr-list">
				<#list data as value>
					<li>
						<a href="${value.item.node.properties["lecm-contract-dic:reference-data-link"]!''}"> ${value.item.node.properties["cm:title"]!''}</a>
					</li>
				</#list>
			</ul>
		</#if>
	</div>
</div>

<script type="text/javascript">
(function() {
	LogicECM.module.Base.Util.loadCSS(['css/lecm-contracts/contracts-sr.css']);
})();
</script>
