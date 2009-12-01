package erwins.util.vender.spring;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.*;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.document.AbstractExcelView;


@Component
public class ExcellView extends AbstractExcelView {

    @SuppressWarnings("unchecked")
    @Override
    protected void buildExcelDocument(Map model, HSSFWorkbook workbook, HttpServletRequest request, HttpServletResponse response)
           {
        HSSFSheet sheet = createFirstSheet(workbook);
        createColumnLabel(sheet);

 /*       List<SessionInfo> UserInfos = (List<SessionInfo>) model.get("UserInfos");
        short rowNum = 1;
        for (SessionInfo rank : UserInfos) {
            createUserInfoRow(sheet, rank, rowNum++);
        }*/
    }

    private HSSFSheet createFirstSheet(HSSFWorkbook workbook) {
        HSSFSheet sheet = workbook.createSheet();
        workbook.setSheetName(0, "페이지 순위", HSSFWorkbook.ENCODING_UTF_16);
        sheet.setColumnWidth((short) 1, (short) (256 * 20));
        return sheet;
    }

    private void createColumnLabel(HSSFSheet sheet) {
        HSSFRow firstRow = sheet.createRow((short) 0);
        HSSFCell cell = firstRow.createCell((short) 0);
        cell.setEncoding(HSSFCell.ENCODING_UTF_16);
        cell.setCellValue("순위");

        cell = firstRow.createCell((short) 1);
        cell.setEncoding(HSSFCell.ENCODING_UTF_16);
        cell.setCellValue("페이지");
    }

/*    private void createUserInfoRow(HSSFSheet sheet, SessionInfo rank, short rowNum) {
        HSSFRow row = sheet.createRow(rowNum);
        HSSFCell cell = row.createCell((short) 0);
        cell.setEncoding(HSSFCell.ENCODING_UTF_16);
        //cell.setCellValue(rank.getUserId());

        cell = row.createCell((short) 1);
        cell.setEncoding(HSSFCell.ENCODING_UTF_16);
        //cell.setCellValue(rank.getName());

    }*/
}
