
package erwins.util.morph;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.ecs.html.Input;
import org.apache.ecs.html.Label;
import org.apache.ecs.html.OptGroup;
import org.apache.ecs.html.Option;

import erwins.util.lib.CollectionUtil;
import erwins.util.lib.DayUtil;
import erwins.util.lib.StringUtil;
import erwins.util.reflexive.Connectable;
import erwins.util.root.Pair;

/**
 * 아파치 ECS의 Option 래핑한다. 반드시 라벨 고려해서 만들것.
 * @author erwins(my.pojo@gmail.com)
 */
public class HtmlOptionBuilder {

    private List<Option> options = new ArrayList<Option>();
    private Option now;

    @Override
    public String toString() {
        return StringUtil.joinTemp(options,",");
    }
    
    public void add(Pair pair) {
        add(pair.getValue(),pair.getName());
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
    
    // ===========================================================================================
    //                                    static
    // ===========================================================================================
    
    public static Option makeOption(Object value, String text){
        Option option = new Option(value.toString());
        option.setTagText(text);
        return option;
    }
    
    public static Option makeOption(Pair pair ){
        return makeOption(pair.getValue(),pair.getName());
    }

    /**
     * 현재 년도 및 5년 간의 년도를 셀렉트 박스로 나타낸다.
     * @param1 전체 선택할것인지? default = false
     */
    public static String makeYearFromNow(int size, boolean isDefault, Integer selectedYear) {
        HtmlOptionBuilder o = new HtmlOptionBuilder();
        int year = DayUtil.YEAR.getIntValue();
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
    public static String getSelectMonth(boolean all, boolean nowMonthSelected){
        HtmlOptionBuilder o = new HtmlOptionBuilder();
        if (all) o.addDefault();
        int month = 0;
        if (nowMonthSelected) month = Integer.parseInt(DayUtil.MONTH.get());
        for (int i = 0; i < 12; i++) {
            o.add(StringUtils.leftPad(String.valueOf(i + 1), 2, '0'), i + 1 + "월");
            if (month != 0 && month == i + 1) o.now().setSelected(true);
        }
        return o.toString();
    }
    
    /**
     * Class는 Enum이여야 하며 Pair를 구현해야 한다.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static String option(Class<?> clazz) {
        Enum<?>[] qwe = ((Class<Enum>)clazz).getEnumConstants();
        return option((Pair[])qwe,true);
    }

    public static String option(Pair[] pairs, boolean all, Pair... ingnor) {
        HtmlOptionBuilder o = new HtmlOptionBuilder();
        if (all) o.addDefault();
        for (Pair pair : pairs) {
            if (CollectionUtil.isEqualsAny(ingnor, pair)) continue;
            o.add(pair);
        }
        return o.toString();
    }
    
    public static String option(Collection<Pair> pairs, boolean all) {
        return option(pairs.toArray(new Pair[pairs.size()]),all);
    }
    
    /**
     * 그룹 옵션을 정의한다. 그룹옵션은 계층형 임으로 여기에서만 정의된다.
     */
    public static String groupOption(Collection<Pair> ... lists) {
        List<OptGroup> groupOption = new ArrayList<OptGroup>();
        for(Collection<Pair> list : lists){
            for (Pair each: list) {
                OptGroup optGroup = new OptGroup("qwe");
                optGroup.addElement(HtmlOptionBuilder.makeOption(each));
                groupOption.add(optGroup);
            }
        }
        return StringUtil.joinTemp(groupOption,",");
    }
    
    /**
     * 그룹 옵션을 정의한다. 그룹옵션은 계층형 임으로 여기에서만 정의된다.
     */
    public static <ID extends Serializable,T extends Connectable<ID, T>> String groupOption(Collection<T> parents) {
        List<OptGroup> groups = new ArrayList<OptGroup>();
        for(T parent : parents){
            OptGroup optGroup = new OptGroup(parent.getName());
            for (T each: parent.getChildren()) {
                optGroup.addElement(HtmlOptionBuilder.makeOption(each));
            }
            groups.add(optGroup);
        }
        return StringUtil.joinTemp(groups,",");
    }
    
    // ===========================================================================================
    //                                    static
    // ===========================================================================================
    
    /**
     * 제일 처음것이 선택된 상태.
     */
    public static String radio(List<Pair> pairs,String entityName) {
        List<Label> list = new ArrayList<Label>();
        for (int i=0;i<pairs.size();i++) {
            Pair each = pairs.get(i);
            Input input = new Input("radio",entityName,each.getValue());
            input.setID(entityName + "_"+i);
            input.setTagText(each.getName());
            if(i==0) input.setChecked(true);
            Label label = getLabel(input);
            list.add(label);
        }
        return StringUtil.joinTemp(list,",");
    }
    
    public static String checkBox(List<Pair> pairs,String entityName) {
        List<Label> list = new ArrayList<Label>();
        for (int i=0;i<pairs.size();i++) {
            Pair each = pairs.get(i);
            Input input = new Input("checkBox",entityName,each.getValue());
            input.setID(entityName + "_"+i);
            input.setTagText(each.getName());
            if(i==0) input.setChecked(true);
            Label label = getLabel(input);
            list.add(label);
        }
        return StringUtil.joinTemp(list,",");
    }
    
    /**
     * input에 라벨을 씌워 리턴한다.
     */
    public static Label getLabel(Input in) {
        Label label = new Label(in.getAttribute("id"));
        label.setStyle("cursor:pointer");
        label.addElement(in);
        return label;
    }

    
}