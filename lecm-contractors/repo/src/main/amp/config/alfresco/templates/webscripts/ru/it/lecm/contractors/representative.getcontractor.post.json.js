var representativeRef = json.has("childRef") ? json.get("childRef") : null;
if (representativeRef != null) {
	var representative = search.findNode(representativeRef);
	if (representative != null) {
		model.representative = representative;
		model.contractors = contractorsRootObject.getContractorsForRepresentative(representativeRef);
	}
}
