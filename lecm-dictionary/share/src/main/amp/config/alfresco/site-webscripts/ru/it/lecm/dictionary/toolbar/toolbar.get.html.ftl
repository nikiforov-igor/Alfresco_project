<!-- Data List Toolbar -->
<@link rel="stylesheet" type="text/css" href="${url.context}/res/components/data-lists/toolbar.css" />
<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/lecm-dictionary/dictionary-toolbar.css" />
<@script type="text/javascript" src="${url.context}/res/scripts/lecm-base/components/lecm-toolbar.js"></@script>
<@script type="text/javascript" src="${url.context}/res/scripts/lecm-dictionary/dictionary-toolbar.js"></@script>

<#assign id = args.htmlid>
<script type="text/javascript">//<![CDATA[
(function(){
	function init() {
	    new LogicECM.module.Dictionary.Toolbar("${id}").setOptions({
		    dictionaryName: "${args.dictionaryName}"
	    }).setMessages(${messages});
	}
	YAHOO.util.Event.onDOMReady(init);
})();
//]]></script>

<#import "/ru/it/lecm/base-share/components/base-components.ftl" as comp/>
<@comp.baseToolbar id true true true>
    <div class="create-row">
            <span id="${id}-newRowButton" class="yui-button yui-push-button">
                <span class="first-child">
                    <button type="button">${msg('logicecm.dictionary.add-element')}</button>
                </span>
            </span>
    </div>
    <div class="divider"></div>
    <div class="delete-row">
            <span id="${id}-deleteButton" class="yui-button yui-push-button onActionDelete">
                <span class="first-child">
                    <button type="button" title="${msg('menu.selected-items.delete')}">&nbsp;</button>
                </span>
            </span>
    </div>
    <div class="exportcsv">
            <span id="${id}-exportCsvButton" class="yui-button yui-push-button">
                <span class="first-child">
                    <button type="button" title="${msg('button.export-csv')}">&nbsp;</button>
                </span>
            </span>
    </div>
</@comp.baseToolbar>
