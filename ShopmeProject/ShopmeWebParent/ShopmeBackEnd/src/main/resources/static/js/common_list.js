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
	//let url = "[[@{'/users/page/1?sortField=' + ${sortField} + 'sortDir=' + ${sortDir}}]]";
	window.location.replace(moduleURL);
}