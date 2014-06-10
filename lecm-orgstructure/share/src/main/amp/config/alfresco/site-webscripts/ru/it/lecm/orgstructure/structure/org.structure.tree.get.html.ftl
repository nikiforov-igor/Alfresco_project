<@script type="text/javascript" src="${url.context}/res/components/form/date-range.js"></@script>
<@script type="text/javascript" src="${url.context}/res/components/form/number-range.js"></@script>
<@link rel="stylesheet" type="text/css" href="${url.context}/res/components/search/search.css" />

<!-- Historic Properties Viewer -->
<@script type="text/javascript" src="${url.context}/res/scripts/lecm-base/components/versions.js"></@script>
<@link rel="stylesheet" type="text/css" href="${url.context}/res/modules/document-details/historic-properties-viewer.css" />

<!-- Tree -->
<@link rel="stylesheet" type="text/css" href="${url.context}/res/yui/treeview/assets/skins/sam/treeview.css"/>
<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/lecm-orgstructure/orgstructure-tree.css" />
<@script type="text/javascript" src="${url.context}/res/scripts/lecm-orgstructure/orgstructure-tree.js"></@script>
<@script type="text/javascript" src="${url.context}/res/scripts/lecm-orgstructure/orgstructure-utils.js"></@script>


<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>
<#import "/ru/it/lecm/orgstructure/components/orgstructure-tree.ftl" as orgTree/>
<#assign id = args.htmlid>
<#assign realDelete = false/>
<#if fullDelete??>
	<#assign realDelete = fullDelete/>
</#if>


<@orgTree.tree nodeType="lecm-orgstr:organization-unit" itemType="lecm-orgstr:organization-unit" 
            fullDelete=realDelete maxNodesOnTopLevel=1 markOnCreateAsParent=true/>


