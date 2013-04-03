<#assign id = args.htmlid>
<#import "/ru/it/lecm/base-share/components/base-components.ftl" as comp/>

<script type="text/javascript">//<![CDATA[

(function() {
	function init() {
		var menu = new window.LogicECM.module.Dictionary.Menu("menu-buttons").setMessages(${messages});
	}
	YAHOO.util.Event.onDOMReady(init);
})();
//]]></script>

<@comp.baseMenu>
    <@comp.baseMenuButton "dictionaries" msg('lecm.dictionaries.btn') args.selected/>
</@comp.baseMenu>