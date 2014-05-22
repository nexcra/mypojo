package erwins.util.spring;

import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;

import lombok.Data;

import org.junit.Test;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;




public class FlatDataBinderTest {
	
	@Test
	public void test() throws Exception {
		
		LocalValidatorFactoryBean tempValidator = new LocalValidatorFactoryBean(); 
		tempValidator.afterPropertiesSet();
		
		DefaultConversionService conversionService = new DefaultConversionService();
		conversionService.addConverter(new Converter<String,AtomicLong>() {
			@Override
			public AtomicLong convert(String source) {
				return new AtomicLong(Long.parseLong(source));
			}
		});
		conversionService.addConverter(new Converter<AtomicLong,String>() {
			@Override
			public String convert(AtomicLong source) {
				return String.valueOf(source.get());
			}
		});

		int index = 0;
		List<LineMetadata> lineMetadatas = Lists.newArrayList();
		lineMetadatas.add(new LineMetadata(index++,"s1","문자"));
		lineMetadatas.add(new LineMetadata(index++,"num1","숫자"));
		lineMetadatas.add(new LineMetadata(index++,"fooBar","ENUM"));
		lineMetadatas.add(new LineMetadata(index++,"atomicLong","커스텀에디터"));
		lineMetadatas.add(new LineMetadata(index++,"invalid","예외발생"));
		lineMetadatas.add(new LineMetadata(index++,"bar.name","바 이름"));
		
		lineMetadatas.add(new LineMetadata(index++,"list[1]","리스트2"));
		lineMetadatas.add(new LineMetadata(index++,"foos[0].atomicLong","FooList-커스텀에디터"));
		lineMetadatas.add(new LineMetadata(index++,"foos[0].fooBar","FooList-ENUM"));
		
		FlatDataBinder<Foo> binder = new FlatDataBinder<Foo>();
		binder.setClazz(Foo.class);
		binder.setLineMetadatas(lineMetadatas);
		binder.setConversionService(conversionService);
		binder.afterPropertiesSet();
		
		//List<String[]> lines = Lists.newArrayList();
		
		Foo foo = binder.bind(new String[]{"qwe","123412","FooBar1","354567878","20","홍콩바","list2","337","FooBar2"}, 0);
		Preconditions.checkState(foo.getS1().equals("qwe"));
		Preconditions.checkState(foo.getNum1().equals(123412L));
		Preconditions.checkState(foo.getFooBar() == FooBar.FooBar1);
		Preconditions.checkState(foo.getBar().getName().equals("홍콩바"));
		Preconditions.checkState(foo.getList().get(0)==null);
		Preconditions.checkState(foo.getList().get(1).equals("list2"));
		Preconditions.checkState(foo.getFoos().get(0).getAtomicLong().get() == 337L);
		Preconditions.checkState(foo.getFoos().get(0).getFooBar() == FooBar.FooBar2);
		
		System.out.println("toString " + foo);
		System.out.println("toStringArray "+Joiner.on(",").useForNull("").join(binder.toStringArray(foo)));
		System.out.println("toMap "+binder.toMap(foo));
		
		try {
			binder.bind(new String[]{"qwe",null,"FooBar1","354567878","21","홍콩바","list2","337","FooBar2"}, 0);
		} catch (BindException e) {
			Set<String> errorSet = Sets.newHashSet();
			for(FieldError err : e.getFieldErrors()) errorSet.add(err.getCode());
			Preconditions.checkState(errorSet.contains("Max"));
			Preconditions.checkState(errorSet.contains("NotNull"));
			System.out.println("E : "+errorSet);
		}
		
	}
	
	public static enum FooBar{
		FooBar1,FooBar2;
	}
	
	@Data
	public static class Foo{
		private String s1;
		@NotNull
		private Long num1;
		@Max(20)
		private Long invalid;
		private FooBar fooBar;
		private AtomicLong atomicLong;
		private Bar bar;
		private List<String> list;
		private List<Bar> bars;
		private List<Foo> foos;
	}
	
	@Data
	public static class Bar{
		private String name;
	}

	
	
	
}
