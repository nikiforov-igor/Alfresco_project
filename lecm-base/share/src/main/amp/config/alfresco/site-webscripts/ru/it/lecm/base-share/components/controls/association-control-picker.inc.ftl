<#macro picker controlId items params>
<#assign id=controlId + '-picker'>
<#assign isComplex = items?size gt 1>
<div id='${id}' class='yui-panel association-picker'>
	<div id='${id}-head' class='hd'>
		<span>Выбор...</span>
	</div>
	<div id='${id}-body' class='bd <#if params.cssClasses??>${params.cssClasses}</#if>'>
		<div class='form-fields'>
			<#if isComplex>
			<div class="control">
                <div class='label-div'>
                    <label for='${id}-select'>${msg('label.type')}:</label>
                </div>
                <div class="container">
                    <button id='${id}-select'></button>
                </div>
			</div>
			</#if>
			<#list items as i>
				<#assign itemKey = i?replace(':', '_')>
				<#assign isPlane = (params[itemKey + '_plane']?? && 'true' == params[itemKey + '_plane']?lower_case) || params.plane?? && 'true' == params.plane?lower_case>
				<#assign showSearch = (params[itemKey + '_showSearch']?? && 'true' == params[itemKey + '_showSearch']?lower_case) || params.showSearch?? && 'true' == params.showSearch?lower_case>
				<#assign showExSearch = (params[itemKey + '_showExSearch']?? && 'true' == params[itemKey + '_showExSearch']?lower_case) || params.showExSearch?? && 'true' == params.showExSearch?lower_case>
				<#assign datatableClass = isPlane?string('panel-datatable', 'panel-right')>
			<div id='${id}-${itemKey}' class='hidden'>
				<#if showSearch>
				<div class='control'>
					<div class='container'>
						<div class='buttons-div'>
							<button id='${id}-${itemKey}-search'></button>
						</div>
						<div class='value-div'>
							<input id='${id}-${itemKey}-search-text' type='text'>
						</div>
					</div>
				</div>
				<div class='clear'></div>
				</#if>
				<#if showExSearch>
                    <div id="${id}-${itemKey}-exSearch" class="search control-ex-search">
						<#-- Контейнер для отрисовки формы -->
                        <div id="${id}-${itemKey}-search-form" class="forms-container form-fields"></div>
						<div class='buttons-div'>
							<button id='${id}-${itemKey}-ex-search' name="-">${msg("button.search")}</button>
							<button id='${id}-${itemKey}-ex-search-clear' name="-">${msg("button.clear")}</button>
						</div>
                    </div>
                    <div class='clear'></div>
				</#if>
				<div>
					<div><span><b>Элементы для выбора</b></span></div>
					<div class='${datatableClass}'>
						<!-- датагрид -->
						<div id='${id}-${itemKey}-datatable' class='datatable'></div>
					</div>
					<#if !isPlane>
					<div class='panel-left'>
						<!-- дерево -->
						<div id='${id}-${itemKey}-tree' class='ygtv-highlight' class='treeview'></div>
					</div>
					<div class='clear'></div>
					</#if>
				</div>
			</div>
			</#list>
			<div>
				<span><b>Выбранные элементы</b></span>
				<div id='${id}-items' class='currentValueDisplay'>
				</div>
			</div>
		</div>
	</div>
	<div id='${id}-foot' class='ft'>
		<button id='${id}-ok'>${msg('button.ok')}</button>
		<button id='${id}-cancel'>${msg('button.cancel')}</button>
	</div>
</div>
</#macro>
