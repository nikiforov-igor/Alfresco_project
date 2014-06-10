<!-- Comments List -->
<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/components/status-string.css" />
<@script type="text/javascript" src="${url.context}/res/scripts/components/status-string.js"></@script>

<#assign el=args.htmlid?js_string>

<div id="${el}-placeholder" class="status-string-placeholder"></div>
<div id="${el}-body" class="status-string">
	<div id="${el}-document-name">
		${documentName}. <span id="${el}-page"></span>
	</div>

	<div id="${el}-form">
		<textarea id="${el}-property-value" cols="80" rows="1"></textarea>
		<span id="${el}-submit-button" class="yui-button yui-push-button search-icon">
            <span class="first-child">
                <button type="button">${msg('button.submit')}</button>
            </span>
        </span>
	</div>

	<script type="text/javascript">//<![CDATA[
		new LogicECM.DocumentStatusString("${el}").setOptions(
				{
					nodeRef: "${nodeRef}",
					propertyName: "lecm-contract:locationRegistration"
				}).setMessages(${messages});
	//]]></script>
</div>
