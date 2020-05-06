var stompClient = null;

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    }
    else {
        $("#conversation").hide();
    }
    $("#greetings").html("");
}

function connect() {
    var socket = new SockJS('/liveclient');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        // setConnected(true);
        console.log('Connected: ' + frame);
        stompClient.subscribe('/user/live/client-ack', function(message) {
            var jsonMessage = JSON.parse(message.body);
            console.log(jsonMessage);
            showSessionData(jsonMessage);
        });
        stompClient.subscribe('/live/' + $("#teamId").val() + '/rundata', function (message) {
            var jsonMessage = JSON.parse(message.body);
            console.log(jsonMessage);
            showRunData(jsonMessage);
        });
        stompClient.subscribe('/live/' + $("#teamId").val() + '/syncdata', function (message) {
            var jsonMessage = JSON.parse(message.body);
            console.log(jsonMessage);
            showSyncData(jsonMessage);
        });
        stompClient.subscribe('/live/' + $("#teamId").val() + '/lapdata', function (message) {
            var jsonMessage = JSON.parse(message.body);
            console.log(jsonMessage);
            showLapData(jsonMessage);
        });
        stompClient.subscribe('/live/' + $("#teamId").val() + '/eventdata', function (message) {
            var jsonMessage = JSON.parse(message.body);
            console.log(jsonMessage);
            showEventData(jsonMessage);
        });
        stompClient.subscribe('/live/' + $("#teamId").val() + '/pitdata', function (message) {
            var jsonMessage = JSON.parse(message.body);
            console.log(jsonMessage);
            showPitData(jsonMessage);
        });
        sendTeamId();
    }, function (frame) {
        console.log('Error: ' + frame);
    });
}


function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    // setConnected(false);
    console.log("Disconnected");
}

function sendTeamId() {
    stompClient.send("/app/liveclient", {}, JSON.stringify({'teamId': $("#teamId").val(), 'text': 'Hello'}));
}

function showSessionData(message) {
    $("#sessionType").text(message.sessionType);
    $("#teamName").text(message.teamName);
    $("#maxCarFuel").val(message.maxCarFuel);
    $("#trackLocation").text(message.trackLocation)
            .removeClass("loc-black")
            .removeClass("loc-blue")
            .removeClass("loc-yellow")
            .removeClass("loc-green")
            .removeClass("loc-orange")
            .addClass(message.trackLocationCssClass);
    $("#timeZone").text(message.timeZone);
    if (message.lastLapData) {
        showLapData(message.lastLapData);
    }
    if (message.pitStops) {
        showPitData(message.pitStops);
    }
}

function showRunData(message) {
    $("#fuelLevel").text(message.fuelLevelStr);
    $("#fuelAvailableLaps").text(message.availableLaps)
        .removeClass("table-danger")
        .removeClass("table-warning")
        .removeClass("table-info")
        .addClass(message.availableLapsCssClass);
    $("#driverName").text(message.driverName);
    $("#flag").text(message.flags[0])
        .removeClass("flag-white")
        .removeClass("flag-black")
        .removeClass("flag-green")
        .removeClass("flag-red")
        .removeClass("flag-blue")
        .removeClass("flag-yellow")
        .removeClass("flag-dq")
        .removeClass("flag-repair")
        .addClass(message.flagCssClass);
    $("#raceSessionTime").text(message.raceSessionTime);
    $("#timeOfDay").text(message.timeOfDay);
    $("#remainingSessionTime").text(message.remainingSessionTime);
    $("#timeInLap").text(message.timeInLap)
    $("#lapNo").text(message.lapNo);
    $("#localClock").text(message.localClock);
}

function showSyncData(message) {
    $("#syncTD-" + message.driverId).text(message.timestamp)
            .removeClass("table-danger")
            .removeClass("table-warning")
            .removeClass("table-success")
            .addClass(message.stateCssClass);
    $("#syncTH-" + message.driverId).removeClass("table-info")
            .addClass(message.inCarCssClass);
}

function showLapData(message) {
    $("#stintNo").text(message.stintNo);
    $("#stintTime").text(message.stintClock);
    $("#lapNo").text(message.lapNo);
    $("#lapsRemaining").text(message.lapsRemaining);
    $("#stintsRemaining").text(message.stintsRemaining);
    $("#stintLap").text(message.stintLap);
    $("#fuelLastLap").text(message.lastLapFuel);
    $("#fuelStintAvg").text(message.stintAvgFuelPerLap);
    $("#fuelStintDelta").text(message.stintAvgFuelDelta)
            .removeClass('table-success')
            .removeClass('table-danger')
            .addClass(message.stintAvgFuelDeltaCssClass);
    $("#lastLapTime").text(message.lastLapTime);
    $("#stintAvgLapTime").text(message.stintAvgLapTime);
    $("#stintAvgTimeDelta").text(message.stintAvgTimeDelta)
            .removeClass('table-success')
            .removeClass('table-danger')
            .addClass(message.stintAvgTimeDeltaCssClass);
    $("#stintRemainingTime").text(message.stintRemainingTime);
    $("#trackTemp").text(message.trackTemp);
    $("#driverBestLap").text(message.driverBestLap);
    $("#estimatedFuelPerLap").text(message.estimatedFuelPerLap);
    setEstimatedFuelDelta(message.lastLapFuel);
    if ($("#estimatedFuelPerLapInput").val() == 0) {
        $("#estimatedFuelPerLapInput").val(message.requiredFuelPerLapOneMore);
        $("#estimatedFuelLaps").text(message.estimatedFuelLaps);
    }
    if ($("#maxCarFuel").val() == 0) {
        $("#maxCarFuel").val(message.maxCarFuel);
        $("#estimatedFuelLaps").text(message.estimatedFuelLaps);
    }
    $("#estimatedLapTime").text(message.estimatedLapTime);
    $("#estimatedLapTimeDelta").text(message.estimatedLapTimeDelta)
            .removeClass('table-success')
            .removeClass('table-danger')
            .addClass(message.estimatedLapTimeDeltaCssClass);
    $("#estimatedStintTime").text(message.estimatedStintTime);
}

function showEventData(message) {
    $("#raceSessionTime").text(message.raceSessionTime);
    $("#timeOfDay").text(message.timeOfDay);
    $("#trackLocation").text(message.trackLocation)
            .removeClass("loc-black")
            .removeClass("loc-blue")
            .removeClass("loc-yellow")
            .removeClass("loc-green")
            .removeClass("loc-orange")
            .addClass(message.trackLocationCssClass);
}

function showPitData(message) {
    var rowsLeft = 50;
    for (var i in message) {
        $("#stintRow-" + i).removeClass("hidden");
        $("#pitStint-" + i).text(message[i].stintNo);
        $("#pitTimeLeft-" + i).text(message[i].raceTimeLeft);
        $("#pitLap-" + i).text(message[i].lapNo);
        $("#pitDriver-" + i).text(message[i].driver)
        $("#pitTime-" + i).text(message[i].timePitted);
        $("#pitStopDuration-" + i).text(message[i].pitStopDuration);
        $("#pitService-" + i).text(message[i].service);
        $("#pitServiceDuration-" + i).text(message[i].serviceDuration);
        $("#pitRefuel-" + i).text(message[i].refuelAmount);
        $("#pitRepairTime-" + i).text(message[i].repairTime);
        rowsLeft -= 1;
    }
    for (var i = 50 - rowsLeft; i < 50; i++) {
        $("#stintRow-" + i).addClass("hidden");
    }
}

function setEstimatedFuelDelta(fuelPerLap) {
    var delta = Number(fuelPerLap) - $("#estimatedFuelPerLapInput").val();
    var cssClass = 'table-success';
    if (delta > 0) {
        cssClass = 'table-danger';
    }
    $("#estimatedFuelDelta").text(delta.toFixed(3))
    .removeClass('table-success')
    .removeClass('table-danger')
    .addClass(cssClass);
}

function maxCarFuelChange(maxFuel) {
    var laps = maxFuel / $("#estimatedFuelPerLapInput").val();
    $("#estimatedFuelLaps").text(laps.toFixed(2));
}

function fuelPerLapChange(fuelPerLap) {
    var laps = $("#maxCarFuel").val() / fuelPerLap;
    $("#estimatedFuelLaps").text(laps.toFixed(2));
    setEstimatedFuelDelta($("#fuelLastLap").text())
}

// $(function () {
//     $("form").on('submit', function (e) {
//         e.preventDefault();
//     });
//     $( "#connect" ).click(function() { connect(); });
//     $( "#disconnect" ).click(function() { disconnect(); });
//     $( "#send" ).click(function() { sendName(); });
// });
