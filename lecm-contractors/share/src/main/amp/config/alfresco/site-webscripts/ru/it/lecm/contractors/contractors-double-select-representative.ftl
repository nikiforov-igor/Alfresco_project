<#include "/ru/it/lecm/base-share/components/controls/association-autocomplete-control.ftl">
<#assign params = field.control.params>
<script>//<![CDATA[
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
                    disabled: ${disabled?string},
                    currentValue: "${field.value!''}",
                <#if params.employeesByOrgDS??>
                    employeesByOrgDS: "${params.employeesByOrgDS}",
                </#if>
                <#if params.representativesByContrDS??>
                    representativesByContrDS: "${params.representativesByContrDS}",
                </#if>
                    employeesNameSubstitute: "${params.employeesNameSubstitute!"{lecm-orgstr:employee-short-name}"}",
                    representativesSubstitute: "${params.representativesSubstitute!"{cm:name}"}",
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
                    employeesType: "${params.employeesType!"lecm-orgstr:employee"}",
                    representativesType: "${params.representativesType!"lecm-representative:representative-type"}"
                });
    }

    YAHOO.util.Event.onDOMReady(init);
})();
//]]></script>
