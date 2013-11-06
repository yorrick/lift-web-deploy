App.namespace("views.usedVehicles");
App.views.usedVehicles.updateVehiclesTable = function(sentTable) {
  "use strict";

  var extractIds = App.views.usedVehicles.extractIds;
  var computeIdDifferences = App.views.usedVehicles.computeIdDifferences;

  var existingTable = $("#entries");
  var existingIds = extractIds(existingTable);
  var sentIds = extractIds($(sentTable));

  var differences = computeIdDifferences(existingIds, sentIds);
  var toRemove = differences.idsToRemove
  var toCreate = differences.idsToCreate

  toRemove.forEach(function (id) {
    $("[data-vehicle-id=" + id + "]").fadeOut().remove();
  });

  toCreate.forEach(function (id) {
    var nodeToAppend = $(sentTable).find("[data-vehicle-id=" + id + "]").css("display", "none");
    existingTable.append(nodeToAppend);
    nodeToAppend.fadeIn();
  });

  $("#description").val('');
  $("#generated-id").val('');
};

App.views.usedVehicles.extractIds = function(table) {
  return table.find("tr").map(function(index, tr){return $(tr).attr("data-vehicle-id")});
}

App.views.usedVehicles.computeIdDifferences = function(existingIds, sentIds) {
  var idsToRemove = [];
  var idsToCreate = [];

  $.grep(existingIds, function(id) {
    if ($.inArray(id, sentIds) == -1) idsToRemove.push(id);
  });

  $.grep(sentIds, function(id) {
    if ($.inArray(id, existingIds) == -1) idsToCreate.push(id);
  });

  return {"idsToRemove": idsToRemove, "idsToCreate": idsToCreate}
}

