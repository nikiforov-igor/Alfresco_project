<!-- Parameters and libs -->
<#assign id=args.htmlid?html/>
<#include "/org/alfresco/include/alfresco-macros.lib.ftl" />

<!-- Markup -->
<#if rateable>
    <div class="document-rating">
        <h2 id="rating_title" class="thin dark">Ваш голос</h2>
        <div id="raiting">
            <div id="raiting_blank"></div>
            <div id="raiting_hover"></div>
            <div id="raiting_votes"></div>
        </div>
    </div>

<script type="text/javascript">//<![CDATA[
    var Dom = YAHOO.util.Dom;
    var nodeRef = "${nodeRef?js_string}";
    var oneStarWidth = 21;
    var yourVote = "Ваш голос учтен"; //todo change to translates
    var ratingBlock = Dom.get('raiting'),
        ratingHover = Dom.get('raiting_hover'),
        ratingVotes = Dom.get('raiting_votes');
    var ratingMargin = Dom.getX(ratingBlock);
    var user_votes;
    var setVotes = function(votes) {
        Dom.setStyle(ratingVotes, 'width', votes + 'px');
        if (votes > 0) {
            Dom.get('rating_title').innerHTML = yourVote;
        }
    };

    ratingBlock.onmouseover = function(e) {
        Dom.setStyle(ratingVotes, 'display', 'none');
        Dom.setStyle(ratingHover, 'display', 'block');
    };
    ratingBlock.onmouseleave = function(e) {
        Dom.setStyle(ratingHover, 'display', 'none');
        Dom.setStyle(ratingVotes, 'display', 'block');
    };
    ratingBlock.onmousemove = function(e) {
        var widht_votes = e.pageX - ratingMargin;

        user_votes = Math.ceil(widht_votes / oneStarWidth);
        Dom.setStyle(ratingHover, 'width', (user_votes * oneStarWidth) + 'px')
    };
    ratingBlock.onclick = function(e) {
        <#-- установить рейтинг -->
        Alfresco.util.Ajax.jsonPost({
            url: Alfresco.constants.PROXY_URI + "lecm/document/api/setRating",
            dataObj: {
                "nodeRef": nodeRef,
                "rating": user_votes
            },
            successCallback: {
                fn: function refreshSuccess(response) {
                    var json = response.json;

                    if (json && json.error == 0) {
//                        setVotes(user_votes * oneStarWidth);
                        location.reload(); // чтобы обновились значения в "основных сведениях"
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
    };


    <#-- Получение рейтинга для текущего пользователя -->
    Alfresco.util.Ajax.request({
        url: Alfresco.constants.PROXY_URI + "lecm/document/api/getRating?nodeRef=" + nodeRef,
        successCallback: {
            fn: function refreshSuccess(response) {
                var json = response.json;

                if (json) {
                    var rating = json.myRating;
                    var starsWidth = rating * oneStarWidth;
                    setVotes(starsWidth);
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
//]]></script>
</#if>
