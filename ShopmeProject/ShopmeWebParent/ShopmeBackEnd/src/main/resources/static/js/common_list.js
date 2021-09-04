$(document).ready(
	function() {
		$(".link-delete").on("click", function(e) {
			e.preventDefault();

			link = $(this);
			toDeleteId = link.attr("toDeleteId");
			objType = link.attr("objType");

			$("#yesBtn").attr("href", link.attr("href"));
			$("#modalBody").text("Are you sure you want to delete this " + objType + " with ID [" + toDeleteId + "] ?");
			$("#confirmationDialog").modal();
		});
	}
);

function clearFilter() {
	window.location.replace(moduleURL);
}