<#assign htmlId = args.htmlid>
<#assign formId = args.htmlid + "-form">
<#assign formContainerId = formId + "-container" />

<#assign contractorId = htmlId + "_contractor" />
<#assign interTypeId = htmlId + "_prop_lecm-contractor_interaction-type" />
<#assign interTypeRadioId = htmlId + "_prop_lecm-contractor_interaction-type_radio" />
<#assign emailId = htmlId + "_prop_lecm-contractor_email" />

<@formLib.renderFormContainer formId = formId>

	<!-- Контрагент -->
	<@formLib.renderField field = form.fields["contractor"] />

	<!-- Способ взаимодействия -->
	<div class="form-field">
		<label for="${interTypeRadioId}">Взаимодействие с контрагентом, ЮЗД:<span class="mandatory-indicator">*</span></label>
		<div id="${interTypeRadioId}"></div>
		<input id="${interTypeId}" class="hidden" name="prop_lecm-contractor_interaction-type" type="text" value="">
	</div>

	<!-- Email -->
	<@formLib.renderField field = form.fields["prop_lecm-contractor_email"] />

</@formLib.renderFormContainer>

<@formLib.renderFormsRuntime formId = formId />

<script type="text/javascript">
	(function () {
		var Bubbling = YAHOO.Bubbling,
			Dom = YAHOO.util.Dom,
			Element = YAHOO.util.Element,
			Ajax = Alfresco.util.Ajax,
			ComponentManager = Alfresco.util.ComponentManager,

			interTypeInput,
			emailInput,

			interTypeField,
			emailField,

			sendForm,
			sendDialog;

		YAHOO.util.Event.onContentReady("${formContainerId}", init);

		function bindAjaxTo(url, dataObj) {
			return function makeDataRequest() {
				debugger;
				Ajax.jsonRequest({
					method: "POST",
					url: Alfresco.constants.PROXY_URI_RELATIVE + url,
					dataObj: dataObj,
					successCallback: {
						fn: function(response) {
							var message,
								authSimpleDialog,

								responses = response.json,

								good = responses.filter(function(v) { return v.gateResponse.responseType == "OK"; }),
								bad = responses.filter(function(v) { return v.gateResponse.responseType != "OK"; }),
								partner = responses.filter(function(v) { return v.gateResponse.responseType == "PARTNER_ERROR"; }),
								unauthorized = responses.filter(function(v) { return v.gateResponse.responseType == "UNAUTHORIZED"; });

							console.log(">>> Всего отправлено документов: " + responses.length);
							console.log("Документов, со статусом \"ОК\": " + good.length);
							console.log("Документов, со статусом отличным от \"OK\": " + bad.length);
							console.log("Документов, со статусом \"PARTNER_ERROR\": " + partner.length);

							// Выходим, если всё хорошо
							if(good.length == responses.length) {
								message = (responses.length > 1) ? "Документы успешно отправлены" : "Документ успешно отправлен";
								Alfresco.util.PopupManager.displayMessage({ text: message });
								return;
							}

							// FUTURE: ...
							if(partner.length == 0) {
								Alfresco.util.PopupManager.displayMessage({ text: "Ошибок партнёра не найдено" });
							}

							if(unauthorized.length == 0) {
								return;
							}

							// Показываем форму авторизации, в ином случае
							authSimpleDialog = new Alfresco.module.SimpleDialog("zxcv-asdf-1234-pppp-qqqq-form");

							authSimpleDialog.setOptions({
								width: "50em",
								templateUrl: Alfresco.constants.URL_SERVICECONTEXT + "components/form",
								templateRequestParams: {
									itemKind: "type",
									itemId: "lecm-orgstr:employees",
									formId: "auth-form",
									mode: "create",
									showCancelButton: "true",
									submitType: "json"
								},
								actionUrl: null,
								destroyOnHide: true,
								doBeforeAjaxRequest: {
									fn: function() {
										var currentContainer = cryptoAppletModule.getCurrentContainer();

										if(currentContainer == "") {
											Alfresco.util.PopupManager.displayMessage({
												text: "Необходимо выбрать сертификат!"
											});
										} else {
											cryptoAppletModule.unicloudAuth(currentContainer, makeDataRequest);
										}

										return false;
									}
								},
								onFailure: {
									fn: function() {
										console.log("Auth Failed!");
									}
								}
							});

							authSimpleDialog.show();
						}
					},
					failureCallback: {
						fn: function() {
							console.log("Ajax Failure!");
						}
					}
				});
			}
		}

		function init() {

			interTypeInput = Dom.get("${interTypeId}");
			emailInput = Dom.get("${emailId}");

			interTypeField = new Element(interTypeInput.parentNode);
			emailField = new Element(emailInput.parentNode);

			debugger;

			sendForm = ComponentManager.get("${htmlId}");
			sendDialog = ComponentManager.get("${htmlId}").dialog;

			// При закрытии формы удаляем созданные Bubbling слои
			sendDialog.subscribe("hide", function() {
				Bubbling.unsubscribe("sendToContractorChanged");
			});

			// Прячем поля
			interTypeField.addClass("hidden");
			emailField.addClass("hidden");

			// Создаём радио
			interTypeGroup = new YAHOO.widget.ButtonGroup({
				container: "${interTypeRadioId}"
			});

			interTypeGroup.addButtons([
				{ label: "Используя спецоператора", value: "SPECOP" },
				{ label: "Используя Email", value: "EMAIL" }
			]);

			// Исправляем стили
			interTypeGroup.getButton(0).setStyle("margin-left", "0");
			interTypeGroup.getButton(1).setStyle("margin-left", "0");

			// Связываем радио и interaction-type-input
			interTypeGroup.subscribe("valueChange", function(event) {
				var newValue = event.newValue;

				interTypeInput.value = newValue;

				if(newValue == "EMAIL") {
					emailField.removeClass("hidden");
					emailInput.focus();
					emailInput.select();
				}

				if(newValue == "SPECOP") {
					emailField.addClass("hidden");
				}
			});

			sendForm.options.doBeforeAjaxRequest = {
				fn: function(config, obj) {
					var dataObj = config.dataObj,
						contentRef = sendForm.options.contentRef;

					dataObj.content = [contentRef];
					dataObj.partner = dataObj["contractor"];
					dataObj.interactionType = dataObj["prop_lecm-contractor_interaction-type"];
					dataObj.email = dataObj["prop_lecm-contractor_email"];

					bindAjaxTo("lecm/signed-docflow/sendContentToPartner", dataObj)();

					return false;
				},
				scope: sendForm
			};

			// "sendToContractorChanged" - это название события changeItemsFireAction (share-config-custom.xml)
			Bubbling.on("sendToContractorChanged", function(layer, args) {
				var selectedContractors = args[1].selectedItems,
					selectedContractor = Object.keys(selectedContractors)[0];

				if(selectedContractor) {
					checkContractor(selectedContractor);
				} else {
					interTypeField.addClass("hidden");
					emailField.addClass("hidden");

					emailInput.value = "";
				}
			});
		}

		// Проверяет контрагента на тип взаимодействия и, в зависимости от этого, выбирает "правильное радио"
		function checkContractor(contractorRef) {
			function onSuccessCallback(response) {
				var properties = response.json.item.node.properties,

					interType = properties["lecm-contractor:interaction-type"],
					email = properties["lecm-contractor:email"];

				emailInput.value = email;

				if(interType == "EMAIL") {
					interTypeGroup.check(1);
					emailField.removeClass("hidden");
					emailInput.focus();
					emailInput.select();
				}

				if(interType == "SPECOP") {
					interTypeGroup.check(0);
				}

				interTypeField.removeClass("hidden");
			}

			function onFailureCallback(response) {
				// NOP
			}

			Ajax.jsonRequest({
				url: Alfresco.constants.PROXY_URI_RELATIVE + "slingshot/doclib2/node/" + contractorRef.replace("://", "/"),
				successCallback: { fn: onSuccessCallback },
				failureCallback: { fn: onFailureCallback }
			});
		}
	})();
</script>