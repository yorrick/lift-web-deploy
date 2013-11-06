App.namespace("views.usedVehicles");
App.views.usedVehicles.updateVehiclesTable = function(vehiclesTable) {
  "use strict";

  var extractIds = App.views.usedVehicles.extractIds;

  var existingIds = extractIds($("#entries"));
  console.log(existingIds);

  var sentIds = extractIds($(vehiclesTable));
  console.log(sentIds);

  $("#entries").replaceWith($(vehiclesTable));

  $("#description").val('');
  $("#generated-id").val('');
};

App.views.usedVehicles.extractIds = function(table) {
  return table.find("tr").map(function(index, tr){return $(tr).attr("data-vehicle-id")});
}