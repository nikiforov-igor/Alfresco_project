<#-- Identifiers -->
<#assign htmlId = args.htmlid>
<#assign formId = htmlId + "-form">
<#assign formContainerId = formId + "-container">
<#assign buttonsContainerId = formId + "-buttons">
<#-- /Identifiers -->

<#-- Fields -->
<#assign propIndexTableRow = "prop_lecm-document_indexTableRow">

<#assign propStartDate = "prop_lecm-contract-table-structure_start-date">
<#assign propEndDate = "prop_lecm-contract-table-structure_end-date">
<#assign propStartDateReal = "prop_lecm-contract-table-structure_start-date-real">
<#assign propEndDateReal = "prop_lecm-contract-table-structure_end-date-real">

<#assign propName = "prop_lecm-contract-table-structure_name">
<#assign propAmount = "prop_lecm-contract-table-structure_stage-amount">
<#assign assocCurrency = "assoc_lecm-contract-table-structure_stage-currency-assoc">

<#assign propComment = "prop_lecm-contract-table-structure_stage-comment">
<#assign assocAttachments = "assoc_lecm-contract-table-structure_attachments-temp-assoc">
<#assign propStatus = "prop_lecm-contract-table-structure_stage-status">
<#-- /Fields -->

<#assign inEditMode = form.mode == "edit">

<div id="${formContainerId}">
<#if formUI == "true">
	<@formLib.renderFormsRuntime formId = formId />
</#if>
<@formLib.renderFormContainer formId = formId>
	<table>
		<tbody>
		<#if inEditMode>
		<tr>
			<td colspan="3"><@formLib.renderField field = form.fields[propStatus] /></td>
			<input id="${htmlId}_${propStatus}" name="${propStatus}" value="${form.fields[propStatus].value}" type="hidden"/>
		</tr>
		</#if>
		<tr>
			<td><@formLib.renderField field = form.fields[propIndexTableRow] /></td>
			<td><@formLib.renderField field = form.fields[propStartDate] /></td>
			<td><@formLib.renderField field = form.fields[propEndDate] /></td>
		</tr>
		<#if inEditMode>
		<tr>
			<td></td>
			<td><@formLib.renderField field = form.fields[propStartDateReal] /></td>
			<td><@formLib.renderField field = form.fields[propEndDateReal] /></td>
		</tr>
		</#if>
		<tr>
			<td colspan="3"><@formLib.renderField field = form.fields[propName] /></td>
		</tr>
		<tr>
			<td><@formLib.renderField field = form.fields[propAmount] /></td>
			<td colspan="2">
				<div style="margin-top: 25px;">
					<@formLib.renderField field = form.fields[assocCurrency] />
				</div>
			</td>
		</tr>
		</tbody>
	</table>
	<@formLib.renderField field = form.fields[propComment] />
	<#if inEditMode>
		<@formLib.renderField field = form.fields[assocAttachments] />
	</#if>
</@>
</div>

<script type="text/javascript">
	(function() {
		function addCustomButtons() {
			var buttonsContainer = Dom.get("${buttonsContainerId}"),
				inputStatus = Dom.get(htmlId + "_${propStatus}"),

				LABEL_IN_WORK = "В работу",
				LABEL_CLOSED = "Закрыть",
				STATUS_IN_WORK = "В работе",
				STATUS_CLOSED = "Закрыт";

			inWorkButton = new Button({
				id: htmlId + "_inWorkButton",
				type: "submit",
				label: LABEL_IN_WORK,
				container: buttonsContainer,
				onclick: {
					fn: function() { inputStatus.value = STATUS_IN_WORK; }
				}
			});

			closeButton = new Button({
				id: htmlId + "_closeButton",
				type: "submit",
				label: LABEL_CLOSED,
				container: buttonsContainer,
				onclick: {
					fn: function() { inputStatus.value = STATUS_CLOSED; }
				}
			});
		}

		function updateButtonStyles() {
			inWorkButton.setStyle("float", "left");
			closeButton.setStyle("float", "left");
			inWorkButton.setStyle("margin-left", "5px");
			closeButton.setStyle("margin-left", "5px");
		}

		function addSubmitElements() {
			dialog.form.addSubmitElement(closeButton);
			dialog.form.addSubmitElement(inWorkButton);
		}

		// Коротко: события в YUI реализованы с использованием паттерна Observer (Наблюдатель).
		// Наблюдатели хранятся/оповещаются в порядке их добавления. Выше мы добавили submit-кнопки с onclick-обработчиками.
		// Тогда массивы наблюдателей будут выглядеть так: ["submit", "onclick"]. Переворачивая массив, мы ставим submit
		// последним в списке.
		function reverseSubscribers() {
			inWorkButton.__yui_events.click.subscribers.reverse();
			closeButton.__yui_events.click.subscribers.reverse();
		}

		function init() {
			YAHOO.Bubbling.unsubscribe("afterFormRuntimeInit", init);

			if(inCreateMode)
				return false;

			addCustomButtons();
			updateButtonStyles();
			addSubmitElements();
			reverseSubscribers();

			return true;
		}

		var closeButton,
			inWorkButton,

			Bubbling = YAHOO.Bubbling,
			Button = YAHOO.widget.Button,
			Dom = YAHOO.util.Dom,
			Event = YAHOO.util.Event,

			htmlId = "${htmlId}",

			dialog = Alfresco.util.ComponentManager.get(htmlId),

			inCreateMode = ${(!inEditMode)?string};

		Bubbling.on("afterFormRuntimeInit", init);
	})();
</script>