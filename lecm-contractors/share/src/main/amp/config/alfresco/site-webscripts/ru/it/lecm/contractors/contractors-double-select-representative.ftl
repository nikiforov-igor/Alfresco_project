<#include "/ru/it/lecm/base-share/components/controls/association-autocomplete-control.ftl">
<#assign params = field.control.params>
<#assign doNotCheckAccess = true/>
<#if params.doNotCheckAccess?? && params.doNotCheckAccess == "false">
    <#assign doNotCheckAccess = false/>
</#if>
<script type="text/javascript">//<![CDATA[
(function () {
    LogicECM.CurrentModules = LogicECM.CurrentModules || {};

    function init() {
        LogicECM.module.Base.Util.loadScripts([
                    'scripts/contractors/double-select-representative-control.js'
                ],
                createControl);
    }

    function createControl() {
        new LogicECM.module.DoubleSelectRepresentativeForContractor("${controlId}",
                "${params.updateOnContractorSelect!"contractor.selected"}",
                "${params.updateOnOrganizationSelect!"organization.selected"}").setOptions({
                <#if field.control.params.showAssocViewForm??>
                    showAssocViewForm: ${field.control.params.showAssocViewForm?string},
                </#if>
                    disabled: ${disabled?string},
                    currentValue: "${field.value!''}",
                    defaultValue: "${fieldValue}",
                <#if params.employeesByOrgDS??>
                    employeesByOrgDS: "${params.employeesByOrgDS}",
                </#if>
                <#if params.representativesByContrDS??>
                    representativesByContrDS: "${params.representativesByContrDS}",
                </#if>
                    employeesNameSubstitute: "${params.employeesNameSubstitute!"{lecm-orgstr:employee-short-name}"}",
                    representativesSubstitute: "${params.representativesSubstitute!"{cm:name}"}",
                    nameSubstituteString: "${params.representativesSubstitute!"{lecm-representative:surname}{lecm-orgstr:employee-last-name} {lecm-representative:firstname}{lecm-orgstr:employee-first-name} {lecm-representative:middlename}{lecm-orgstr:employee-middle-name} {..lecm-orgstr:employee-link-employee-assoc(lecm-orgstr:employee-link-is-primary = true)/../lecm-orgstr:element-member-position-assoc/cm:name} {..lecm-orgstr:employee-link-employee-assoc(lecm-orgstr:employee-link-is-primary = true)/../../lecm-orgstr:element-short-name}"}",
                    selectedItemsNameSubstituteString: "${params.representativesSubstitute!"{lecm-representative:surname}{lecm-orgstr:employee-last-name} {lecm-representative:firstname}{lecm-orgstr:employee-first-name} {lecm-representative:middlename}{lecm-orgstr:employee-middle-name} {..lecm-orgstr:employee-link-employee-assoc(lecm-orgstr:employee-link-is-primary = true)/../lecm-orgstr:element-member-position-assoc/cm:name} {..lecm-orgstr:employee-link-employee-assoc(lecm-orgstr:employee-link-is-primary = true)/../../lecm-orgstr:element-short-name}"}",
                <#if params.employeesLocation??>
                    employeesLocation: "${params.employeesLocation}",
                </#if>
                <#if params.representativesLocation??>
                    representativesLocation: "${params.representativesLocation}",
                </#if>
                <#if params.employeesDefaultValueDS?? && params.employeesDefaultValueDS != "">
                    employeesDefaultValueDS: "${params.employeesDefaultValueDS}",
                </#if>
                <#if params.autoCompleteJsName??>
                    autoCompleteJsName: "${params.autoCompleteJsName}",
                </#if>
                <#if params.treeViewJsName??>
                    treeViewJsName: "${params.treeViewJsName}",
                </#if>
                <#if params.representativesDefaultValueDS?? && params.representativesDefaultValueDS != "">
                    representativesDefaultValueDS: "${params.representativesDefaultValueDS}",
                </#if>
                <#if params.defaultValueUseOnce??>
                    defaultValueUseOnce: ${params.defaultValueUseOnce?string},
                </#if>
                    employeesType: "${params.employeesType!"lecm-orgstr:employee"}",
                    representativesType: "${params.representativesType!"lecm-representative:representative-type"}",
					fieldHtmlId: "${fieldHtmlId}",
                    doNotCheckAccess: ${doNotCheckAccess?string}
                });
    }

    YAHOO.util.Event.onDOMReady(init);
})();
//]]></script>
