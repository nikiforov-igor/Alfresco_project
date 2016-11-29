<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/lecm-contracts/contracts-stages-view-mode.css" />
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

<#assign startId = htmlId + "_" + propStartDate?replace("prop_", "")?replace("_", ":") + "_" + "componentReady"/>
<#assign endId = htmlId + "_" + propEndDate?replace("prop_", "")?replace("_", ":") + "_" + "componentReady"/>
<#-- /Fields -->

<#assign inEditMode = form.mode == "edit">
<#assign inViewMode = form.mode == "view">
<#assign inEditOrViewMode = (inEditMode || inViewMode)>

<div id="${formContainerId}">
<#if formUI == "true">
	<@formLib.renderFormsRuntime formId = formId />
</#if>
<@formLib.renderFormContainer formId = formId>
    <table class="${form.mode}-stage">
        <tbody>
			<#if inEditOrViewMode>
            <tr>
                <td colspan="3"><@formLib.renderField field = form.fields[propStatus] /></td>
                <input id="${htmlId}_${propStatus}" name="${propStatus}" value="${form.fields[propStatus].value}" type="hidden"/>
            </tr>
			</#if>
        <tr>
			<td colspan="3" <#if inViewMode>id="propIndexTableRow"</#if>><@formLib.renderField field = form.fields[propIndexTableRow] /></td>
		</tr>
        <tr <#if inViewMode>class="tableRowData"</#if>>
			<td></td>
            <td <#if inViewMode>id="propStartDate"</#if>><@formLib.renderField field = form.fields[propStartDate] /></td>
            <td <#if inViewMode>id="propEndDate"</#if>><@formLib.renderField field = form.fields[propEndDate] /></td>
        </tr>
			<#if inEditOrViewMode>
            <tr class="${form.mode}-stage-dates tableRowData" >
                <td></td>
                <td <#if inViewMode>id="propStartDateReal"</#if>><@formLib.renderField field = form.fields[propStartDateReal] /></td>
                <td <#if inViewMode>id="propEndDateReal"</#if>><@formLib.renderField field = form.fields[propEndDateReal] /></td>
            </tr>
			</#if>
        <tr>
            <td colspan="3" <#if inViewMode>class="view-name"</#if>><@formLib.renderField field = form.fields[propName] /></td>
        </tr>
        <tr>
            <td <#if inViewMode>id="price"</#if> colspan="3">
				<#list form.structure as item>
					<#if item.id == "price">
					<#include "${item.template}" />
					<#break>
				</#if>
				</#list>
            </td>
        </tr>
        <tr>
            <td colspan="3">
				<@formLib.renderField field = form.fields[propComment] />
            </td>
        </tr>
        </tbody>
    </table>

	<#if inEditOrViewMode>
		<@formLib.renderField field = form.fields[assocAttachments] />
	</#if>
</@>
</div>
<script type="text/javascript">
	(function() {
		<#if inEditMode>
		function addCustomButtons() {
			var buttonsContainer = Dom.get("${buttonsContainerId}"),
				inputStatus = Dom.get(htmlId + "_${propStatus}"),

				LABEL_IN_WORK = "${msg('label.contracts.start-stage')}",
				LABEL_CLOSED = "${msg('label.contracts.end-stage')}",
				STATUS_IN_WORK = "${msg('label.in-work')}",
				STATUS_CLOSED = "${msg('label.closed')}";

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

            LogicECM.module.Base.Util.loadCSS([
                'css/lecm-contracts/contracts-stages.css'
            ]);

            addCustomButtons();
			updateButtonStyles();
			addSubmitElements();
			reverseSubscribers();
		}

		var closeButton,
			inWorkButton,

			Bubbling = YAHOO.Bubbling,
			Button = YAHOO.widget.Button,
			Dom = YAHOO.util.Dom,
			Event = YAHOO.util.Event,

			htmlId = "${htmlId}",

			dialog = Alfresco.util.ComponentManager.get(htmlId);

		Bubbling.on("afterFormRuntimeInit", init);
		</#if>
		<#if !inViewMode>
		function initJs() {
            LogicECM.module.Base.Util.loadResources([
                'scripts/lecm-base/components/lecm-datarange.js'
			], [
				'css/lecm-contracts/contracts-stages.css'
			], createDateRange);
		}
		function createDateRange() {
			new LogicECM.DateRange("${formId}").setOptions({
				startDateHtmlId: "${htmlId}_${propStartDate}",
				endDateHtmlId: "${htmlId}_${propEndDate}"
			}).setMessages(${messages});

			new LogicECM.DateRange("${formId}").setOptions({
				startDateHtmlId: "${htmlId}_${propStartDateReal}",
				endDateHtmlId: "${htmlId}_${propEndDateReal}"
			}).setMessages(${messages});
		}
            YAHOO.util.Event.onAvailable('${startId}', function() {
                YAHOO.util.Event.onAvailable('${endId}', initJs);
            });
		</#if>
	})();
</script>