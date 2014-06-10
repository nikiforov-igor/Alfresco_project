<#if msg?? && msg?length gt 0>
    "${msg}"
<#else>
    <#escape x as x?js_string>
    {
        "rating": "${rating!0}",
        "ratedPersonsCount": "${ratedPersonsCount!0}",
        "myRating": "${myRating!0}"
    }
    </#escape>
</#if>
