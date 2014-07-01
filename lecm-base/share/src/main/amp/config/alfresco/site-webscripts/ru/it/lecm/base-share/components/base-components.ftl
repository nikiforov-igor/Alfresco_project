<#macro baseMenu>
<div id="menu-buttons">
	<#nested>
</div>
</#macro>

<#-- Макрос для кнопки вертикального меню
Список параметров:
id (обязательный) - идентификатор раздела
title (необязательный) - всплывающая подсказка кнопки
selectedEl (необязательный) - идентификатор раздела, выбранного в модуле в данный момент
hasText (необязательный) - добавлять ли подпись к кнопке
longText (необязательный) - подпись к кнопке - длинная, занимает две строки
-->
<#macro baseMenuButton id title='' selectedEl='' hasText=false longText=false>
    <span id="menu-buttons-${id}Btn" class="yui-button yui-push-button menu-button <#if selectedEl == id>selected</#if>
        <#if longText> long-text</#if>">
        <span class="first-child">
            <button type="button" title="${title}">&nbsp;</button>
            <#if hasText>
                <span>
                    ${title}
                </span>
            </#if>
        </span>
    </span>
</#macro>

<#-- Макрос для подключения тулбара
Список параметров:
id(обязательный) - идентификатор, использующийся для построения html и передающийся в объект DataGrid. Лучше использовать args.htmlid (по аналогии с другими местами в Alfresco)
showButtons(необязательный) - показывать ли блок с кнопками. Сама разметка кнопок передается в макрос как Nested содержимое
showSearchBlock (необязательный) - показывать блок полнотекстового поиска (по умолчанию - показывать)
showExSeacrhBtn(необязательный) - добавить(показывать) кнопку атрибутивного поиска.
-->
<#macro baseToolbar id showButtons=true showSearchBlock=true showExSeacrhBtn=false>
<div id="${id}-body" class="datalist-toolbar toolbar">
	<div id="${id}-headerBar" class="header-bar flat-button">
		<div class="left">
			<#if showButtons>
				<#nested/>
			</#if>
		</div>

		<div class="right <#if !showSearchBlock>hidden1</#if>">
            <span id="${id}-searchInput" class="search-input">
				<input type="text" id="${id}-full-text-search" value="">
	            <a href="javascript:void(0);" id="${id}-clearSearchInput"  class="clear-search">
		            <span>&nbsp;</span>
	            </a>
			</span>
            <span id="${id}-searchButton" class="search yui-button yui-push-button">
                <span class="first-child">
                    <button type="button" id ="searchBtn" title="${msg('button.search')}"></button>
                </span>
            </span>
            <span id="${id}-extendSearchButton" class="ex-search yui-button yui-push-button <#if !showExSeacrhBtn>hidden1</#if>">
                <span class="first-child">
                    <button type="button" id="exsearchBtn" title="${msg('button.ex_search')}"></button>
                </span>
            </span>
		</div>
		<#if showExSeacrhBtn>
        <div class="hidden1">
			<div id="searchBlock" class="yui-panel">
				<div id="${id}-search-head" class="hd">${msg("search-block")}</div>
				<div id="${id}-search-body" class="bd">
					<div id="${id}-search-content">
						<div id="searchBlock-content" >
							<div id="${id}-searchContainer" class="search">
								<#-- Контейнер для отрисовки формы -->
								<div id="searchBlock-forms" class="forms-container form-fields"></div>
							</div>
						</div>
						<div class="bdft">
							<#-- Кнопка Очистки -->
							<div class="yui-u align-right right">
                            <span id="searchBlock-clearSearch-button" class="yui-button yui-push-button search-icon">
                                <span class="first-child">
                                    <button type="button">${msg('button.clear')}</button>
                                </span>
                            </span>
							</div>

							<#-- Кнопка Найти -->
							<div class="yui-u align-right">
                            <span id="searchBlock-search-button" class="yui-button yui-push-button search-icon">
                                <span class="first-child">
                                    <button type="button">${msg('button.search')}</button>
                                </span>
                            </span>
							</div>
						</div>
					</div>
				</div>
			</div>
        </div>
		</#if>
        <div class="clear"></div>
	</div>
</div>
</#macro>