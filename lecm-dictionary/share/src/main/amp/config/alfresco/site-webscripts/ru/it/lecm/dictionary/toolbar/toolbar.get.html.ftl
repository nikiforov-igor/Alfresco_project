<#assign id = args.htmlid>
<#assign buttons = true/>
<#if showButtons??>
    <#assign buttons = showButtons/>
</#if>
<#assign searchBlock = true/>
<#if showSearchBlock??>
    <#assign searchBlock = showSearchBlock/>
</#if>
<#assign exSearch = true/>
<#if showExSearchBtn??>
    <#assign exSearch = showExSearchBtn/>
</#if>

<script type="text/javascript">//<![CDATA[
(function(){
	function createToolbar() {
	    new LogicECM.module.Dictionary.Toolbar("${id}").setOptions({
		    dictionaryName: "${args.dictionaryName}"
	    }).setMessages(${messages});
	}

    function init() {
        LogicECM.module.Base.Util.loadResources([
            'scripts/lecm-base/components/lecm-toolbar.js',
            'scripts/lecm-dictionary/dictionary-toolbar.js'
        ], [
            'components/data-lists/toolbar.css',
            'css/lecm-dictionary/dictionary-toolbar.css'
        ], createToolbar);
    }

    YAHOO.util.Event.onDOMReady(init);
})();
//]]></script>

<#import "/ru/it/lecm/base-share/components/base-components.ftl" as comp/>
<@comp.baseToolbar id buttons searchBlock exSearch>
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
