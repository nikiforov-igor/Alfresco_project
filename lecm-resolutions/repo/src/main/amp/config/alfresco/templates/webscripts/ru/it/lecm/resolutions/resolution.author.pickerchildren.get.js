<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/pickerchildren.lib.js">

function main() {
  var data = [],
  availableChooseAuthor;

  availableChooseAuthor = resolutionsScript.getAvailableAuthor();

   if (availableChooseAuthor != null) {
        var filter = getFilterForAvailableElement(availableChooseAuthor);
        data = getPickerChildrenItems(filter);
   } else {
        data = getPickerChildrenItems();
   }

   model.parent = data.parent;
   model.rootNode = data.rootNode;
   model.results = data.results;
   model.additionalProperties = data.additionalProperties;

}
main();