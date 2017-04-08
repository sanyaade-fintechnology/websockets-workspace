var echo_websocket;
var output;
var textID;

function init() {
    output = $("#output")[0];
    textID = $("#textID")[0];
}

function send_echo() {
    var wsUri = "ws://localhost:8080/echo";
    writeToScreen(">>> Connecting to " + wsUri);
    echo_websocket = new WebSocket(wsUri);

    echo_websocket.onopen = function (event) {
        writeToScreen("<<< Connected");
        doSend(textID.value);
    };

    echo_websocket.onmessage = function (event) {
        writeToScreen("<<<<<< " + event.data);
        writeToScreen("========================");
        echo_websocket.close();
    };

    echo_websocket.onerror = function (event) {
        writeToScreen('<span style="color: red;">ERROR:</span> ' + event.data);
        echo_websocket.close();
    };
}

function doSend(message) {
    echo_websocket.send(message);
    writeToScreen(">>>>>> " + message);
}

function writeToScreen(message) {
    var pre = document.createElement("p");
    pre.style.wordWrap = "break-word";
    pre.innerHTML = message;
    output.appendChild(pre);
}

window.addEventListener("load", init, false);
