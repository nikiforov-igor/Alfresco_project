function main() {
	var makePrimaryCount = 0;
	var makeNotPrimaryCount = 0;
	var linkRef = json.get("nodeRef");
	if (linkRef != null) {
		var aLinks = orgstructure.getEmployeeLinksByLink(linkRef);
		for each(var link in aLinks) {
			if (link.nodeRef == ("" + linkRef)) {
				link.properties["lecm-orgstr:employee-link-is-primary"] = true;
				link.save();
				makePrimaryCount++;
			} else if (link.properties["lecm-orgstr:employee-link-is-primary"] == true) {
				link.properties["lecm-orgstr:employee-link-is-primary"] = false;
				link.save();
				makeNotPrimaryCount++;
			}
		}
	}
    if (makePrimaryCount > 0) {
        var node = search.findNode(linkRef);
        var desc = "Должностная позиция #mainobject помечена как основная в подразделении #object1";
        businessJournal.fire(null, linkRef, "Сделать основной", desc, [node.getParent().getNodeRef()]);
    }
	model.makePrimaryCount = makePrimaryCount;
	model.makeNotPrimaryCount = makeNotPrimaryCount;
}

main();
