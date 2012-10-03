<#assign id = args.htmlid>
<script type="text/javascript">//<![CDATA[
new Alfresco.component.DataListToolbar("${id}").setOptions(
		{
			siteId: "site"
		}).setMessages(${messages});
//]]></script>
<div id="${args.htmlid}-body" class="datalist-toolbar toolbar">
	<div id="${args.htmlid}-headerBar" class="header-bar flat-button theme-bg-2">
		<div class="left">
			<div class="new-row">
            <span id="${id}-newRowButton" class="yui-button yui-push-button">
               <span class="first-child">
                  <button type="button">${msg('button.new-row')}</button>
               </span>
            </span>
			</div>
			<div class="selected-items">
				<button class="no-access-check" id="${args.htmlid}-selectedItems-button" name="doclist-selectedItems-button">${msg("menu.selected-items")}</button>
				<div id="${args.htmlid}-selectedItems-menu" class="yuimenu">
					<div class="bd">
						<ul>
						<#list actionSet as action>
							<li><a type="${action.asset!""}" rel="${action.permission!""}" href="${action.href}"><span class="${action.id}">${msg(action.label)}</span></a></li>
						</#list>
							<li><a href="#"><hr /></a></li>
							<li><a href="#"><span class="onActionDeselectAll">${msg("menu.selected-items.deselect-all")}</span></a></li>
						</ul>
					</div>
				</div>
			</div>
		</div>

		<div class="right" style="display: none;">
         <span id="${id}-printButton" class="yui-button yui-push-button print">
             <span class="first-child">
                 <button type="button">${msg('button.print')}</button>
             </span>
         </span>
         <span id="${id}-rssFeedButton" class="yui-button yui-push-button rss-feed">
             <span class="first-child">
                 <button type="button">${msg('button.rss-feed')}</button>
             </span>
         </span>
		</div>
	</div>
</div>

<#--item: workspace://SpacesStore/55db65fe-49e7-4fdb-b2b8-4a7729e16fb9-->
<#--"/share/service/components/form?itemKind=type&itemId=lecm-dic:dictionary_values&destination=workspace://SpacesStore/55db65fe-49e7-4fdb-b2b8-4a7729e16fb9&mode=create&submitType=json&formId=dictionary-node-form&showCancelButton=true"-->

<#--dict: workspace://SpacesStore/5c2a8105-5d4b-4992-a110-6c3081484353-->
<#--"/share/service/components/form?itemKind=type&itemId=lecm-dic:dictionary_values&destination=workspace://SpacesStore/5c2a8105-5d4b-4992-a110-6c3081484353&mode=create&submitType=json&formId=dictionary-node-form&showCancelButton=true"-->

<#--"/share/service/components/form?itemKind=type&itemId=lecm-dic:dictionary_values&destination=workspace://SpacesStore/55db65fe-49e7-4fdb-b2b8-4a7729e16fb9&mode=create&submitType=json&showCancelButton=true"-->