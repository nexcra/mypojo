package erwins.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import erwins.util.lib.LibTest;
import erwins.util.lib.RegExTest;
import erwins.util.lib.security.SecurityTest;
import erwins.util.morph.DissolverTest;
import erwins.util.morph.DuplicatorTest;
import erwins.util.morph.MapToBeanTest;
import erwins.util.tools.MappTest;
import erwins.util.vender.apache.PoiTest;
import erwins.util.vender.hibernate.HqlBuilderTest;


@RunWith(Suite.class)
@Suite.SuiteClasses( { SecurityTest.class,LibTest.class,RegExTest.class,PoiTest.class
	,DissolverTest.class,DuplicatorTest.class,HqlBuilderTest.class,MappTest.class,MapToBeanTest.class}
)
public class Short_Test {
    
}
