/*-
 * #%L
 * tt-cloud-server
 * %%
 * Copyright (C) 2020 bausdorf engineering
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

function saveUser(index) {
	var url = '/savesiteuser?userId=' + $("#userId-" + index).val()
			+ '&role=' + $("#role-" + index).val()
			+ '&enabled=' + $("#enabled-" + index).prop('checked')
			+ '&locked=' + $("#locked-" + index).prop('checked')
			+ '&expired=' + $("#expired-" + index).prop('checked');

	window.location = url;
}

function checkDriverStats() {
	var url = '/checkdriverstats?driverId=' + $("#driverId").val()
			+ '&planId=' + $("#planId").val();

	window.location = url;
}

function confirmEventRemove() {
	$("#event-remove-confirm").modal('show');
}

function confirmMemberRemove(index) {
	$("#member-remove-confirm-" + index).modal('show');
}

function confirmTeamDelete(teamId) {
	$("#team-delete-confirm-" + teamId).modal('show');
}

function confirmPlanDelete() {
	$("#plan-delete-confirm").modal('show');
}

function newPlanDialog() {
	$("#trackId").val($("#eventTrackId").val());
	$("#raceDuration").val($("#duration").val());
	$("#startTime").val($("#sessionDateTime").val());
	$("#todStartTime").val($("#simDateTime").val());

	$('#carId').find('option').remove().end();
	$.each($("#carsIds").val(), function (index, value) {
		$("#carId").append($("#car-option-" + value).clone());
	});

	$("#raceplan-modal").modal('show');
}

function enableButtons() {
	var eventId = $("#eventId").val();
	if(eventId) {
		$("#deleteLink").attr('href', '/deleteEvent?eventId=' + eventId);
		$("#deleteButton").prop('disabled', false);
		$("#planButton").prop('disabled', false);
	} else {
		$("#deleteLink").attr('href', '');
		$("#deleteButton").prop('disabled', true);
		$("#planButton").prop('disabled', true);
	}
}

function updateRacePlanLink() {
	var planId = $("#racePlanSelect").val();
	$("#racePlanLink").attr('href', '/planning?viewMode=time&planId=' + planId);
}

function selectExistingPlans() {
	var myTeams = [];
	$('#teamId option').each(function() {
		myTeams.push($(this).val())
	});

	var urlParams = {
		teamIds: myTeams,
		trackId: $("#eventTrackId").val(),
		duration: $("#duration").val()
	};

	var paramString = decodeURIComponent($.param(urlParams))
			.replaceAll("[", "")
			.replaceAll("]", "");

	$.ajax({
		type: "GET",
		dataType: "json",
		url: "/planList?" + paramString,
		success: function (data) {
			$('#racePlanSelect').find('option').remove().end();
			$.each(data, function (index, value) {
				var o = new Option(value.team + ' - ' + value.name, value.id);
				// jquerify the DOM object 'o' so we can use the html method
				//$(o).html("option text");
				$("#racePlanSelect").append(o);
				if(index === 0) {
					$("#racePlanSelect").val(value.id);
					updateRacePlanLink();
				}
			});
		}
	});
}

function raceEventClick(info) {
	$.ajax({type: "GET",
			dataType: "json",
			url: "/event?eventId=" + info.event.id,
			success: function(eventView) {
				$("#eventId").val(eventView.eventId);
				$("#season").val(eventView.season);
				$("#series").val(eventView.series);
				$("#name").val(eventView.name);
				$("#sessionDateTime").val(eventView.sessionDateTime);
				$("#simDateTime").val(eventView.simDateTime);
				$("#duration").val(eventView.duration);
				$("#timezone").val(eventView.timezone);
				$("#eventTrackId").val(eventView.eventTrackId);
				$("#carsIds").multiselect();
				$("#carsIds").multiselect('deselectAll', false)
						.multiselect('select', eventView.carsIds)
						.multiselect('refresh');
				// $("#carsIds").multiselect('select', eventView.carsIds);
				enableButtons();
				selectExistingPlans();
			}
	});
}

function selectTimezoneFromUtcOffset(timezone) {
	if (!timezone) {
		var utcOffsetHours = moment().utcOffset() / 60;
		var tz = 'GMT';
		if (utcOffsetHours >= 0) {
			tz = tz + '+' + utcOffsetHours;
		} else {
			tz = tz + utcOffsetHours;
		}
		$("#timezone > option").each(function() {
			if (this.value == tz) {
				this.selected = true;
			}
		});
	}
}