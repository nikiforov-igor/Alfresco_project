var employee = search.findNode(args["nodeRef"]);
var photos = employee.assocs["lecm-orgstr:employee-photo-assoc"];
var photo = null;
if (photos){
    photo = photos[0];
}
model.photo = photo;