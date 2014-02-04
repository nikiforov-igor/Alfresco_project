<#assign id = args.htmlid>
<script type="text/javascript">//<![CDATA[
function init() {
    new LogicECM.module.ARM.DocumentsToolbar("${id}").setMessages(${messages});
}

YAHOO.util.Event.onDOMReady(init);
//]]></script>

<#import "/ru/it/lecm/base-share/components/base-components.ftl" as comp/>
<@comp.baseToolbar id true false false>
</@comp.baseToolbar>
