<#assign id = args.htmlid>
<!--[if IE]>
<iframe id="yui-history-iframe" src="${url.context}/res/yui/history/assets/blank.html"></iframe>
<![endif]-->
<input id="yui-history-field" type="hidden" />
<script type="text/javascript">//<![CDATA[
	var $html = Alfresco.util.encodeHTML,
		$links = Alfresco.util.activateLinks,
		$userProfile = Alfresco.util.userProfileLink;

	LogicECM.module.Base.DataGrid.prototype.getCellFormatter = function () {
		var scope = this;

		/**
		 * Data Type custom formatter
		 *
		 * @method renderCellDataType
		 * @param elCell {object}
		 * @param oRecord {object}
		 * @param oColumn {object}
		 * @param oData {object|string}
		 */
		return function DataGrid_renderCellDataType(elCell, oRecord, oColumn, oData)
		{
			var html = "";

			// Populate potentially missing parameters
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
						for (var i = 0, ii = oData.length, data; i < ii; i++)
						{
							data = oData[i];

							if (datalistColumn.name == "lecm-dic:plane")  {
								html += data.displayValue ? "${msg('logicecm.dictionary.plane')}" : "${msg('logicecm.dictionary.hierarchical')}";
							} else if (datalistColumn.name == "cm:name") {
								html += "<a href='" + Alfresco.constants.URL_PAGECONTEXT + "dictionary?dic=" + encodeURIComponent(data.displayValue) + "'>" + data.displayValue + "</a>";
							} else {
								switch (datalistColumn.dataType.toLowerCase())
								{
									case "cm:person":
										html += '<span class="person">' + $userProfile(data.metadata, data.displayValue) + '</span>';
										break;

									case "datetime":
										html += Alfresco.util.formatDate(Alfresco.util.fromISO8601(data.value), scope.msg("date-format.default"));
										break;

									case "date":
										html += Alfresco.util.formatDate(Alfresco.util.fromISO8601(data.value), scope.msg("date-format.defaultDateOnly"));
										break;

									case "text":
										html += $links($html(data.displayValue));
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
	}

	LogicECM.module.Base.DataGrid.prototype.getDataTableColumnDefinitions = function () {
		// YUI DataTable column definitions
		var columnDefinitions =
				[
					{ key: "nodeRef", label: "<input type='checkbox' id='select-all-records'>", sortable: false, formatter: this.fnRenderCellSelected(), width: 16 }
				];

		var column;
		for (var i = 0, ii = this.datagridColumns.length; i < ii; i++)
		{
			column = this.datagridColumns[i];
			var label = column.label;
			if (column.name == "lecm-dic:plane") {
				label = "${msg('logicecm.dictionary.type')}";
			}
			columnDefinitions.push(
					{
						key: this.dataResponseFields[i],
						label: label,
						sortable: true,
						sortOptions:
						{
							field: column.formsName,
							sortFunction: this.getSortFunction()
						},
						formatter: this.getCellFormatter(column.dataType)
					});
		}

		// Add actions as last column
		columnDefinitions.push(
				{ key: "actions", label: this.msg("label.column.actions"), sortable: false, formatter: this.fnRenderCellActions(), width: 80 }
		);
		return columnDefinitions;
	}

	new LogicECM.module.Base.DataGrid('${id}').setOptions(
	        {
	            usePagination: true
	        }).setMessages(${messages});
//]]></script>

<div id="${id}-body" class="datagrid">
    <div class="datagrid-meta">
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
        <hr/>
        <h2 id="${id}-title"></h2>
        <div id="${id}-description" class="datagrid-description"></div>
    </div>
    <div id="${id}-datagridBar" class="yui-ge datagrid-bar flat-button">
        <div class="yui-u first align-center">
            <#--<div class="item-select">
                <button id="${args.htmlid}-itemSelect-button" name="datagrid-itemSelect-button">${msg("menu.select")}</button>
                <div id="${args.htmlid}-itemSelect-menu" class="yuimenu">
                    <div class="bd">
                        <ul>
                            <li><a href="#"><span class="selectAll">${msg("menu.select.all")}</span></a></li>
                            <li><a href="#"><span class="selectInvert">${msg("menu.select.invert")}</span></a></li>
                            <li><a href="#"><span class="selectNone">${msg("menu.select.none")}</span></a></li>
                        </ul>
                    </div>
                </div>
            </div>-->
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
            <div class="onActionShowMore"><a href="#" class="show-more" title="${msg("actions.more")}"><span>${msg("actions.more")}</span></a></div>
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
