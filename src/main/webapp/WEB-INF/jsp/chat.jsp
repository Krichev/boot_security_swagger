<!DOCTYPE html>
<html>
<head>
    <title>Title</title>
    <style>
        .chatbox {
            display: none;
        }

        /*#allIn {*/
        /*    display: none;*/
        /*}*/

        .messages {
            background-color: #369;
            width: 500px;
            padding: 20px;
        }

        .start .username {
            width: 420px;
            padding: 20px;
        }

        .messages .msg {
            background-color: #fff;
            border-radius: 10px;
            margin-bottom: 10px;
            overflow: hidden;
        }

        .messages .msg .from {
            background-color: #396;
            line-height: 30px;
            text-align: center;
            color: white;
        }

        .messages .msg .text {
            padding: 10px;
        }

        #start {
            position: relative;
            z-index: 0;
        }

        textarea.msg {
            width: 540px;
            padding: 10px;
            resize: none;
            border: none;
            box-shadow: 2px 2px 5px 0 inset;
        }

        input:invalid {
            /*border: 2px dashed red;*/
            border: 30px solid red;
            z-index: 9999;

            position: absolute;


        }

        input:valid .start #start {
            border: 2px solid black;
            display: block;
        }
    </style>
    <script src="https://code.jquery.com/jquery-2.2.2.js"
            integrity="sha256-4/zUCqiq0kqxhZIyp4G0Gk+AOtCJsY1TA00k5ClsZYE="
            crossorigin="anonymous"></script>
    <script>
         var result;

        let ChatUnit = {
                init() {
                    this.view = document.getElementById("allIn");
                    // this.view.style.display = "none";
                    this.token = localStorage.getItem("token");
                    this.nameUser = localStorage.getItem("name");
                    this.roleUser = localStorage.getItem("role");
                    // if (role == "ROLE_CLIENT") {
                    //     this.roleUser = "client";
                    // } else {
                    //     this.roleUser = "agent";
                    // }
                    // alert(this.token && this.nameUser && this.roleUser);
                    if (this.token && this.nameUser && this.roleUser) {
                        this.check();
                    } else {
                        // alert("boolean");
                        window.location.href = window.origin + "/login"
                    }

                },
                check() {
                    // function chat() {
                    $.ajax({
                        url: "/check",
                        type: "GET",
                        /*contentType: "application/json; charset=utf-8",
                        dataType: "json",*/
                        headers: {"Authorization": "Bearer " + this.token},

                        success: function (data, textStatus, jqXHR) {
                            localStorage.setItem("checkResult", "ok");
                            result = true;

                        }, error: function (jqXHR, textStatus, errorThrown) {
                           result = false;
                           // alert(result)+"errror";
                            var phrase = "Something went wrong,";
                            if (jqXHR.status === 403) {
                                phrase = "You are not authorized,";
                            } else if (jqXHR.status === 500) {
                                phrase = "Your token has expired,";

                            }
                            this.allScreen = document.getElementById("allIn");
                            this.allScreen.innerText = phrase + " in 5 seconds You will be redirected to Login page";
                            // .html("<p>Spring exception:<br>" + jqXHR.responseJSON.exception + "</p>");
                            setTimeout(function () {
                                window.location.href = window.origin + "/login"
                            }, 5000);

                        }
                    });
                    // alert(  (localStorage.getItem("checkResult")==="ok")) ;

                    if (localStorage.getItem("checkResult")==="ok") {// because setTimeout works async
                   // if(result){
                        this.startSocket();
                    }

                },
                startSocket() {
                    // this.view.style.display = "block";
                    this.startbox = document.querySelector(".start");
                    this.chatbox = document.querySelector(".chatbox");
                    // this.chatbox.style.display = "block";
                    this.startBtn = this.startbox.querySelector("button");
                    this.msgTextArea = document.querySelector("textarea");

                    this.chatMessageContainer = this.chatbox.querySelector(".messages");
                    this.bindEvents();
                }
                ,
                bindEvents() {
                    // this.nameInput.addEventListener("input", function (event) {
                    //     if (this.nameInput.validity.valid) {
                    //         this.startBtn.style.display="block";
                    //     }
                    // });
                    // if (this.nameInput.checkValidity())
                    //     this.startBtn.style.display = "block";
                    // this.startBtn.addEventListener("click", e => this.openSocket());
                    this.openSocket();
                    // this.nameInput.addEventListener("input", e => this.openSocket());
                    this.msgTextArea.addEventListener("keyup", e => {
                        if (e.ctrlKey && e.keyCode === 13) {
                            e.preventDefault();
                            this.send();
                        }

                    })
                }
                ,
                closeUI: function () {
                    // this.allScreen = document.getElementById("allIn");
                    this.view.innerText = "you have closed the chat, to restart conversation, please update the page"
                }
                ,
                send() {
                    this.txInput = this.msgTextArea.value;

                    if (this.txInput !== "") {
                        this.sendMessage({
                            name: this.nameUser + "_" + this.roleUser,
                            text: this.msgTextArea.value
                        });
                    }

                    if (this.txInput === "/exit") {
                        this.closeUI();
                    }
                }
                ,
                onOpenSock() {

                }
                ,
                onMessage(msg) {
                    let msgBlock = document.createElement("div");
                    msgBlock.className = "msg";
                    let fromBlock = document.createElement("div");
                    fromBlock.className = "from";
                    fromBlock.innerText = msg.name;
                    let textBlock = document.createElement("div");
                    textBlock.className = "text";
                    textBlock.innerText = msg.text;

                    msgBlock.appendChild(fromBlock);
                    msgBlock.appendChild(textBlock);
                    this.chatMessageContainer.prepend(msgBlock);

                }
                ,
                onClose() {

                }
                ,
                sendMessage(msg) {
                    this.onMessage({name: "I`m ", text: msg.text});
                    this.msgTextArea.value = "";
                    this.ws.send(JSON.stringify(msg));

                }
                ,
                openSocket() {

                    // this.name = this.nameInput.value;

                    // this.namein = this.name.split(" ")[2];
                    // this.roleUser = this.name.split(" ")[1];


                    this.ws = new WebSocket("ws://localhost:8080/chat/" + this.nameUser + "_" + this.roleUser);
                    this.ws.onopen = () => this.onOpenSock();
                    this.ws.onmessage = (e) => this.onMessage(JSON.parse(e.data));
                    this.ws.onclose = () => this.onClose();


                    // this.startbox.style.display = "none";
                    this.chatbox.style.display = "block";

                }
            }
        ;

        window.addEventListener("load", e => ChatUnit.init());
    </script>

</head>
<body>
<h1>Support</h1>
<div id="allIn">
    <div class="start">
        <%--        <input type="text" class="username" placeholder="to start enter such command [ /create (client or agent) name] "--%>
        <%--               required pattern="/create agent [a-z]+|/create client [a-z]+">--%>
        <%--        <button id="start">start</button>--%>
    </div>
    <div class="chatbox" id="ch">
    <textarea class="msg" placeholder="enter something, empty messages prohibited">

        </textarea>
        <div class="messages">

        </div>
    </div>
</div>
</body>
</html>
