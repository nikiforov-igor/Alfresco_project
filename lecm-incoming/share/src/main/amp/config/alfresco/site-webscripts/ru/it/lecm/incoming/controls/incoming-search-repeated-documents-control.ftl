<#include "/org/alfresco/components/component.head.inc">
<@script type="text/javascript" src="${url.context}/res/scripts/lecm-base/components/lecm-association-search.js"></@script>

<#assign controlId = fieldHtmlId + "-cntrl">

<#assign disabled = form.mode == "view" || (field.disabled && !(field.control.params.forceEditable?? && field.control.params.forceEditable == "true"))>

<#--<div class="form-field">-->
<#--<#if disabled>-->
	<#--<div id="${controlId}" class="viewmode-field">-->
		<#--<#if (field.endpointMandatory!false || field.mandatory!false) && field.value == "">-->
		<#--<span class="incomplete-warning"><img src="${url.context}/res/components/form/images/warning-16.png" title="${msg("form.field.incomplete")}" /><span>-->
		<#--</#if>-->
		<#--<span class="viewmode-label">${field.label?html}:</span>-->
		<#--<span id="${controlId}-currentValueDisplay" class="viewmode-value"></span>-->
	<#--</div>-->
<#--<#else>-->
	<#--<label for="${controlId}">${field.label?html}:<#if field.endpointMandatory!false || field.mandatory!false><span class="mandatory-indicator">${msg("form.required.fields.marker")}</span></#if></label>-->
	<#--<div id="${controlId}" class="object-finder">-->

		<#--<#if field.disabled == false>-->
			<#--<input type="hidden" id="${controlId}-added" name="${field.name}_added"/>-->
			<#--<input type="hidden" id="${controlId}-removed" name="${field.name}_removed"/>-->
			<#--<input type="hidden" id="${controlId}-selectedItems"/>-->

			<#--<input type="checkbox" id="${controlId}-search-similar">-->
			<#--<label for="${controlId}-search-similar" class="checkbox">${msg("label.search.similar")}</label>-->
			<#--<@renderSearchPickerHTML controlId/>-->
		<#--</#if>-->

		<#--<div id="${controlId}-currentValueDisplay" class="current-values"></div>-->

		<#--<div class="clear"></div>-->
	<#--</div>-->
<#--</#if>-->
	<#--<input type="hidden" id="${fieldHtmlId}" name="${field.name}" value="${field.value?html}" />-->
<#--</div>-->

<#macro renderSearchPickerHTML controlId>
	<#assign pickerId = controlId + "-picker">

<div id="${pickerId}" class="object-finder" xmlns="http://www.w3.org/1999/html">
    <div id="${pickerId}-body" class="bd">
		<div id="${pickerId}-dataTable">
			<div id="${pickerId}-group-members"  class="picker-items"></div>
		</div>
    </div>
</div>
</#macro>

<#macro renderExtendedCheckboxItem formArgument attributeName>
<div id="${optionsControlId}-${attributeName}">
	<div class="label-div">
		<div class="clear"></div>
	</div>
	<div class="value-div">
		<input type="checkbox" name="${attributeName}" id="${optionsControlId}-attributes-match-${attributeName}"/>
		<label class="checkbox-label" for="${optionsControlId}-attributes-match-${attributeName}">${msg("label.incoming.search_repeats_options.attributes_match." + attributeName)}:</label>
		<div class="match-value-div">
			<#if formArgument?? && formArgument != "">
				<span id="${optionsControlId}-attributes-match-${attributeName}-value">${formArgument}</span>
			<#else>
				<span id="${optionsControlId}-attributes-match-${attributeName}-value">${msg("form.control.novalue")}</span>
			</#if>
		</div>
	</div>
	<div class="clear"></div>
</div>
</#macro>

<#if disabled>
	<div id="${controlId}" class="control incoming-search-repeated viewmode">
		<div class="label-div">
			<#if (field.endpointMandatory!false || field.mandatory!false) && field.value == "">
			<span class="incomplete-warning"><img src="${url.context}/res/components/form/images/warning-16.png" title="${msg("form.field.incomplete")}" /><span>
			</#if>
			<label>${field.label?html}:</label>
		</div>
		<div class="container">
			<div class="value-div">
				<input type="hidden" id="${fieldHtmlId}" name="${field.name}" value="${field.value?html}" />
				<span id="${controlId}-currentValueDisplay" class="mandatory-highlightable"></span>
			</div>
		</div>
	</div>
<#else>
	<div class="control incoming-search-repeated editmode">
		<div class="label-div">
            <div class="clear"></div>
		</div>

		<#assign optionsControlId = controlId + "_search-repeats-options">

        <div id="${optionsControlId}" class="search-repeats-options">
            <div id="${optionsControlId}-switch" class="switch">
                <a id="${optionsControlId}-switch-link" class="switch-link">${msg("label.incoming.search_repeats_options.switch-link.show")}</a>
            </div>
            <div class="set">
				<div id="${optionsControlId}-search-attributes" class="search-attributes hidden">
					<div id="${optionsControlId}-contains-in-the-title" class="control">
						<div class="container">
							<div class="label-div">
								<label for="${controlId}-picker-searchText">${msg("label.incoming.search_repeats_options.contains_in_the_title")}:</label>
							</div>
							<div class="value-div">
								<input type="text" name="contains-in-the-title" id="${controlId}-picker-searchText" value="" maxlength="256"/>
							</div>
						</div>
					</div>
					<div id="${optionsControlId}-attributes-match" class="attributes-match control">
						<div class="container">
							<div id="${optionsControlId}-attributes-match-control">
								<div class="attributes-match-label label-div">
									<label for="${optionsControlId}-attributes-match">${msg("label.incoming.search_repeats_options.attributes_match")}:</label>
								</div>
								<div class="value-div">
									<input type="checkbox" name="select-all" id="${optionsControlId}-attributes-match-select-all"/>
									<label class="checkbox-label" for="${optionsControlId}-attributes-match-select-all">${msg("label.incoming.search_repeats_options.attributes_match.select_all")}</label>
								</div>
                                <div class="clear"></div>
                            </div>

							<#if form.arguments["lecm-incoming:outgoing-date"]?? && form.arguments["lecm-incoming:outgoing-date"] != "">
								<#assign outgoingDate = form.arguments["lecm-incoming:outgoing-date"]?datetime("yyyy-MM-dd'T'HH:mm:ss")>
								<#assign formatedOutgoingDate = outgoingDate?string["dd.MM.yyyy"]>
							<#else>
								<#assign formatedOutgoingDate = "">
							</#if>

							<#assign subjects = form.arguments["lecm-document:subject-assoc-text-content"]>
							<#assign formatedSubjects = subjects?replace(";", ", ")>

                            <@renderExtendedCheckboxItem form.arguments["lecm-incoming:sender-assoc-text-content"] "sender"/>
							<@renderExtendedCheckboxItem form.arguments["lecm-incoming:addressee-assoc-text-content"] "addressee"/>
							<@renderExtendedCheckboxItem form.arguments["lecm-document:title"] "title"/>
							<@renderExtendedCheckboxItem form.arguments["lecm-incoming:outgoing-number"] "outgoing_number"/>
							<@renderExtendedCheckboxItem formatedOutgoingDate "outgoing_date"/>
							<@renderExtendedCheckboxItem formatedSubjects "subject"/>

						</div>
                        <div class="clear"></div>
					</div>

					<div id="${optionsControlId}-search-mode-control">
						<div class="container">
							<div class="label-div">
								<label for="${optionsControlId}-search-mode">${msg("label.incoming.search_repeats_options.search_mode")}:</label>
							</div>
							<div class="value-div">
								<select id="${optionsControlId}-search-mode">
									<option selected value="at_least_one">${msg("label.incoming.search_repeats_options.search_mode.at_least_one_attribute_matches")}</option>
									<option value="all">${msg("label.incoming.search_repeats_options.search_mode.all_attributes_match")}</option>
								</select>
							</div>
                            <div class="clear"></div>
						</div>
					</div>

                    <div id="${optionsControlId}-buttons" class="search-repeats-options buttons">
						<div class="buttons-div">
							<span class="search-button">
								<button id="${controlId}-picker-searchButton">${msg("label.incoming.search_repeats_options.buttons.search-button")}</button>
							</span>
							<span class="clear-button">
								<button id="${optionsControlId}-clearButton">${msg("label.incoming.search_repeats_options.buttons.clear-button")}</button>
							</span>
						</div>
                        <div class="clear"></div>
					</div>
                </div>
            </div>
        </div>
        <div class="clear"></div>

		<div class="label-div search-similar-label">
			<label for="${controlId}">
			${field.label?html}:
				<#if field.endpointMandatory!false || field.mandatory!false>
                    <span class="mandatory-indicator">${msg("form.required.fields.marker")}</span>
				</#if>
			</label>
		</div>
		<div id="${controlId}" class="container">
			<#if field.disabled == false>
				<input type="hidden" id="${controlId}-added" name="${field.name}_added"/>
				<input type="hidden" id="${controlId}-removed" name="${field.name}_removed"/>
				<input type="hidden" id="${controlId}-selectedItems"/>

				<@renderSearchPickerHTML controlId/>
			</#if>

			<div class="value-div">
				<input type="hidden" id="${fieldHtmlId}" name="${field.name}" value="${field.value?html}" />
				<div id="${controlId}-currentValueDisplay" class="control-selected-values mandatory-highlightable"></div>
			</div>
		</div>
	</div>
</#if>
<div class="clear"></div>

<script type="text/javascript">
	<#if field.control.params.selectedValueContextProperty??>
		<#if context.properties[field.control.params.selectedValueContextProperty]??>
			<#assign renderPickerJSSelectedValue = context.properties[field.control.params.selectedValueContextProperty]>
		<#elseif args[field.control.params.selectedValueContextProperty]??>
			<#assign renderPickerJSSelectedValue = args[field.control.params.selectedValueContextProperty]>
		<#elseif context.properties[field.control.params.selectedValueContextProperty]??>
			<#assign renderPickerJSSelectedValue = context.properties[field.control.params.selectedValueContextProperty]>
		</#if>
	</#if>
	<#assign optionSeparator="|">
	<#assign labelSeparator=":">

	function init() {
        LogicECM.module.Base.Util.loadScripts([
	        'scripts/lecm-base/components/lecm-association-search.js',
            'scripts/lecm-incoming/incoming-search-repeated-documents.js'
	    ], process);
        LogicECM.module.Base.Util.loadCSS([
            'css/lecm-incoming/controls/incoming-search-repeated-document-control.css'
        ]);
	}

	function process() {
		new LogicECM.module.Incoming.SearchRepeatedDocuments("${fieldHtmlId}").setOptions({
			<#if disabled>
				disabled: true,
			</#if>
			<#if field.control.params.rootLocation??>
				rootLocation: "${field.control.params.rootLocation}",
			</#if>
			<#if field.mandatory??>
				mandatory: ${field.mandatory?string},
			<#elseif field.endpointMandatory??>
				mandatory: ${field.endpointMandatory?string},
			</#if>
			multipleSelectMode: ${field.endpointMany?string},

			<#if field.control.params.nameSubstituteString??>
				nameSubstituteString: "${field.control.params.nameSubstituteString}",
			</#if>
			<#if field.control.params.selectedItemsNameSubstituteString??>
				selectedItemsNameSubstituteString: "${field.control.params.selectedItemsNameSubstituteString}",
			</#if>
			<#if field.control.params.childrenDataSource??>
				childrenDataSource: "${field.control.params.childrenDataSource}",
			</#if>
			<#if field.control.params.changeItemsFireAction??>
				changeItemsFireAction: "${field.control.params.changeItemsFireAction}",
			</#if>
			<#if args.ignoreNodes??>
				ignoreNodes: "${args.ignoreNodes}".split(","),
			</#if>
			currentValue: "${field.value!''}",
			<#if renderPickerJSSelectedValue??>
				selectedValue: "${renderPickerJSSelectedValue}",
			</#if>
			<#if field.control.params.fireAction?? && field.control.params.fireAction != "">
				fireAction: {
					<#list field.control.params.fireAction?split(optionSeparator) as typeValue>
						<#if typeValue?index_of(labelSeparator) != -1>
							<#assign type=typeValue?split(labelSeparator)>
							<#if type[0] == "addItem">
								addItem: "${type[1]}",
							</#if>
							<#if type[0] == "cancel">
								cancel: "${type[1]}",
							</#if>
						</#if>
					</#list>
				},
			</#if>
			showSelectedItems: true,
			documentRef: "${form.arguments.documentNodeRef!""}",
			itemType: "${field.endpointType}"
		}).setMessages( ${messages} );
	}
	YAHOO.util.Event.onDOMReady(init);
</script>