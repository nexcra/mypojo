package erwins.test;

import static org.mockito.Mockito.*;

import org.junit.Test;
import org.mockito.Mockito;

import erwins.domain.RootUserEntity;

public class _MockTest {


	@Test
	public void test(){
		
		//HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
		
		RootUserEntity m = Mockito.mock(RootUserEntity.class);
		
		m.setRownum(10);
		
		verify(m,never()).getRownum();
		verify(m,times(1)).setRownum(10);
		
		when(m.getRownum()).thenReturn(5);
		
		
		
	}

}
