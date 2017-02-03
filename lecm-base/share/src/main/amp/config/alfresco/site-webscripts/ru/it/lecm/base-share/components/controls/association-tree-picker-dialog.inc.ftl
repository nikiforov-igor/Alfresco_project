<#macro renderTreePickerDialogHTML controlId plane showSearch>
<#assign pickerId = controlId + "-picker">
<div id="${pickerId}" class="yui-panel assoc-dialog association-tree-picker">
    <div id="${pickerId}-head" class="hd">${msg("form.control.object-picker.header")}</div>
    <div id="${pickerId}-body" class="bd">
	    <#if showSearch>
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
        </#if>

        <div>
            <strong>${msg("logicecm.base.elements-for-select")}</strong>
            <div>
                <#if !plane>
                    <div id="${pickerId}-treeSelector" class="yui-u panel-left tree">
                        <div id="${pickerId}-groups" class="picker-items tree-items ygtv-highlight">
                            <#nested>
                        </div>
                    </div>
                </#if>
                <div id="${pickerId}-dataTable" class="<#if !plane>yui-u panel-right</#if>">
                    <div id="${pickerId}-picker-items" class="picker-items">
	                    <div id="${pickerId}-group-members"></div>
	                    <div id="${pickerId}-picker-items-loading" class="loading-image-container">
		                    <img src="${url.context}/res/components/images/lightbox/loading.gif">
	                    </div>
                    </div>
                </div>
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