<#assign id = args.htmlid>
<#import "/ru/it/lecm/base-share/components/base-components.ftl" as comp/>
<@comp.baseMenu>
	<div class="statemachine-menu">
		<span id="menu-buttons-new-status" class="yui-button yui-push-button">
	        <span class="first-child">
	            <button type="button" title="Добавить статус">&nbsp;</button>
	        </span>
	    </span>
	</div>
<div class="statemachine-menu">
		<span id="menu-buttons-machine-properties" class="yui-button yui-push-button">
	        <span class="first-child">
	            <button type="button" title="Свойства">&nbsp;</button>
	        </span>
	    </span>
</div>
</@comp.baseMenu>
