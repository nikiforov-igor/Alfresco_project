<#import "/ru/it/lecm/base-share/components/base-components.ftl" as comp/>
<@comp.baseMenu>
	<br/>
	<div id="employees" class="yui-skin-sam"></div><br/>
	<div id="staff-list" class="yui-skin-sam"></div><br/>
	<div id="orgstructure" class="yui-skin-sam"></div><br/>
	<div id="work-groups" class="yui-skin-sam"></div><br/>
	<div id="positions" class="yui-skin-sam"></div><br/>
	<div id="roles" class="yui-skin-sam"></div><br/>
	<div id="organization" class="yui-skin-sam"></div><br/>
</@comp.baseMenu>

<script type="text/javascript">//<![CDATA[

(function() {
	function init() {
		var menu = new LogicECM.module.OrgStructure.Menu("menu-buttons");
		menu.setMessages(${messages});
		menu.draw();
	}
	YAHOO.util.Event.onDOMReady(init);
})();
//]]></script>
