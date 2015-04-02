if (typeof LogicECM == "undefined" || !LogicECM) {
	LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.Calendar = LogicECM.module.Calendar || {};

(function() {

	LogicECM.module.Calendar.Edit = function(htmlId) {
		LogicECM.module.Calendar.Edit.superclass.constructor.call(this, htmlId);
		return this;
	};

	YAHOO.extend(LogicECM.module.Calendar.Edit, LogicECM.module.Documents.Edit);

	YAHOO.lang.augmentObject(LogicECM.module.Calendar.Edit.prototype, {
		onFormSubmitSuccess: function (response) {
			window.location.href = Alfresco.constants.URL_PAGECONTEXT + "event?nodeRef=" + response.json.persistedObject;
		}
	}, true);
})();