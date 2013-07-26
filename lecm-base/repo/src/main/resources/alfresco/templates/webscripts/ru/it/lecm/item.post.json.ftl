<#--макрос скопирован из оригинальног \alfresco\templates\webscripts\org\alfresco\slingshot\documentlibrary-v2\item.lib.ftl
Изменения: webdavUrl теперь всегда пустой-->
<#macro itemJSON item>
    <#assign node = item.node>
    <#assign version = "1.0">
    <#if node.hasAspect("cm:versionable") && node.versionHistory?size != 0><#assign version = node.versionHistory[0].versionLabel></#if>
    <#escape x as jsonUtils.encodeJSONString(x)>
    "version": "${version}",
    "webdavUrl": "",
        <#if item.activeWorkflows?? && (item.activeWorkflows?size > 0)>"activeWorkflows": ${item.activeWorkflows?size?c},</#if>
        <#if item.isFavourite??>"isFavourite": ${item.isFavourite?string},</#if>
        <#if (item.workingCopyJSON?length > 2)>"workingCopy": <#noescape>${item.workingCopyJSON}</#noescape>,</#if>
        <#if item.likes??>"likes":
        {
        "isLiked": ${item.likes.isLiked?string},
        "totalLikes": ${item.likes.totalLikes?c}
        }</#if>,
    "location":
    {
    "repositoryId": "${(node.properties["trx:repositoryId"])!(server.id)}",
        <#if item.location.site??>
        "site":
        {
        "name": "${(item.location.site)!""}",
        "title": "${(item.location.siteTitle)!""}",
        "preset": "${(item.location.sitePreset)!""}"
        },
        </#if>
        <#if item.location.container??>
        "container":
        {
        "name": "${(item.location.container)!""}",
        "type": "${(item.location.containerType)!""}"
        },
        </#if>
    "path": "${(item.location.path)!""}",
    "file": "${(item.location.file)!""}",
    "parent":
    {
        <#if (item.location.parent.nodeRef)??>
        "nodeRef": "${item.location.parent.nodeRef}"
        </#if>
    }
    }
    </#escape>
</#macro>
<#escape x as jsonUtils.encodeJSONString(x)>
{
   "versionable": ${data.versionable?string},
   "metadata":
   {
      "parent":
      {
      <#if data.parent??>
         <#assign parentNode = data.parent.node>
         "nodeRef": "${parentNode.nodeRef}",
         "permissions":
         {
            "userAccess":
            {
            <#list data.parent.userAccess?keys as perm>
               <#if data.parent.userAccess[perm]?is_boolean>
               "${perm?string}": ${data.parent.userAccess[perm]?string}<#if perm_has_next>,</#if>
               </#if>
            </#list>
            }
         }
      </#if>
      }
   },
   "item":
   {
      <@itemJSON data.item />
   }
}
</#escape>
