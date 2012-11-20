<#assign id = args.htmlid>
<script type="text/javascript">//<![CDATA[
function init() {
	new LogicECM.module.OrgStructure.Toolbar("${id}").setMessages(${messages});
}

YAHOO.util.Event.onDOMReady(init);
//]]></script>
<div id="${id}-searchBar" class="datalist-toolbar toolbar" style="visibility: visible;">
	<div id="${id}-searchHeaderBar" class="header-bar flat-button theme-bg-2">
		<div class="left">
			<span id="${id}-searchInput" class="search-input">
				<input type="text" id="full-text-search" value="">
			</span>
			<div class="search">
				<span id="${id}-searchButton" class="yui-button yui-push-button">
					<span class="first-child">
						<button type="button" id ="searchBtn" title="${msg('button.search')}"></button>
					</span>
				</span>
			</div>
			<div class="ex-search">
				<span id="${id}-extendSearchButton" class="yui-button yui-push-button">
					<span class="first-child">
						<button type="button" id="exsearchBtn" title="${msg('button.ex_search')}"></button>
					</span>
				</span>
			</div>
		</div>
	</div>
</div>
<div id="${args.htmlid}-body" class="datalist-toolbar toolbar">
    <div id="${args.htmlid}-headerBar" class="header-bar flat-button theme-bg-2">
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
            <div class="selected-items">
                <button class="no-access-check" id="${args.htmlid}-selectedItems-button" name="doclist-selectedItems-button">${msg("menu.selected-items")}</button>
                <div id="${args.htmlid}-selectedItems-menu" class="yuimenu">
                    <div class="bd">
                        <ul>
                        <#list actionSet as action>
                            <li><a type="${action.asset!""}" rel="${action.permission!""}" href="${action.href}"><span class="${action.id}">${msg(action.label)}</span></a></li>
                        </#list>
                            <li><a href="#"><hr/></a></li>
                            <li><a href="#"><span class="onActionDeselectAll">${msg("menu.selected-items.deselect-all")}</span></a></li>
                        </ul>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>