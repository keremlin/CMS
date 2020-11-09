package ara.excel;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;

import ara.models.olap;
import ara.models.olapIndex;



public class writeExcel {

   
    //private String uploadPath;

    public  String write(olapIndex OlapIndex,List<olap> listOlap,String uploadPath){
        // create excell file
        //uploadPath="E:\\pic\\";
        // create the header 
        Workbook workbook = new XSSFWorkbook();
 
        Sheet sheet = workbook.createSheet("Persons");
        sheet.setRightToLeft(true);
        sheet.setColumnWidth(0, 6000);
        sheet.setColumnWidth(1, 4000);
        
        // set the olap index on top
        XSSFFont fontTop = ((XSSFWorkbook) workbook).createFont();
        fontTop.setFontName("Calibri");
        fontTop.setFontHeightInPoints((short) 11);
        fontTop.setBold(true);

        Row headerOlap = sheet.createRow(0);
        Cell headerCellOlap = headerOlap.createCell(3);
        CellStyle TopStyle = workbook.createCellStyle();
        TopStyle.setFont(fontTop);
        headerCellOlap.setCellStyle(TopStyle);

        headerCellOlap.setCellValue(OlapIndex.getTitle());
        headerOlap = sheet.createRow(1);
        headerCellOlap = headerOlap.createCell(1);
        headerCellOlap.setCellStyle(TopStyle);
        headerCellOlap.setCellValue(OlapIndex.getDateCreated());
        headerOlap = sheet.createRow(2);
        headerCellOlap = headerOlap.createCell(1);
        headerCellOlap.setCellStyle(TopStyle);
        headerCellOlap.setCellValue(OlapIndex.getDateRange()+" "+OlapIndex.getDateRangeEnd());
        //headerCellOlap.setCellStyle(headerStyle);
        
        // set the header of table
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        //headerStyle.setBorderRight(BorderStyle.THIN);
        
        XSSFFont font = ((XSSFWorkbook) workbook).createFont();
        font.setFontName("Calibri");
        font.setFontHeightInPoints((short) 11);
        font.setBold(true);
        headerStyle.setFont(font);
        int limiter=3;
        if(isDigit(listOlap.get(2).getD2()))
            limiter=2;

        for(int i=0;i<limiter;i++){
            Row header = sheet.createRow(i+3);
            
            Cell headerCell = header.createCell(0);
            headerCell.setCellValue(listOlap.get(i).getD1());
            headerCell.setCellStyle(styler(workbook,listOlap.get(i).getD1()));
            
            headerCell = header.createCell(1);
            headerCell.setCellValue(listOlap.get(i).getD2());
            headerCell.setCellStyle(styler(workbook,listOlap.get(i).getD2()));
            
            headerCell = header.createCell(2);
            headerCell.setCellValue(listOlap.get(i).getD3());
            headerCell.setCellStyle(styler(workbook,listOlap.get(i).getD3()));
            

            headerCell = header.createCell(3);
            headerCell.setCellValue(listOlap.get(i).getD4());
            headerCell.setCellStyle(styler(workbook,listOlap.get(i).getD4()));
            //System.out.println("border bottom : "+i + " :"+headerStyle.getBorderBottom()+"");
            headerCell = header.createCell(4);
            headerCell.setCellValue(listOlap.get(i).getD5());
            headerCell.setCellStyle(styler(workbook,listOlap.get(i).getD5()));
            
            headerCell = header.createCell(5);
            headerCell.setCellValue(listOlap.get(i).getD6());
            headerCell.setCellStyle(styler(workbook,listOlap.get(i).getD6()));
            
            headerCell = header.createCell(6);
            headerCell.setCellValue(listOlap.get(i).getD7());
            headerCell.setCellStyle(styler(workbook,listOlap.get(i).getD7()));

            headerCell = header.createCell(7);
            headerCell.setCellValue(listOlap.get(i).getD8());
            headerCell.setCellStyle(styler(workbook,listOlap.get(i).getD8()));

            headerCell = header.createCell(8);
            headerCell.setCellValue(listOlap.get(i).getD9());
            headerCell.setCellStyle(styler(workbook,listOlap.get(i).getD9()));

            headerCell = header.createCell(9);
            headerCell.setCellValue(listOlap.get(i).getD10());
            headerCell.setCellStyle(styler(workbook,listOlap.get(i).getD10()));

            headerCell = header.createCell(10);
            headerCell.setCellValue(listOlap.get(i).getD11());
            headerCell.setCellStyle(styler(workbook,listOlap.get(i).getD11()));

            headerCell = header.createCell(11);
            headerCell.setCellValue(listOlap.get(i).getD12());
            headerCell.setCellStyle(styler(workbook,listOlap.get(i).getD12()));

            headerCell = header.createCell(12);
            headerCell.setCellValue(listOlap.get(i).getD13());
            headerCell.setCellStyle(styler(workbook,listOlap.get(i).getD13()));

            headerCell = header.createCell(13);
            headerCell.setCellValue(listOlap.get(i).getD14());
            headerCell.setCellStyle(styler(workbook,listOlap.get(i).getD14()));

            headerCell = header.createCell(14);
            headerCell.setCellValue(listOlap.get(i).getD15());
            headerCell.setCellStyle(styler(workbook,listOlap.get(i).getD15()));

            headerCell = header.createCell(15);
            headerCell.setCellValue(listOlap.get(i).getD16());
            headerCell.setCellStyle(styler(workbook,listOlap.get(i).getD16()));

            headerCell = header.createCell(16);
            headerCell.setCellValue(listOlap.get(i).getD17());
            headerCell.setCellStyle(styler(workbook,listOlap.get(i).getD17()));

            headerCell = header.createCell(17);
            headerCell.setCellValue(listOlap.get(i).getD18());
            headerCell.setCellStyle(styler(workbook,listOlap.get(i).getD18()));

            headerCell = header.createCell(18);
            headerCell.setCellValue(listOlap.get(i).getD19());
            headerCell.setCellStyle(styler(workbook,listOlap.get(i).getD19()));

            headerCell = header.createCell(19);
            headerCell.setCellValue(listOlap.get(i).getD20());
            headerCell.setCellStyle(styler(workbook,listOlap.get(i).getD20()));

            headerCell = header.createCell(20);
            headerCell.setCellValue(listOlap.get(i).getD21());
            headerCell.setCellStyle(styler(workbook,listOlap.get(i).getD21()));

            headerCell = header.createCell(21);
            headerCell.setCellValue(listOlap.get(i).getD22());
            headerCell.setCellStyle(styler(workbook,listOlap.get(i).getD22()));
        }
       
        
        

        // body of table
        CellStyle style = workbook.createCellStyle();
        XSSFFont fontBody = ((XSSFWorkbook) workbook).createFont();
        font.setFontName("Calibri");
        font.setFontHeightInPoints((short) 11);
        font.setBold(false);
        style.setWrapText(true);
        style.setFont(fontBody);
        for(int j=limiter;j<listOlap.size();j++){
            
            Row row = sheet.createRow(j+3);

            Cell headerCell = row.createCell(0);
            headerCell.setCellValue(listOlap.get(j).getD1());
            headerCell.setCellStyle(style);

            headerCell = row.createCell(1);
            headerCell=selectCell(headerCell,listOlap.get(j).getD2());
            headerCell.setCellStyle(style);

            headerCell = row.createCell(2);
            headerCell=selectCell(headerCell,listOlap.get(j).getD3());
            headerCell.setCellStyle(style);

            headerCell = row.createCell(3);
            headerCell=selectCell(headerCell,listOlap.get(j).getD4());
            headerCell.setCellStyle(style);

            headerCell = row.createCell(4);
            headerCell=selectCell(headerCell,listOlap.get(j).getD5());
            headerCell.setCellStyle(style);

            headerCell = row.createCell(5);
            headerCell=selectCell(headerCell,listOlap.get(j).getD6());
            headerCell.setCellStyle(style);

            headerCell = row.createCell(6);
            headerCell=selectCell(headerCell,listOlap.get(j).getD7());
            headerCell.setCellStyle(style);

            headerCell = row.createCell(7);
            headerCell=selectCell(headerCell,listOlap.get(j).getD8());
            headerCell.setCellStyle(style);

            headerCell = row.createCell(8);
            headerCell=selectCell(headerCell,listOlap.get(j).getD9());
            headerCell.setCellStyle(style);

            headerCell = row.createCell(9);
            headerCell=selectCell(headerCell,listOlap.get(j).getD10());
            headerCell.setCellStyle(style);

            headerCell = row.createCell(10);
            headerCell=selectCell(headerCell,listOlap.get(j).getD11());
            headerCell.setCellStyle(style);

            headerCell = row.createCell(11);
            headerCell=selectCell(headerCell,listOlap.get(j).getD12());
            headerCell.setCellStyle(style);

            headerCell = row.createCell(12);
            headerCell=selectCell(headerCell,listOlap.get(j).getD13());
            headerCell.setCellStyle(style);

            headerCell = row.createCell(13);
            headerCell=selectCell(headerCell,listOlap.get(j).getD14());
            headerCell.setCellStyle(style);

            headerCell = row.createCell(14);
            headerCell=selectCell(headerCell,listOlap.get(j).getD15());
            headerCell.setCellStyle(style);

            headerCell = row.createCell(15);
            headerCell=selectCell(headerCell,listOlap.get(j).getD16());
            headerCell.setCellStyle(style);

            headerCell = row.createCell(16);
            headerCell=selectCell(headerCell,listOlap.get(j).getD17());
            headerCell.setCellStyle(style);

            headerCell = row.createCell(17);
            headerCell=selectCell(headerCell,listOlap.get(j).getD18());
            headerCell.setCellStyle(style);

            headerCell = row.createCell(18);
            headerCell=selectCell(headerCell,listOlap.get(j).getD19());
            headerCell.setCellStyle(style);

            headerCell = row.createCell(19);
            headerCell=selectCell(headerCell,listOlap.get(j).getD20());
            headerCell.setCellStyle(style);

            headerCell = row.createCell(20);
            headerCell=selectCell(headerCell,listOlap.get(j).getD21());
            headerCell.setCellStyle(style);

            headerCell = row.createCell(21);
            headerCell=selectCell(headerCell,listOlap.get(j).getD22());
            headerCell.setCellStyle(style);
        }
        
        
       

        // write the content to file

        //File currDir = new File(uploadPath);
        //String path = currDir.getAbsolutePath();
        String tempExcelFileID=""+(new java.util.Date().getTime());
        String fileLocation = uploadPath + "Report"+tempExcelFileID+".xlsx";
        
        try{
            FileOutputStream outputStream = new FileOutputStream(fileLocation);
            workbook.write(outputStream);
            workbook.close();
        }catch(Exception ex){return null;}
        

        return tempExcelFileID;
    }
    private static Cell selectCell(Cell item,String value){
        if(isDigit(value)){
            item.setCellValue(Integer.parseInt(value));
        }else{
            item.setCellValue(value);
        }
        
        return item;
    }
    private static boolean isDigit(String input) {

        // null or length < 0, return false.
        if (input == null || input.length() < 0)
            return false;

        // empty, return false
        input = input.trim();
        if ("".equals(input))
            return false;

        if (input.startsWith("-")) {
            // negative number in string, cut the first char
            return input.substring(1).matches("[0-9]*");
        } else {
            // positive number, good, just check
            return input.matches("[0-9]*");
        }

    }
    private static CellStyle styler(Workbook workbook ,String data ){
        CellStyle headerStyle=workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        //headerStyle.setBorderRight(BorderStyle.THIN);
        
        XSSFFont font = ((XSSFWorkbook) workbook).createFont();
        font.setFontName("Calibri");
        font.setFontHeightInPoints((short) 11);
        font.setBold(true);
        headerStyle.setFont(font);
        
        if(data==null || data=="" || data==" "){
             headerStyle.setBorderLeft(BorderStyle.NONE);
        }
        else{
            headerStyle.setBorderLeft(BorderStyle.THIN);
           
        }
        
        return headerStyle;

    }

}
