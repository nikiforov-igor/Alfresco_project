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

<#assign propComment = "prop_lecm-contract-table-structure_stage-comment">
<#assign assocAttachments = "assoc_lecm-contract-table-structure_attachments-temp-assoc">
<#assign propStatus = "prop_lecm-contract-table-structure_stage-status">

<#assign startId = htmlId + "_" + propStartDate?replace("prop_", "")?replace("_", ":") + "_" + "componentReady"/>
<#assign endId = htmlId + "_" + propEndDate?replace("prop_", "")?replace("_", ":") + "_" + "componentReady"/>
<#-- /Fields -->

<#assign inEditMode = (form.mode == "edit" || form.mode == "create")>
<#assign inViewMode = form.mode == "view">
<#assign inEditOrViewMode = true>

<div id="${formContainerId}">
<#if formUI == "true">
	<@formLib.renderFormsRuntime formId = formId />
</#if>
<@formLib.renderFormContainer formId = formId>
    <table class="${form.mode}-stage" style="border-spacing: 0;">
        <tbody>
			<#if inEditOrViewMode>
            <tr>
                <td colspan="3"><@formLib.renderField field = form.fields[propStatus] /></td>
            </tr>
			</#if>
        <tr>
			<td colspan="3" <#if inViewMode>id="propIndexTableRow"</#if>><@formLib.renderField field = form.fields[propIndexTableRow] /></td>
		</tr>
        <tr <#if inViewMode>class="tableRowData"</#if>>
            <td colspan="2" <#if inViewMode>id="propStartDate"</#if>><@formLib.renderField field = form.fields[propStartDate] /></td>
            <td <#if inViewMode>id="propEndDate"</#if>><@formLib.renderField field = form.fields[propEndDate] /></td>
        </tr>
		<#if inEditOrViewMode>
		<tr class="${form.mode}-stage-dates tableRowData" >
			<td colspan="2" <#if inViewMode>id="propStartDateReal"</#if>><@formLib.renderField field = form.fields[propStartDateReal] /></td>
			<td <#if inViewMode>id="propEndDateReal"</#if>><@formLib.renderField field = form.fields[propEndDateReal] /></td>
		</tr>
		</#if>
        <tr>
            <td colspan="3" <#if inViewMode>class="view-name"</#if>><@formLib.renderField field = form.fields[propName] /></td>
        </tr>
        <tr>
            <td colspan="3" <#if inViewMode>class="view-name"</#if>><@formLib.renderField field = form.fields[propAmount] /></td>
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
				STATUS_IN_WORK = "IN_WORK",
				STATUS_CLOSED = "CLOSED";

			inWorkButton = new Button({
				id: htmlId + "_inWorkButton",
				type: "submit",
				label: LABEL_IN_WORK,
				container: buttonsContainer,
				onclick: {
					fn: function() {
						inputStatus.removeAttribute("disabled");
						inputStatus.value = STATUS_IN_WORK;
					}
				}
			});

			closeButton = new Button({
				id: htmlId + "_closeButton",
				type: "submit",
				label: LABEL_CLOSED,
				container: buttonsContainer,
				onclick: {
					fn: function() {
                        inputStatus.removeAttribute("disabled");
						inputStatus.value = STATUS_CLOSED;
					}
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

		// ??????????????: ?????????????? ?? YUI ?????????????????????? ?? ???????????????????????????? ???????????????? Observer (??????????????????????).
		// ?????????????????????? ????????????????/?????????????????????? ?? ?????????????? ???? ????????????????????. ???????? ???? ???????????????? submit-???????????? ?? onclick-??????????????????????????.
		// ?????????? ?????????????? ???????????????????????? ?????????? ?????????????????? ??????: ["submit", "onclick"]. ?????????????????????????? ????????????, ???? ???????????? submit
		// ?????????????????? ?? ????????????.
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
			new LogicECM.DateRange("${htmlId}").setOptions({
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