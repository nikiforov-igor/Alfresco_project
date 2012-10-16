<#escape x as x?js_string>
<br />
<table border="0" cellpadding="5" cellspacing="0">
    <tr>
        <td>Название:</td>
        <td>Статус:</td>
        <td>Действие:</td>
    </tr>
    <#list documents as document>
        <tr>
            <td>${document.name}</td>
            <td>${document.status}</td>
            <td></td>
        </tr>
    </#list>
</table>
</#escape>