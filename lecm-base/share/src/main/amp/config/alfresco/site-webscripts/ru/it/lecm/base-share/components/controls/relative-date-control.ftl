<#include "/org/alfresco/components/form/controls/common/utils.inc.ftl" />
<#assign id=args.htmlid/>
<#assign defaultSettings = "{\"type\":\"RELATIVE_DATE\", \"daysMode\":\"WORK\", \"mode\":\"RELATIVE\", \"days\":0, \"date\":\"\"}">
<#if (field.control.params.defaultSettings)?has_content>
    <#assign defaultSettings = field.control.params.defaultSettings>
</#if>

<#assign defaultValue=field.value>
<#if form.mode == "create">
    <#if (form.arguments[field.name])?has_content>
        <#assign defaultValue=form.arguments[field.name]>
    <#elseif (field.control.params.defaultValue)?has_content>
        <#assign defaultValue=field.control.params.defaultValue>
    <#else>
        <#assign defaultValue = defaultSettings>
    </#if>
</#if>

<#assign defaultConfig=defaultValue?eval>

<div class="relative-date-set">
    <input type="hidden" id="${fieldHtmlId}" name="${field.name}" value="${defaultValue?html}"/>
    <div class="relative-date-set-radio">
        <#assign optionSeparator=",">
        <#assign labelSeparator="|">
        <#assign radioControlId= fieldHtmlId + "-radio">

        <div id="${radioControlId}-parent" class="control selectone-radiobuttons editmode">
            <div class="label-div">
                <label for="${radioControlId}">${field.label?html}:
                <#if field.mandatory>
                    <span class="mandatory-indicator">${msg("form.required.fields.marker")}</span>
                </#if>
                </label>
            </div>
            <div class="container">
                <div class="value-div">
                <#if field.control.params.radioOptions?? && field.control.params.radioOptions != "">
                    <input id="${radioControlId}" type="hidden" name="-"/>
                    <#list field.control.params.radioOptions?split(optionSeparator) as nameValue>
                        <#if nameValue?index_of(labelSeparator) == -1>
                                <input type="radio" name="${field.name}-radio" value="${nameValue?html}" <#if nameValue == defaultConfig.mode?string>checked</#if>/>
                                <label class="checkbox">${nameValue?html}</label>
                        <#else>
                            <#assign choice=nameValue?split(labelSeparator)>
                                <input type="radio" name="${field.name}-radio" value="${choice[0]?html}" <#if choice[0] == defaultConfig.mode?string>checked</#if>/>
                                <label class="checkbox">${msgValue(choice[1])?html}</label>
                        </#if>
                    </#list>
                <#else>
                    <div id="${radioControlId}" class="missing-options">${msg("form.control.selectone.missing-options")}</div>
                </#if>
                </div>
            </div>
            <div class="clear"></div>
        </div>
    </div>

    <div class="relative-date-set-days">
        <#assign daysControlId = fieldHtmlId + "-days">
        <div id="${daysControlId}-parent" class="control textfield editmode">
            <div class="container">
                <div class="value-div">
                    <input id="${daysControlId}" name="-" type="text" value="${defaultConfig.days}"/>
                </div>
            </div>
        </div>
        <div class="clear"></div>
    </div>

    <div class="relative-date-set-type">
        <#assign daysModeControlId = fieldHtmlId + "-daysMode">
        <div id="${daysModeControlId}-parent" class="control selectone editmode">
            <div class="container">
                <div class="value-div">
                <#if field.control.params.modeOptions?? && field.control.params.modeOptions != "">
                    <select id="${daysModeControlId}" name="-">
                        <#list field.control.params.modeOptions?split(optionSeparator) as nameValue>
                            <#if nameValue?index_of(labelSeparator) == -1>
                                <option value="${nameValue?html}" <#if nameValue == defaultConfig.daysMode?string> selected="selected"</#if>>${nameValue?html}</option>
                            <#else>
                                <#assign choice=nameValue?split(labelSeparator)>
                                <option value="${choice[0]?html}" <#if choice[0] == defaultConfig.daysMode?string> selected="selected"</#if>>${msgValue(choice[1])?html}</option>
                            </#if>
                        </#list>
                    </select>
                <#else>
                    <div id="${daysModeControlId}" class="missing-options">${msg("form.control.selectone.missing-options")}</div>
                </#if>
                </div>
            </div>
        </div>
        <div class="clear"></div>
    </div>

    <#if field.control.params.showTime?? && field.control.params.showTime == "true">
        <#assign showTime=true>
    <#else>
        <#assign showTime=false>
    </#if>
    <div class="relative-date-set-date<#if showTime>-with-time</#if>">
        <#assign formId = args.htmlid?js_string + "-form">
        <#assign containerId = formId + "-container_c">
        <#assign dateControlId = fieldHtmlId + "-date">
        <#assign currentValue = defaultConfig.date?js_string>
        <#if !(currentValue)?has_content>
            <#if (field.control.params.defaultTime)?has_content>
                <#assign currentValue = .now?string("yyyy-MM-dd'T'" + field.control.params.defaultTime + ":00.000")>
            <#else>
                <#assign currentValue = .now?string("yyyy-MM-dd")>
            </#if>
        </#if>
        <#assign minLimit = ""/>
        <#if field.control.params.minLimitCurrentDate?? && field.control.params.minLimitCurrentDate == "true">
            <#if showTime>
                <#assign minLimit = .now?string("yyyy-MM-dd'T'HH:mm:00.000")/>
            <#else>
                <#assign minLimit = .now?string("yyyy-MM-dd")/>
            </#if>
        </#if>
        <div id="${dateControlId}-parent" class="control date editmode">
            <div class="container">
                <div class="buttons-div">
                    <a id="${dateControlId}-icon" class="calendar-icon"></a>
                </div>
                <div id="${dateControlId}" class="datepicker"></div>
                <div class="value-div">
                    <div class="date-entry-container">
                        <input id="${dateControlId}-value" type="hidden" name="${field.name}" value="${currentValue?html}"/>
                        <input id="${dateControlId}-date" name="-" type="text" class="date-entry mandatory-highlightable"/>
                    </div>
                    <#if showTime>
                        <div id="${dateControlId}-time-container" class="time-entry-container">
                            <input id="${dateControlId}-time" name="-" type="text" class="time-entry"/>
                            <div id="${dateControlId}-time-format">
                                <span class="time-format">${msg("form.control.date-picker.display.time.format")}</span>
                            </div>
                        </div>
                    </#if>
                </div>
            </div>
        </div>
        <div class="clear"></div>
    </div>
    <div class="clear"></div>
</div>

<script type="text/javascript">//<![CDATA[
(function () {
    LogicECM.module.Base.Util.loadResources([
        'scripts/lecm-base/components/controls/relative-date-control.js',
        'scripts/lecm-base/components/lecm-date-picker.js'
    ], [
        'css/lecm-base/components/lecm-date-picker.css',
        'css/lecm-base/components/controls/relative-date-control.css'
    ], processController, ["button", "calendar"]);

    function processController() {
        var controller = new LogicECM.module.RelativeDateController("${fieldHtmlId}").setOptions({
        <#if field.control.params.fireChangeEventName??>
            fireChangeEventName: '${field.control.params.fireChangeEventName}',
        </#if>
            fieldName: "${field.name}",
            fieldId: "${field.configName}",
            formId: "${args.htmlid}"
        });
        controller.setValue(eval('(${defaultValue})'));

        var datePicker = new LogicECM.DatePicker("${dateControlId}", "${dateControlId}-value").setOptions(
                {
                    currentValue: "${currentValue}",
                    showTime: ${showTime?string},
                    mandatory: false,
                    fieldId: "${field.configName}",
                    formId: "${args.htmlid}",
                    minLimit: "${minLimit?string}",
                    changeFireAction: "dateSelected"
                }).setMessages(${messages});

        <#if showTime>

            var zIndex = $('#${containerId}').zIndex(),
                    parentNode = $('#${dateControlId}-parent'),
                    fieldNode = $('#${dateControlId}-time');
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
                onSelect: function () {
                    YAHOO.Bubbling.fire("handleFieldChange", {
                        fieldId: "${field.configName}",
                        formId: "${args.htmlid}"
                    });
                    YAHOO.Bubbling.fire('mandatoryControlValueUpdated', this);
                },
                onBeforeClose: function () {
                    YAHOO.Bubbling.fire("handleFieldChange", {
                        fieldId: "${field.configName}",
                        formId: "${args.htmlid}"
                    });
                    YAHOO.Bubbling.fire('mandatoryControlValueUpdated', this);
                }
            });
        </#if>

        datePicker.draw();
        controller.datePicker = datePicker;
    }
})();
//]]></script>
