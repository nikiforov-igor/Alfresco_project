<#assign id = args.htmlid>
<script type="text/javascript">//<![CDATA[
function init() {
	new LogicECM.module.OrgStructure.Toolbar("${id}").setMessages(${messages}).setOptions({
		bubblingLabel:"${bubblingLabel!''}"
	});
}

YAHOO.util.Event.onDOMReady(init);
//]]></script>
<div id="${args.htmlid}-body" class="datalist-toolbar toolbar">
    <div id="${args.htmlid}-headerBar" class="header-bar flat-button">
        <div class="left">
        <#if showNewUnitBtn>
	        <div class="new-row">
            <span id="${id}-newUnitButton" class="yui-button yui-push-button">
               <span class="first-child">
                  <button type="button">${msg('button.new-unit')}</button>
               </span>
            </span>
	        </div>
        <#else>
	        <div class="new-row">
            <span id="${id}-newRowButton" class="yui-button yui-push-button">
               <span class="first-child">
                  <button type="button">${msg('button.new-row')}</button>
               </span>
            </span>
	        </div>
        </#if>
        </div>

        <div class="right">
            <span id="${id}-searchInput" class="search-input">
				<input type="text" id="full-text-search" value="">
			</span>
            <span id="${id}-searchButton" class="search yui-button yui-push-button">
                <span class="first-child">
                    <button type="button" id ="searchBtn" title="${msg('button.search')}"></button>
                </span>
            </span>
            <span id="${id}-extendSearchButton" class="ex-search yui-button yui-push-button">
                <span class="first-child">
                    <button type="button" id="exsearchBtn" title="${msg('button.ex_search')}"></button>
                </span>
            </span>
        </div>
    </div>
</div>