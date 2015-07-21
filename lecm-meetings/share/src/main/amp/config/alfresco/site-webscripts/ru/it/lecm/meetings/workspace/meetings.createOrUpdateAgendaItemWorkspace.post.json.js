function main()
{
   model.success = false;
	
   var clientRequest = json.toString();
	
   var conn = remote.connect("alfresco");
   var repoResponse = conn.post("/lecm/meetings/workspace/check", clientRequest, "application/json");
   
   if (repoResponse.status == 401)
   {
      status.setCode(repoResponse.status, "error.loggedOut");
   }
   else
   {
		 var repoJSON = eval('(' + repoResponse + ')');
		 if (repoJSON.siteShortName)
		 {
			// Yes we did, now create the Surf objects in the web-tier and the associated configuration elements
			// Retry a number of times until success - remove the site on total failure
			for (var r=0; r<3 && !model.success; r++)
			{
			   var tokens = [];
			   tokens["siteid"] = repoJSON.siteShortName;
			   model.success = sitedata.newPreset("site-dashboard", tokens);
			}
			// if we get here - it was a total failure to create the site config - even after retries
			if (!model.success)
			{
			   // Delete the st:site folder structure and set error handler
			   conn.del("/api/sites/" + encodeURIComponent(repoJSON.siteShortName));
			   status.setCode(status.STATUS_INTERNAL_SERVER_ERROR, "error.create");
			}
		 }
   }
}
main();