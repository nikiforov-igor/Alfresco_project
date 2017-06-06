<@standalone>
    <@markup id="css" >
        <#include "/org/alfresco/components/form/form.css.ftl"/>
    </@>

    <@markup id="js">
        <#include "/org/alfresco/components/form/form.js.ftl"/>
    </@>

    <@markup id="widgets">
        <@createWidgets/>
    </@>

    <@markup id="html">
        <@uniqueIdDiv>
            <#import "/org/alfresco/components/form/form.lib.ftl" as formLib />

            <#if form?exists>
                <#assign formId=args.htmlid?js_string?html + "-form">
                <#assign formUI><#if args.formUI??>${args.formUI}<#else>true</#if></#assign>

                <#if formUI == "true">
                    <@formLib.renderFormsRuntime formId=formId />
                </#if>
            <div id="test-test"/>
                <#if form.preferencesControl??>
                    <@markup id="html">
                        <@uniqueIdDiv>
                            <#--<#assign field=form.preferencesControl />
                            <#assign fieldHtmlId=args.htmlid?html + "_" + field.id?html >
                            <#include "${field.control.template}" />-->
                        </@>
                    </@>
                </#if>
                <@formLib.renderFormContainer formId=formId>
                    <#list form.structure as item>
                        <#if item.kind == "set">
                            <#if item.children?size &gt; 0>
                                <@formLib.renderSet set=item />
                            </#if>
                        <#else>
                            <@formLib.renderField field=form.fields[item.id] />
                        </#if>
                    </#list>
                </@>
            <#else>
            <div class="form-container">${msg("form.not.present")}</div>
            </#if>
        </@>
    </@>
</@>
