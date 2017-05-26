<#assign el=args.htmlid?html>
<@markup id="html">
	<@uniqueIdDiv>
		<#if hasPermission>
            <div class="container notifications-unsubscribe-btn">
                <span>${msg('msg.unsubscribe.continue') + ' '}</span>
                <span class="templateDescription">${templateDescription}</span>
                <span class="first-child">
			            <button id="${el}-unsubscribe" type="button" onclick="unsubscribe();" tabindex="0">${msg("label.unsubscribe")}</button>
                </span>
            </div>
		<#else>
            <div class="notifications-header">
                <div class="status-banner">${msg('msg.unsubscribe_not_allowed', employeeName)}</div>
            </div>
		</#if>
	</@>
</@>
<script type="text/javascript">//<![CDATA[
LogicECM.module.Base.Util.loadCSS(
        [
            'css/lecm-notifications/notifications-unsubscribe.css'
        ], null);

function unsubscribe() {
    Alfresco.util.Ajax.jsonPost({
        url: Alfresco.constants.PROXY_URI + "lecm/notifications/template/unsubscribe",
        dataObj: {
            <#if (employee)?has_content>employee: "${employee}",</#if>
            template: "${templateCode}"
        },
        successCallback: {
            fn: function (response) {
                var button = YAHOO.util.Dom.get("${el}-unsubscribe");
                if (button) {
                    button.setAttribute("disabled", "true");
                }
                Alfresco.util.PopupManager.displayMessage(
                        {
                            text: "${msg('lecm.notification.unsubscribe.success')}"
                        });
            }
        },
        failureMessage: "${msg('lecm.notification.unsubscribe.failure')}"
    });
    return false;
}
//]]></script>