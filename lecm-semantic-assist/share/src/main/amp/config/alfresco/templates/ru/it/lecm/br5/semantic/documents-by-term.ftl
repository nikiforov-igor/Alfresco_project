<#include "/org/alfresco/include/alfresco-template.ftl" />
<#include "/org/alfresco/include/documentlibrary.inc.ftl" />

<@templateHeader>
	<@script type="text/javascript" src="${url.context}/res/scripts/semantic-assist/documents-by-document-list.js"></@script>
</@>

<#assign el="semantic-documents-by-tag-htmlid">
<#import "/ru/it/lecm/base/base-page.ftl" as bpage/>

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
	  initialSemanticTag: "${semanticTag?js_string}",
	  initialSemanticNodeRef: "${semanticNodeRef?js_string}",
	  initialSemanticDocType: "${semanticDocType?js_string}"
   });
//]]></script>

<@bpage.basePage showToolbar=false>

	<div id="semantic-documents-by-tag-htmlid">
		<div id="${el}-body" class="search">
		   <div id="${el}">
			   <div id="${el}-results" class="results"></div>

			   <div id="${el}-search-bar-bottom" class="yui-gc search-bar search-bar-bottom theme-bg-color-3 hidden">
				  <div class="yui-u first">
					 <div class="search-info">&nbsp;</div>
					 <div id="${el}-paginator-bottom" class="paginator paginator-bottom"></div>
				  </div>
			   </div>
			</div>
		</div>
	</div>

</@bpage.basePage>