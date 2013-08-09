<#assign htmlId = args.htmlid>
<#assign formId = args.htmlid + "-form">
<#assign signedContentRef = args.signedContentRef>

<@formLib.renderFormContainer formId = formId>
<div id="${htmlId}" class="signs-wrapper">
	<div id="${htmlId}-refresh" class="signs-refresh"></div>
	<div id="${htmlId}-signs-header" class="signs-header"></div>
	<div id="${htmlId}-signs-container" class="signs-container">
		<div>
			<div class="signs-subheader">Подписи контрагентов</div>
			<div id="${htmlId}-signs-contractor"></div>
		</div>
		<div>
			<div class="signs-subheader">Подписи нашей организации</div>
			<div id="${htmlId}-signs-our"></div>
		</div>
	</div>
</div>
</@formLib.renderFormContainer>

<@formLib.renderFormsRuntime formId = formId />

<script type="text/javascript">
	new LogicECM.module.SignsInfoForm("${htmlId}").setOptions({ "signedContentRef": "${signedContentRef}" });
</script>