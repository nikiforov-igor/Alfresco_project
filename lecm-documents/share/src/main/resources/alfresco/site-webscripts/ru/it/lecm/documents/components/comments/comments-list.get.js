<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">
<import resource="classpath:/alfresco/site-webscripts/ru/it/lecm/documents/utils/document-utils.js">
<import resource="classpath:/alfresco/site-webscripts/ru/it/lecm/documents/utils/permission-utils.js">

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

function main()
{
   AlfrescoUtil.param('nodeRef', null);
   AlfrescoUtil.param('site', null);
   AlfrescoUtil.param('maxItems', 10);
   AlfrescoUtil.param('activityType', null);
   AlfrescoUtil.param('hasViewCommentPerm', false);

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
      model.hasViewCommentPerm = hasPermission(model.nodeRef,'_lecmPerm_CommentView');
      if (model.hasViewCommentPerm) {
      model.hasCreateCommentPerm = hasPermission(model.nodeRef,'_lecmPerm_CommentCreate');
      model.hasDeleteCommentPerm = hasPermission(model.nodeRef,'_lecmPerm_CommentDelete');
//      model.hasEditCommentPerm = hasPermission(model.nodeRef,'_lecmPerm_CommentEdit');
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
