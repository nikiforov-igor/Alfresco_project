<#macro renderSearchPickerDialogHTML controlId>
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