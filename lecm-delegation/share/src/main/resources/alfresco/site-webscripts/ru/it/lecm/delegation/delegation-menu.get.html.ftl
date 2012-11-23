<#import "/ru/it/lecm/base-share/components/base-components.ftl" as comp/>

<script type="text/javascript"> //<![CDATA[
	(function () {
		var delegationMenu = new LogicECM.module.Delegation.Menu ("menu-buttons");
		delegationMenu.setMessages(${messages});
	})();
//]]>
</script>

<@comp.baseMenu>
	<div class="delegation-menu">
		<span id="menu-buttons-delegationBtn" class="yui-button yui-push-button">
	        <span class="first-child">
	            <button type="button" title="делегирование полномочий (старое)">&nbsp;</button>
	        </span>
	    </span>
	</div>
	<div class="delegation-menu">
		<span id="menu-buttons-delegationOptsBtn" class="yui-button yui-push-button">
            <span class="first-child">
                <button type="button" title="настройка параметров делегирования">&nbsp;</button>
            </span>
	    </span>
	</div>
</@comp.baseMenu>

