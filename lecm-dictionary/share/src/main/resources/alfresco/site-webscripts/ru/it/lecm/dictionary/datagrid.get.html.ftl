<#assign id = args.htmlid>
<!--[if IE]>
<iframe id="yui-history-iframe" src="${url.context}/res/yui/history/assets/blank.html"></iframe>
<![endif]-->

<#assign viewFormId = "dictionary-view-node-form">

<input id="yui-history-field" type="hidden" />
<script type="text/javascript">//<![CDATA[
	function createDatagrid(attributeForShow) {
		var $html = Alfresco.util.encodeHTML,
				$links = Alfresco.util.activateLinks,
				$userProfile = Alfresco.util.userProfileLink;

		LogicECM.module.Base.DataGrid.prototype.getCellFormatter = function () {
			var scope = this;

			return function DataGrid_renderCellDataType(elCell, oRecord, oColumn, oData)
			{
				var html = "";

				if (!oRecord)
				{
					oRecord = this.getRecord(elCell);
				}
				if (!oColumn)
				{
					oColumn = this.getColumn(elCell.parentNode.cellIndex);
				}

				if (oRecord && oColumn)
				{
					if (!oData)
					{
						oData = oRecord.getData("itemData")[oColumn.field];
					}

					if (oData)
					{
						var datalistColumn = scope.datagridColumns[oColumn.key];
						if (datalistColumn)
						{
							oData = YAHOO.lang.isArray(oData) ? oData : [oData];
							var plane = true;

							for (var i = 0, ii = oData.length, data; i < ii; i++)
							{
								data = oData[i];

								switch (datalistColumn.dataType.toLowerCase())
								{
									case "cm:person":
										html += '<span class="person">' + $userProfile(data.metadata, data.displayValue) + '</span>';
										break;

									case "datetime":
										var content = Alfresco.util.formatDate(Alfresco.util.fromISO8601(data.value), scope.msg("date-format.default"));
										if (datalistColumn.name == attributeForShow) {
											content = "<a href='javascript:void(0);' onclick=\"viewDictionaryAttributes(\'" + oRecord.getData("nodeRef") + "\')\">" + content + "</a>";
										}
										html += content;
										break;

									case "date":
										var content = Alfresco.util.formatDate(Alfresco.util.fromISO8601(data.value), scope.msg("date-format.defaultDateOnly"));
										if (datalistColumn.name == attributeForShow) {
											content = "<a href='javascript:void(0);' onclick=\"viewDictionaryAttributes(\'" + oRecord.getData("nodeRef") + "\')\">" + content + "</a>";
										}
										html += content;
										break;

									case "text":
										var content = $html(data.displayValue);
										if (datalistColumn.name == attributeForShow) {
											html += "<a href='javascript:void(0);' onclick=\"viewDictionaryAttributes(\'" + oRecord.getData("nodeRef") + "\')\">" + content + "</a>";
										} else {
											html += $links(content);
										}
										break;

									default:
										if (datalistColumn.type == "association")
										{
											html += '<a href="' + Alfresco.util.siteURL((data.metadata == "container" ? 'folder' : 'document') + '-details?nodeRef=' + data.value) + '">';
											html += '<img src="' + Alfresco.constants.URL_RESCONTEXT + 'components/images/filetypes/' + Alfresco.util.getFileIcon(data.displayValue, (data.metadata == "container" ? 'cm:folder' : null), 16) + '" width="16" alt="' + $html(data.displayValue) + '" title="' + $html(data.displayValue) + '" />';
											html += ' ' + $html(data.displayValue) + '</a>'
										}
										else
										{
											html += $links($html(data.displayValue));
										}
										break;
								}

								if (i < ii - 1)
								{
									html += "<br />";
								}
							}
						}
					}
				}

				elCell.innerHTML = html;
			};
		};

		new LogicECM.module.Base.DataGrid('${id}').setOptions(
				{
					usePagination: true
				}).setMessages(${messages});
	}

	var viewDialog = null;

	function viewDictionaryAttributes(nodeRef) {
		Alfresco.util.Ajax.request(
		{
			url: Alfresco.constants.URL_SERVICECONTEXT + "components/form",
			dataObj:
			{
				htmlid: "DictionaryMetadata-" + nodeRef,
				itemKind: "node",
				itemId: nodeRef,
				formId: "${viewFormId}",
				mode: "view"
			},
			successCallback:
			{
				fn: showViewDialog
			},
			failureMessage: "message.failure",
			execScripts: true
		});
		return false;
	}

	function showViewDialog(response) {
		var formEl = Dom.get("${viewFormId}-content");
		formEl.innerHTML = response.serverResponse.responseText;
		if (viewDialog!= null) {
			viewDialog.show();
		}
	}

	function hideViewDialog() {
		if (viewDialog!= null) {
			viewDialog.hide();
		}
	}

	function loadDictionary() {
		var me = this;
		var  sUrl = Alfresco.constants.PROXY_URI + "/lecm/dictionary/api/getDictionary?dicName=" + encodeURIComponent("${page.url.args.dic!''}");

		var callback = {
			success:function (oResponse) {
				var oResults = eval("(" + oResponse.responseText + ")");
				if (oResults != null) {
					createDatagrid(oResults.attributeForShow);
				}
			},
			failure:function (oResponse) {
				alert("Failed to load dictionary " + me.options.dictionaryName);
			},
			argument:{
			}
		};
		YAHOO.util.Connect.asyncRequest('GET', sUrl, callback);
	}

	function init() {
		loadDictionary();

		viewDialog = Alfresco.util.createYUIPanel("${viewFormId}",
				{
					width: "487px"
				});
	}

	YAHOO.util.Event.onDOMReady(init);
//]]></script>

<div id="${id}-body" class="datagrid">
   <div class="datagrid-meta">

		<div id="${viewFormId}" class="yui-panel">

			<div id="${viewFormId}-head" class="hd">${msg("logicecm.dictionary.view")}</div>

			<div id="${viewFormId}-body" class="bd">

				<div id="${viewFormId}-content"></div>

				<div class="bdft">
					<button id="${viewFormId}-cancel" tabindex="0" onclick="hideViewDialog();">${msg("button.close")}</button>
				</div>
			</div>
		</div>

	   <div id="searchBlock" style="display: none;">
		   <h2 id="${id}-heading" class="thin dark">
		   ${msg("search-block")}
		   </h2>
		   <div id="${id}-searchContainer" class="search">
			   <div class="yui-gc form-row">
			   <#-- search button -->
				   <div class="yui-u align-right">
	                    <span id="${id}-search-button-1" class="yui-button yui-push-button search-icon">
	                        <span class="first-child">
	                        <button type="button">${msg('button.search')}</button>
	                        </span>
	                    </span>
	                    <span id="${id}-clear-button" class="yui-button yui-push-button">
	                        <span class="first-child">
	                            <button type="button">${msg('button.clear')}</button>
	                        </span>
	                    </span>
				   </div>
			   </div>

		   <#-- keywords entry box - DIV structure mirrors a generated Form to collect the correct styles -->
			   <div class="forms-container keywords-box">
				   <div class="share-form">
					   <div class="form-container">
						   <div class="form-fields">
							   <div class="set">
								   <div>${msg("label.keywords")}:</div>
								   <input type="text" class="terms" name="${id}-search-text" id="${id}-search-text"
								          value="${(page.url.args["st"]!"")?html}" maxlength="1024"/>
							   </div>
						   </div>
					   </div>
				   </div>
			   </div>
		   <#-- container for forms retrieved via ajax -->
			   <div id="${id}-forms" class="forms-container form-fields"></div>

			   <div class="yui-gc form-row">
				   <div class="yui-u first"></div>
			   <#-- search button -->
				   <div class="yui-u align-right">
	                    <span id="${id}-search-button-2" class="yui-button yui-push-button search-icon">
	                        <span class="first-child">
	                            <button type="button">${msg('button.search')}</button>
	                        </span>
	                    </span>
				   </div>
			   </div>
		   </div>
		   <script type="text/javascript">//<![CDATA[
		   /*Alfresco.util.createTwister.collapsed =
								   "OrgstructureSearch" + (Alfresco.util.createTwister.collapsed.length > 0 ? ",":"") + Alfresco.util.createTwister.collapsed ;*/
		   Alfresco.util.createTwister("${id}-heading", "OrgstructureSearch");
		   //]]></script>
	   </div>

	   <form id="uploadForm" enctype="multipart/form-data" method="post" target=uploadFrame
	         action="${url.context}/proxy/alfresco/lecm/dictionary/post/import-csv">
			<p><input type="file" name="f">
			<input type="submit" value="Import"></p>
	   </form>
	   <div id="progressBar" style="display:none;">
	   </div>
	   <iframe id="uploadFrame" name="uploadFrame" src="" style="display:none;"></iframe>
		<h2 id="${id}-title"></h2>
      <div id="${id}-description" class="datagrid-description"></div>
   </div>
   <div id="${id}-datagridBar" class="yui-ge datagrid-bar flat-button">
      <div class="yui-u first align-center">
	     <div class="item-select">&nbsp;</div>
         <div id="${id}-paginator" class="paginator"></div>
      </div>
      <div class="yui-u align-right">
         <div class="items-per-page" style="visibility: hidden;">
            <button id="${id}-itemsPerPage-button">${msg("menu.items-per-page")}</button>
         </div>
      </div>
   </div>

   <div id="${id}-grid" class="grid"></div>

   <div id="${id}-selectListMessage" class="hidden select-list-message">${msg("message.select-list")}</div>

   <div id="${id}-datagridBarBottom" class="yui-ge datagrid-bar datagrid-bar-bottom flat-button">
      <div class="yui-u first align-center">
         <div class="item-select">&nbsp;</div>
         <div id="${id}-paginatorBottom" class="paginator"></div>
      </div>
   </div>

   <!-- Action Sets -->
   <div style="display:none">
      <!-- Action Set "More..." container -->
      <div id="${args.htmlid}-moreActions">
         <div class="onActionShowMore"><a href="#" class="show-more" title="${msg("actions.more")}"></a></div>
	     <#--<div class="onActionVersion"><a href="#" class="show-more" title="${msg("actions.more")}"></a></div>-->
         <div class="more-actions hidden"></div>
      </div>

      <!-- Action Set Templates -->
      <div id="${args.htmlid}-actionSet" class="action-set simple">
      <#list actionSet as action>
         <div class="${action.id}"><a rel="${action.permission!""}" href="${action.href}" class="${action.type}" title="${msg(action.label)}"><span>${msg(action.label)}</span></a></div>
      </#list>
      </div>
   </div>
</div>