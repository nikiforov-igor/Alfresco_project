<#assign htmlId = args.htmlid>
<#assign formId = args.htmlid + "-form">
<#assign controlId = args.htmlid + "-control">
<#assign signedContentRef = args.signedContentRef>

<@formLib.renderFormContainer formId = formId>
<div id="${controlId}" class="signs-wrapper">
	<div id="${controlId}-refresh" class="signs-refresh"></div>

	<#-- Не убирайте display: none; Это важно для knockout'а! -->
	<div data-bind="visible: $root.singsIsEmpty()" style="display: none;">
		<p>Вложения участвующие в юридически значимом документообороте (ЮЗД) отсутствуют.</p>
		<p>Чтобы добавить документ/вложение в ЮЗД:</p>
		<div>
			<div>1. Перейдите на карточку документа/вложения, которое Вы хотите добавить;</div>
			<div>2. В правой части карточки поставьте галочку напротив "На подпись".</div>
		</div>
	</div>

	<!-- ko ifnot: $root.singsIsEmpty() -->
	<#-- Не убирайте display: none; Это важно для knockout'а! -->
	<div data-bind="visible: $root.singsIsNotEmpty(), foreach: signs" style="display: none;">
		<h1 class="signs-category-header" data-bind="text: categoryName, visible: $root.categoryIsNotEmpty($context)"></h1>
		<div data-bind="foreach: signedContent">
			<p class="signs-file-header" data-bind="text: fileName"></p>

			<!-- ko if: $root.signsInfoIsEmpty($context) -->
			<p>Вложение не подписывалось.</p>
			<!-- /ko -->

			<!-- ko ifnot: $root.signsInfoIsEmpty($context) -->
			<table class="signs-table">
				<tbody>
				<!-- ko foreach: signsInfo -->
				<!-- ko ifnot: isOur -->
				<tr>
					<td data-bind="text: $root.getSignOwner($context)"></td>
					<td data-bind="html: $root.getSignDescription($context)"></td>
					<td><a href="#" class="signs-chain-link" data-bind="attr: { onclick: $root.getViewAttributes($context) }"></a></td>
				</tr>
				<!-- /ko -->
				<!-- /ko -->

				<!-- ko foreach: signsInfo -->
				<!-- ko if: isOur -->
				<tr class="signs-table">
					<td data-bind="text: $root.getSignOwner($context)"></td>
					<td data-bind="html: $root.getSignDescription($context)"></td>
					<td><a href="#" class="signs-chain-link" data-bind="attr: { onclick: $root.getViewAttributes($context) }"></a></td>
				</tr>
				<!-- /ko -->
				<!-- /ko -->
				</tbody>
			</table>
			<!-- /ko -->
		</div>
	</div>
	<!-- /ko -->
</div>
</@formLib.renderFormContainer>

<@formLib.renderFormsRuntime formId = formId />

<script type="text/javascript">
(function() {
	LogicECM.module.Base.Util.loadScripts([
		'scripts/signed-docflow/signs-all-form.js',
		'scripts/lecm-base/third-party/knockout.js'
		], function() {
			new LogicECM.module.SignsAllForm("${htmlId}", "${controlId}").setOptions({
				signedContentRef: "${signedContentRef}",
				refreshBeforeShow: false
			});
	});

})();
</script>
