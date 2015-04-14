package erwins.jsample.validation;

import erwins.util.validation.constraints.Match.MatchValue;




public enum TestEnum{
	
	EN01,
	EN02,
	EN03,
	
	;
	
	public static final Enum<?>[] INPUT_A = new Enum<?>[]{EN01,EN02};
	
	public static class InputA implements MatchValue{
		@Override
		public Object[] includeValue() {
			return new Enum<?>[]{EN02,EN03};
		}
	};
	

}
