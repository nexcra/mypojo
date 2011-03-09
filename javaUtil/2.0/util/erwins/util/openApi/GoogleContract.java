package erwins.util.openApi;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.google.api.gbase.client.GoogleBaseEntry;
import com.google.gdata.client.Query;
import com.google.gdata.client.contacts.ContactsService;
import com.google.gdata.data.contacts.ContactEntry;
import com.google.gdata.data.contacts.ContactFeed;
import com.google.gdata.data.contacts.GroupMembershipInfo;
import com.google.gdata.data.extensions.Email;
import com.google.gdata.data.extensions.Name;
import com.google.gdata.data.extensions.PhoneNumber;

import erwins.util.collections.MapForList;
import erwins.util.collections.MapType;


/** 구글 데이터의 연락처  */
public class GoogleContract{
	
	private static final int LIMIT_SIZE = 20;
	private final String googleId;
	private final String googlePass;
	
	public GoogleContract(String googleId,String googlePass){
		this.googleId = googleId;
		this.googlePass = googlePass;
	}
	
	public void load(){
		ContactsService service = new ContactsService("Contract");
		try {
			ContactFeed resultFeed = login(service);
			buildConttact(service, resultFeed);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void buildConttact(ContactsService service, ContactFeed resultFeed) throws Exception{
		Map<String,String> labelCache = new TreeMap<String,String>();
		
		for (ContactEntry feed : resultFeed.getEntries()) {
			
			Name name = feed.getName();
			if (!feed.hasName() || !name.hasFullName()){
				skiped++;
				continue;
			}
			
			ContractEntry entry = new ContractEntry();
			entry.name = name.getFullName().getValue();
			for (Email each : feed.getEmailAddresses())  entry.emails.add(each.getAddress());
			
			String cont = null;
			try {
				cont = feed.getPlainTextContent();
			} catch (IllegalStateException e) {
				//무시한다. 버그인듯.
			}
			entry.memo = cont; 
			
			for (PhoneNumber property : feed.getPhoneNumbers()) entry.phoneNumber.add(property.getPhoneNumber());
			
			for (GroupMembershipInfo each : feed.getGroupMembershipInfos()) {
				String href = each.getHref();
				String label = labelCache.get(href);
				if(label==null){
					URL groupMemberUrl  = new URL(href);
					GoogleBaseEntry baseEntry = service.getEntry(groupMemberUrl, GoogleBaseEntry.class);
					label = baseEntry.getPlainTextContent();
					labelCache.put(href, label);	
				}
				if(label.equals("System Group: My Contacts")) continue; //기본라벨 무시.
				contracts.add (label,entry);
			}
		}
	}

	private ContactFeed login(ContactsService service) throws Exception{
		service.setUserCredentials(googleId, googlePass);
		URL url = new URL("http://www.google.com/m8/feeds/contacts/"+googleId+"/full");
		Query query = new Query(url);
		query.setMaxResults(LIMIT_SIZE);
		ContactFeed resultFeed = service.getFeed(query, ContactFeed.class);
		this.title =  resultFeed.getTitle().getPlainText();
		return resultFeed;
	}
	
	private String title;
	private int skiped;
	private MapForList<ContractEntry> contracts = new MapForList<ContractEntry>(MapType.Tree);
	
	public class ContractEntry{
		private String name;
		private List<String> emails = new ArrayList<String>();
		private List<String> phoneNumber = new ArrayList<String>();
		private String memo;
		public String getName() {
			return name;
		}
		public List<String> getEmails() {
			return emails;
		}
		public List<String> getPhoneNumber() {
			return phoneNumber;
		}
		public String getMemo() {
			return memo;
		}
	}

	public String getTitle() {
		return title;
	}

	public int getSkiped() {
		return skiped;
	}

	public MapForList<ContractEntry> getContracts() {
		return contracts;
	}

}
