function main() {
	var taglist = getTags();
	if (typeof taglist!= "undefined" && taglist!=null){
		model.tagsList = taglist;
	}
}

function getTags() {
    var tags = [];
	var nodeRef = page.url.args.nodeRef;
	var url;
	var tagsList = null;
	if (typeof nodeRef!= "undefined" && nodeRef!=null && nodeRef.length!=0){
		url = '/lecm/br5/semantic/cloud/document-tags-cloud?sDocument='+nodeRef;
	}

    var result = remote.connect("alfresco").get(url);

    if (result.status == 200) {
        var tagsList = eval('(' + result + ')');
    }

    return tagsList;
}

main();