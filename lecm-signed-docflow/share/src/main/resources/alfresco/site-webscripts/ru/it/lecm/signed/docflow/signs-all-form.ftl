<#assign htmlId = args.htmlid>
<#assign formId = args.htmlid + "-form">
<#assign signedContentRef = args.signedContentRef>

<@formLib.renderFormContainer formId = formId>
<div id="${htmlId}" class="signs-wrapper">
	<div id="${htmlId}-refresh" class="signs-refresh"></div>
	<div data-bind="foreach: signs">

		<h1 class="separator" data-bind="text: categoryName"></h1>

		<div data-bind="foreach: signedContent">
			<p data-bind="text: fileName"></p>

			<!-- ko if: $root.singsIsEmpty() -->
			<p>Никто не подписывал</p>
			<!-- /ko -->

			<!-- ko ifnot: $root.singsIsEmpty() -->
			<table>
				<tbody>
				<!-- ko foreach: signsInfo -->
				<!-- ko ifnot: isOur -->
				<tr>
					<td data-bind="text: $root.getSignOwner($context)"></td>
					<td data-bind="text: $root.getSignDescription($context)"></td>
					<td><a href="#" data-bind="attr: { onclick: $root.getViewAttributes($context) }">View</a></td>
				</tr>
				<!-- /ko -->
				<!-- /ko -->

				<!-- ko foreach: signsInfo -->
				<!-- ko if: isOur -->
				<tr>
					<td data-bind="text: $root.getSignOwner($context)"></td>
					<td data-bind="text: $root.getSignDescription($context)"></td>
					<td><a href="#" data-bind="attr: { onclick: $root.getViewAttributes($context) }">View</a></td>
				</tr>
				<!-- /ko -->
				<!-- /ko -->
				</tbody>
			</table>
			<!-- /ko -->

		</div>
	</div>
</div>
</@formLib.renderFormContainer>

<@formLib.renderFormsRuntime formId = formId />

<script type="text/javascript">
	new LogicECM.module.SignsAllForm("${htmlId}").setOptions({ "signedContentRef": "${signedContentRef}" });
</script>