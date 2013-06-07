<#assign el=args.htmlid?js_string>

<div id="${el}-body" class="status-string">
	<div id="${el}-document-name">
		${documentName}. <span id="${el}-page"></span>
	</div>

	<div id="${el}-form">
		<input id="${el}-property-value" type="text" name="propertyValue">
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
