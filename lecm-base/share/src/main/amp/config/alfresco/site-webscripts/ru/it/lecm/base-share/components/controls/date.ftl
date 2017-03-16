<#assign formId = args.htmlid?js_string + "-form">
<#assign containerId = formId + "-container_c">

<#if field.control.params.showTime?? && field.control.params.showTime == "true"><#assign showTime=true><#else><#assign showTime=false></#if>
<#if showTime><#assign viewFormat>${msg("form.control.date-picker.view.time.format")}</#assign><#else><#assign viewFormat>${msg("form.control.date-picker.view.date.format")}</#assign></#if>

<#assign disabled=field.disabled>
<#if field.control.params.forceEditable?? && field.control.params.forceEditable == "true">
    <#assign disabled=false>
</#if>

<#assign hideDateFormat=true>
<#if field.control.params.hideDateFormat??>
    <#assign hideDateFormat=field.control.params.hideDateFormat == "true">
</#if>

<#assign readonly = false>
<#assign defaultValue=field.value>
<#if form.mode == "create">
    <#if form.arguments[field.name]?has_content>
        <#assign defaultValue=form.arguments[field.name]>
	<#elseif form.arguments['readonly_' + field.name]?has_content>
		<#assign defaultValue=form.arguments['readonly_' + field.name]>
		<#assign readonly = true>
    </#if>
</#if>

<#if defaultValue?string == "" && field.control.params.selectedItemsFormArgs??>
    <#assign selectedItemsFormArgs = field.control.params.selectedItemsFormArgs?split(",")>
    <#list selectedItemsFormArgs as selectedItemsFormArg>
        <#if form.arguments[selectedItemsFormArg]??>
            <#if (defaultValue?length > 0)>
                <#assign defaultValue = defaultValue + ","/>
            </#if>
            <#assign defaultValue = defaultValue + form.arguments[selectedItemsFormArg]/>
        </#if>
    </#list>
</#if>

<#assign multiValued=false>
<#if defaultValue != "" && defaultValue?index_of(",") != -1>
    <#assign multiValued=true>
</#if>

<#assign minLimit = ""/>
<#if field.control.params.minLimitArg??>
    <#assign minLimit = form.arguments[field.control.params.minLimitArg]!"" />
<#elseif field.control.params.minLimitCurrentDate?? && field.control.params.minLimitCurrentDate == "true">
	<#if showTime>
        <#assign minLimit = .now?string("yyyy-MM-dd'T'HH:mm:00.000")/>
    <#else>
        <#assign minLimit = .now?string("yyyy-MM-dd")/>
    </#if>
</#if>

<#assign maxLimit = ""/>
<#if field.control.params.maxLimitArg??>
    <#assign maxLimit = form.arguments[field.control.params.maxLimitArg]!"" />
<#elseif field.control.params.maxLimitCurrentDate?? && field.control.params.maxLimitCurrentDate == "true">
    <#if showTime>
        <#assign maxLimit = .now?string("yyyy-MM-dd'T'HH:mm:00.000")/>
    <#else>
        <#assign maxLimit = .now?string("yyyy-MM-dd")/>
    </#if>
</#if>

<#assign controlId = fieldHtmlId + "-cntrl">

<#if form.mode == "view">
<div id="${controlId}-parent" class="control date viewmode">
    <div class="label-div">
        <#if field.mandatory && field.value == "">
        <span class="incomplete-warning"><img src="${url.context}/res/components/form/images/warning-16.png"
                                              title="${msg("form.field.incomplete")}"/><span>
        </#if>
        <label>${field.label?html}:</label>
    </div>
    <div id="${controlId}-container" class="container">
        <div class="value-div">
	        <script type="text/javascript">//<![CDATA[
	        (function () {
		        function initBoobling() {
			        LogicECM.module.Base.Util.createComponentReadyElementId("${controlId}-container", "${args.htmlid}", "${field.configName}");

			        YAHOO.Bubbling.on("hideControl", onHideControl);
			        YAHOO.Bubbling.on("showControl", onShowControl);
		        }
                function onHideControl(layer, args) {
	                if ("${args.htmlid}" == args[1].formId && "${field.configName}" == args[1].fieldId) {
		                Dom.setStyle("${controlId}-parent", "display", "none");
	                }
                }
                function onShowControl(layer, args) {
	                if ("${args.htmlid}" == args[1].formId && "${field.configName}" == args[1].fieldId) {
		                Dom.setStyle("${controlId}-parent", "display", "block");
	                }
                }

		        YAHOO.util.Event.onContentReady("${controlId}-parent", initBoobling);
				<#if readonly>
					LogicECM.module.Base.Util.readonlyControl('${args.htmlid}', '${field.configName}', true);
				</#if>
	        })();
	        //]]></script>

            <#if field.value == "">
                ${msg("form.control.novalue")}
            <#elseif !multiValued>

                <script type="text/javascript">//<![CDATA[
                (function () {
                    function init() {
                        LogicECM.module.Base.Util.loadScripts([
                            'scripts/lecm-base/components/lecm-date-display.js'
                        ], createDateDisplay);
                    }

                    function createDateDisplay() {
                        new LogicECM.DateDisplayControl("${controlId}").setOptions(
                                {
                                    currentValue: "${field.value?js_string}",
                                    showTime: ${showTime?string}
                                    <#if field.control.params.dateFormatString??>
                                        , formatDateStr: "${field.control.params.dateFormatString}"
                                    </#if>
                                }).setMessages(
                        ${messages}
                        );
                    }

                    YAHOO.util.Event.onDOMReady(init);
                })();
                //]]></script>

                <span id="${controlId}-date" <#if field.description??>title="${field.description}"</#if> />
            <#else>
                <#list field.value?split(",") as dateEl>
                ${xmldate(dateEl)?string(viewFormat)}<#if dateEl_has_next>,</#if>
                </#list>
            </#if>
        </div>
    </div>
</div>
<#elseif !multiValued>
        <#assign currentValue = defaultValue?js_string>
        <#if  !currentValue?has_content && !disabled >
             <#assign currentValue = field.control.params.defaultValue!""?js_string>
             <#if currentValue == "now">
                    <#if field.control.params.defaultTime?? >
                        <#assign currentValue = .now?string("yyyy-MM-dd'T'" + field.control.params.defaultTime + ":00.000")>
                    <#else>
                        <#assign currentValue = .now?string("yyyy-MM-dd")>
                    </#if>
             </#if>
        </#if>
        <script type="text/javascript">//<![CDATA[
        (function () {
            function init() {
                var resources = [
                    'scripts/lecm-base/components/lecm-date-picker.js'
                ]

                LogicECM.module.Base.Util.loadResources(resources, [
                    'css/lecm-base/components/lecm-date-picker.css'
                ], createDatePicker, ["button", "calendar"]);
            }

            function createDatePicker() {
                var picker = new LogicECM.DatePicker("${controlId}", "${fieldHtmlId}").setOptions(
                        {
                            <#if form.mode == "view" || disabled>disabled: true,</#if>
                            currentValue: "${currentValue}",
                            <#if field.control.params.defaultScriptURL?has_content>
                                defaultScript: '${field.control.params.defaultScriptURL?js_string}',
                                destination: "${form.destination!""}",
                                itemKind: "${form.arguments.itemKind!""}",
                            </#if>
                            <#if field.control.params.changeFireAction??>
	                            changeFireAction: "${field.control.params.changeFireAction}",
                            </#if>
                            showTime: ${showTime?string},
                            mandatory: ${field.mandatory?string},
                            minLimit: "${minLimit?string}",
                            maxLimit: "${maxLimit?string}",
	                        fieldId: "${field.configName}",
	                        formId: "${args.htmlid}"
                        }).setMessages(
                ${messages}
                );
            <#if showTime>

                var zIndex = $('#${containerId}').zIndex(),
                parentNode = $('#${controlId}-parent'),
                fieldNode = $('#${controlId}-time');
                parentNode.zIndex(zIndex+1);

                fieldNode.timepicker({
                    timeFormat: '${msg("title.timepicker.timeformat")}',
                    timeOnlyTitle: '${msg("title.timepicker.select-time")}',
                    timeText: '${msg("title.timepicker.time")}',
                    hourText: '${msg("title.timepicker.hours")}',
                    minuteText: '${msg("title.timepicker.minutes")}',
                    secondText: '${msg("title.timepicker.seconds")}',
                    currentText: '${msg("title.timepicker.current")}',
                    closeText: '${msg("title.timepicker.select")}',
                    stepMinute: 15,
                    onSelect: function (selectedDateTime) {
                        YAHOO.Bubbling.fire("handleFieldChange", {
                            fieldId: "${field.configName}",
                            formId: "${args.htmlid}"
                        });
                        YAHOO.Bubbling.fire('mandatoryControlValueUpdated', this);
                    },
                    onBeforeClose: function (selectedDateTime) {
                        YAHOO.Bubbling.fire("handleFieldChange", {
                            fieldId: "${field.configName}",
                            formId: "${args.htmlid}"
                        });
                        YAHOO.Bubbling.fire('mandatoryControlValueUpdated', this);
                    }
                });
            </#if>
                picker.draw();
				<#if readonly>
					LogicECM.module.Base.Util.readonlyControl('${args.htmlid}', '${field.configName}', true);
				</#if>
            }

            YAHOO.util.Event.onAvailable('${fieldHtmlId}', init, this, true);
        })();
        //]]></script>

        <div id="${controlId}-parent" class="control date editmode">
            <div class="label-div">
                <label for="${controlId}-date">
                    ${field.label?html}:
                    <#if field.mandatory><span class="mandatory-indicator">${msg("form.required.fields.marker")}</span></#if>
                </label>
            </div>
            <div class="container">
                <#if disabled == false>
                    <div class="buttons-div">
                        <@formLib.renderFieldHelp field=field />
                        <a id="${controlId}-icon" class="calendar-icon"></a>
                    </div>
                    <div id="${controlId}" class="datepicker"></div>
                </#if>
                <div class="value-div">
                    <div class="date-entry-container <#if !showTime>only-date</#if>">
                    <input id="${fieldHtmlId}" type="hidden" name="${field.name}" value="${defaultValue?html}"/>
                        <input id="${controlId}-date" name="-" type="text" class="date-entry mandatory-highlightable"
                               <#if field.description??>title="${field.description}"</#if> <#if disabled>disabled="true"
                               <#else>tabindex="0"</#if> />
                        <div>
                            <span class="date-format <#if hideDateFormat>hidden1</#if>">${msg("lecm.form.control.date-picker.display.date.format")}</span>
                        </div>
                    </div>

                    <#if showTime>
                        <div id="${controlId}-time-container" class="time-entry-container">
                            <input id="${controlId}-time" name="-" type="text" class="time-entry"
                                   <#if field.description??>title="${field.description}"</#if> <#if disabled>disabled="true"
                                   <#else>tabindex="0"</#if> />
                            <div id="${controlId}-time-format">
                                <span class="time-format<#if disabled>-disabled</#if>">${msg("form.control.date-picker.display.time.format")}</span>
                            </div>
                        </div>
                    </#if>
                </div>
            </div>
        </div>
</#if>
<div class="clear"></div>
