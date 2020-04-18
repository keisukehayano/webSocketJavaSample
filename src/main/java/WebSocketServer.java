package main.java;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.websocket.CloseReason;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

//これをつけるとWebSocketServer
//URLはワイルドカード可能
@ServerEndpoint("/WebSocketServer")
public class WebSocketServer {

	//Session(通信)を保存しておくためのMap
	private static Map<String, Session> session = new HashMap<>();

	//自分のIDを保存しておく変数
	 String myId = "";

	 //自分の名前を保存しておく変数
	 String myName = "";

	/**
	 * 接続時に呼ばれるメソッド
	 */
	@OnOpen
	public void onOpen(final Session client, final EndpointConfig config) {
		/* セッション確立時の処理 */
		String log = client.getId() + " Was connected.";
		System.out.println(log);

	}

	/**
	 * エラー時に呼ばれるメソッド
	 */
	@OnError
	public void onError(final Session client, final Throwable error) {
		/* エラー発生時の処理 */
		String log = client.getId() + " was error. [" + error.getMessage() + "]";
		System.out.println(log);
	}

	/**
	 * 切断時に呼ばれるメソッド
	 */
	@OnClose
	public void onClose(final Session client, final CloseReason reason) throws IOException {
		/* セッション解放時の処理 */
		String log = client.getId() + " was closed by " + reason.getCloseCode() + "[" + reason.getCloseCode().getCode()
				+ "]";
		System.out.println(log);
	}

	/**
	 * メッセージが呼ばれた時に呼ばれるメソッド
	 */
	@OnMessage
	public void onMessage(final String text, final Session client) throws IOException {
		/* メッセージ受信時の処理 */
		System.out.println("WebSocket受信：" + text);

		//メッセージの内容は、改行区切りで操作、idが記述されているものとする
		String[] t = text.split("\n");
		String event = t[0];
		String id = t[1];

		System.out.println("EVENT:" + event);
		System.out.println("ID:" + id);
		switch (event) {

		case "login":
			//HashMapにSessionを保存しておく
			session.put(id, client);
			//変数にIDを保存する。
			myId = id;

			//名前の切り出し
			int strSize = id.length();
			int cutLength = 4;
			int nameCut = strSize - cutLength;
			//変数に名前だけを保存
			myName = id.substring(0,nameCut);
			System.out.println("切り出した名前:" + myName);

			//idで保存したセッションに文字列を送信
			Session mySession = session.get(id);
			mySession.getBasicRemote().sendText("Welcom to " + myName + " !!");
			System.out.println("login:" + id);
			break;

		case "commit":
			//ブロードキャスト
			for (Session s : session.values()) {
				s.getBasicRemote().sendText(myName + ": " + t[1]);
			}
			System.out.println("commit:" + t[1]);
			break;

		case "close":
			//セッション一覧から削除
			System.out.println("削除するID:" + myId);
			//保存したセッションを削除
			session.remove(myId);
			System.out.println("close:セッション削除");
			break;
		}

		//client.getBasicRemote().sendText(text);
	}

}
