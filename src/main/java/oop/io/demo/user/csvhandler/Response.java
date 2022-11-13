package oop.io.demo.user.csvhandler;

import java.util.ArrayList;

/**
* Copyright by https://loizenai.com
* @author loizenai.com
*
*/
public class Response {
   private String filename;
   private ArrayList<String> messages;
   private String status;
   
   public Response(){}

   public Response(String filename, String message, String status){
    this.filename = filename;
    this.messages = new ArrayList<>();
    this.messages.add(message);
    this.status = status;
   }

   public void setFilename(String filename) {
       this.filename = filename;
   }
   
   public String getFilename() {
       return this.filename;
   }
   
   public void addMessage(String message) {
       this.messages.add(message);
   }
   
   public ArrayList<String> getMessage() {
       return this.messages;
   }
   
   public void setStatus(String status) {
       this.status = status;
   }
   
   public String getStatus() {
       return this.status;
   }
}