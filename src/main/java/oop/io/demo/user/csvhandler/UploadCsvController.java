package oop.io.demo.user.csvhandler;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


/**
 * 
 * Copyright by https://loizenai.com
 * @author loizenai.com
 *
 */

@RestController
@RequestMapping("/uploadcsv")
public class UploadCsvController {
    @Autowired
	CsvHandlerService csvHandlerService;

	@PostMapping("/users")
	public Response uploadSingleCSVFile(@RequestParam("csvfile") MultipartFile csvfile) {

		// Checking the upload-file's name before processing
		if (csvfile.getOriginalFilename().isEmpty()) {
			return new Response(csvfile.getOriginalFilename(), 
			"No selected file to upload! Please select a File", 
			"fail");
		}
	
		// checking the upload file's type is CSV or NOT
		if(!ApacheCommonsCsvUtil.isCSVFile(csvfile)) { 
	        return new Response(csvfile.getOriginalFilename(), "Error: this is not a CSV file! Please check your file type.", "fail");
		}
		  
		try {
			// save file data to database
			ArrayList<String> messages= csvHandlerService.store(csvfile.getInputStream());
			Response response = new Response(csvfile.getOriginalFilename(), "Uploaded File Successfully!", "ok");
			for(String m: messages){
				response.addMessage(m);
			}
			return response;
		} catch (Exception e) {
			return new Response(csvfile.getOriginalFilename(), e.getMessage(), "fail");
		}
	
	}
}
