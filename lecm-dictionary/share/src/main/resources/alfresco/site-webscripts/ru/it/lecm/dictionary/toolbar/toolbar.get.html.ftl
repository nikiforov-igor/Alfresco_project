<#assign id = args.htmlid>
<script type="text/javascript">//<![CDATA[
function init() {
	new LogicECM.module.Dictionary.Toolbar("${id}").setMessages(${messages});
}

YAHOO.util.Event.onDOMReady(init);
//]]></script>

<div id="${args.htmlid}-body" class="datalist-toolbar toolbar">
	<div id="${args.htmlid}-headerBar" class="header-bar flat-button theme-bg-2">
		<div class="left">
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
		</div>

		<div class="right">
			<span class="search-input">
				<input type="text" id="${id}-searchInput" value="">
				<a href="javascript:void(0);" id="${id}-clearSearchInput"  class="clear-search">
					<span>&nbsp;</span>
				</a>
			</span>
			<span id="${id}-searchButton" class="yui-button yui-push-button search">
                <span class="first-child">
                    <button type="button" title="${msg('button.search')}">&nbsp;</button>
			    </span>
			</span>
		</div>
	</div>
</div>