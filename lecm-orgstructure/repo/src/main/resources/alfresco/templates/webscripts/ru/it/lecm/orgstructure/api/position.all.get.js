var positionDictionary = companyhome.childByNamePath("Dictionary/Должностные позиции");
var nodes = positionDictionary.getChildAssocsByType("lecm-orgstr:staffPosition");

model.positions = nodes;
