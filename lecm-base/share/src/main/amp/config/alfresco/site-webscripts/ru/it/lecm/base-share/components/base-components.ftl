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
<div id="${id}-body" class="datalist-toolbar toolbar <#if !showButtons && !showSearchBlock && !showExSeacrhBtn>hidden1</#if>">
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

<#-- Макрос для типового контрола
Список параметров:
field (обязательный) - объект в котором хранится описание поля для которого строится контрол
name (обязательный) - имя контрола, используется вместе с fieldHtmlId для построения полного html-идентификатора контрола, имеет вид ${fieldHtmlId}-${name}
classes (необязательный) - дополнительные css-классы с помощью которых настраивается внешний вид контрола
buttons (необязательный) - html-верстка кнопок для контрола
value (необязательный) - html-верстка полей отвечающих за ввод, хранение и отображение данных в контроле
disabled (необязательный) - отключение контрола, на данный момент отключает в верстке вывод признака обязательности
nested - дополнительная верстка,
-->
<#macro baseControl field name classes='' buttons='' value='' disabled=false>
<#assign fieldHtmlId = args.htmlid?html + '_' + field.id>
<div id='${fieldHtmlId}-${name}' class='control ${classes}'>
	<div class='label-div'>
		<label for='${fieldHtmlId}'>
			<span>${field.label?html}:</span>
			<#if field.mandatory!false>
                <#if !disabled><span class="mandatory-indicator">${msg("form.required.fields.marker")}</span></#if>
			<span class='incomplete-warning'>
				<img src='${url.context}/res/components/form/images/warning-16.png' title='${msg("form.field.incomplete")}'/>
			<span>
			</#if>
		</label>
	</div>
	<div class='container'>
		<#if buttons?? && buttons?has_content>
		<div class='buttons-div'>${buttons}</div>
		</#if>
		<#if value?? && value?has_content>
		<div class='value-div'>${value}</div>
		</#if>
	</div>
	<#nested/>
</div>
</#macro>

<#macro baseControlBtns field renderPickerBtn=true renderCreateBtn=true renderHelpBtn=false>
<#assign fieldHtmlId = args.htmlid?html + '_' + field.id>
<#if renderPickerBtn>
<span id='${fieldHtmlId}-btn-pick' class='yui-button'>
	<span class='first-child'>
		<input type='button' value='...'>
	</span>
</span>
</#if>
<#if renderCreateBtn>
<span id='${fieldHtmlId}-btn-create' class='create-new-button yui-button'>
	<span class='first-child'>
		<input type='button' value=''>
	</span>
</span>
</#if>
<#nested/>
<#if renderHelpBtn>
	<@formLib.renderFieldHelp field=field/>
</#if>
</#macro>

<#macro baseControlValue field fieldValue showAutocomplete isDefaultValue>
<#assign fieldHtmlId = args.htmlid?html + '_' + field.id>
<#assign addedValue = ""/>
<#if isDefaultValue>
	<#assign addedValue = fieldValue/>
</#if>

<input type='hidden' id='${fieldHtmlId}-added' name='${field.name}_added' value='${addedValue?html}'>
<input type='hidden' id='${fieldHtmlId}-removed' name='${field.name}_removed'>
<input type='hidden' id='${fieldHtmlId}' name='${field.name}' value='${fieldValue?html}'>
<#if showAutocomplete>
<input type='text' id='${fieldHtmlId}-autocomplete'>
</#if>
<div id='${fieldHtmlId}-displayed' class='control-selected-values'></div>
</#macro>
