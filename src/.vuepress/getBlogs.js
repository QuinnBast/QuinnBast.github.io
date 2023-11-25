var fs = require('fs');

module.exports = function(path) {
  var files = fs.readdirSync(path);
  console.log(files);
  var list = [];
  for (var i in files) {
    var filename = files[i].split('.').slice(0, -1).join('.');
    if (filename.toLowerCase() !== "index" && filename.toLowerCase() != undefined) {
        list.push(filename);
    }
  }
  console.log(`${path}: `, list);
  return list;
}