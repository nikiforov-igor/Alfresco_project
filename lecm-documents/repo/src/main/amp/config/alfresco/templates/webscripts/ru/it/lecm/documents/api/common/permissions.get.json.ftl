<#escape x as jsonUtils.encodeJSONString(x)!''>
{
    "readAccess":"${hasReadAccess?string}",
    "writeAccess": "${hasWriteAccess?string}"
}
</#escape>