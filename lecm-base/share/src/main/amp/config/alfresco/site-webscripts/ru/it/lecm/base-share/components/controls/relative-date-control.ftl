<#include "/org/alfresco/components/form/controls/common/utils.inc.ftl" />
<#assign id=args.htmlid/>
<#assign fieldValue=field.value>

<div class="relative-date-set">
    <div class="relative-date-set-radio">
    <#assign optionSeparator=",">
    <#assign labelSeparator="|">
    <#assign radioValue="WORK">
    <#assign radioControlId= fieldHtmlId + "-radio">
    <#if form.mode == "create" && fieldValue?string == "">
        <#if field.control.params.defaultValue??>
            <#assign fieldValue=field.control.params.defaultValue>
        </#if>
        <#if form.arguments[field.name]?has_content>
            <#assign fieldValue=form.arguments[field.name]>
        </#if>
    </#if>
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
                            <input type="radio" name="${field.name}-radio" value="${nameValue?html}"/>
                            <label class="checkbox">${nameValue?html}</label>
                    <#else>
                        <#assign choice=nameValue?split(labelSeparator)>
                            <input type="radio" name="${field.name}-radio" value="${choice[0]?html}"/>
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
                    <input id="${daysControlId}" name="-" type="text"/>
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
                                <option value="${nameValue?html}">${nameValue?html}</option>
                            <#else>
                                <#assign choice=nameValue?split(labelSeparator)>
                                <option value="${choice[0]?html}">${msgValue(choice[1])?html}</option>
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

    <div class="relative-date-set-date">
        <#assign dateControlId = fieldHtmlId + "-date">
        <#assign currentValue = .now?string("yyyy-MM-dd")>
        <div id="${dateControlId}-parent" class="control date editmode">
            <div class="container">
                <div class="buttons-div">
                    <a id="${dateControlId}-icon" class="calendar-icon"></a>
                </div>
                <div id="${dateControlId}" class="datepicker"></div>
                <div class="value-div">
                    <div class="date-entry-container only-date">
                        <input id="${dateControlId}-value" type="hidden" name="${field.name}"/>
                        <#--<input id="${dateControlId}" type="hidden" name="-"/>-->
                        <input id="${dateControlId}-date" name="-" type="text" class="date-entry mandatory-highlightable"/>
                    </div>
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

        var datePicker = new LogicECM.DatePicker("${dateControlId}", "${dateControlId}-value").setOptions(
                {
                    currentValue: "${currentValue}",
                    showTime: false,
                    mandatory: false,
                    fieldId: "${field.configName}",
                    formId: "${args.htmlid}",
                    changeFireAction: "dateSelected"
                }).setMessages(${messages});
        datePicker.draw();
        controller.datePicker = datePicker;
    }
})();
//]]></script>
