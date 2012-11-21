<#assign id = args.htmlid>
<#import "/ru/it/lecm/base-share/components/base-components.ftl" as comp/>

<script type="text/javascript">//<![CDATA[

(function() {
	function init() {
		var menu = new LogicECM.module.Dictionary.Menu("menu-buttons").setMessages(${messages});
	}
	YAHOO.util.Event.onDOMReady(init);
})();
//]]></script>

<@comp.baseMenu>
	<div class="dictionary-menu">
		<span id="menu-buttons-dictionariesBtn" class="yui-button yui-push-button">
			<span class="first-child">
				<button type="button" title="${msg('lecm.dictionaries.btn')}">&nbsp;</button>
			</span>
		</span>
	</div>
</@comp.baseMenu>