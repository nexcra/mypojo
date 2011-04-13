package erwins.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import erwins.test.domain.All_DomainTest;
import erwins.util.openApi.OpenApiTest;


@RunWith(Suite.class)
@Suite.SuiteClasses( { OpenApiTest.class,All_DomainTest.class}
)
public class Long_lTest {
    
}
