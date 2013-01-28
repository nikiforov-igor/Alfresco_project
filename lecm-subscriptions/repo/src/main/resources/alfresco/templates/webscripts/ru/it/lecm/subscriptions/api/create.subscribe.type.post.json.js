var name = json.get("name");
// описание
var description = json.get("description");
// тип объекта
var objectTypeRef = json.get("objectType");
// категория события
var eventCategoryRef = json.get("eventCategory");
// тип доставки
var notificationType = json.get("notificationType").split(",");
// сотрудники
var employee = json.get("employee").split(",");
// рабочие группы
var workGroup = json.get("workGroup").split(",");
// подразделения
var organizationUnit = json.get("organizationUnit").split(",");
// должностные позиции
var position = json.get("position").split(",");

model.subscribeType = subscription.createSubscribeType(name, description, objectTypeRef, eventCategoryRef, notificationType, employee, workGroup, organizationUnit, position);