<#assign id = args.htmlid>
<script type="text/javascript">//<![CDATA[
new Alfresco.component.DataListToolbar("${id}").setOptions(
		{
			siteId: "site"
		}).setMessages(${messages});
//]]></script>

<div id="${args.htmlid}-body" class="datalist-toolbar toolbar">
	<div id="${args.htmlid}-headerBar" class="header-bar flat-button theme-bg-2">
		<div class="left">
			<div class="create-row">
                <span id="${id}-newRowButton" class="yui-button yui-push-button">
                    <button type="button">${msg('logicecm.dictionary.add-element')}</button>
                </span>
			</div>
			<div class="delete-row">
                <span id="${id}-deleteButton" class="yui-button yui-push-button onActionDelete">
                    <button type="button" title="${msg('menu.selected-items.delete')}"/>
                </span>
			</div>
			<div class="exportcsv">
                <span id="${id}-exportCsvButton" class="yui-button yui-push-button">
                    <button type="button" title="${msg('button.export-csv')}"/>
                </span>
			</div>
            <div class="import-csv">
                <div id="show-import-csv" title="${msg('button.import-csv')}"></div>

                <div class="form-container" title="${msg('button.import-csv')}">
                    <form method="post" id="import-csv-form" enctype="multipart/form-data"
                          action="${url.context}/proxy/alfresco/lecm/dictionary/post/import-csv">
                        <input type="file" id="import-csv-input" name="f" accept=".csv,application/csv,text/csv">
                        <input type="hidden" value="" name="nodeRef" id="nodeRef" />
                    </form>
                </div>
            </div>
		</div>
		<div class="right">
			<span id="${id}-searchInput" class="search-input">
				<input type="text" id="dictionaryFullSearchInput" value="">
			</span>
			<span id="${id}-searchButton" class="yui-button yui-push-button search">
                <button type="button" title="${msg('button.search')}"/>
			</span>
		</div>
	</div>
</div>