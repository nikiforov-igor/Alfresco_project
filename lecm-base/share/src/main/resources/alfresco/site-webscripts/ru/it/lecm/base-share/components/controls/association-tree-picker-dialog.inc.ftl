<#macro renderTreePickerDialogHTML controlId plane showSearch>
<#assign pickerId = controlId + "-picker">
<div id="${pickerId}" class="picker yui-panel tree-picker">

    <div id="${pickerId}-head" class="hd">${msg("form.control.object-picker.header")}</div>

    <div id="${pickerId}-body" class="bd">

	    <#if showSearch>
	        <div class="yui-gb orgchart-picker-menu">
	            <div id="${pickerId}-searchContainer" class="first yui-skin-sam search">
	                <input type="text" class="search-input" name="-" id="${pickerId}-searchText" value="" maxlength="256" />
	                <span class="search-button"><button id="${pickerId}-searchButton" name="-">&nbsp;</button></span>
	            </div>
	        </div>
	    </#if>

        <div><strong>${msg("logicecm.base.elements-for-select")}</strong></div>
        <div class="yui-g">
            <#if !plane>
                <div id="${pickerId}-treeSelector" class="yui-u first panel-left tree">
                    <div id="${pickerId}-groups" class="picker-items ygtv-highlight picker-groups">
                        <#nested>
                    </div>
                </div>
            </#if>
            <div id="${pickerId}-dataTable" class="<#if !plane>yui-u panel-right<#else>width100</#if>">
                <div id="${pickerId}-group-members" class="picker-items group-members"></div>
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