$(function() {
	var url = 'ws://' + window.location.host + '/SampleServer/WebSocketServer';
    var ws = null;
    var inputId;
    console.log('ロケーション:' + window.location.host);

		//4桁のランダムな数を生成するための変数
		var min = 1000;
		var max = 9999;

		//名前に添付してID化するためのソルトコード
		var saltCode;


    //initButtonを押すとここが走るよ
      $('#login').on("click",function() {
        if (ws == null) {
            inputId = $('#box1').find('input').val();
             //IDがが空なら登録させない
             if(inputId != "") {
            //Websocketを初期化
             ws = new WebSocket(url);
            //イベントハンドラの登録
             ws.onopen = onOpen;
             ws.onmessage = onMessage;
             ws.onclose = onClose;
             ws.onerror = onError;
             console.log("ID:" + inputId);
            } else {
                alert("ID名を入力してください!!");
            }
        }

      });



    //WebSocket open
    function onOpen(event) {
       $("#log").prepend("&lt;onopen&gt; " + "<br/>");
        console.log("WebSocket接続確立");
				//4桁のランダムな整数を生成
				saltCode = Math.floor(Math.random() * (max + 1 - min)) + min;
				//明示的に文字列へのキャスト
				String(saltCode);
				//ソルトコードをくっつけてID作成
				inputId = inputId + saltCode;
				console.log('ソルトコード付きID:' + inputId);
       ws.send("login\n" + inputId);
    };

    //WebSocket message
    function onMessage(receive) {
       $("#log").prepend(receive.data + "<br/><br/>");
        console.log("レスポンスメッセージ:" + receive.data);
    };

    //WebSocket error
    function onError() {
        $("#log").prepend("&lt;onerror&gt; " + "<br/>");
        console.log("エラー処理");
        alert("error");
    };

    //WebSocket close
    function onClose() {
        $("#log").prepend("&lt;onclose&gt; " + "<br/>");
        ws.send("close\ndelete")
        ws = null;
        console.log("WebSocket切断");
    };


    //windowが閉じられた(例:ブラウザを閉じた)時のイベントを設定
    $(window).on("beforeunload",function() {
        ws.onclose(); //WebSocket close
    });

    //WebSocketを使ってメッセージをサーバに送信
   $('#send').on('click',function() {
       var sendMessage = $('#message').val();
       console.log("メッセージ:" + sendMessage);
       if(sendMessage != "") {
       ws.send("commit\n" + sendMessage);
           $('#message').val("");
       } else {
           console.log("空なので送信しません");
       }
   });



});
