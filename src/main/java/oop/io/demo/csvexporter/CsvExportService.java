/**
 * Contains methods called by CsvExporterController to export the following as Maps:
 * 1. Loans writeLoansToJSON()
 * 2. Attractions writeAttractionsToJSON()
 * 3. Passes writePassesToJSON()
 * 4. Users writeUsersToJSON()
 */

package oop.io.demo.csvexporter;

import org.springframework.stereotype.Service;

import java.util.*;

import oop.io.demo.loan.Loan;
import oop.io.demo.loan.LoanRepository;
import oop.io.demo.attraction.Attraction;
import oop.io.demo.attraction.AttractionRepository;
import oop.io.demo.user.User;
import oop.io.demo.user.UserRepository;
import oop.io.demo.pass.Pass;
import oop.io.demo.pass.PassRepository;

import java.io.*;
import java.net.URL;
import java.nio.file.Paths;
import java.net.MalformedURLException;
import org.apache.commons.io.FileUtils;

@Service
public class CsvExportService {

    private final LoanRepository loanRepository;
    private final AttractionRepository attractionRepository;
    private final UserRepository userRepository;
    private final PassRepository passRepository;

    public CsvExportService(LoanRepository loanRepository, AttractionRepository attractionRepository, UserRepository userRepository, PassRepository passRepository) {
        this.loanRepository = loanRepository;
        this.attractionRepository = attractionRepository;
        this.userRepository = userRepository;
        this.passRepository = passRepository;
    }

    //Export Loans as Map
    public Map<Integer,Map<String,String>> writeLoansToJSON() throws Exception{
        Map<Integer,Map<String,String>> output = new TreeMap<>();
        try{    
            List<Loan> loans = loanRepository.findAll();

            int counter = 0;
            for (Loan loan : loans) {
                Map<String,String> temp = new TreeMap();
                temp.put("LoanId", loan.getLoanID());
                temp.put("LoanDate",loan.getLoanDate().toString());
                temp.put("AttractionName", loan.getAttractionName());
                temp.put("UserEmail", loan.getUserEmail());
                temp.put("ContactNo", loan.getContactNo());
                temp.put("PassNo", loan.getPassId());
                temp.put("Status", loan.getStatus().toString());
                output.put(counter, temp);
                counter++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return output;
    }

    //Export Attractions as Map
    public Map<Integer,Map<String,String>> writeAttractionsToJSON() throws Exception{
        Map<Integer,Map<String,String>> output = new TreeMap<>();
        try{    
            List<Attraction> attractions = attractionRepository.findAll();

            int counter = 0;
            for (Attraction attraction : attractions) {
                Map<String,String> temp = new TreeMap();
                temp.put("AttractionName", attraction.getAttractionName());
                temp.put("ReplacementFee",String.valueOf(attraction.getReplacementFee()));
                temp.put("PassType", attraction.getPassType().toString());
                temp.put("isActive", String.valueOf(attraction.isActive()));
                temp.put("Template", attraction.getTemplateFilename());
                temp.put("Image", attraction.getImageFilename());
                temp.put("Attachment", attraction.getAttachmentPDFFilename());
                output.put(counter, temp);
                counter++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return output;
    }
            
    //Export Passes as Map
    public Map<Integer,Map<String,String>> writePassesToJSON() throws Exception{
        Map<Integer,Map<String,String>> output = new TreeMap<>();
        try{    
            List<Pass> passes = passRepository.findAll();

            int counter = 0;
            for (Pass pass : passes) {
                Map<String,String> temp = new TreeMap();
                temp.put("PassId", pass.getPassId());
                temp.put("PassNo",pass.getPassNo());
                temp.put("AttractionName", pass.getAttractionName());
                temp.put("PassStatus", pass.getPassStatus().toString());
                output.put(counter, temp);
                counter++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return output;
    }

    //Export Users as Map
    public Map<Integer,Map<String,String>> writeUsersToJSON() throws Exception{
        Map<Integer,Map<String,String>> output = new TreeMap<>();
        try{    
            List<User> users = userRepository.findAll();

            int counter = 0;
            for (User user : users) {
                Map<String,String> temp = new TreeMap();
                temp.put("Email", user.getEmail());
                temp.put("Name",user.getName());
                temp.put("ContactNo", user.getContactNo());
                temp.put("UserType", user.getUserType().toString());
                temp.put("isVerified", String.valueOf(user.isVerified()));
                temp.put("OutstandingFees", String.valueOf(user.getOutstandingFees()));
                output.put(counter, temp);
                counter++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return output;
    }

        //Export Email Templates
        public String exportEmail(String attractionName) throws Exception{
            try {
                //Get webpage
                Attraction attraction = attractionRepository.findByAttractionName(attractionName).get();
                String templateName = attraction.getTemplateFilename();
                String templatePath = attraction.getTemplateFilePath();

                String webpage = templatePath + "/" + templateName;

                URL url = new File(getClass().getResource(webpage).getFile()).toURI().toURL();
                String strUrl1 = url.toString().replace("target/classes", "src/main/resources");
                String strUrl2 = strUrl1.toString().replaceAll("%2520", "%20");

                URL newUrl = new URL(strUrl2);
                 
                // Create URL object
                BufferedReader readr = 
                  new BufferedReader(new InputStreamReader(newUrl.openStream()));
      
                // Enter filename in which you want to download
                //Specify the file name and path here
                String writeFilePath = (newUrl.getPath().split("Documents"))[0] + "Desktop/" + templateName;
                File file = new File(writeFilePath);

                /* This logic will make sure that the file 
                * gets created if it is not present at the
                * specified location*/
                if (!file.exists()) {
                    file.createNewFile();
                }

                BufferedWriter writer = 
                  new BufferedWriter(new FileWriter(file));
                  
                // read each line from stream till end
                String line;
                while ((line = readr.readLine()) != null) {
                    writer.write(line);
                }
      
                readr.close();
                writer.close();
                
                return "Successfully Downloaded Email Template.";
            }
      
            // Exceptions
            catch (MalformedURLException mue) {
                return "Malformed URL Exception raised";
            }
            catch (IOException ie) {
                ie.printStackTrace();
                return "IOException raised";
            }
        }
        
        //Export Attachment
        public String exportAttachment(String attractionName) throws Exception{
            try {
                //Get webpage
                Attraction attraction = attractionRepository.findByAttractionName(attractionName).get();
                String attachmentName = attraction.getAttachmentPDFFilename();
                String attachmentPath = attraction.getAttachmentPDFFilePath();

                String webpage = attachmentPath + "/" + attachmentName;

                File source = new File(getClass().getResource(webpage).getFile().replaceAll("%20", " "));
                String srcLink = source.getAbsolutePath();
                String writeFilePath = (srcLink.split("Documents"))[0] + "Desktop\\" + attachmentName;
                File dest = new File(writeFilePath);

                FileUtils.copyFile(source, dest);
                
                return "Successfully Downloaded Attachment Template.";
            }
      
            // Exceptions
            catch (MalformedURLException mue) {
                return "Malformed URL Exception raised";
            }
            catch (IOException ie) {
                ie.printStackTrace();
                return "IOException raised";
            }
        }

        //Export Image
        public String exportImage(String attractionName) throws Exception{
            try {
                //Get webpage
                Attraction attraction = attractionRepository.findByAttractionName(attractionName).get();
                String imageName = attraction.getImageFilename();
                String imagePath = attraction.getImageFilePath().replace("/src/main/resources","");

                File source = new File(getClass().getResource(imagePath).getFile().replaceAll("%20", " "));
                String srcLink = source.getAbsolutePath();
                String writeFilePath = (srcLink.split("Documents"))[0] + "Desktop\\" + imageName;
                File dest = new File(writeFilePath);

                FileUtils.copyFile(source, dest);
                
                return "Successfully Downloaded Image Template.";
            }
      
            // Exceptions
            catch (MalformedURLException mue) {
                return "Malformed URL Exception raised";
            }
            catch (IOException ie) {
                ie.printStackTrace();
                return "IOException raised";
            }
        }
}