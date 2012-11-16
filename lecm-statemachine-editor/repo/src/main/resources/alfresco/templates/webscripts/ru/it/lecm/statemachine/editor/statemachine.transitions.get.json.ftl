<#escape x as x?js_string>
[
    <#list  transitions as transition>
    {
        expression: "${transition.expression}",
        transition: "${transition.transition}"
    }
        <#if transition_has_next>,</#if>
    </#list>
]
</#escape>