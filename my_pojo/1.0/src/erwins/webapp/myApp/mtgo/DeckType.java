package erwins.webapp.myApp.mtgo;

import erwins.util.morph.BeanToJson;
import erwins.util.root.Pair;



public enum DeckType implements Pair{
	standard,
	classic,
	legacy,pauper;

	@Override
	public String getValue() {
		return this.name();
	}

	@Override
	public String getName() {
		return this.name();
	}
	
	public static final String JSON = BeanToJson.pairToJson(DeckType.values()).toString();
	
}
