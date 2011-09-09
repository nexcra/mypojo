package erwins.webapp.myApp.common.xmpp;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import erwins.util.exception.Check;
import erwins.util.lib.StringUtil;

public abstract class GoogleTalkBot{

	public void parseAndSend(String adress,String message) {
		String[] chat = StringUtil.getFirstOf(message, " ");
		String command = chat[0].toLowerCase();
		Token token = tokens.find(command);
		if(token==null){
			sendMessage(adress,"["+command+"] -> 없는 명령어 입니다. 명령어를 보시려면 help를 입력하세요");
			return ;
		}
		try {
			token.messageTo.send(adress, chat[1]);
		} catch (Exception e) {
			String msg = "error : " + e.getMessage();
			sendMessage(adress,msg);
		}
	}

	protected abstract void sendMessage(String mailAdress, String message);

	protected final TokenSet tokens = new TokenSet();
	
	final Token HELP = tokens.add("help", "도움말", "도움말 기능입니다.", new MessageTo() {
		public void send(String mailAdress, String message) {
			StringBuilder b = new StringBuilder();
			b.append("== 내장된 명령어 입니다.==");
			b.append("\n");
			for (Token each : tokens) {
				String temp = "[" + each.parse + "]";
				temp = StringUtil.rightPad(temp, 8, ' ');
				b.append(temp);
				b.append(each.name);
				b.append(" : ");
				b.append(each.desc);
				b.append("\n");
			}
			sendMessage(mailAdress, b.toString());
		}
	});

	final Token A = tokens.add("a", "알람", "분단위 입력을 받아서 알람을 나타냅니다.", new MessageTo() {
		public void send(final String mailAdress, String message) {
			final Double min;
			try {
				min = Double.parseDouble(message);
			} catch (NumberFormatException e1) {
				throw new RuntimeException("숫자형 파라메터(분)만을 입력해야 합니다.");
			} catch (Exception e1) {
				throw new RuntimeException("a 명령어는 하나의 숫자 파라메터를 입력해야 합니다. ex)a 5.2");
			}
			Check.isPositive(min, "0보다 큰 숫자가 입력되어야 합니다.");
			sendMessage(mailAdress, min + "분 후에 알람으로 알려드리겠습니다.");
			try {
				Thread.sleep(Math.round(min * 60 * 1000));
			} catch (InterruptedException e) {}
			sendMessage(mailAdress, "주인님 " + min + "분이 지났습니다. ");
		}
	});

	final Token SQL = tokens.add("sql", "SQL", "SQL을 서버로 전송 후 응답을 받습니다.", new MessageTo() {
		public void send(String mailAdress, String message) {
			sendMessage(mailAdress, "작성 예정 ㅋ");
		}
	});
	final Token STATE = tokens.add("state", "서버상태", "서버의 현재 상태를 알려줍니다.", new MessageTo() {
		public void send(String mailAdress, String message) {
			sendMessage(mailAdress, "작성 예정 ㅋ");
		}
	});


	/** 개별 토큰들. 한개의 명령만을 지원한다. */
	protected class Token {
		private Token(String parse, String name, String desc, MessageTo messageTo) {
			this.parse = parse.toLowerCase();
			this.name = name;
			this.desc = desc;
			this.messageTo = messageTo;
		}

		public final String desc;
		public final String name;
		public final String parse;
		public final MessageTo messageTo;
	}

	protected class TokenSet implements Iterable<Token> {
		private List<Token> parsers = new ArrayList<Token>();

		public Token add(String lexeme, String name, String desc, MessageTo runnable) {
			Token p = new Token(lexeme, name, desc, runnable);
			parsers.add(p);
			return p;
		}

		public Iterator<Token> iterator() {
			return parsers.iterator();
		}

		public Token find(String command) {
			for (Token token : this)
				if (token.parse.equals(command)) return token;
			return null;

		}

	}

	protected interface MessageTo{
		public void send(String mailAdress, String message);
	}

}
