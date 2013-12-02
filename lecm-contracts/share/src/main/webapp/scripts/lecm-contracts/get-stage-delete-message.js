if (typeof LogicECM == "undefined" || !LogicECM) {
	var LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.Contracts = LogicECM.module.Contracts || {};

LogicECM.module.Contracts.getStageDeleteMessage = function(items, itemsString) {
	if (items.length > 1) {
		return this.msg("message.confirm.delete.group.description", items.length);
	} else {
		if (items[0].itemData["assoc_lecm-contract-table-structure_attachments-temp-assoc"]) {
			return this.msg("message.confirm.delete.stageWithAttachment.description");
		} else {
			return this.msg("message.confirm.delete.description", itemsString);
		}
	}
};
