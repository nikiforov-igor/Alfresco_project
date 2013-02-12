(function(){
    /**
     * YUI Library aliases
     */
    var Event = YAHOO.util.Event,
        Dom = YAHOO.util.Dom,
        Bubbling = YAHOO.Bubbling;

    //Set block height
    function setTreeHeight() {

        var bd = Dom.get('bd');
        var block = Dom.get('lecm-page');
        var wrapper = Dom.getElementsByClassName('sticky-wrapper', 'div');

        var h = parseInt(Dom.getStyle(wrapper, 'height')) - Dom.getY(block)
            - parseInt(Dom.getStyle(block, 'margin-bottom')) - parseInt(Dom.getStyle(bd, 'margin-bottom'));
        // Высота header-а
        var headerDivTopHeight = parseInt(Dom.getStyle(Dom.getElementsByClassName('header-bar toolbar flat-button theme-bg-2', 'div'), 'height'));
        // Высота footer-a
        var footerDivBottomHeight = parseInt(Dom.getStyle(Dom.get('lecm-content-ft'), 'height'));
        block = Dom.get('dictionary');
        var height = h - headerDivTopHeight - footerDivBottomHeight - 16;
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
        Bubbling.on("GridRendered", setTreeHeight, this);
        Bubbling.on("HeightSetted", setTreeHeight, this);
        Bubbling.on("SetHeaderWidth",setHeaderWidth, this);
        // Собитие на перемещение ползунка scrollbar-а
        Event.on(Dom.getElementsByClassName("datalists tree"),"scroll",function(e){
            var treeWidth = Dom.getElementsByClassName('datalists tree')[0].scrollWidth;
            var header = Dom.getElementsByClassName('header-bar toolbar flat-button theme-bg-2')[0];
            var resizer = parseInt(Dom.getStyle(Dom.getElementsByClassName("yui-resize-handle yui-resize-handle-r"),"width"));
            // Удлиняем header при перемещении ползунка scrollbar-а
            Dom.setStyle(header, 'width', treeWidth + resizer + 'px');
        },this,true);
    });

})();