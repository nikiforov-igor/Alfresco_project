<#import "/ru/it/lecm/base-share/components/lecm-datagrid.ftl" as grid/>
<#import "/ru/it/lecm/orgstructure/components/orgstructure-tree.ftl" as orgTree/>
<#assign id = args.htmlid>
<#assign realDelete = false/>
<#if fullDelete??>
	<#assign realDelete = fullDelete/>
</#if>


<@orgTree.tree nodeType="lecm-orgstr:organization-unit" itemType="lecm-orgstr:organization-unit" 
            fullDelete=realDelete maxNodesOnTopLevel=1 markOnCreateAsParent=true bubblingLabel="orgstructure"/>


