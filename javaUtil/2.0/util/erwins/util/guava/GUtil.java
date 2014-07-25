package erwins.util.guava;

import com.google.common.base.Joiner;
import com.google.common.base.Joiner.MapJoiner;
import com.google.common.base.Splitter;
import com.google.common.base.Splitter.MapSplitter;

/** GuavaUtil의 줄임말.. 괜히 줄인듯 */
public abstract class GUtil {
	
	public static final MapJoiner WEB_JOINER = Joiner.on('&').withKeyValueSeparator("=");
	public static final MapSplitter WEB_SPLITTER = Splitter.on('&').omitEmptyStrings().trimResults().withKeyValueSeparator('=');
	
	public static final Joiner COMMMA_JOINER = Joiner.on(',').skipNulls();
	public static final Splitter COMMMA_SPLITTER = Splitter.on(',').omitEmptyStrings().trimResults();

}
