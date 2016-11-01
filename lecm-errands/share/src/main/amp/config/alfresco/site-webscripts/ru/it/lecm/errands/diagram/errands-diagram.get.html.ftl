<!-- Errands Diagram-->
<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/lecm-errands/errands-diagram.css"/>
<@script type="text/javascript" src="${url.context}/res/scripts/lecm-errands/errands-diagram.js"/>

<#assign id = args.htmlid>
<#assign jsid = args.htmlid?js_string>

<script type="text/javascript">//<![CDATA[
(function()
{
    function init(){
        new  LogicECM.module.Errands.Diagram("${jsid}").setOptions(
                {
                    maxItems: 20
                }).setMessages(${messages});
    }

    YAHOO.util.Event.onDOMReady(init);
})();
//]]></script>

<div>
    <div id="${id}-errands-diagram"></div>
    <div id="${id}-errands-paginator" class="errands pagination"></div>
</div>