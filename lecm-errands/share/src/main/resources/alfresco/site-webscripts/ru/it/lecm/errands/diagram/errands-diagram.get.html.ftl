<#import "/ru/it/lecm/base-share/components/view.lib.ftl" as view/>
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
    <@view.viewForm formId="${id}-errands-diagram-dialog"/>
    <div id="${id}-errands-diagram"></div>
    <div id="${id}-errands-paginator" class="errands pagination"></div>
</div>