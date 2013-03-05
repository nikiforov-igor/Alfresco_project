<#if page.url.args.statemachineId??>
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
    <span id="menu-buttons-new-end-event" class="yui-button yui-push-button">
        <span class="first-child">
            <button type="button" title="Добавить финальный статус">&nbsp;</button>
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
<div class="statemachine-menu">
		<span id="menu-buttons-machine-deploy" class="yui-button yui-push-button">
	        <span class="first-child">
	            <button type="button" title="Задеплоить">&nbsp;</button>
	        </span>
	    </span>
</div>
<div class="statemachine-menu">
		<span id="menu-buttons-machine-export" class="yui-button yui-push-button">
	        <span class="first-child">
	            <button type="button" title="Экспорт">&nbsp;</button>
	        </span>
	    </span>
</div>
<div class="statemachine-menu">
		<span id="menu-buttons-machine-import" class="yui-button yui-push-button">
	        <span class="first-child">
	            <button type="button" title="Импорт">&nbsp;</button>
	        </span>
	    </span>
</div>

<div class="statemachine-menu">
		<span id="menu-buttons-machine-status-fields" class="yui-button yui-push-button">
	        <span class="first-child">
	            <button type="button" title="Доступ к полям на статусе">&nbsp;</button>
	        </span>
	    </span>
</div>
</@comp.baseMenu>
</#if>