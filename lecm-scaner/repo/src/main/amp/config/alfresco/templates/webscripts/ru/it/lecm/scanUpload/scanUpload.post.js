var filename = null;
var content = null;
var title = "";
var description = "";

for each (field in formdata.fields)
{
  if (field.name == "title")
  {
    title = field.value;
  }
  else if (field.name == "desc")
  {
    description = field.value;
  }
  else if (field.name == "file" && field.isFile)
  {
    filename = field.filename;
    content = field.content;
  }
}

// ensure mandatory file attributes have been located
if (filename == undefined || content == undefined)
{
  status.code = 400;
  status.message = "Uploaded file cannot be located in request";
  status.redirect = true;
}
else
{
  // create document in company home for uploaded file
  upload = userhome.createFile("upload" + userhome.children.length + "_" + filename) ;
  
  upload.properties.content.write(content);
  upload.properties.content.setEncoding("UTF-8");
  upload.properties.content.guessMimetype(filename);
  
  upload.properties.title = "Отсканированный документ";
  upload.properties.description = "Отсканированный документ";
  upload.save();
  model.upload = upload;
}