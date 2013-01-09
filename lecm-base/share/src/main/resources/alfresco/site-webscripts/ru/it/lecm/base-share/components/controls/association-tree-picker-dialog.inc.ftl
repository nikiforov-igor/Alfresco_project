<#macro renderTreePickerDialogHTML controlId plane showSearch>
<#assign pickerId = controlId + "-picker">

<div id="${pickerId}" class="picker yui-panel">

    <style type="text/css" media="screen">
        #${pickerId}-searchContainer {
            padding: 0;
            margin: 0;
            text-align: left;
            width: 100%;
        }
        #${pickerId}-view-selector { margin-left: 1%; }
        #${pickerId}-dataTable {
            <#if plane>
                width: 100%;
            </#if>
        }
        #${pickerId}-userDetails { margin-left: 1% }
        #${pickerId}-group-members thead { display: none; }
        #${pickerId}-group-members table { border: none; width: 100% }
        #${pickerId}-group-members td { border-right: none; }
        #${pickerId}-groups td { text-align: left; }
        #${pickerId}-searchText {
            width: 410px;
            padding: 0.3em 1em 0.4em 0.5em;
        }
        #${pickerId}-view-selector { padding-top: 0.7em; }
        #${pickerId}-view-roles, #${pickerId}-view-people { padding-left: 1em; }

        .picker .bd > div:not(.bdft) {
            margin: 0 2px;
        }
        .picker .bd > div:not(.bdft):first-of-type {
            padding-top: 10px;
        }
        .picker .bd div.yui-gb,
        .picker .bd div.yui-g {
            margin-bottom: 10px;
        }

    </style>

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
                    <div id="${pickerId}-groups" class="picker-items ygtv-highlight">
                        <#nested>
                    </div>
                </div>
            </#if>
            <div id="${pickerId}-dataTable" <#if !plane>class="yui-u panel-right"</#if>>
                <div id="${pickerId}-group-members" class="picker-items"></div>
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