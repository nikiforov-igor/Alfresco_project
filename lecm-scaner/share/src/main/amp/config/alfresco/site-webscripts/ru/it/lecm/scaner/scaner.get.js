var targertURL,
	fileName;
	
targetURL = args["target"];
fileName = args["fileName"];

if(targetURL == null){
	targetURL = "/share/proxy/alfresco/scanUpload";
}

if(fileName == null){
	fileName = "ScannedDocument";
}

model.targetURL = targetURL;
model.fileName = fileName;