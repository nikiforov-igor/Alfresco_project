if (typeof LogicECM == "undefined" || !LogicECM) {
    LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.DocumentsJournal = LogicECM.module.DocumentsJournal || {};
(function () {

    LogicECM.module.DocumentsJournal.Menu = function (htmlId) {
        LogicECM.module.DocumentsJournal.Menu.superclass.constructor.call(
            this,
            "LogicECM.module.DocumentsJournal.Menu",
            htmlId,
            ["button"]);

        return this;
    };

    YAHOO.extend(LogicECM.module.DocumentsJournal.Menu, Alfresco.component.Base, {
        menus: [],
        onReady: function () {
            var filtersDivs = YAHOO.util.Dom.getElementsByClassName('journal-filter', 'div');
            var me = this;
            for (var i = 0; i < filtersDivs.length; i++) {
                var filter = filtersDivs[i];
                var type = filter.id;
                var casesSpans = YAHOO.util.Dom.getChildren(filter);
                var cases = [];
                for (var j = 0; j < casesSpans.length; j++) {
                    var obj = casesSpans[j];
                    obj.id = obj.id.replace(type + "-", "");
                    cases.push({
                        text: obj.title,
                        onclick: {
                            fn: function () {
                                me.doChangeFilter(
                                    {filterId:this.id, filterData:this.innerHTML});

                            }.bind(obj),
                            scope: this
                        }
                    });
                }
                var filterMenu = new YAHOO.widget.Menu("filterMenu-" + type);
                filterMenu.addItems(cases);
                filterMenu.render(type);
                this.menus.push(filterMenu);

                Alfresco.util.createYUIButton(this, type, function(e) {
                    this.moveTo(e.clientX, e.clientY);
                    this.show();
                }.bind(filterMenu), {});
            }

        },

        doChangeFilter: function (filter) {
            location.hash = '#filter=' + filter.filterId + "|" + filter.filterData;
            var filters = [];
            filters.push(filter);

            YAHOO.Bubbling.fire("datagridRefresh",
                {
                    filter: filters,
                    bubblingLabel: "documents-journal"
                });
        }
    });
})();
