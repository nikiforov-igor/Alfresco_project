<#if allowEdit >
    <#assign el=args.htmlid?html>

    <div id="${el}-body" class="resolutions-dashlet-settings">
        <div class="yui-g">
            <div class="yui-u first">
                <div class="title">${msg("label.title")}</div>
            </div>
        </div>

        <div id="${el}-settings"></div>
    </div>

    <script type="text/javascript">//<![CDATA[
    (function() {
        function createPage() {
            new LogicECM.module.ResolutionsDashletSettings("${el}").setMessages(${messages});
        }

        LogicECM.module.Base.Util.loadResources([
            'components/console/consoletool.js',
            'scripts/lecm-resolution/resolutions-dashlet-settings.js',
            'components/form/form.js'
        ], [
            'css/lecm-resolution/resolutions-dashlet-settings.css'
        ], createPage);

    })();
    //]]></script>

<#else>
    <#include "/ru/it/lecm/base-share/components/forbidden.get.html.ftl">
</#if>