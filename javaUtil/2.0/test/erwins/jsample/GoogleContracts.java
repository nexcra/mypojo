package erwins.jsample;

import java.util.List;
import java.util.Map.Entry;

import org.junit.Test;

import erwins.test.MyAccount;
import erwins.util.openApi.GoogleContract;
import erwins.util.openApi.GoogleContract.ContractEntry;

/** 구글 데이터의 연락처 보기 샘플. */
public class GoogleContracts {

	@Test
	public void test(){
		GoogleContract c = new GoogleContract(MyAccount.GOOGLE.ID, MyAccount.GOOGLE.PASS);
		c.load();
		System.out.println(c.getTitle());
		
		for(Entry<String, List<ContractEntry>> each : c.getContracts()){
			System.out.println("===== "+each.getKey());
			for(ContractEntry entry : each.getValue()){
				System.out.println(entry.getName());
				System.out.println(entry.getPhoneNumber());
			}
		}
		System.out.println(c.getSkiped());
	}


}
