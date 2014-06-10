function main()
{
   //http://localhost:8080/alfresco/service/api/classes/lecm-document_base/subclasses?r=false
   //var uri = "/api/blog/post/site/" + url.templateArgs.site +"/" + url.templateArgs.container +"/" + url.templateArgs.path;
   //http://localhost:8082/MavenAlfrescoShareModule/service/components/documentlibrary/data/doclist/all/node/alfresco/company/home/%D0%A1%D0%BB%D0%BE%D0%B2%D0%B0%D1%80%D1%8C%20%D0%B4%D0%B0%D0%BD%D0%BD%D1%8B%D1%85/%D0%9C%D0%BE%D0%B4%D0%B5%D0%BB%D0%B8?filter=path&size=50&pos=1&sortAsc=true&sortField=cm%3Aname&libraryRoot=alfresco%3A%2F%2Fcompany%2Fhome&view=browse&noCache=1373271352122
   var uri = "/api/classes/lecm-document_base/subclasses?r=false";
   var connector = remote.connect("alfresco");
   var result = connector.get(encodeURI(uri));
   if (result.status.code == status.STATUS_OK)
   {
      // Strip out possible malicious code
      var docs = eval("(" + result.response + ")");
      if (docs && docs.item && docs.item.content) {
    	  docs.item.content = stringUtils.stripUnsafeHTML(docs.item.content);
      }
      return jsonUtils.toJSONString(docs);
   }
   else
   {
      status.code = result.status.code;
      status.message = msg.get("message.failure");
      status.redirect = true;
   }
}

model.docmodels = main(); 