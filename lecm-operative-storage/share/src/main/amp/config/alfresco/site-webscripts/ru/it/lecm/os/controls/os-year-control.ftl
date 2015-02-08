<#include "/ru/it/lecm/base-share/components/controls/number.ftl">
<script>

	(function() {
		var isCentralized = false;
		var subscribed = false;
		var orgXPath;

		function getOrganizationsPath() {
			Alfresco.util.Ajax.jsonGet(
			{
				url: Alfresco.constants.PROXY_URI + "/lecm/os/nomenclature/getOrgPath",
				successCallback:
				{
					fn: function (response) {
						var oResults = response.json;
						orgXPath = oResults.xPath;
					}
				}
			});
		}

		function getSettings() {
			Alfresco.util.Ajax.jsonGet(
			{
				url: Alfresco.constants.PROXY_URI + "/lecm/operative-storage/checkCentralized",
				successCallback:
				{
					fn: function (response) {
						var oResults = response.json;
						isCentralized = oResults.isCentralized;
					}
				}
			});
		}

		getSettings();
		// getOrganizationsPath();

		function messageHandler() {
			if(isCentralized) {
				return 'Неверное значение';
			}
			var orgValue = this.getFormData()['assoc_lecm-os_nomenclature-organization-assoc_added'];
			if(!orgValue) {
				return 'Необходимо выбрать организацию';
			}

			return 'Выбранный год уже существует';
		}

		function reInitializeThisShit() {
			debugger;
			if(isCentralized) {
				LogicECM.module.Base.Util.disableControl("${args.htmlid}", "lecm-os:nomenclature-organization-assoc");
			}

			Alfresco.util.Ajax.jsonGet(
			{
				url: Alfresco.constants.PROXY_URI + "/lecm/os/nomenclature/getOrgPath",
				successCallback:
				{
					fn: function (response) {
						var oResults = response.json;
						orgXPath = oResults.xPath;
						LogicECM.module.Base.Util.reInitializeControl("${args.htmlid}", "lecm-os:nomenclature-organization-assoc", {
								rootLocation: orgXPath
							});
					}
				}
			});

			LogicECM.module.Base.Util.reInitializeControl("${args.htmlid}", "lecm-os:nomenclature-organization-assoc", {
				rootLocation: orgXPath
			});
		}

		YAHOO.Bubbling.on('formValueChanged', function(layer, args) {
			YAHOO.Bubbling.fire('mandatoryControlValueUpdated');
		});

		YAHOO.Bubbling.on('afterFormRuntimeInit', function() {
			if(!subscribed) {

				reInitializeThisShit();

				YAHOO.Bubbling.fire('registerValidationHandler', {
					message: messageHandler,
					fieldId: '${fieldHtmlId}',
					handler: validationHandler.bind(this),
					when: 'keyup'
				});
				subscribed = true;
			}
		});

		function validationHandler(field, args, event, form, silent, message) {
				debugger;
				var valid = false;
				var orgValue = form.getFormData()['assoc_lecm-os_nomenclature-organization-assoc_added'];

				if(field.value.length != 4 || (!orgValue && !isCentralized)) {
					return false;
				}

				$.ajax({
					url: Alfresco.constants.PROXY_URI + "lecm/os/nomenclature/isYearUniq?year=" + field.value + "&orgNodeRef=" + orgValue,
					context: this,
					success: function (response) {
						debugger;
							var oResults = response;
							if (oResults != null && oResults.uniq) {
								valid = true;
							}
						},
					async: false
				});

				return valid;
			}


	})();
</script>