<#macro renderOrgchartPickerDialogHTML controlId>
<#assign pickerId = controlId + "-picker">

<div id="${pickerId}" class="picker yui-panel">

    <style type="text/css" media="screen">
        #${pickerId}-searchContainer { padding-left: 6px; text-align: left; }
        #${pickerId}-view-selector { margin-left: 1%; }
        #${pickerId}-dataTable { margin-left: 1% }
        #${pickerId}-userDetails { margin-left: 1% }
        #${pickerId}-group-members thead { display: none; }
        #${pickerId}-group-members table { border: none; width: 100% }
        #${pickerId}-group-members td { border-right: none; }
        #${pickerId}-selection { padding-left: 6px; border-top: 1px solid #CBCBCB; }
        #${pickerId}-groups td { text-align: left; }
        #${pickerId}-searchText { padding: 0.3em 1em 0.4em 0.5em; }
        #${pickerId}-view-selector { padding-top: 0.7em; }
        #${pickerId}-view-roles, #${pickerId}-view-people { padding-left: 1em; }
    </style>

    <div id="${pickerId}-head" class="hd">${msg("form.control.object-picker.header")}</div>

    <div id="${pickerId}-body" class="bd">

        <div class="yui-gb orgchart-picker-menu">
            <div id="${pickerId}-searchContainer" class="yui-u first yui-skin-sam search">
                <input type="text" class="search-input" name="-" id="${pickerId}-searchText" value="" maxlength="256" />
                <span class="search-button"><button id="${pickerId}-searchButton" name="-">${msg("form.control.object-picker.search")}</button></span>
            </div>
            <div class="yui-u yui-skin-sam">
            </div>
        </div>

        <div class="yui-g">
            <div id="${pickerId}-treeSelector" class="yui-u first panel-left">
                <div id="${pickerId}-groups" class="picker-items ygtv-highlight">
                    <#nested>
                </div>
            </div>
            <div id="${pickerId}-dataTable" class="yui-u panel-right">
                <div id="${pickerId}-group-members" class="picker-items"></div>
            </div>
        </div>

        <div id="${pickerId}-selection">
            <div><strong>${msg("alvex.orgchart.selected_users")}</strong></div>
            <div id="${pickerId}-selected-users"></div>
        </div>

        <div class="bdft">
            <button id="${controlId}-ok" tabindex="0">${msg("button.ok")}</button>
            <button id="${controlId}-cancel" tabindex="0">${msg("button.cancel")}</button>
        </div>
    </div>
</div>

</#macro>