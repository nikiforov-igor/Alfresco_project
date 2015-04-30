<@markup id="css" >
<#-- CSS Dependencies -->
    <@link rel="stylesheet" type="text/css" href="${url.context}/res/modules/taglibrary/taglibrary.css" group="calendar"/>
</@>

<@markup id="js">
<#-- JavaScript Dependencies -->
    <@script type="text/javascript" src="${url.context}/res/modules/simple-dialog.js" group="calendar"/>
    <@script type="text/javascript" src="${url.context}/res/scripts/lecm-events/lecm-calendar-view.js" group="calendar"/>
    <@script type="text/javascript" src="${url.context}/res/scripts/lecm-events/calendar-view-agenda.js" group="calendar"/>
    <@script type="text/javascript" src="${url.context}/res/scripts/lecm-events/calendar-view-search.js" group="calendar"/>
</@>

<@markup id="widgets">
    <@createWidgets group="calendar"/>
</@>

<@markup id="html">
    <@uniqueIdDiv>
        <#assign el=args.htmlid?html>
        <#assign toolbarId = el + "-toolbar">
        <!-- search -->
        <div class="calendar-search-header">
            <#import "/ru/it/lecm/base-share/components/base-components.ftl" as comp/>
            <@comp.baseToolbar toolbarId true true true>
                <div><a href="#" id="${el}_expand_all">${msg("calendar.search.expand-all")}</a></div>
                <div><a href="#" id="${el}_collapse_all">${msg("calendar.search.collapse-all")}</a></div>
            </@comp.baseToolbar>
        </div>
        <#assign filtersId = el + "-filters-toolbar"/>
        <div id="${filtersId}" class="arm-filters-bar">
            <@comp.baseToolbar filtersId true false false>
                <div>
                    <span class="arm-filters-label">
                        ${msg("calendar.filters.current")} (<a href="javascript:void(0);" id="${filtersId}-delete-all-link">${msg("calendar.delete.all-filters")}</a>):
                    </span>
                    <span id="${filtersId}-current-filters"></span>
                </div>
            </@comp.baseToolbar>
        </div>
        <div id="${el}Container" class="alf-calendar agendaview">
            <div id="${el}View">
                <div id="${el}View-noEvent" class="noEvent">
                    <p id="${el}View-defaultText" class="instructionTitle">${msg("search.initial-text")}</p>
                </div>
            </div>
        </div>
    </@>
</@>