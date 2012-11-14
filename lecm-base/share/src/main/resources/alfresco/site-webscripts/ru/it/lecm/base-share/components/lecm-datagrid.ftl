<#macro extendedSearch id>
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
							       value="" maxlength="1024"/>
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
	Alfresco.util.createTwister("${id}-heading", "OrgstructureSearch");
	//]]></script>
</div>
</#macro>

<#macro viewForm viewFormId>
<div id="${viewFormId}" class="yui-panel">
	<div id="${viewFormId}-head" class="hd">${msg("logicecm.dictionary.view")}</div>
	<div id="${viewFormId}-body" class="bd">
		<div id="${viewFormId}-content"></div>
		<div class="bdft">
			<button id="${viewFormId}-cancel" tabindex="0" onclick="hideViewDialog();">${msg("button.close")}</button>
		</div>
	</div>
</div>
</#macro>

<#macro datagrid id showSearchBlock showViewForm viewFormId>
<#nested>
<!--[if IE]>
<iframe id="yui-history-iframe" src="${url.context}/res/yui/history/assets/blank.html"></iframe>
<![endif]-->
<input id="yui-history-field" type="hidden" />
<div id="${id}-body" class="datagrid">
	<div class="datagrid-meta">
		<#if showViewForm>
			<@viewForm viewFormId/>
		</#if>
		<#if showSearchBlock>
			<@extendedSearch id/>
		</#if>
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
		<div id="${id}-moreActions">
			<div class="onActionShowMore"><a href="#" class="show-more" title="${msg("actions.more")}"><span>${msg("actions.more")}</span></a></div>
			<div class="more-actions hidden"></div>
		</div>

		<!-- Action Set Templates -->
		<div id="${id}-actionSet" class="action-set simple">
			<#list actionSet as action>
				<div class="${action.id}"><a rel="${action.permission!""}" href="${action.href}" class="${action.type}" title="${msg(action.label)}"><span>${msg(action.label)}</span></a></div>
			</#list>
		</div>
	</div>
</div>
</#macro>