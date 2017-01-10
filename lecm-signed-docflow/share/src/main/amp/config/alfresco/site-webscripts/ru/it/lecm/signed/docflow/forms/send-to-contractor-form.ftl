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
		<label for="${interTypeRadioId}">${msg("lecm.signdoc.lbl.inter.counterp")}:<span class="mandatory-indicator">*</span></label>
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

			sendForm = ComponentManager.get("${htmlId}"),
			sendDialog,

			interTypeInput,
			emailInput,

			interTypeField,
			emailField;

		// Делаем нужный нам запрос, вместо submit'а формы
		sendForm.options.doBeforeAjaxRequest = {
			fn: function(config, obj) {
				var dataObj = config.dataObj,
					contentRef = sendForm.options.contentRef;

				dataObj.content = [contentRef];
				dataObj.partner = dataObj["contractor"];
				dataObj.interactionType = dataObj["prop_lecm-contractor_interaction-type"];
				dataObj.email = dataObj["prop_lecm-contractor_email"];

				bindAjaxTo("lecm/signed-docflow/sendContentToPartner", dataObj)();

				// Отменяем submit формы
				return false;
			},
			scope: sendForm
		};

		YAHOO.util.Event.onContentReady("${formContainerId}", checkDocument);

		function bindAjaxTo(url, dataObj) {
			return function makeDataRequest() {

				var loadingPopup = Alfresco.util.PopupManager.displayMessage({
					text: "${msg('lecm.signdoc.msg.doc.sending')}",
					spanClass: "wait",
					displayTime: 0,
					modal: true
				});

				loadingPopup.center();

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

							function hideAndReload() {
								loadingPopup.destroyWithAnimationsStop();
								window.location.reload();
							}

							loadingPopup.destroyWithAnimationsStop();

							console.log(">>> ${msg('lecm.signdoc.msg.doc.sent.total')}: " + responses.length);
							console.log("${msg('lecm.signdoc.msg.docs.with.status')} \"ОК\": " + good.length);
							console.log("${msg('lecm.signdoc.msg.docs.with.status.other')} \"OK\": " + bad.length);
							console.log("${msg('lecm.signdoc.msg.docs.with.status')} \"PARTNER_ERROR\": " + partner.length);

							// Выходим, если всё хорошо
							if(good.length == responses.length) {
								message = (responses.length > 1) ? "${msg('lecm.signdoc.msg.docs.sent.success')}" : "${msg('lecm.signdoc.msg.doc.sent.success')}";
								loadingPopup = Alfresco.util.PopupManager.displayMessage({ text: message });
								YAHOO.lang.later(2500, null, hideAndReload);
								return;
							}
							if(unauthorized.length == 0) {
								return;
							}

							// Показываем форму авторизации, в ином случае
							authSimpleDialog = new Alfresco.module.SimpleDialog("${htmlId}-auth-form");

							authSimpleDialog.setOptions({
								width: "50em",
								templateUrl: Alfresco.constants.URL_SERVICECONTEXT + "components/form",
								templateRequestParams: {
									itemKind: "type",
									itemId: "lecm-orgstr:employees",
									formId: "auth-form",
									mode: "create",
									showCancelButton: "true",
									submitType: "json",
									showCaption: false
								},
								actionUrl: null,
								destroyOnHide: true,
								doBeforeDialogShow:{
									fn: function(form, simpleDialog) {
										simpleDialog.dialog.setHeader("${msg('lecm.signdoc.msg.auth.req')}");
									}
								},
								doBeforeAjaxRequest: {
									fn: function() {
											CryptoApplet.unicloudAuth({successCallback: {fn: makeDataRequest, scope: this}});
										return false;
									}
								},
								onFailure: {
									fn: function() {
										Alfresco.util.PopupManager.displayMessage({
											text: "${msg('lecm.signdoc.msg.open.auth.form.failed')}"
										});
									}
								}
							});

							authSimpleDialog.show();
						}
					},
					failureCallback: {
						fn: function() {
							loadingPopup.destroyWithAnimationsStop();
							Alfresco.util.PopupManager.displayMessage({
								text: "${msg('lecm.signdoc.msg.doc.send.failed')}"
							});
						}
					}
				});
			}
		}

		function init(response) {

			var isValue = YAHOO.lang.isValue,

				clearSelectionButton,

				interType = response.json.interactionType,
				contrRef = response.json.contractorRef,
				contrEmail = response.json.contractorEmail,

				treeViewer = Alfresco.util.ComponentManager.get("${htmlId}_contractor");

			sendDialog = sendForm.dialog;

			// Собираем поля формы
			interTypeInput = Dom.get("${interTypeId}");
			emailInput = Dom.get("${emailId}");

			// Получаем "обёртки" через parentNode
			interTypeField = new Element(interTypeInput.parentNode);
			emailField = new Element(emailInput.parentNode);

			// При закрытии формы удаляем созданные Bubbling слои
			sendDialog.subscribe("hide", function() {
				Bubbling.unsubscribe("sendToContractorChanged");
			});

			// Прячем поля
			if(isValue(interType)) {
				treeViewer.addSelectedItem(contrRef); // async

				if(interType == "SPECOP") {
					emailField.addClass("hidden");
					interTypeInput.value = "SPECOP";
				} else { // interType == "EMAIL"
					interTypeInput.value = "EMAIL";
					emailInput.value = contrEmail;
					emailInput.disabled = true;
				}

				treeViewer.widgets.pickerButton.set("disabled", true);
				treeViewer.widgets.pickerButton.setStyle("display", "none");
			} else {
				interTypeField.addClass("hidden");
				emailField.addClass("hidden");
			}

			// Создаём радио
			interTypeGroup = new YAHOO.widget.ButtonGroup({
				container: "${interTypeRadioId}"
			});

			interTypeGroup.addButtons([
				{
					label: "${msg('lecm.signdoc.lbl.using.sp.oper')}",
					value: "SPECOP",
					checked: interType == "SPECOP",
					disabled: interType == "EMAIL"
				},
				{
					label: "${msg('lecm.signdoc.lbl.using.email')}",
					value: "EMAIL",
					checked: interType == "EMAIL",
					disabled: interType == "SPECOP"
				}
			]);

			// Исправляем стили
			interTypeGroup.getButton(0).setStyle("margin-left", "0");
			interTypeGroup.getButton(1).setStyle("margin-left", "0");

			// Связываем радио и interaction-type-input
			interTypeGroup.subscribe("valueChange", function(event) {

				var newValue;

				if(isValue(interType)) {
					return false;
				}

				newValue = event.newValue;
				interTypeInput.value = newValue;

				if(newValue == "EMAIL") {
					emailField.removeClass("hidden");
					emailInput.focus();
					emailInput.select();
				}

				if(newValue == "SPECOP") {
					emailField.addClass("hidden");
				}

				return true;
			});

			// "sendToContractorChanged" - это название события changeItemsFireAction (share-config-custom.xml)
			Bubbling.on("sendToContractorChanged", function(layer, args) {

				var selectedContractors = args[1].selectedItems,
					selectedContractor = Object.keys(selectedContractors)[0],

					okButton = sendForm.widgets.okButton;

				// Если этот документ уже отправлялся
				if(isValue(interType)) {
					// Включаем кнопку "ОК"
					okButton.set("disabled", false);

					// Убираем кнопку "-" в контроле выбора контрагента
					clearSelectionButton = Dom.get("${htmlId}_contractor-cntrl-currentValueDisplay").getElementsByTagName("a");
					clearSelectionButton = new Element(clearSelectionButton);
					clearSelectionButton.setStyle("display", "none");

					// И больше ничего не делаем
					return false;
				}

				// Если мы дошли до этого места, значит документ отправляется впервые
				if(selectedContractor) {
					okButton.set("disabled", false);

					checkContractor(selectedContractor);
				} else {
					okButton.set("disabled", true);

					interTypeField.addClass("hidden");
					emailField.addClass("hidden");

					emailInput.value = "";
				}
			});
		}

		function checkDocument() {
			function onFailureCallback(response) {
				Alfresco.util.PopupManager.displayMessage({
					text: "${msg('lecm.signdoc.msg.get.counterp.data.failed')}"
				});
			}

			Ajax.jsonRequest({
				url: Alfresco.constants.PROXY_URI_RELATIVE + "lecm/signed-docflow/getContractorInfoBySendedContent",
				dataObj: { nodeRef: sendForm.options.contentRef },
				successCallback: { fn: init },
				failureCallback: { fn: onFailureCallback }
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
			}

			Ajax.jsonRequest({
				url: Alfresco.constants.PROXY_URI_RELATIVE + "slingshot/doclib2/node/" + contractorRef.replace("://", "/"),
				successCallback: { fn: onSuccessCallback },
				failureCallback: { fn: onFailureCallback }
			});
		}
	})();
</script>