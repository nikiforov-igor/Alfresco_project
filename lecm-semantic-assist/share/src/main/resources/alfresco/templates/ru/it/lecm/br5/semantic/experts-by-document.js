function main() {
	var expertlist = getExpertsData();
	if (typeof expertlist!= "undefined" && expertlist!=null){
		model.dataExperts = expertlist;
	}
}

function getExpertsData() {
	var nodeRef = page.url.args.nodeRef;
	var maxCount = page.url.args.maxCount;
	var url;
	var expertlist = null;
	if (typeof nodeRef!= "undefined" && nodeRef!=null && nodeRef.length!=0){
		url = '/lecm/br5/semantic/experts/experts-by-document?sDocument='+nodeRef;
	}
	if (maxCount){
		url += "&maxCount="+maxCount;
	}

    var result = remote.connect("alfresco").get(url);

    if (result.status == 200) {
        var expertlist = eval('(' + result + ')');
    }

    return expertlist;
}

main();