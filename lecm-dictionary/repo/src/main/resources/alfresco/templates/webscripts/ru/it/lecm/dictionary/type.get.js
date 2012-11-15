/**
 *  Получение по рутовому узлу, тип значений справочника
 */
function main()
{
    var type = "";
    var nodeRef = "";
    if (args["nodeRef"] != null || args["nodeRef"] != "") {
        nodeRef = search.findNode(args["nodeRef"]);
        while(nodeRef.type != "{http://www.it.ru/lecm/dictionary/1.0}dictionary"){
            nodeRef = nodeRef.parent;
        }
        if (nodeRef.properties["lecm-dic:type"]!=null){
            type = nodeRef.properties["lecm-dic:type"];
        }
    }
    model.type = type;
}
main();