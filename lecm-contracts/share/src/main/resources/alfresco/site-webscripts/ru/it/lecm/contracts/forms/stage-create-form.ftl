<#assign htmlId = args.htmlid>
<#assign formId = htmlId + "-form">
<#assign formContainerId = formId + "-container">

<div id="${formContainerId}">
<#if formUI == "true">
	<@formLib.renderFormsRuntime formId = formId />
</#if>
<@formLib.renderFormContainer formId = formId>
	<table>
		<tbody>
			<tr>
				<td><@formLib.renderField field = form.fields["prop_lecm-document_indexTableRow"] /></td>
				<td><@formLib.renderField field = form.fields["prop_lecm-contract-table-structure_start-date"] /></td>
				<td><@formLib.renderField field = form.fields["prop_lecm-contract-table-structure_end-date"] /></td>
			</tr>
			<tr>
				<td colspan="3"><@formLib.renderField field = form.fields["prop_lecm-contract-table-structure_name"] /></td>
			</tr>
			<tr>
				<td><@formLib.renderField field = form.fields["prop_lecm-contract-table-structure_stage-amount"] /></td>
				<td colspan="2">
					<div style="margin-top: 25px;">
						<@formLib.renderField field = form.fields["assoc_lecm-contract-table-structure_stage-currency-assoc"] />
					</div>
				</td>
			</tr>
		</tbody>
	</table>
	<@formLib.renderField field = form.fields["prop_lecm-contract-table-structure_stage-comment"] />
</@>
</div>