
package erwins.util.webapp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import net.sf.json.JSONObject;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreInputStream;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;

import erwins.util.root.exception.IORuntimeException;
import erwins.util.root.exception.PropagatedRuntimeException;


public abstract class AppJExcell{
	
	public static Collection<JSONObject> uploadFromJSON(HttpServletRequest req,String uploadName) {
		BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
		Map<String,BlobKey> blobs = blobstoreService.getUploadedBlobs(req);
		BlobKey key = blobs.get(uploadName);
		Collection<JSONObject> result = new ArrayList<JSONObject>();
		try {
			Workbook book = Workbook.getWorkbook(new BlobstoreInputStream(key));
			Sheet sheet = book.getSheets()[0];
			int length = sheet.getRows();
			for(int i=1;i<length;i++){ //첫줄 무시.
				Cell[] cells = sheet.getRow(i);
				String jsonString = cells[0].getContents();
				JSONObject json = JSONObject.fromObject(jsonString);
				result.add(json);
			}
		} catch (BiffException e) {
			throw new PropagatedRuntimeException(e);
		} catch (IOException e) {
			throw new IORuntimeException(e);
		}
		return result;
	}
}
