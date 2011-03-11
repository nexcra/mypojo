
package erwins.gsample;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import erwins.gsample.grammar.GClosure;
import erwins.gsample.grammar.GCollection;
import erwins.gsample.grammar.GGrammer;
import erwins.gsample.grammar.GMethodChange;
import erwins.gsample.grammar.GMixin;
import erwins.gsample.grammar.GParameter;
import erwins.gsample.sdk.BindingUse;
import erwins.gsample.sdk.DynamicRunByJava;
import erwins.gsample.sdk.GRegEx;
import erwins.gsample.sdk.MarkupBuilderUse;
import erwins.gsample.sdk.NodeByBuilder;
import erwins.gsample.sdk.NodeByCollection;



@RunWith(Suite.class)
@Suite.SuiteClasses( { DynamicRunByJava.class,BindingUse.class,
	GMixin.class,
    GBean.class,NodeByCollection.class,GClosure.class,GGrammer.class,
    MarkupBuilderUse.class,NodeByBuilder.class,GParameter.class,GMethodChange.class,
    GCollection.class,GRegEx.class})
public class AllGroovyTest {
    
}