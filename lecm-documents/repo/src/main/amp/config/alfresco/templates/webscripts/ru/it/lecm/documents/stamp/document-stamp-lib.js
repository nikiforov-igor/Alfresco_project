function getDocumentStamp(node, code) {
	var stamp = documentStamp.getStamp(node, code);
	return stamp == null ? {error:"STAMP.NULL"} : stamp;
}
