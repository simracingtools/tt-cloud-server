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
        stompClient.subscribe('/live/' + $("#teamId").val() + '/sesiondata', function (message) {
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
    if (message.lastLapData) {
        showLapData(message.lastLapData)
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
    $("#timeOfDay").text(message.timeOfDay)
    $("#remainingSessionTime").text(message.remainingSessionTime);
    $("#timeInLap").text(message.timeInLap)
    $("#lapNo").text(message.lapNo);
}

function showSyncData(message) {
    for (var i in message) {
        $("#syncTD-" + message[i].driverId).text(message[i].timestamp);
        $("#syncTD-" + message[i].driverId).removeClass("table-danger")
                .removeClass("table-warning")
                .removeClass("table-success")
                .addClass(message[i].stateCssClass);
        $("#syncTH-" + message[i].driverId).removeClass("table-info")
                .addClass(message[i].inCarCssClass);
    }
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
// $(function () {
//     $("form").on('submit', function (e) {
//         e.preventDefault();
//     });
//     $( "#connect" ).click(function() { connect(); });
//     $( "#disconnect" ).click(function() { disconnect(); });
//     $( "#send" ).click(function() { sendName(); });
// });
