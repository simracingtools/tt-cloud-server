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
        stompClient.subscribe('/live/client-ack', function(message) {
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
}

function showRunData(message) {
    $("#fuelLevel").text(message.fuelLevelStr);
    $("#driverName").text(message.driverName);
    $("#flag").text(message.flags[0]);
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
// $(function () {
//     $("form").on('submit', function (e) {
//         e.preventDefault();
//     });
//     $( "#connect" ).click(function() { connect(); });
//     $( "#disconnect" ).click(function() { disconnect(); });
//     $( "#send" ).click(function() { sendName(); });
// });
