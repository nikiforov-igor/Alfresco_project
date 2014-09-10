(function(){
    /**
     * YUI Library aliases
     */
    var Event = YAHOO.util.Event,
        Dom = YAHOO.util.Dom,
        Selector = YAHOO.util.Selector,
        Bubbling = YAHOO.Bubbling;

    //Set block height
    function setTreeHeight() {
        var wrapper = Selector.query('div.sticky-wrapper', null, true);
        var bd = Dom.get('bd');
        var block = Dom.get('lecm-page');

        var h = parseInt(Dom.getStyle(wrapper, 'height')) - Dom.getY(block)
            - parseInt(Dom.getStyle(block, 'margin-bottom')) - parseInt(Dom.getStyle(bd, 'margin-bottom'))
            - (parseInt(Dom.getStyle(Selector.query('div.sticky-footer', wrapper, true), 'height')) || 0);
        // Высота header-а
        var headerDiv = Selector.query('div.header-bar.toolbar.flat-button.theme-bg-2', block, true);
        var headerDivTopHeight = headerDiv ? (parseInt(Dom.getStyle(headerDiv, 'height'))
            + parseInt(Dom.getStyle(headerDiv, 'border-top')) + parseInt(Dom.getStyle(headerDiv, 'border-bottom'))) : 0;
        // Высота footer-a
        var footerDivBottomHeight = parseInt(Dom.getStyle('lecm-content-ft', 'height')) || 0;
        block = Dom.get('dictionary');
        var height = h - headerDivTopHeight - footerDivBottomHeight - 20;
        // Выставляем высоту, чтобы scrollbar был внизу перед footer-ом
        Dom.setStyle(block, 'height', height +'px');
        Dom.setStyle(block, "position", "inherit");
        Dom.setStyle(block, 'width', 'auto');
    }

    function setHeaderWidth(){
        // ширина дерева
        var treeWidth = parseInt(Dom.getStyle(Dom.getElementsByClassName('datalists tree')[0], 'width'));
        // ширина ползунка
        var resizer = parseInt(Dom.getStyle(Dom.getElementsByClassName("yui-resize-handle yui-resize-handle-r"),"width"));
        var header = Dom.getElementsByClassName('header-bar toolbar flat-button theme-bg-2')[0];
        // Удлиняем header при перемещении ползунка resize-ра
        Dom.setStyle(header, 'width', treeWidth+resizer+'px');
    }

    Event.onDOMReady(function() {
        LogicECM.module.Base.Util.removeAllBubbles(this);
        Bubbling.on("GridRendered", setTreeHeight, this);
        Bubbling.on("HeightSetted", setTreeHeight, this);
        Bubbling.on("SetHeaderWidth",setHeaderWidth, this);
        // Событие на перемещение ползунка scrollbar-а
        Event.on(Dom.getElementsByClassName("datalists tree"),"scroll",function(e){
            var treeWidth = Dom.getElementsByClassName('datalists tree')[0].scrollWidth;
            var header = Dom.getElementsByClassName('header-bar toolbar flat-button theme-bg-2')[0];
            var resizer = parseInt(Dom.getStyle(Dom.getElementsByClassName("yui-resize-handle yui-resize-handle-r"),"width"));
            // Удлиняем header при перемещении ползунка scrollbar-а
            Dom.setStyle(header, 'width', treeWidth + resizer + 'px');
        },this,true);
    });

})();