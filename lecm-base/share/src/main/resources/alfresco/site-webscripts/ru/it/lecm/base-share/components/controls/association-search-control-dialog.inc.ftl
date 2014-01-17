<#macro renderSearchPickerHTML controlId>
	<#assign pickerId = controlId + "-picker">

	<div id="${pickerId}" class="search-picker">
		<div id="${pickerId}-body" class="bd">
			<div class="yui-gb orgchart-picker-menu">
				<div class="first yui-skin-sam search">
					<input type="text" class="search-input" name="-" id="${pickerId}-searchText" value="" maxlength="256" />
					<span class="search-button"><button id="${pickerId}-searchButton" name="-">&nbsp;</button></span>
				</div>
			</div>

			<div><strong>${msg("logicecm.base.elements-for-select")}</strong></div>
			<div class="yui-g">
				<div id="${pickerId}-dataTable">
					<div id="${pickerId}-group-members"  class="picker-items"></div>
				</div>
			</div>
		</div>
	</div>
</#macro>

<#macro renderSearchPickerDialogHTML controlId>
	<#assign pickerId = controlId + "-picker">

	<div id="${pickerId}" class="picker yui-panel tree-picker">

		<div id="${pickerId}-head" class="hd">${msg("form.control.object-picker.header")}</div>

		<div id="${pickerId}-body" class="search-picker bd">
			<div class="yui-gb orgchart-picker-menu">
				<div class="first yui-skin-sam search">
					<input type="text" class="search-input" name="-" id="${pickerId}-searchText" value="" maxlength="256" />
					<span class="search-button"><button id="${pickerId}-searchButton" name="-">&nbsp;</button></span>
				</div>
			</div>

			<div><strong>${msg("logicecm.base.elements-for-select")}</strong></div>
			<div class="yui-g">
				<div id="${pickerId}-dataTable">
					<div id="${pickerId}-group-members"  class="picker-items"></div>
				</div>
			</div>

			<div id="${pickerId}-selection">
				<div><strong>${msg("logicecm.base.selected-elements")}</strong></div>
				<div id="${pickerId}-selected-elements"></div>
			</div>

			<div class="bdft">
				<button id="${controlId}-ok" tabindex="0">${msg("button.ok")}</button>
				<button id="${controlId}-cancel" tabindex="0">${msg("button.cancel")}</button>
			</div>
		</div>
	</div>
</#macro>