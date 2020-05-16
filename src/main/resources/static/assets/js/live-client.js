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
var stompClient = null;
var timeFormat = 'H:mm:ss.SSS';
var chartConfig = {
    type: 'line',
    data: {
        labels: [
        ],
        datasets: [{
            label: 'Temp',
            backgroundColor: 'rgb(255, 99, 132)',//.alpha(0.5).rgbString(),
            borderColor: 'rgb(235, 99, 132)',
            fill: false,
            yAxisID: 'temp',
            data: [
            ],
        }, {
            label: 'Laptime',
            backgroundColor: 'rgb(54, 162, 235)',//.alpha(0.5).rgbString(),
            borderColor: 'rgb(54, 162, 235)',
            fill: false,
            yAxisID: 'time',
            data: [
            ],
        }]
    },
    options: {
        aspectRatio: 4,
        maintainAspectRatio: false,
        title: {
            text: 'Laptime / Track temp'
        },
        scales: {
            yAxes: [{
                id: 'time',
                type: 'time',
                position: 'left',
                time: {
                    parser: timeFormat,
                    // tooltipFormat: 'mm:ss.SSS',
                    unit: 'seconds',
                    stepSize : 2,
                    displayFormats: {
                        'seconds': 'mm:ss.SSS',
                        'minutes': 'mm:ss.SSS',
                    }
                },
                scaleLabel: {
                    display: true,
                    labelString: 'Laptime'
                },
                gridLines: {
                    color: 'rgb(74, 182, 255)'
                },
                ticks: {
                    beginAtZero: false
                }
            }, {
                id: 'temp',
                type: 'linear',
                position: 'right',
                scaleLabel: {
                    display: true,
                    labelString: 'Temp'
                },
                gridLines: {
                    color: 'rgb(255, 179, 212)'
                },
                ticks: {
                    beginAtZero: false
                }
            }],
            xAxes: [{
                position: 'bottom',
                scaleLabel: {
                    display: true,
                    labelString: 'Laps'
                }
            }]
        },
    }
};

function addChartData(lap, temp, lapTime, slowest, fastest) {
    window.lapCharts.data.labels.push(lap);
    window.lapCharts.data.datasets[0].data.push(temp);
    window.lapCharts.options.scales.yAxes[0].ticks.min = fastest;
    window.lapCharts.options.scales.yAxes[0].ticks.max = slowest;
    if (lapTime !== '0:00:00.000') {
        window.lapCharts.data.datasets[1].data.push(lapTime);
    } else {
        window.lapCharts.data.datasets[1].data.push('');
    }
    window.lapCharts.update();
}

function addSessionChartData(chartData, slowest, fastest) {
    for (var i in chartData.lapNos) {
        window.lapCharts.data.labels.push(chartData.lapNos[i]);
        window.lapCharts.data.datasets[0].data.push(chartData.temps[i]);
        window.lapCharts.data.datasets[1].data.push(chartData.laps[i]);
    }
    window.lapCharts.options.scales.yAxes[0].ticks.min = fastest;
    window.lapCharts.options.scales.yAxes[0].ticks.max = slowest;
    window.lapCharts.update();
}

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
    addSessionChartData(message.chartData, message.slowestLap, message.fastestLap);
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
    $("#trackTemp").text(message.trackTemp + '°C');
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
    addChartData(message.lapNo, message.trackTemp, message.lastLapTime,
        message.slowestLap, message.fastestLap);
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
        $("#pitTime-" + i).text(message[i].timePitted);
        $("#pitStopDuration-" + i).text(message[i].pitStopDuration);
        $("#pitService-" + i).text(message[i].service);
        $("#pitServiceDuration-" + i).text(message[i].serviceDuration);
        $("#pitRefuel-" + i).text(message[i].refuelAmount);
        $("#pitRepairTime-" + i).text(message[i].repairTime);
        $("#pitDriverSelect-" + i).empty();
        for (var k in message[i].allDrivers) {
            $("<option/>").val(message[i].allDrivers[k])
                    .text(message[i].allDrivers[k])
                    .appendTo("#pitDriverSelect-" + i);
        }
        $("#pitDriverSelect-" + i).val(message[i].driver);
        if (message[i].pitStopDuration !== '') {
            $("#pitDriverSelect-" + i).prop('disabled', 'disabled')
        }
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
