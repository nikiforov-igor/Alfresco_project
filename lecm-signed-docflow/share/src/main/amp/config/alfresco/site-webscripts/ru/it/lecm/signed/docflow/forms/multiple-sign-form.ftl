<#assign htmlId = args.htmlid>
<#assign formId = htmlId + "-form">
<#assign controlId = htmlId + "-cntrl">
<#assign formContainerId = formId + "-container">
<#assign t = args.obj>
<#assign data = t?eval>
<div id="${formContainerId}">
<#if formUI == "true">
	<@formLib.renderFormsRuntime formId = formId />
</#if>
<#if t == '[]'>
	<br/>
	<p class="title-label">${msg('lecm.signdoc.lbl.no.sign.attachs')}</p>
</#if>
<@formLib.renderFormContainer formId = formId>
<#list data as group>
	<p>
	<strong>${group.categoryName}</strong><br/>
	<div id="${htmlId}-underline" class="form-text-underline"></div>
	<#list group.content as content>
		<input style="margin: 5px 0px 10px 10px;" type="checkbox" value="${content.nodeRef}" checked>
			&nbsp;<a style="margin: 5px 0px 10px 10px;" href="${url.server}${url.context}/page/document-attachment?nodeRef=${content.nodeRef}">${content.name}</a>
		</input>
	<br/>
	</#list>
</#list>
</@>
</div>
<script type="text/javascript">
    (function () {
        LogicECM.module.Base.Util.loadCSS([
            'css/signed-docflow/multiple-sign-form.css'
        ])
    })();
</script>