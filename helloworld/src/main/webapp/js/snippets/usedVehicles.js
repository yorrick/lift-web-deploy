App.namespace("views.usedVehicles");
App.views.usedVehicles.updateVehiclesTable = function(sentTableRows) {
  "use strict";

  console.log(sentTableRows);

  var extractIds = App.views.usedVehicles.extractIds;
  var computeIdDifferences = App.views.usedVehicles.computeIdDifferences;

  var existingTableBody = $("#entries");
  var existingTableRows = existingTableBody.find("tr");
  var existingIds = extractIds(existingTableRows);
  var sentIds = extractIds($(sentTableRows));

  var differences = computeIdDifferences(existingIds, sentIds);
  var toRemove = differences.idsToRemove
  var toCreate = differences.idsToCreate

  console.log("toRemove " + toRemove);
  console.log("toCreate " + toCreate);

  toRemove.forEach(function (id) {
    var rowToRemove = $("[data-vehicle-id=" + id + "]").parent();
    rowToRemove.fadeOut({'complete': function() {
        rowToRemove.remove();
    }});
  });

  toCreate.forEach(function (id) {
    console.log("id " + id);
    var nodes = $.grep($(sentTableRows), function(element) {
        if ($(element).find("td[data-vehicle-id=" + id + "]").length > 0) {
            return true;
        }
    })
    var nodeToAppend = $(nodes[0]).css("display", "none")[0];

    existingTableBody.append(nodeToAppend);
    $(nodeToAppend).fadeIn();
  });

  $("#description").val('');
  $("#generated-id").val('');
};

App.views.usedVehicles.extractIds = function(tableRows) {
  return tableRows.map(function(index, tr){return $(tr).find("td.removeAction").attr("data-vehicle-id")});
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

