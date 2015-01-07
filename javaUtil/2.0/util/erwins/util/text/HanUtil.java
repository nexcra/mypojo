package erwins.util.text;

import java.util.List;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Range;

/** 
 * 주워온 소스를 수정
 * unused 등등 고칠게 많아보인다.
 *  */
@SuppressWarnings("unused")
public abstract class HanUtil {

	/* **********************************************
	 * 자음 모음 분리 설연수 -> ㅅㅓㄹㅇㅕㄴㅅㅜ, 바보 -> ㅂㅏㅂㅗ
	 * *********************************************
	 */
	/** 초성 - 가(ㄱ), 날(ㄴ) 닭(ㄷ) */
	private static char[] arrChoSung = { 0x3131, 0x3132, 0x3134, 0x3137, 0x3138,
			0x3139, 0x3141, 0x3142, 0x3143, 0x3145, 0x3146, 0x3147, 0x3148,
			0x3149, 0x314a, 0x314b, 0x314c, 0x314d, 0x314e };
	/** 중성 - 가(ㅏ), 야(ㅑ), 뺨(ㅑ) */
	private static char[] arrJungSung = { 0x314f, 0x3150, 0x3151, 0x3152,
			0x3153, 0x3154, 0x3155, 0x3156, 0x3157, 0x3158, 0x3159, 0x315a,
			0x315b, 0x315c, 0x315d, 0x315e, 0x315f, 0x3160, 0x3161, 0x3162,
			0x3163 };
	/** 종성 - 가(없음), 갈(ㄹ) 천(ㄴ) */
	private static char[] arrJongSung = { 0x0000, 0x3131, 0x3132, 0x3133,
			0x3134, 0x3135, 0x3136, 0x3137, 0x3139, 0x313a, 0x313b, 0x313c,
			0x313d, 0x313e, 0x313f, 0x3140, 0x3141, 0x3142, 0x3144, 0x3145,
			0x3146, 0x3147, 0x3148, 0x314a, 0x314b, 0x314c, 0x314d, 0x314e };

	/* **********************************************
	 * 알파벳으로 변환 설연수 -> tjfdustn, 멍충 -> ajdcnd
	 * *********************************************
	 */
	/** 초성 - 가(ㄱ), 날(ㄴ) 닭(ㄷ) */
	private static String[] arrChoSungEng = { "r", "R", "s", "e", "E", "f", "a",
			"q", "Q", "t", "T", "d", "w", "W", "c", "z", "x", "v", "g" };

	/** 중성 - 가(ㅏ), 야(ㅑ), 뺨(ㅑ) */
	private static String[] arrJungSungEng = { "k", "o", "i", "O", "j", "p",
			"u", "P", "h", "hk", "ho", "hl", "y", "n", "nj", "np", "nl", "b",
			"m", "ml", "l" };

	/** 종성 - 가(없음), 갈(ㄹ) 천(ㄴ) */
	private static String[] arrJongSungEng = { "", "r", "R", "rt", "s", "sw",
			"sg", "e", "f", "fr", "fa", "fq", "ft", "fx", "fv", "fg", "a", "q",
			"qt", "t", "T", "d", "w", "c", "z", "x", "v", "g" };

	/** 단일 자음 - ㄱ,ㄴ,ㄷ,ㄹ... (ㄸ,ㅃ,ㅉ은 단일자음(초성)으로 쓰이지만 단일자음으론 안쓰임) */
	private static String[] arrSingleJaumEng = { "r", "R", "rt", "s", "sw",
			"sg", "e", "E", "f", "fr", "fa", "fq", "ft", "fx", "fv", "fg", "a",
			"q", "Q", "qt", "t", "T", "d", "w", "W", "c", "z", "x", "v", "g" };

	private static final int HAN_PREFIX = 0xAC00; // 44032
	/** A. 자음과 모음이 합쳐진 글자인경우 */
	private static final Range<Integer> HAN_RANGE = Range.closed(0, 11172);
	/** 단일자음인 경우 */
	private static final Range<Integer> HAN_J = Range.closed(34097, 34126);
	/** 단일모음인 경우 */
	private static final Range<Integer> HAN_M = Range.closed(34127, 34147);
	private static final Joiner JOINER = Joiner.on("");

	/** 한글을 초중종성으로 분리한 뒤 알파벳으로 변경한다. */
	public static String hanToAlpha(String words) {
		List<Character> separated = separateHan(words);
		List<String> en = Lists.newArrayList();

		for (Character word : separated) {
			char hanRange = (char) (word - HAN_PREFIX);
			if (HAN_J.contains((int) hanRange)) {
				int jaum = (hanRange - 34097);
				en.add(arrSingleJaumEng[jaum]);
			} else if (HAN_M.contains((int) hanRange)) {
				int moum = (hanRange - 34127);
				en.add(arrJungSungEng[moum]);
			} else {
				/* 알파벳인 경우 */
				en.add(String.valueOf(word));
			}
		}
		return JOINER.join(en);
	}

	protected static List<Character> separateHan(String words) {
		List<Character> separated = Lists.newArrayList();

		for (int i = 0; i < words.length(); i++) {
			char word = words.charAt(i);
			int hanRange = word - HAN_PREFIX;

			if (HAN_RANGE.contains(hanRange)) {

				/* A-1. 초/중/종성 분리 */
				int chosung = hanRange / (21 * 28);
				int jungsung = hanRange % (21 * 28) / 28;
				int jongsung = hanRange % (21 * 28) % 28;

				/* A-2. result에 담기 */
				separated.add(arrChoSung[chosung]);
				separated.add(arrJungSung[jungsung]);
				/* A-3. 종성이 존재할경우 result에 담는다 */
				if (jongsung != 0x0000)
					separated.add(arrJongSung[jongsung]);

			} else {
				/* B. 한글이 아니거나 자음만 있을경우 */
				separated.add(word);
			}
		}
		return separated;
	}

	// =============================== 이하 주워온 소스만 남겨놓음. 개선의 여지 있음

	// 코드타입 - 초성, 중성, 종성
	enum CodeType {
		chosung, jungsung, jongsung
	}

	/**
	 * 영어를 한글로...
	 */
	public static String engToKor(String eng) {
		StringBuffer sb = new StringBuffer();
		int initialCode = 0, medialCode = 0, finalCode = 0;
		int tempMedialCode, tempFinalCode;

		for (int i = 0; i < eng.length(); i++) {

			// 초성코드 추출
			initialCode = getCode(CodeType.chosung, eng.substring(i, i + 1));
			//System.out.println(i + "[초성]" + (char) (0xAC00 + initialCode));
			if (i != 0) {
				if (eng.charAt(i - 1) == ' ') {
					sb.append(" ");
				}
			}
			i++; // 다음문자로

			// 중성코드 추출
			tempMedialCode = getDoubleMedial(i, eng); // 두 자로 이루어진 중성코드 추출

			if (tempMedialCode != -1) {
				medialCode = tempMedialCode;
				//System.out.println(i + "[중성2자리]" + (char) (0xAC00 + medialCode));

				i += 2;
			} else { // 없다면,
				medialCode = getSingleMedial(i, eng); // 한 자로 이루어진 중성코드 추출
				////System.out.println(i + "[중성1자리]" + (char) (0xAC00 + medialCode));
				i++;
			}

			// 종성코드 추출
			tempFinalCode = getDoubleFinal(i, eng); // 두 자로 이루어진 종성코드 추출
			if (tempFinalCode != -1) {
				finalCode = tempFinalCode;
				// 그 다음의 중성 문자에 대한 코드를 추출한다.
				tempMedialCode = getSingleMedial(i + 2, eng);
				if (tempMedialCode != -1) { // 코드 값이 있을 경우
					finalCode = getSingleFinal(i, eng); // 종성 코드 값을 저장한다.
				} else {
					i++;
				}
			} else { // 코드 값이 없을 경우 ,
				tempMedialCode = getSingleMedial(i + 1, eng); // 그 다음의 중성 문자에 대한
																// 코드 추출.
				if (tempMedialCode != -1) { // 그 다음에 중성 문자가 존재할 경우,
					finalCode = 0; // 종성 문자는 없음.
					i--;
				} else {
					finalCode = getSingleFinal(i, eng); // 종성 문자 추출
					if (finalCode == -1)
						finalCode = 0;
				}

			}
			// 추출한 초성 문자 코드, 중성 문자 코드, 종성 문자 코드를 합한 후 변환하여 스트링버퍼에 넘김
			//System.out.println(i + "[번째][" + initialCode + "][" + medialCode+ "][" + finalCode + "]");

			sb.append((char) (0xAC00 + (initialCode + medialCode + finalCode)));
			//System.out.println(i + "[번째결과]" + (char) (0xAC00 + initialCode + medialCode + finalCode));

		}
		//System.out.println("[결과]" + sb.toString());
		return sb.toString();
	}

	/**
	 * 해당 문자에 따른 코드를 추출한다.
	 * 
	 * @param type
	 *            초성 : chosung, 중성 : jungsung, 종성 : jongsung 구분
	 * @param char 해당 문자
	 */
	private static int getCode(CodeType type, String c) {
		//System.out.println("codeType" + type + "[String]" + c);
		// 초성
		String init = "rRseEfaqQtTdwWczxvg";
		// 중성
		String[] mid = { "k", "o", "i", "O", "j", "p", "u", "P", "h", "hk",
				"ho", "hl", "y", "n", "nj", "np", "nl", "b", "m", "ml", "l" };
		// 종성
		String[] fin = { "r", "R", "rt", "s", "sw", "sg", "e", "f", "fr", "fa",
				"fq", "ft", "fx", "fv", "fg", "a", "q", "qt", "t", "T", "d",
				"w", "c", "z", "x", "v", "g" };

		switch (type) {
		case chosung:
			int index = init.indexOf(c);
			if (index != -1) {
				return index * 21 * 28;
			}
			break;
		case jungsung:

			for (int i = 0; i < mid.length; i++) {
				if (mid[i].equals(c)) {
					return i * 28;
				}
			}
			break;
		case jongsung:
			for (int i = 0; i < fin.length; i++) {
				if (fin[i].equals(c)) {
					return i + 1;
				}
			}
			break;
		default:
			throw new IllegalStateException("잘못된 타입 입니다 " + type);
		}
		//System.out.println("초중종검색된 것없음");
		return -1;
	}

	// 한 자로 된 중성값을 리턴한다
	// 인덱스를 벗어낫다면 -1을 리턴
	private static int getSingleMedial(int i, String eng) {
		if ((i + 1) <= eng.length()) {
			return getCode(CodeType.jungsung, eng.substring(i, i + 1));
		} else {
			return -1;
		}
	}

	// 두 자로 된 중성을 체크하고, 있다면 값을 리턴한다.
	// 없으면 리턴값은 -1
	private static int getDoubleMedial(int i, String eng) {
		int result;
		if ((i + 2) > eng.length()) {
			return -1;
		} else {
			result = getCode(CodeType.jungsung, eng.substring(i, i + 2));
			if (result != -1) {
				return result;
			} else {
				return -1;
			}
		}
	}

	// 한 자로된 종성값을 리턴한다
	// 인덱스를 벗어낫다면 -1을 리턴
	private static int getSingleFinal(int i, String eng) {
		if ((i + 1) <= eng.length()) {
			return getCode(CodeType.jongsung, eng.substring(i, i + 1));
		} else {
			return -1;
		}
	}

	// 두 자로된 종성을 체크하고, 있다면 값을 리턴한다.
	// 없으면 리턴값은 -1
	private static int getDoubleFinal(int i, String eng) {
		if ((i + 2) > eng.length()) {
			return -1;
		} else {
			return getCode(CodeType.jongsung, eng.substring(i, i + 2));
		}
	}

}
