<#if allowEdit >
<#assign el=args.htmlid?html>

<script type="text/javascript">//<![CDATA[
(function(){
    function createPage() {
        new LogicECM.ContractDashletSettings("${el}").setMessages(${messages});
    }

    function init() {
        LogicECM.module.Base.Util.loadResources([
            'components/console/consoletool.js',
            'scripts/lecm-contracts/contract-dashlet-settings.js',
            'components/form/form.js'
        ], [
            'css/lecm-contracts/contract-dashlet-settings.css'
        ], createPage);
    }

    YAHOO.util.Event.onDOMReady(init);
})();
//]]></script>

<div id="${el}-body" class="contract-dashlet-settings">
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