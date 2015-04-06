package erwins.util.spring.web;

import java.util.List;
import java.util.Map;

import erwins.util.root.EnumDescription;


/** 
 * EL에서 MAP 형식으로 Enum을 HTML을 구성하도록 도와주는 헬퍼
 * jsp 자체를 잘 안쓰는 추세임으로 사용을 지양하자.
 * ex) <form:select path="password" items="${enum['BulkUploadDiv']}" />
 * ex) <form:select path="password" items="${enum['{name=BulkUploadDiv,eq={aa1:true},start={전체:all}}']}" />
 * */ 
public class EnumSpringTagMapSupport extends AbstractSpringTagMapSupport<Enum<?>>{
	
	private EnumFinder enumFinder;

	@Override
	protected List<Enum<?>> findByName(String name) {
		return enumFinder.findBySimpleName(name);
	}

	@Override
	protected void addToMap(Map<String, String> tag, Enum<?> each) {
		String name = each.name();
		if(each instanceof EnumDescription){
			EnumDescription nv = (EnumDescription) each;
			tag.put(name, nv.getDescription()); // ID / NAME 순이다.
		}else tag.put(name, name);		
	}

	public EnumFinder getEnumFinder() {
		return enumFinder;
	}

	public void setEnumFinder(EnumFinder enumFinder) {
		this.enumFinder = enumFinder;
	}
	

}

