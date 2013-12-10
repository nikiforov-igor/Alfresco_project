[
<#list classdefs as classdef>
{
    <#if classdef.name??>"name": "${classdef.name.toPrefixString()}",</#if>
    "title": "${classdef.title!""}"
}
<#if classdef_has_next>,</#if>
</#list>
]