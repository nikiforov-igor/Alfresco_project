<#-- Макрос для подключения грида
Список параметров:
id(обязательный) - идентификатор, использующийся для построения html и передающийся в объект DataGrid. Лучше использовать args.htmlid (по аналогии с другими местами в Alfresco)
showViewForm(необязательный) - включать/не включать всплывающее окна по клику на запись
viewFormId(необязательный) - по умолчанию равен view-node-form. Идентификатор, использующийся для построения html для всплывающего окна
-->
<#macro datagrid id showViewForm=true viewFormId="view-node-form" showArchiveCheckBox=false>
<!--[if IE]>
<iframe id="yui-history-iframe" src="${url.context}/res/yui/history/assets/blank.html"></iframe>
<![endif]-->
<input id="yui-history-field" type="hidden" />
<div id="${id}-body" class="datagrid">
	<div class="datagrid-meta">
		<h2 id="${id}-title"></h2>
		<div id="${id}-description" class="datagrid-description"></div>
	</div>
	<div id="${id}-datagridBar" class="yui-ge datagrid-bar flat-button hidden1">
		<div class="yui-u first align-center">
			<div class="item-select">&nbsp;</div>
			<div id="${id}-paginator" class="paginator"></div>
		</div>
		<div class="yui-u align-right">
			<div class="items-per-page visible-hidden">
				<button id="${id}-itemsPerPage-button">${msg("menu.items-per-page")}</button>
			</div>
		</div>
	</div>

    <div id="${id}-toolbar" class="datagrid-toolbar hidden1">
         <span id="${id}-newRowButton" class="yui-button yui-push-button">
               <span class="first-child">
                  <button type="button">${msg('actions.add')}</button>
               </span>
         </span>
    </div>
	<#if showArchiveCheckBox>
        <div class="show-archive-cb-div">
            <input type="checkbox" class="formsCheckBox" id="${id}-cbShowArchive" onChange="YAHOO.Bubbling.fire('archiveCheckBoxClicked', null)">
            <label class="checkbox" for="${id}-cbShowArchive">${msg("logicecm.base.show-arhive.label")}</label>
        </div>
	</#if>
	<div id="${id}-grid" class="grid"></div>

	<div id="${id}-datagridBarBottom" class="yui-ge datagrid-bar datagrid-bar-bottom flat-button">
		<div class="yui-u first align-center">
			<div class="item-select">&nbsp;</div>
			<div id="${id}-paginatorBottom" class="paginator"></div>
		</div>
	</div>

	<!-- Action Sets -->
	<div class="hidden1">
		<!-- Action Set "More..." container -->
		<div id="${id}-moreActions">
			<div class="onActionShowMore">
				<a href="javascript:void(0)" title="${msg("actions.more")}">
					<span></span>
				</a>
			</div>
			<div class="more-actions hidden"></div>
		</div>

		<!-- Action Set Templates -->
		<div id="${id}-actionSet" class="action-set simple">
			<#if actionSet??>
                <#list actionSet as action>
                    <div class="${action.id}"><a rel="${action.permission!""}" href="${action.href}" class="datagrid-action-link ${action.type}" title="${msg(action.label)}"><span>${msg(action.label)}</span></a></div>
                </#list>
            </#if>
		</div>
	</div>
</div>
<#nested>
</#macro>