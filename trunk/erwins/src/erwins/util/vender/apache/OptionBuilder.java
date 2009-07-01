
package erwins.util.vender.apache;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.JspException;

import org.apache.commons.lang.StringUtils;
import org.apache.ecs.html.Option;

import erwins.util.lib.*;

/**
 * 아파치 ECS의 Option 래핑한다. 반드시 라벨 고려해서 만들것.
 * 
 * @author erwins(my.pojo@gmail.com)
 */
public class OptionBuilder {

    private List<Option> options = new ArrayList<Option>();
    private Option now;

    @Override
    public String toString() {
        return Strings.joinTemp(options);
    }

    public void add(Object value, String text) {
        now = new Option(value.toString());
        now.setTagText(text);
        options.add(now);
    }

    /**
     * Option생성자의 경우 value,label,text순이다. lable이 사용되지 않을 확율이 높다.
     */
    public void add(Object value, String text, String label) {
        add(value, text);
        now.setLabel(label);
    }

    public void addDefault() {
        addDefault("전체");
    }

    public void addDefault(String text) {
        now = new Option("");
        now.setTagText(text);
        options.add(now);
    }

    public Option now() {
        return now;
    }

    /**
     * 현재 년도 및 5년 간의 년도를 셀렉트 박스로 나타낸다.
     * @param1 전체 선택할것인지? default = false
     */
    public static String makeYearFromNow(int size, boolean isDefault, Integer selectedYear) {
        OptionBuilder o = new OptionBuilder();
        int year = Days.YEAR.getIntValue();
        if (isDefault) o.addDefault();
        for (int i = 0; i < 5; i++) {
            o.add(year - i, year - i + "년");
            if (i + selectedYear == 0) o.now().setSelected(true);
        }
        return o.toString();
    }

    /**
     * 현재 년도 및 5년 간의 년도를 셀렉트 박스로 나타낸다.
     * 
     * @param1 전체 선택할것인지? default = false
     */
    public static String makeYearFromNow(int size, boolean isDefault) {
        return makeYearFromNow(size, isDefault, null);
    }

    /**
     * 12개의 월을 셀렉트 박스로 나타낸다. 값은 2자리 (01월,02월)
     * 
     * @param1 전체 선택할것인지? default = false
     * @param2 현재 월을 기본으로 지정할 것인지? default = false
     */
    public static String getSelectMonth(boolean all, boolean nowMonthSelected) throws JspException {
        OptionBuilder o = new OptionBuilder();
        if (all) o.addDefault();
        int month = 0;
        if (nowMonthSelected) month = Integer.parseInt(Days.MONTH.get());
        for (int i = 0; i < 12; i++) {
            o.add(StringUtils.leftPad(String.valueOf(i + 1), 2, '0'), i + 1 + "월");
            if (month != 0 && month == i + 1) o.now().setSelected(true);
        }
        return o.toString();
    }

    @SuppressWarnings("unchecked")
    public static String getOption(Enum<?>[] en, boolean all, Enum<?>... ingnor) {
        OptionBuilder o = new OptionBuilder();
        if (all) o.addDefault();
        for (Enum<?> mode : en) {
            if (Sets.isEquals(ingnor, mode)) continue;
            o.add(mode.name(), mode.toString());
        }
        return o.toString();
    }
}