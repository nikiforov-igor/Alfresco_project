reasons = search.luceneSearch("TYPE:\"lecm-absence:reason\"")

if (reasons != null) {
	for (i = 0; i < reasons.length; i++) {
		if (reasons[i].properties["cm:name"] == "\u0414\u0440\u0443\u0433\u043e\u0435") { // "Другое"
			model.nodeRef = reasons[i].nodeRef.toString();
		}
	}
} else {
	logger.log("ERROR: No absence reasons created!");
}