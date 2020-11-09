package ara;

import ara.auth.AuthenticationEventListener;
import ara.models.document;
import ara.models.documentFile;
import ara.models.excelFile;
import ara.models.olap;
import ara.models.olapIndex;
import ara.repository.HistoryRepository;
import ara.repository.excelRepository;
import ara.repository.olapIndexRepository;
import ara.repository.olapRepository;
import ara.repository.documentFileRepository;
import ara.repository.documentRepository;

import org.apache.poi.sl.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

//import java.;
//import java.nio.file.Paths;

@RestController
@EnableScheduling
public class fileController implements WebMvcConfigurer {

    @Autowired
    private documentFileRepository repoDocumentFile;
    @Autowired
    private excelRepository repoExcel;
    @Autowired
    private documentRepository repoDocument;
    @Autowired
    private HistoryRepository repoHistory;
    @Autowired
    private olapRepository repoOlap;
    @Autowired
    private olapIndexRepository repoOlapIndex;


    @RequestMapping(value="/admin/fileList")
    public @ResponseBody List<documentFile> fileList(int documentId){

        try {
            document tempDocument= repoDocument.findDocumentById(documentId);
            List<documentFile> list=repoDocumentFile.findAllByDocument(tempDocument);
            return list;
        }
        catch(Exception ex){
            log("### FILE LIST ###"," User : "+getCurrentUserName()+ " an exception is called");
            return null;
        }
    }

    @RequestMapping(value="/admin/fileRemove")
    public @ResponseBody int fileRemoving(long id){
        

        try {
            // first find the related documentFile
            documentFile document=repoDocumentFile.findDocumentFileById(id);
            // then DELETE PHYSICAL file
            File fileToDelete = new File(
                    document.getPath() );
            fileToDelete.delete();
            //Lastly delete database record of file.
            repoDocumentFile.delete(document);
            log("## PHYSICAL FILE DELETED ##"," User : "+getCurrentUserName()+" "+document.getPath());
            // if deleting file is socussfull return file id
            return (int)id;
        }
        // else return -1
        catch(Exception ex){log("## DELETE PHYSICAL FILE FAILD "," User : "+getCurrentUserName()+" "+ "file id : "+id);return -1;}
    }

    @Value("${ara.uploadPath}")
    private String uploadPath;

    @Value("${ara.uploadExcel}")
    private String uploadExcel;

    @RequestMapping(value = "/admin/uploadFile", method = RequestMethod.POST)
    public documentFile submit(@RequestParam("file") MultipartFile file,@RequestParam("documentIdUpload") String documentId) {

        repoHistory.save(AuthenticationEventListener.userHistory("## UserHistory ## ",
                "Upload:\""+documentId+"\"",AdminController.getCurrentUserName(),4));

        
        String path=uploadPath;
        File f1;
        document Document=repoDocument.findDocumentById(Integer.valueOf(documentId));
        try {
            Random rand = new Random();

            f1 = new File(path +rand.nextInt(10000000)+ file.getOriginalFilename());
            file.transferTo(f1);

            documentFile documentFileTemp = new documentFile();
            documentFileTemp.setEnabled(true);
            documentFileTemp.setPath(f1.getPath());
            documentFileTemp.setName( file.getOriginalFilename());
            documentFileTemp.setDateCreated(new Date());
            documentFileTemp.setDocument(Document);
            repoDocumentFile.save(documentFileTemp);
            log("## Upload a file ## " ," User : "+getCurrentUserName()+" File : "+file.getOriginalFilename());
            return documentFileTemp;
        }
        catch(Exception ex){
            //TODO:Exception
            return null;
        }

        
    }
    @RequestMapping(value = "/admin/uploadExcelFile", method = RequestMethod.POST)
    public excelFile submitExcel(@RequestParam("file") MultipartFile file) {
      
        String path=uploadPath;
        File f1;
        
        try {
            //create randome name
            Random rand = new Random();
            //upload file
            f1 = new File(path +rand.nextInt(10000000)+ file.getOriginalFilename());
            file.transferTo(f1);
            // open the excel file from a given location
            FileInputStream fileStream = new FileInputStream(f1);
            Workbook workbook = new XSSFWorkbook(fileStream);
            //retrieve the first sheet of the file and iterate through each row
            //Sheet sheet =;
            
            int j=0;
            Map<Integer, List<String>> data = new HashMap<>();
            int i = 0;
            for (Row row :  workbook.getSheetAt(0)) {
                data.put(i, new ArrayList<String>());
                for (Cell cell : row) {
                    if(cell!=null && data.get(i)!= null){
                        String temmp=cell.toString().trim().replaceAll("(?<=^\\d+)\\.0*$", "");;
                    data.get(i).add(temmp);
                }
                        
                    j++;
                }
                i++;
            }
            
            // save indexolap
            olapIndex tempOlapIndex=new olapIndex();
            tempOlapIndex.setTitle(data.get(0).get(0));
            tempOlapIndex.setDateRange(data.get(1).get(1));
            tempOlapIndex.setDateCreated(data.get(2).get(0));
            // get extra for ezdevaj-talagh
            if(data.get(2).size()>1 && data.get(2).get(1)!=null && data.get(2).get(1)!="")
                tempOlapIndex.setDateRangeEnd(data.get(2).get(1));
            tempOlapIndex.setType((byte)0);
            tempOlapIndex=repoOlapIndex.save(tempOlapIndex);
            //save olap data
           
            
            for(i=3;i<data.size();i++){
                olap tempOlap=new olap();
                tempOlap.setOlapIndexer(tempOlapIndex);
                tempOlap.setIndexer(i); 
                tempOlap.setD1(data.get(i).get(0));
                tempOlap.setD2(data.get(i).get(1));
                tempOlap.setD3(data.get(i).get(2));
                tempOlap.setD4(data.get(i).get(3));
                tempOlap.setD5(data.get(i).get(4));
                tempOlap.setD6(data.get(i).get(5));
                tempOlap.setD7(data.get(i).get(6));
                if(data.get(i).size()>7){
                    tempOlap.setD8(data.get(i).get(7));
                    tempOlap.setD9(data.get(i).get(8));
                    tempOlap.setD10(data.get(i).get(9));
                    tempOlap.setD11(data.get(i).get(10));
                    tempOlap.setD12(data.get(i).get(11));
                    tempOlap.setD13(data.get(i).get(12));
                    tempOlap.setD14(data.get(i).get(13));
                    tempOlap.setD15(data.get(i).get(14));
                    tempOlap.setD16(data.get(i).get(15));
                    tempOlap.setD17(data.get(i).get(16));
                    tempOlap.setD18(data.get(i).get(17));
                    tempOlap.setD19(data.get(i).get(18));
                    tempOlap.setD20(data.get(i).get(19));
                    if(data.get(i).size()>20){
                        tempOlap.setD21(data.get(i).get(20));
                        tempOlap.setD22(data.get(i).get(21));
                    }
                }
                repoOlap.save(tempOlap);
            }
            

            workbook.close();
            
            // save excel file in db
            excelFile documentFileTemp=new excelFile();
            documentFileTemp.setEnabled(true);
            documentFileTemp.setPath(f1.getPath());
            documentFileTemp.setName( file.getOriginalFilename());
            documentFileTemp.setDateCreated(new Date());
            repoExcel.save(documentFileTemp);
            log("## Upload a excel file ## " ," User : "+getCurrentUserName()+" File : "+file.getOriginalFilename());
            return documentFileTemp;
        }
        catch(Exception ex){
            //TODO:Exception
            log("@@@@@@@@@@@@@@ error @@@@@@@@@@@@@@@@@@",ex.toString());
            return null;
        }

    }
    @RequestMapping(path="/download/file",method = RequestMethod.GET)
    public void downloadPDFResource(HttpServletRequest request, HttpServletResponse response,
                                    @RequestParam Long fileId) throws IOException {
        repoHistory.save(AuthenticationEventListener.userHistory("## UserHistory ## ",
                "Download:\""+fileId+"\"",AdminController.getCurrentUserName(),3));
        File file = new File(repoDocumentFile.findDocumentFileById(fileId).getPath());
        if (file.exists()) {

            //get the mimetype
            String mimeType = URLConnection.guessContentTypeFromName(file.getName());
            if (mimeType == null) {
                //unknown mimetype so set the mimetype to application/octet-stream
                mimeType = "application/octet-stream";
            }

            response.setContentType(mimeType);

            response.setHeader("Content-Disposition", String.format("inline; filename=\"" + file.getName() + "\""));

            //Here we have mentioned it to show as attachment

            response.setContentLength((int) file.length());

            InputStream inputStream = new BufferedInputStream(new FileInputStream(file));

            FileCopyUtils.copy(inputStream, response.getOutputStream());
            log("## Download file ##"," User : "+getCurrentUserName());

        }
    }
    @Scheduled(cron="${ara.excelFilesDeleteCron}")
    public void deleteExcelFiles(){
        log(" schedule "," schedule is fired");
        File file = new File(uploadExcel);
        try{
            FileUtils.cleanDirectory(file); 
        }catch(Exception e){}
    }

    @RequestMapping(path="/download/excel",method = RequestMethod.GET)
    public void downloadExcelResource(HttpServletRequest request, HttpServletResponse response,
                                    @RequestParam Long fileId) throws IOException {
        repoHistory.save(AuthenticationEventListener.userHistory("## UserHistory ## ",
                "Download:\""+fileId+"\"",AdminController.getCurrentUserName(),3));

        File file = new File(uploadExcel+"Report"+fileId+".xlsx");
        if (file.exists()) {

            //get the mimetype
            String mimeType = URLConnection.guessContentTypeFromName(file.getName());
            if (mimeType == null) {
                //unknown mimetype so set the mimetype to application/octet-stream
                mimeType = "application/octet-stream";
            }

            response.setContentType(mimeType);

            response.setHeader("Content-Disposition", String.format("inline; filename=\"" + file.getName() + "\""));

            //Here we have mentioned it to show as attachment

            response.setContentLength((int) file.length());

            InputStream inputStream = new BufferedInputStream(new FileInputStream(file));

            FileCopyUtils.copy(inputStream, response.getOutputStream());
            log("## Download excel ##"," User : " + getCurrentUserName());
            file.delete();

        }
    }
    private void log(String  head,String log){
        Calendar cal = Calendar.getInstance();
        System.out.println(head + " #"+cal.getTime()+" : "+log);
        return;
    }
    public static String getCurrentUserName(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
}

}
