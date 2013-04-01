<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">
<import resource="classpath:/alfresco/site-webscripts/ru/it/lecm/documents/utils/document-utils.js">

function getActivityParameters(nodeRef, defaultValue)
{
   var cm = "{http://www.alfresco.org/model/content/1.0}",
      metadata = AlfrescoUtil.getMetaData(nodeRef, {});
   if (metadata.properties)
   {
      if (model.activityType == "document")
      {
         return (
         {
            itemTitle: metadata.properties[cm + 'title'] || metadata.properties[cm + 'name'],
            page: 'document-details',
            pageParams:
            {
               nodeRef: metadata.nodeRef
            }
         });
      }
      else if (model.activityType == "folder")
      {
         return (
         {
            itemTitle: metadata.properties[cm + 'title'] || metadata.properties[cm + 'name'],
            page: 'folder-details',
            pageParams:
            {
               nodeRef: metadata.nodeRef
            }
         });
      }
      else if (model.activityType == "link")
      {
         var lm = "{http://www.alfresco.org/model/linksmodel/1.0}";
         return (
         {
            itemTitle: metadata.properties[lm + "title"],
            page: 'links-view',
            pageParams:
            {
               linkId: metadata.properties[cm + "name"]
            }
         });
      }
      else if (model.activityType == "blog")
      {
         return (
         {
            itemTitle: metadata.properties[cm + 'title'] || metadata.properties[cm + 'name'],
            page: 'blog-postview',
            pageParams:
            {
               postId: metadata.properties[cm + "name"]
            }
         });
      }
   }
   return defaultValue;
}

function hasViewCommentPermission(nodeRef) {
    var url = '/lecm/security/api/getPermission?nodeRef=' + nodeRef + '&permission=_lecmPerm_CommentView';
    var result = remote.connect("alfresco").get(url);
    if (result.status != 200) {
        return false;
    }
    var permission = eval('(' + result + ')');
    return (("" + permission) == "true");
}

function hasCreateCommentPermission(nodeRef) {
    var url = '/lecm/security/api/getPermission?nodeRef=' + nodeRef + '&permission=_lecmPerm_CommentCreate';
    var result = remote.connect("alfresco").get(url);
    if (result.status != 200) {
    return false;
    }
var permission = eval('(' + result + ')');
return (("" + permission) == "true");
}

function main()
{
   AlfrescoUtil.param('nodeRef', null);
   AlfrescoUtil.param('site', null);
   AlfrescoUtil.param('maxItems', 10);
   AlfrescoUtil.param('activityType', null);
   AlfrescoUtil.param('hasPerm', false);

   if (!model.nodeRef)
   {
      // Handle urls that doesn't use nodeRef
      AlfrescoUtil.param('postId', null);
      if (model.postId)
      {
         // translate blog post "postId" to a nodeRef
         AlfrescoUtil.param('container', 'blog');
         model.nodeRef = AlfrescoUtil.getBlogPostDetailsByPostId(model.site, model.container, model.postId, {}).nodeRef;
      }
      else
      {
         AlfrescoUtil.param('linkId', null);
         if (model.linkId)
         {
            // translate link's "linkId" to a nodeRef
            AlfrescoUtil.param('container', 'links');
            model.nodeRef = AlfrescoUtil.getLinkDetailsByPostId(model.site, model.container, model.linkId, {}).nodeRef;
         }
      }
   }

   if (model.nodeRef) {
      var hasPerm = hasViewCommentPermission(model.nodeRef);
      model.hasPerm = hasPerm;
      model.hasCreateCommentPermission = hasCreateCommentPermission(model.nodeRef);
      if (hasPerm) {
      var documentDetails = DocumentUtils.getNodeDetails(model.nodeRef, model.site);
         if (documentDetails)
         {
            var activityParameters = getActivityParameters(model.nodeRef, null);
            if (activityParameters)
            {
                model.activityParameterJSON = jsonUtils.toJSONString(activityParameters);
            }
         }
      }
   }
}

main();
