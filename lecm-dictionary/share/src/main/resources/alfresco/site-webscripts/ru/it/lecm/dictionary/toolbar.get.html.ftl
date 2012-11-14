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
                   <span class="first-child">
                      <button type="button">${msg('logicecm.dictionary.add-element')}</button>
                   </span>
                </span>
			</div>
			<div class="delete-row">
                <span id="${id}-deleteButton" class="yui-button yui-push-button">
                   <span class="first-child" class="onActionDelete">
                      <button type="button" title="${msg('menu.selected-items.delete')}"/>
                   </span>
                </span>
			</div>
			<div class="exportcsv">
                <span id="${id}-exportCsvButton" class="yui-button yui-push-button">
                   <span class="first-child">
                      <button type="button" title="${msg('button.export-csv')}"/>
                   </span>
                </span>
			</div>
			<div class="file-import-csv">
				<span id="${id}-importCsvButton" class="yui-button yui-push-button">
					<span class="first-child" >
						<button type="button" title="${msg('button.import-csv')}"></button>
					</span>
				</span>
			</div>
			<div class="file-import-xml">
				<span id="${id}-importXmlButton" class="yui-button yui-push-button">
					<span class="first-child" >
						<button type="button" title="${msg('button.import-xml')}">XML</button>
					</span>
				</span>
			</div>
		</div>
		<div class="import-csv">
			<form name="panel-2-form" id="panel-2-form" method="post" enctype="multipart/form-data"
			      action="${url.context}/proxy/alfresco/lecm/dictionary/post/import-csv" target="importFrameCsv">
				<div id="panel-2">
					<div class="hd">${msg('button.import-csv')}</div>
					<input type="file" name="f">
					<input type="hidden" value="" name="nodeRef" id="nodeRef" />
					<input type="submit" id="panel-2-button-2" name="button-2" value="Submit"></p>
				</div>
			</form>
			<iframe id="importFrameCsv" name="importFrameCsv" src="" style="display:none;"></iframe>
		</div>
		<div class="right">
			<span id="${id}-searchInput" class="search-input">
				<input type="text" id="dictionaryFullSearchInput" value="">
			</span>
			<span id="${id}-searchButton" class="yui-button yui-push-button search">
				<span class="first-child">
					<button type="button" title="${msg('button.search')}"/>
				</span>
			</span>
		</div>

		<div class="import-xml">
			<form name="panel-1-form" id="panel-1-form" method="post" enctype="multipart/form-data"
			      action="${url.context}/proxy/alfresco/lecm/dictionary/post/import" target="importFrameXml">
				<div id="panel-1">
					<div class="hd">${msg('button.import-xml')}</div>
					<p><input type="file" name="f">
						<input type="submit" id="panel-1-button-1" name="button-1" value="Submit"></p>
				</div>
			</form>
			<iframe id="importFrameXml" name="importFrameXml" src="" style="display:none;"></iframe>
		</div>

	</div>
</div>