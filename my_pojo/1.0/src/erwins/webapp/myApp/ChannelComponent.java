package erwins.webapp.myApp;

import org.springframework.stereotype.Component;

import erwins.util.webapp.ChannelHelper;

@Component
public class ChannelComponent extends ChannelHelper{
	
	private static final String MESSAGE = "message";

	@Override
	protected String getMessageKey() {
		return MESSAGE;
	}
	
}
