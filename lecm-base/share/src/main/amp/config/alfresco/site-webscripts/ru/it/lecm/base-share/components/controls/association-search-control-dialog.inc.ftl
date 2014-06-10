<#macro renderSearchPickerHTML controlId>
	<#assign pickerId = controlId + "-picker">

	<div id="${pickerId}">
		<div id="${pickerId}-body" class="bd">
			<div class="control">
				<div id="${pickerId}-searchContainer" class="container">
					<div class="buttons-div">
						<span class="search-button"><button id="${pickerId}-searchButton" name="-">&nbsp;</button></span>
					</div>
					<div class="value-div">
						<input type="text" name="-" id="${pickerId}-searchText" value="" maxlength="256" />
					</div>
				</div>
			</div>
            <div class="clear"></div>

			<div>
				<strong>${msg("logicecm.base.elements-for-select")}</strong>
				<div id="${pickerId}-dataTable">
					<div id="${pickerId}-group-members"  class="picker-items"></div>
				</div>
			</div>
		</div>
	</div>
</#macro>

<#macro renderSearchPickerDialogHTML controlId>
	<#assign pickerId = controlId + "-picker">

	<div id="${pickerId}" class="yui-panel assoc-dialog association-search-dialog">
		<div id="${pickerId}-head" class="hd">${msg("form.control.object-picker.header")}</div>
		<div id="${pickerId}-body" class="bd">
            <div class="control">
                <div id="${pickerId}-searchContainer" class="container">
                    <div class="buttons-div">
                        <span class="search-button"><button id="${pickerId}-searchButton" name="-">&nbsp;</button></span>
                    </div>
                    <div class="value-div">
                        <input type="text" name="-" id="${pickerId}-searchText" value="" maxlength="256" />
                    </div>
                </div>
            </div>
            <div class="clear"></div>

			<div>
                <strong>${msg("logicecm.base.elements-for-select")}</strong>
                <div id="${pickerId}-dataTable">
                    <div id="${pickerId}-group-members"  class="picker-items"></div>
                </div>
            </div>
			<div id="${pickerId}-selection">
				<strong>${msg("logicecm.base.selected-elements")}</strong>
				<div id="${pickerId}-selected-elements"></div>
			</div>

			<div class="bdft">
				<button id="${controlId}-ok" tabindex="0">${msg("button.ok")}</button>
				<button id="${controlId}-cancel" tabindex="0">${msg("button.cancel")}</button>
			</div>
		</div>
	</div>
</#macro>