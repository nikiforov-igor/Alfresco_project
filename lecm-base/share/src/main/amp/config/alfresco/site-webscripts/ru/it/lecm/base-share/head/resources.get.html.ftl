<#include "/org/alfresco/components/component.head.inc">

<#--
 ALF-3832 временное решение для entreprise-версии alfresco
 в community-версии, в отличие от entreprise-версии, YAHOO.util.History входит в yui-common.js
-->
<@markup id="yui">
	<#if !DEBUG>
		<@script type="text/javascript" src="${url.context}/res/yui/history/history.js" group="lecm-head-resources"/>
	</#if>
</@>

<@markup id="resources">
	<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/font-awesome.css" group="lecm-head-resources"/>
	<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/lecm-base/base-styles.css" group="lecm-head-resources"/>
	<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/lecm-calendar/jquery-ui-1.10.3.custom.css" group="lecm-head-resources"/>
	<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/lecm-calendar/jquery-ui-timepicker-addon.css" group="lecm-head-resources"/>

	<@link rel="stylesheet" type="text/css" href="${url.context}/res/jquery/fullcalendar/fullcalendar.css" group="lecm-head-resources"/>
	<@link rel="stylesheet" type="text/css" href="${url.context}/res/jquery/fullcalendar/fullcalendar.css" group="lecm-head-resources"/>
	<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/lecm-events/viewmore.css" group="lecm-head-resources"/>
	<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/lecm-events/formbubble.css" group="lecm-head-resources"/>

	<#--
	 сделаем метод Bubbling.unsubscribe более безопасным
	 добавим проверку на существование bubble[layer]
	 которая отсутствует в оригинальном методе.
	 Оригинальный метод описан в вебскрипте org\alfresco\components\head\resources.get.html.ftl
	 Таким образом, YAHOO.Bubbling.unsubscribe будет сперва определен в org\alfresco\components\head\resources.get.html.ftl
	 а затем переопределен ru\it\lecm\base-share\head\resources.get.html.ftl
	-->
	<@inlineScript group="lecm-head-resources">
	(function() {
		var Bubbling = YAHOO.Bubbling;
		if (Bubbling) {
			Bubbling.unsubscribe = function(layer, handler, scope) {
				var bubble = this.bubble[layer];
				if (bubble) {
					bubble.unsubscribe(handler, scope);
				}
			};
		}
		
		// ALFFIVE-144
		// В новой версии изменился forms-runtime.
		// Теперь безусловно вызывается this._toggleSubmitElements(true); сразу после запроса.
		// Что приводит к возможности закликивания формы. 

		// Достаточно злой костыль, проксирование _submitInvoked

		var oldSubmitFunction = Alfresco.forms.Form.prototype._submitInvoked;
		Alfresco.forms.Form.prototype._submitInvoked = function (event) {
			oldSubmitFunction.call(this, event);

			// На всякий случай проверим валидна ли форма перед отключением кнопок
			if (this._runValidations(event, null, Alfresco.forms.Form.NOTIFICATION_LEVEL_CONTAINER)) {
				this._toggleSubmitElements(false);
			}
		}
	})();
	</@>

	<@script type="text/javascript" src="${url.context}/res/jquery/jquery-1.6.2.js" group="lecm-head-resources"/>
	<@script type="text/javascript" src="${url.context}/res/scripts/lecm-base/third-party/jquery.inputmask.js" group="lecm-head-resources"/>
	<@script type="text/javascript" src="${url.context}/res/scripts/lecm-base/third-party/jquery.inputmask.date.extensions.js" group="lecm-head-resources"/>
	<@script type="text/javascript" src="${url.context}/res/scripts/lecm-base/third-party/jquery-ui-1.10.3.custom.js" group="lecm-head-resources"/>
	<@script type="text/javascript" src="${url.context}/res/scripts/lecm-base/third-party/jquery-ui-timepicker-addon.js" group="lecm-head-resources"/>
	<@script type="text/javascript" src="${url.context}/res/scripts/lecm-base/third-party/jquery-ui-sliderAccess.js" group="lecm-head-resources"/>
	<@script type="text/javascript" src="${url.context}/res/jquery/fullcalendar/fullcalendar.js" group="lecm-head-resources"/>
	<@script type="text/javascript" src="${url.context}/res/scripts/lecm-events/jquery.formbubble.js" group="lecm-head-resources"/>
	<@script type="text/javascript" src="${url.context}/res/scripts/lecm-events/fullcalendar.viewmore.js" group="lecm-head-resources"/>

	<@script type="text/javascript" src="${url.context}/res/scripts/lecm-base/components/base64.js" group="lecm-head-resources"/>
	<@script type="text/javascript" src="${url.context}/res/scripts/lecm-base/components/base-utils.js" group="lecm-head-resources"/>
	<#-- Исправление баги alfresco: при некоторых условиях во время добавления тега страница перезагружается -->
	<@script type="text/javascript" src="${url.context}/res/scripts/lecm-base/alfresco/tag-editor-fix.js" group="lecm-head-resources"/>
	<@script type="text/javascript" src="${url.context}/res/scripts/lecm-base/constraints/regex-color-validator.js" group="lecm-head-resources"/>
</@>
