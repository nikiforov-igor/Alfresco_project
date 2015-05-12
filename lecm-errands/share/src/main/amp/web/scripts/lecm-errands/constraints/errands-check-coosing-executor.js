if (typeof LogicECM == "undefined" || !LogicECM) {
	LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.Errands = LogicECM.module.Errands || {};

LogicECM.module.Errands.hasRoleChoosingExecutor = false;
LogicECM.module.Errands.hasRoleChoosingExecutorRequestSend = false;

LogicECM.module.Errands.checkChoosingInitiatorValidation =
	function (field, args,  event, form, silent, message) {
		var formId = form.formId.replace("-form", "");

		if (LogicECM.module.Errands.hasRoleChoosingExecutor) {
			LogicECM.module.Base.Util.enableControl(formId, "lecm-errands:initiator-assoc");
		} else {
			LogicECM.module.Base.Util.disableControl(formId, "lecm-errands:initiator-assoc");
		}

		if (!LogicECM.module.Errands.hasRoleChoosingExecutorRequestSend) {
			LogicECM.module.Errands.hasRoleChoosingExecutorRequestSend = true;

			Alfresco.util.Ajax.request(
				{
					url: Alfresco.constants.PROXY_URI + "lecm/orgstructure/isCurrentEmployeeHasBusinessRole",
					dataObj: {
						roleId: 'ERRANDS_CHOOSING_INITIATOR'
					},
					successCallback: {
						fn: function (response) {
							LogicECM.module.Errands.hasRoleChoosingExecutor = response.json;
							if (LogicECM.module.Errands.hasRoleChoosingExecutor) {
								LogicECM.module.Base.Util.enableControl(formId, "lecm-errands:initiator-assoc");
							}
						}
					},
					failureMessage: {
						fn: function (response) {
							alert(response.responseText);
						}
					}
				});
		}

		return true;
	};
