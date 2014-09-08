<#if allowEdit >
<#assign el=args.htmlid?html>

<@markup id="js">
	<@script type="text/javascript" src="${url.context}/res/scripts/lecm-base/components/base-utils.js"/>
</@>

<script type="text/javascript">//<![CDATA[
(function(){
    function createPage() {
        new LogicECM.DelegationGlobalSettings("${el}").setMessages(${messages});
    }

    function init() {
        LogicECM.module.Base.Util.loadResources([
            'components/console/consoletool.js',
            'scripts/lecm-delegation/delegation-global-settings.js',
            'components/form/form.js'
        ], [
            'css/lecm-delegation/delegation-global-settings.css'
        ], createPage);
    }

    YAHOO.util.Event.onDOMReady(init);
})();
//]]></script>

<div id="${el}-body" class="delegation-settings">
    <div class="yui-g">
        <div class="yui-u first">
            <div class="title">${msg("label.title")}</div>
        </div>
    </div>

    <div id="${el}-settings"></div>
</div>

<#else>
    <#include "/ru/it/lecm/base-share/components/forbidden.get.html.ftl">
</#if>