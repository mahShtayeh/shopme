$(document).ready(
	function() {
		$("#cancelBtn").on("click", function() {
			window.location = moduleURL;
		});

		$("#fileImage").change(function() {
			fileSize = this.files[0].size;

			if (fileSize > 1048576) {
				this.setCustomValidity("You must choose an image less than 1MB !");
				this.reportValidity();
			} else {
				this.setCustomValidity("");
				showImageThumbnail(this);
			}
		});
	}
);

function showImageThumbnail(fileInput) {
	let file = fileInput.files[0];

	let reader = new FileReader();
	reader.onload = function(e) {
		$("#thumbnail").attr("src", e.target.result);
	};

	reader.readAsDataURL(file);
}

function showModalDialog(title, message) {
	$("#modalTitle").text(title);
	$("#modalBody").text(message);
	$("#modalDialog").modal();
}

function showWarningModalDialog(message) {
	showModalDialog("Warning", message); 
}

function showErrorModalDialog(message) {
	showModalDialog("Error", message); 
}