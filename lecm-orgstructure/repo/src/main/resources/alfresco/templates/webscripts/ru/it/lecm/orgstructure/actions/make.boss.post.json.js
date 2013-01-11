function main() {
	var makeBossCount = 0;
	var makeNotBossCount = 0;
	var nodeRef = json.get("nodeRef");
	if (nodeRef != null) {
		var node = search.findNode(nodeRef);
		var allStaffs = orgstructure.getAllStaffLists(node.parent.nodeRef);
		for each(var staff in allStaffs) {
			if (staff.nodeRef == ("" + nodeRef)) {
				staff.properties["lecm-orgstr:staff-list-is-boss"] = true;
				staff.save();
				makeBossCount++;
			} else if (staff.properties["lecm-orgstr:staff-list-is-boss"] == true) {
				staff.properties["lecm-orgstr:staff-list-is-boss"] = false;
				staff.save();
				makeNotBossCount++;
			}
		}
	}
    if (makeBossCount > 0) {
        var desc = "Должностная позиция #mainobject помечена как руководящая в подразделении #object1";
        businessJournal.fire(null, nodeRef, "Сделать руководящей", desc, [node.getParent().getNodeRef()]);
    }
	model.makeBossCount = makeBossCount;
	model.makeNotBossCount = makeNotBossCount;
}

main();
