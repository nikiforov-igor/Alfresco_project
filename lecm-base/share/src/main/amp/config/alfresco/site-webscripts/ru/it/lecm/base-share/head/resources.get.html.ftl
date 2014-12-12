<@markup id="resources">
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
	})();
	</@>
	<@script type="text/javascript" src="${url.context}/res/jquery/jquery-1.6.2.js" group="lecm-head-resources"/>
	<@script type="text/javascript" src="${url.context}/res/scripts/lecm-base/components/base-utils.js" group="lecm-head-resources"/>
	<#-- Исправление баги alfresco: при некоторых условиях во время добавления тега страница перезагружается -->
	<@script type="text/javascript" src="${url.context}/res/scripts/lecm-base/alfresco/tag-editor-fix.js" group="lecm-head-resources"/>
</@>
