if (typeof LogicECM == "undefined" || !LogicECM) {
	LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.Calendar = LogicECM.module.Calendar || {};

(function() {

	LogicECM.module.Calendar.Create = function(htmlId) {
		LogicECM.module.Calendar.Create.superclass.constructor.call(this, htmlId);
		return this;
	};

	YAHOO.extend(LogicECM.module.Calendar.Create, LogicECM.module.Documents.Create);

	YAHOO.lang.augmentObject(LogicECM.module.Calendar.Create.prototype, {
		onFormSubmitSuccessRedirect: function(nodeRef) {
			var reloadCheckbox = Dom.get('document-form-close-and-create-new');
			if (reloadCheckbox && reloadCheckbox.checked) {
				window.location.reload();
			} else if (this.options.backUrl != null) {
				window.location.href = Alfresco.constants.URL_PAGECONTEXT + decodeURIComponent(this.options.backUrl);
			} else {
				window.location.href = Alfresco.constants.URL_PAGECONTEXT + 'event?nodeRef=' + nodeRef;
			}
		}
	}, true);
})();