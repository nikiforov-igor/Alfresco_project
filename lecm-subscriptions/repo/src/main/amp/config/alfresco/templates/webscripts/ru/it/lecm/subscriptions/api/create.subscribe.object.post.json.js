var name = json.get("name");
// описание
var description = json.get("description");
// ссылка на объект
var objectRef = json.get("objectRef");
// категория события
var eventCategoryRef = json.get("eventCategory");
// тип доставки
var notificationType = json.get("notificationType").split(",");
// сотрудники
var employee = json.get("employee").split(",");

model.subscribeObject = subscription.createSubscribeObject(name, objectRef, description, notificationType, employee);

