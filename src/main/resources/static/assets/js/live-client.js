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
            console.log(JSON.parse(message.body).content);
        });
        stompClient.subscribe('/live/' + $("#teamId").val() + '/sesiondata', function (message) {
            showSessionData(JSON.parse(message.body).content);
        });
        stompClient.subscribe('/live/' + $("#teamId").val() + '/rundata', function (message) {
            showRunData(JSON.parse(message.body).content);
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
    // $("#greetings").append("<tr><td>" + message + "</td></tr>");
    $("#sessionType").val(message.sessionType);
}

function showRunData(message) {
    console.log(message);
}

// $(function () {
//     $("form").on('submit', function (e) {
//         e.preventDefault();
//     });
//     $( "#connect" ).click(function() { connect(); });
//     $( "#disconnect" ).click(function() { disconnect(); });
//     $( "#send" ).click(function() { sendName(); });
// });
