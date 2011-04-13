
package erwins.test.domain;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import erwins.util.exception.LoginRequiredException;
import erwins.util.exception.RoleNotFoundException;
import erwins.util.exception.Check;
import erwins.util.exception.Check.ExceptionRunnable;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:dataSourceTest.xml", "classpath:config.xml" })
public class SecurityAopTest extends RootSptingTest {
	
	@Test
    public void deleteUserBySecurityAOP() throws Exception {
		Check.isThrowException(new ExceptionRunnable() {
			@Override
			public void run() throws Exception {
				try {
					userService.delete(2025);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		},LoginRequiredException.class);
		testLogin();
		Check.isThrowException(new ExceptionRunnable() {
			@Override
			public void run() throws Exception {
				try {
					userService.delete(2025);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		},RoleNotFoundException.class);
        
    }
	
	@Override
    protected boolean needLogin() {
        return false;
    }
}