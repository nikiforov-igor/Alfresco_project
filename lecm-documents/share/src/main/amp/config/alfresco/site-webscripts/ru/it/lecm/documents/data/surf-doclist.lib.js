var lecmDoclist_main = function surfDoclist_main()
{
   var json = "{}",
      webscript = url.templateArgs.webscript,
      params = encodeURIComponent(url.templateArgs.params).replace(/%2f/ig,"/"),
      dataUrl = "/lecm/document/" + webscript + "/" + params,
      argsArray = [];

   // Need to reconstruct and encode original args
   if (args.length > 0)
   {
      for (arg in args)
      {
         argsArray.push(arg + "=" + encodeURIComponent(args[arg].replace(/%25/g,"%2525")));
      }
      
      dataUrl += "?" + argsArray.join("&");
   }
   
   var result = remote.call(dataUrl);
   if (result.status == 200)
   {
      var obj = eval('(' + result + ')');
      if (obj && (obj.item || obj.items))
      {
         DocList.processResult(obj,
         {
            actions: true,
            indicators: true,
            metadataTemplate: true
         });
         json = jsonUtils.toJSONString(obj);
      }
   }
   else
   {
      status.setCode(result.status);
   }

   model.json = json;
};