package erwins.test.runAndSeeResult;
import org.apache.ecs.html.A;
import org.apache.ecs.html.LI;
import org.apache.ecs.html.UL;
import org.junit.Test;

import erwins.util.tools.StopWatch;

public class SpeedOfBuildHtml {

	@Test
	public void t1() throws Exception {
		final int size = 1000;
		final UL root = new UL();
		System.out.println(StopWatch.load(new Runnable() {
			@Override
			public void run() {
				for(int i=0;i<size;i++){
					LI li = new LI();
			        li.setNeedClosingTag(true);
			        li.addElement(new A("eee").setTagText("name"));
			        
			        UL ul = new UL();
			        ul.setTagText("아오빡쳐");
			        li.addElement(ul);
			        root.addAttribute(i+"", li);
				}
				root.toString();
			}
		}));
		
		final StringBuilder b = new StringBuilder();
		
		System.out.println(StopWatch.load(new Runnable() {
			@Override
			public void run() {
				for(int i=0;i<size;i++){
					b.append("<a href='eee'>name</a><ul>아오빡쳐</ul></li> 0=<li><a href='eee'>name</a><ul>아오빡쳐</ul></li>"+i);
				}
			}
		}));
		//System.out.println();
		//System.out.println(b.toString());
	}

}
