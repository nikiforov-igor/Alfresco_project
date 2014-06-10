<#assign el=args.htmlid+"htmlid">
<#assign searchconfig=config.scoped['Search']['search']>
<script type="text/javascript">//<![CDATA[
   new Alfresco.SemanticSearch("${el}").setOptions(
   {
      siteId: "${siteId}",
      siteTitle: "${siteTitle?js_string}",
      //initialSearchTerm: "${searchTerm?js_string}",
	  initialSearchTerm: "1",
      initialSearchTag: "${searchTag?js_string}",
      initialSearchAllSites: ${searchAllSites?string},
      initialSearchRepository: ${searchRepo?string},
      initialSort: "${searchSort?js_string}",
      searchQuery: "${searchQuery?js_string}",
	  minSearchTermLength:1,
	  maxSearchResults:1000,
	  initialSemanticNodeRef: "${semanticNodeRef?js_string}",
	  initialSemanticDocType: "${semanticDocType?js_string}"
   });
//]]></script>

<div id="${el}">
	<div id="${el}-body" class="search">

		   <div id="${el}-results" class="results"></div>

		   <div id="${el}-search-bar-bottom" class="yui-gc search-bar search-bar-bottom theme-bg-color-3 hidden">
			  <div class="yui-u first">
				 <div class="search-info">&nbsp;</div>
				 <div id="${args.htmlid}-paginator-bottom" class="paginator paginator-bottom"></div>
			  </div>
		   </div>
	</div>
</div>