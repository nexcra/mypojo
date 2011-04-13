package erwins.test.domain;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import erwins.test.Short_Test;

@RunWith(Suite.class)
@Suite.SuiteClasses( { EtcTest.class, BoardTest.class, BookTest.class, DataBaseTest.class,
		LabelTest.class, SecurityAopTest.class,Short_Test.class })
public class All_DomainTest {
	
}