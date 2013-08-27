<#assign htmlId = args.htmlid>
<#assign formId = args.htmlid + "-form">
<#assign controlId = args.htmlid + "-control">

<#assign propInteractionTypeId = formId + "_prop_lecm-contractor_interaction-type">
<#assign propInteractionTypeInputId = propInteractionTypeId + "_input">

<@formLib.renderFormContainer formId = formId>

	<!-- Контрагент -->
	<#--<@formLib.renderField field = form.fields["contractor"] />-->


	<!-- Способ взаимодействия -->
	<div class="form-field">
		<label for="${propInteractionTypeId}">Взаимодействие с контрагентом, ЮЗД:<span class="mandatory-indicator">*</span></label>
		<div id="${propInteractionTypeId}"></div>
		<input id="${propInteractionTypeInputId}" class="hidden" name="prop_lecm-contractor_interaction-type" type="text" value="">
	</div>

	<!-- Email -->
	<#--<@formLib.renderField field = form.fields["prop_lecm-contractor_email"] />-->


</@formLib.renderFormContainer>

<@formLib.renderFormsRuntime formId = formId />

<script type="text/javascript">
	(function(htmlId, formId) {

		var onContentReady = YAHOO.util.Event.onContentReady,
			formContainerId = formId + "-container",

			propInteractionTypeId = formId + "_prop_lecm-contractor_interaction-type";

		YAHOO.util.Event.onContentReady(formContainerId, init);

		function hideFields() {
			var get = YAHOO.util.Dom.get,
				Element = YAHOO.util.Element,

				interTypeInputId = formId + "_prop_lecm-contractor_interaction-type",
				interTypeFieldHtml = get(interTypeInputId).parentNode,
				interTypeFieldElem = new Element(interTypeFieldHtml),

				emailInputId = formId + "_prop_lecm-contractor_email",
				emailFieldHtml = get(emailInputId).parentNode,
				emailFieldElem = new Element(emailFieldHtml);

			interTypeFieldElem.addClass("hidden");
			emailFieldElem.addClass("hidden");
		}

		function init() {

			hideFields();

			var get = YAHOO.util.Dom.get,
				Element = YAHOO.util.Element,

				propInteractionTypeId = formId + "_prop_lecm-contractor_interaction-type",

				propInteractionTypeInputId = propInteractionTypeId + "_input",
				propInteractionTypeInputElem = new Element(propInteractionTypeInputId),

				propEmailId = htmlId + "_prop_lecm-contractor_email",
				propEmailHtml = get(propEmailId),
				propEmailField = new Element(propEmailHtml.parentNode);

			interactionTypeGroup = new YAHOO.widget.ButtonGroup({
				container: propInteractionTypeId
			});

			interactionTypeGroup.addButtons([
				{ label: "Используя спецоператора", value: "SPECOP" },
				{ label: "Используя Email", value: "EMAIL" }
			]);

			interactionTypeGroup.getButton(0).setStyle("margin-left", "0");
			interactionTypeGroup.getButton(1).setStyle("margin-left", "0");

			interactionTypeGroup.subscribe("checkedButtonChange", function(event) {
				propInteractionTypeInputElem.value = event.newValue;
			});

			interactionTypeGroup.subscribe("valueChange", function(event) {
				var newValue = event.newValue;

				if(newValue == "EMAIL") {
					propEmailField.removeClass("hidden");
				}

				if(newValue == "SPECOP") {
					propEmailField.addClass("hidden");
				}
			});
		}

	})("${htmlId}", "${formId}");
</script>