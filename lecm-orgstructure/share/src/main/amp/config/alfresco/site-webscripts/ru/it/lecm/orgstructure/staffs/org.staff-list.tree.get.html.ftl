<!-- Tree -->
<@link rel="stylesheet" type="text/css" href="${url.context}/res/yui/treeview/assets/skins/sam/treeview.css"/>
<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/lecm-orgstructure/orgstructure-tree.css" />
<@script type="text/javascript" src="${url.context}/res/scripts/lecm-orgstructure/orgstructure-tree.js"></@script>
<@script type="text/javascript" src="${url.context}/res/scripts/lecm-orgstructure/orgstructure-utils.js"></@script>

<#import "/ru/it/lecm/orgstructure/components/orgstructure-tree.ftl" as orgTree/>

<@orgTree.tree nodeType="lecm-orgstr:organization-unit" itemType="lecm-orgstr:staff-list" fullDelete=true>
</@orgTree.tree>


