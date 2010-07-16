package erwins.swt.network;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import erwins.domain.talk.BroadcastMessage;
import erwins.domain.talk.Protocol;
import erwins.domain.talk.TalkContext;
import erwins.swt.StoreForMap;
import erwins.swtUtil.lib.MessageUtil;
import erwins.util.lib.Strings;
import erwins.util.nio.MessageCallback;
import erwins.util.nio.TalkClient;
import erwins.util.root.Shutdownable;

public class TalkClientActivator extends TalkClientActivatorUI implements Shutdownable{
	
	private static final StoreForMap<String> directory = new StoreForMap<String>("TalkClient");
	private static final String SAVED_ID = "savedId";
	
	private TalkClient client;
	private boolean isLogined = false;
	
	private void executeMessage(final String header,final String textMessage) {
		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				if(header.equals(Protocol.LOGIN)) login(textMessage);
				else if(header.equals(Protocol.MESSAGE)) message(textMessage);
				else if(header.equals(Protocol.LOGIN_INFO)) loginInfo(textMessage);
				else if(header.equals(Protocol.LOGOUT)) logout(textMessage);
				else if(header.equals(Protocol.EXIT)) exit(textMessage);
				else error(header, textMessage);
			}
		});
	}

	private void error(String header, String textMessage) {
		addMessage("Error : {0} / {1}",header,textMessage);
	}

	private void addMessage(String template, String ... textMessage) {
		view.append(Strings.formatStr(template,textMessage)+"\n");
	}

	private void exit(String textMessage) {
		addMessage("[{0}] 님의 접속이 강제종료되었습니다.",textMessage);
	}

	private void logout(String textMessage) {
		addMessage("[{0}] 님이 로그아웃하셨습니다.",textMessage);
	}

	private void loginInfo(String textMessage) {
		loginList.removeAll();
		String[] list = Protocol.splitText(textMessage);
		for(String each : list) loginList.add(each);
	}

	private void message(String textMessage) {
		BroadcastMessage message = new BroadcastMessage();
		message.parsText(textMessage);
		addMessage("[{0}] {1}",message.getSenderId(),message.getMessage());
	}

	private void login(String textMessage) {
		addMessage("[{0}] 님이 로그인했습니다.",textMessage);
	}
	
	private void sendMessage() {
		String textMessage = message.getText();
		if(textMessage==null){
			MessageUtil.alert(shell, "메세지를 입력해 주세요.");
			return;
		}
		client.sendToServer(Protocol.newMessage(Protocol.MESSAGE,textMessage));
		message.setText("");
	}	
	
	protected void addMainListener() {
		final MessageCallback callback = new MessageCallback(){
			@Override
			public void messageFromServer(String message) {
				String[] messages = Protocol.splitMessage(message);
				for(String eachMessage : messages){
					String[] text = Protocol.splitHeader(eachMessage);
					if(text.length!=2) throw new RuntimeException("text must be array!!! @_@;");
					executeMessage(text[0],text[1]);	
				}
			}
		};
		connect.addListener(SWT.MouseUp, new Listener() {
			@Override
			public void handleEvent(Event arg0) {
				try {
					client = new TalkClient(TalkContext.TALK_SERVER_ID, TalkContext.TALK_SERVER_PORT);
					client.startup(callback);
				} catch (Exception e) {
					shutdown();
					MessageUtil.alert(shell, e.getMessage());
				}
				mediator();
				Display.getDefault().syncExec(new Runnable() {
					@Override
					public void run() {
						addMessage("서버와 접속되었습니다.");
					}
				});
			}
		});
		
		login.addListener(SWT.MouseUp, new Listener() {
			@Override
			public void handleEvent(Event arg0) {
				String id = loginId.getText();
				if(id==null){
					MessageUtil.alert(shell, "ID를 입력해 주세요");
					return;
				}
				directory.put(SAVED_ID, id);
				System.out.println(id);
				client.sendToServer(Protocol.newMessage(Protocol.LOGIN,id));
				isLogined = true;
				mediator();
			}
		});
		logout.addListener(SWT.MouseUp, new Listener() {
			@Override
			public void handleEvent(Event arg0) {
				client.sendToServer(Protocol.newMessage(Protocol.LOGOUT,loginId.getText()));
				shutdown();
			}
		});
		viewClear.addListener(SWT.MouseUp, new Listener() {
			@Override
			public void handleEvent(Event arg0) {
				view.setText("");
			}
		});
		send.addListener(SWT.MouseUp, new Listener() {
			@Override
			public void handleEvent(Event arg0) {
				sendMessage();
			}
		});
		message.addKeyListener(new KeyListener() {
			/** 기존 채팅의 관례를 따흔다. */
			@Override
			public void keyReleased(KeyEvent e) {
				int code = e.keyCode;
				if(code!=13) return;
				if(e.stateMask == SWT.SHIFT) return;
				sendMessage();
			}
			@Override
			public void keyPressed(KeyEvent arg0) {
			}
		});
		mediator();
	}

	protected void initialize() {
		String savedId = directory.get(SAVED_ID);
		if(savedId==null) return;
		loginId.setText(savedId);
	}
	
	private void mediator() {
		connect.setEnabled(client==null);
		loginId.setEnabled(client!=null && !isLogined);
		login.setEnabled(client!=null && !isLogined);
		boolean writeable = client!=null && isLogined; 
		logout.setEnabled(writeable);
		send.setEnabled(writeable);
		message.setEnabled(writeable);
	}
	
	public void shutdown() {
		if(client!=null){
			client.shutdown();
			client = null;	
		}
		isLogined = false;
		try {
			loginList.removeAll();
			view.setText("");
			mediator();
		} catch (NullPointerException e) {
			//NON
		}
	}	
	

}
