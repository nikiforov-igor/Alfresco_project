/* global YAHOO, Alfresco */

if (typeof LogicECM == 'undefined' || !LogicECM) {
	LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.ModelEditor = LogicECM.module.ModelEditor || {};

(function () {
	LogicECM.module.ModelEditor.FormManager = function (containerId, options, messages) {
		LogicECM.module.ModelEditor.FormManager.superclass.constructor.call(this, containerId);
		this.name = 'LogicECM.module.ModelEditor.FormManager';
		Alfresco.util.ComponentManager.reregister(this);
		this.options = YAHOO.lang.merge(this.options, LogicECM.module.ModelEditor.FormManager.superclass.options);
		this.setOptions(options);
		this.setMessages(messages);

		YAHOO.Bubbling.on('afterFormRuntimeInit', this.onAfterFormRuntimeInit, this);

		return this;
	};

	YAHOO.extend(LogicECM.module.ModelEditor.FormManager, Alfresco.component.FormManager, {

		component: null,
		runtime: null,

		_doSomething: function (obj) {
			var form = YAHOO.util.Dom.get(this.runtime.formId);
			if (obj.model.model_description) {
				form.elements['prop_cm_lecmModelDescription'].value = obj.model.model_description;
			}
			if (obj.model.typeTitle) {
				form.elements['prop_cm_lecmTypeTitle'].value = obj.model.typeTitle;
			}
			if (obj.model.parentRef) {
				form.elements['prop_cm_lecmParentRef'].value = obj.model.parentRef;
			}
			if (obj.model.presentString) {
				form.elements['prop_cm_lecmPresentString'].value = obj.model.presentString;
			}
			if (obj.model.armUrl) {
				form.elements['prop_cm_lecmArmUrl'].value = obj.model.armUrl;
			}
			if (obj.model.createUrl) {
				form.elements['prop_cm_lecmCreateUrl'].value = obj.model.createUrl;
			}
			if (obj.model.viewUrl) {
				form.elements['prop_cm_lecmViewUrl'].value = obj.model.viewUrl;
			}
			if (obj.model.authorProperty) {
				form.elements['prop_cm_lecmAuthorProperty'].value = obj.model.authorProperty;
			}
			if (obj.model.regNumbersProperties) {
				form.elements['prop_cm_lecmRegNumbersProperties'].value = obj.model.regNumbersProperties;
			}
			if (obj.model.rating) {
				form.elements['prop_cm_lecmRating'].value = obj.model.rating;
			}
			if (obj.model.signed) {
				form.elements['prop_cm_lecmSigned'].value = obj.model.signed;
			}
			if (obj.model.model_description) {

			}
		},

		onBeforeFormRuntimeInit: function (layer, args) {
			LogicECM.module.ModelEditor.FormManager.superclass.onBeforeFormRuntimeInit.call(this, layer, args);
			args[1].runtime.doBeforeFormSubmit = {
				scope: this,
				obj: null,
				fn: this.onBeforeFormSubmit
			};
		},

		onAfterFormRuntimeInit: function (layer, args) {
			this.component = args[1].component;
			this.runtime = args[1].runtime;
			// здесь мы получим XML, или поднимем из него json, или куда-нибудь передадим
			LogicECM.module.ModelEditor.ModelPromise.then(this._doSomething, this);
		},

		onBeforeFormSubmit: function (form, obj) {
			debugger;
		}
	}, true);
})();
