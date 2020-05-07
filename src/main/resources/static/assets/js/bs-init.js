$(document).ready(function(){
	$('[data-bs-tooltip]').tooltip();
});

function confirmMemberRemove(index) {
	$("#member-remove-confirm-" + index).modal('show');
}

function confirmTeamDelete(teamId) {
	$("#team-delete-confirm-" + teamId).modal('show');
}