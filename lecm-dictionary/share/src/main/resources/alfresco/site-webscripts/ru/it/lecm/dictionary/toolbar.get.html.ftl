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

		<div class="import-xml">
			<button class="left" id="show-dialog-1" title="${msg('button.import-xml')}">${msg('button.import-xml')}</button>
			<form name="panel-1-form" id="panel-1-form" method="post" enctype="multipart/form-data"
			      action="${url.context}/proxy/alfresco/lecm/dictionary/post/import">
				<div id="panel-1">
					<p><input type="file" name="f">
						<input type="submit" id="panel-1-button-1" name="button-1" value="Submit"></p>
				</div>
			</form>
		</div>
		<div class="file-upload">
			<span id="${id}-submitButton" class="yui-button yui-push-button">
				<span class="first-child">
					<button type="button" title="${msg('button.import-csv')}">${msg('button.import-csv')}</button>
				</span>
			</span>
		</div>
		<div class="import-csv">
		<#--<button class="left" id="show-dialog-2" title="${msg('button.import-csv')}">${msg('button.import-csv')}</button>-->
		<form name="panel-2-form" id="panel-2-form" method="post" enctype="multipart/form-data" action="${url.context}/proxy/alfresco/lecm/dictionary/post/import-csv">
			<div id="panel-2">

				<input type="file" name="f">
				<input type="hidden" value="" name="nodeRef" id="nodeRef" />
					<input type="submit" id="panel-2-button-2" name="button-2" value="Submit"></p>
			<#--<span id="${id}-submitButton" class="yui-button yui-push-button">-->
			<#--<span class="first-child">-->
			<#--<button type="button" title="${msg('button.search')}"></button>-->
			<#--</span>-->
			<#--</span>-->
			</div>
		</form>
		</div>
	</div>
</div>