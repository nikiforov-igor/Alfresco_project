<@link rel="stylesheet" type="text/css" href="${url.context}/res/css/components/document-rating.css" />

<!-- Parameters and libs -->
<#assign id=args.htmlid?html/>
<#include "/org/alfresco/include/alfresco-macros.lib.ftl" />

<!-- Markup -->
<#if rateable?? && rateable>
    <div class="document-rating">
        <h2 id="rating_title" class="thin dark">${msg("document.rating.your-voice")}</h2>
        <div id="rating">
            <div id="rating_blank"></div>
            <div id="rating_hover"></div>
            <div id="rating_votes"></div>
        </div>
        <a id="rating_change" href="javascript:void(0);">${msg("document.rating.change")}</a>
    </div>

<script type="text/javascript">//<![CDATA[
    var Dom = YAHOO.util.Dom,
        Event = YAHOO.util.Event;
    var nodeRef = "${nodeRef?js_string}";
    var oneStarWidth = 21;
    var yourVote = '${msg("document.rating.your-voice-set")}';

    Event.onContentReady('rating', function() {
        var ratingBlock = Dom.get('rating'),
            ratingHover = Dom.get('rating_hover'),
            ratingVotes = Dom.get('rating_votes'),
            change = Dom.get('rating_change');
        var ratingMargin = Dom.getX(ratingBlock);
        var userVote,
            currentRating;
        <#-- Обработчики: -->
        var onMouseOver = function(e) {
                Dom.setStyle(ratingVotes, 'display', 'none');
                Dom.setStyle(ratingHover, 'display', 'block');
            },
            onMouseOut = function(e) {
                Dom.setStyle(ratingHover, 'display', 'none');
                Dom.setStyle(ratingVotes, 'display', 'block');
            },
            onMouseMove = function(e) {
                var width_votes = e.pageX - ratingMargin;

                userVote = Math.ceil(width_votes / oneStarWidth);
                Dom.setStyle(ratingHover, 'width', (userVote * oneStarWidth) + 'px')
            },
            onClick = function(e) {
                if (currentRating == userVote) {
                    setRating(currentRating);
                } else {
                    <#-- установить рейтинг -->
                    Alfresco.util.Ajax.jsonPost({
                        url: Alfresco.constants.PROXY_URI + "lecm/document/api/setRating",
                        dataObj: {
                            "nodeRef": nodeRef,
                            "rating": userVote
                        },
                        successCallback: {
                            fn: function refreshSuccess(response) {
                                var json = response.json;

                                if (json && json.error == 0) {
                                    setRating(userVote);
                                }
                            },
                            scope: this
                        },
                        failureCallback: {
                            fn: function refreshFailure(response) {
                                console.log(response);
                            },
                            scope: this
                        }
                    });
                }
            };

        var addListeners = function() {
            ratingBlock.addEventListener('mouseover', onMouseOver);
            ratingBlock.addEventListener('mouseout', onMouseOut);
            ratingBlock.addEventListener('mousemove', onMouseMove);
            ratingBlock.addEventListener('click', onClick);
        };
        var setRating = function(rating) {
            currentRating = rating;
            if (rating > 0) {
                Dom.setStyle(ratingVotes, 'width', (rating * oneStarWidth) + 'px');
                Dom.get('rating_title').innerHTML = yourVote;
                Dom.setStyle(change, 'display', 'inline');

                ratingBlock.removeEventListener('mouseover', onMouseOver);
                ratingBlock.removeEventListener('mouseout', onMouseOut);
                ratingBlock.removeEventListener('mousemove', onMouseMove);
                ratingBlock.removeEventListener('click', onClick);
            } else {
                addListeners();
            }
            onMouseOut();
        };

        change.onclick = function(e) {
            addListeners();
            Dom.setStyle(change, 'display', 'none');
        };

        <#-- Получение рейтинга для текущего пользователя (при загрузке страницы)-->
        Alfresco.util.Ajax.jsonGet({
            url: Alfresco.constants.PROXY_URI + "lecm/document/api/getRating",
            dataObj: {
                nodeRef: nodeRef
            },
            successCallback: {
                scope: this,
                fn: function refreshSuccess(response) {
                    var json = response.json;

                    if (json) {
                        var rating = json.myRating;
                        setRating(rating);
                    }
                }
            },
            failureCallback: {
                scope: this,
                fn: function refreshFailure(response) {
                    console.log(response);
                }
            }
        });
    });

//]]></script>
</#if>
