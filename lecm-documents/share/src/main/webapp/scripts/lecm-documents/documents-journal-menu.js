(function () {

    LogicECM.module.DocumentsJournal.Menu = function (htmlId) {
        return LogicECM.module.DocumentsJournal.Menu.superclass.constructor.call(
            this,
            "LogicECM.module.DocumentsJournal.Menu",
            htmlId,
            ["button"]);
    };

    YAHOO.extend(LogicECM.module.DocumentsJournal.Menu, Alfresco.component.Base, {
        onReady: function () {
            var contractsMenu = new YAHOO.widget.Menu("contractsMenu");

            contractsMenu.addItems([
                {
                    text: "Все",
                    onclick: {
                        fn: function () {
                            this.doChangeFilter("TYPE:\"lecm-contract:document\"");

                        },
                        scope: this
                    }
                },
                {
                    text: "Только мои",
                    onclick: {
                        fn: function () {
                            this.doChangeFilter("@cm\\:creator:" + LogicECM.module.DocumentsJournal.CURRENT_USER);
                        },
                        scope: this
                    }
                }
            ]);

            contractsMenu.render("contracts-menu");

            var onClickContractsMenuButton = function(e) {
                contractsMenu.moveTo(e.clientX, e.clientY);
                contractsMenu.show();
            };

            this.widgets.contractsButton = Alfresco.util.createYUIButton(this, "contractsBtn", onClickContractsMenuButton, {});
        },

        doChangeFilter: function (filter) {
            YAHOO.Bubbling.fire("changeFilter",
                {
                    filter:filter,
                    bubblingLabel:"documents-journal"
                });
        }
    });
})();
