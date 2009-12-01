
package erwins.util.vender.spring;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.document.AbstractPdfView;

import com.lowagie.text.Document;
import com.lowagie.text.pdf.PdfWriter;

@Component
public class PdfView extends AbstractPdfView {

    @SuppressWarnings("unchecked")
    @Override
    protected void buildPdfDocument(Map model, Document document, PdfWriter writer, HttpServletRequest request, HttpServletResponse response){
/*        List<SessionInfo> UserInfos = (List<SessionInfo>) model.get("UserInfos");
        Table table = new Table(2, UserInfos.size() + 1);
        table.setPadding(5);

        BaseFont bfKorean = BaseFont.createFont("c:\\windows\\fonts\\batang.ttc,0", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);

        Font font = new Font(bfKorean);
        Cell cell = new Cell(new Paragraph("순위", font));
        cell.setHeader(true);
        table.addCell(cell);
        cell = new Cell(new Paragraph("페이지", font));
        table.addCell(cell);
        table.endHeaders();

        for (SessionInfo user : UserInfos) {
            //table.addCell(user.getUserId());
            //table.addCell(user.getName());
        }
        document.add(table);*/
    }

}
