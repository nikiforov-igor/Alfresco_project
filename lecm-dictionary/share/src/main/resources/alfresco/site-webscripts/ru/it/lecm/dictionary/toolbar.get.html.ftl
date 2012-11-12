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
                      <button type="button" title="${msg('button.export')}"/>
                   </span>
                </span>
			</div>
			<div class="file-upload">
               <span id="${id}-fileUpload-button" class="yui-button yui-push-button">
                  <span class="first-child">
                     <button name="fileUpload">${msg("button.upload")}</button>
                  </span>
               </span>
			</div>
			<div id="uploaderUI" style="width:100px;height:40px;margin-left:5px;float:left"></div>
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
	</div>
</div>