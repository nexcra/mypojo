
package erwins.util.text;

import java.text.MessageFormat;
import java.util.List;

import lombok.Data;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

/**
 * 간단 문자열 조합기.
 */
@Data
public class MessageBuilder{
    
	private final List<String> lines = Lists.newArrayList();
	private char lineSeparator = '\n';
	@Override
	public String toString(){
		return Joiner.on(lineSeparator).skipNulls().join(lines);
	}
	public MessageBuilder add(String line){
		lines.add(line);
		return this;
	}
	public MessageBuilder addFormat(String pattern,Object ... arguments){
		lines.add(MessageFormat.format(pattern, arguments));
		return this;
	}
	public static MessageBuilder createAddFormat(String pattern,Object ... arguments){
		MessageBuilder builder = new MessageBuilder();
		return builder.addFormat(pattern, arguments);
	}
	public static MessageBuilder createAdd(String line){
		MessageBuilder builder = new MessageBuilder();
		return builder.add(line);
	}

}
