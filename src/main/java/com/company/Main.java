package com.company;


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.w3c.dom.ls.LSOutput;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLOutput;
import java.text.SimpleDateFormat;
import java.util.*;

public class Main {

    private static final String COMMA_DELIMITER = ",";
    private static final String TITLE_REPLACE = "{{TITLE}}";
    private static final String FIRST_NAME_REPLACE = "{{FIRST_NAME}}";
    private static final String LAST_NAME_REPLACE = "{{LAST_NAME}}";
    private static final String TODAY_REPLACE = "{{TODAY}}";
    private static final String CSV_EXTENTION = "csv";
    private static final String JSON_EXTENTION = "json";

    public static void main(String[] args) {

        String emailTemplate = null, customerFilePath = null, outputEmailPath = null, outputErrorPath = null;
        System.out.print("Enter your email template : ");
        emailTemplate = inputPathFile(JSON_EXTENTION);
        System.out.print("Enter your customer file path : ");
        customerFilePath = inputPathFile(CSV_EXTENTION);
        System.out.print("Output email path : ");
        outputEmailPath = inputPathDirectory();
        System.out.print("Output errors csv path : ");
        outputErrorPath = inputPathFile(CSV_EXTENTION);


        List<List<String>> dataCustomer = readFileCSV(customerFilePath);

        EmailTemplate email = readFileEmailTemplate(emailTemplate);

        List<Customer> listCustomer = getListCustomer(dataCustomer);

        writeOutputEmail(email,listCustomer,outputEmailPath,outputErrorPath);

    }

    public static String inputPathFile(String extention){
        Scanner input = new Scanner(System.in);
        String path = input.nextLine();

        while(!isValidPathFile(path) || !getFileExtension(path).equals(extention)) {
            System.err.print("ERROR Please enter a valid path file with extention ."+extention+" : ");
            path = input.nextLine();
        }
        return path;
    }

    public static String getFileExtension(String filePath) {
        int lastIndexOf = filePath.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return ""; // empty extension
        }
        return filePath.substring(lastIndexOf + 1);
    }

    public static String inputPathDirectory(){
        Scanner input = new Scanner(System.in);
        String path = input.nextLine();

        while(!isValidPathDirectory(path)) {
            System.err.print("ERROR Please enter a valid path directory : ");
            path = input.nextLine();
        }
        return path;
    }

    public static boolean isValidPathFile(String path) {
        return Files.isRegularFile(Paths.get(path));
    }

    public static boolean isValidPathDirectory(String path) {
        return Files.isDirectory(Paths.get(path));
    }

    public static void writeOutputEmail(EmailTemplate email, List<Customer> listCustomer,String outputEmailPath,String outputErrorPath){
        SimpleDateFormat today = new SimpleDateFormat("dd MMM yyyy");
        List<Customer> errorlist = new ArrayList<Customer>();
        for(Customer customer : listCustomer){
            EmailTemplate saveEmail = new EmailTemplate();

            if(customer.getEmail() != null && !customer.getEmail().isBlank() && !customer.getEmail().isEmpty()) {
                saveEmail.setTo(customer.getEmail());
            }else{
                errorlist.add(customer);
                continue;
            }

            saveEmail.setFrom(email.getFrom());
            saveEmail.setMimeType(email.getMimeType());
            saveEmail.setSubject(email.getSubject());

            String newBody = email.getBody().replace(TITLE_REPLACE,customer.getTitle())
                    .replace(FIRST_NAME_REPLACE,customer.getFirstName())
                    .replace(LAST_NAME_REPLACE,customer.getLastName())
                    .replace(TODAY_REPLACE,today.format(Calendar.getInstance().getTime()));
            saveEmail.setBody(newBody);

            JSONObject emailOutput = saveEmail.toJSON();

            String outputPath = outputEmailPath+"/"+customer.getFirstName().toLowerCase()+customer.getLastName().toLowerCase()+".json";

            //write to file
            WriteToFile(emailOutput.toJSONString(),outputPath);

        }
        if(!errorlist.isEmpty()){
            writeErrorsOutput(errorlist,outputErrorPath);
        }
    }

    public static void writeErrorsOutput(List<Customer> errorlist,String outputErrorPath){
        try
        {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputErrorPath), "UTF-8"));

            StringBuffer firstLine = new StringBuffer();
            firstLine.append(EmailTemplateEnum.TITLE);
            firstLine.append(COMMA_DELIMITER);
            firstLine.append(EmailTemplateEnum.FIRST_NAME);
            firstLine.append(COMMA_DELIMITER);
            firstLine.append(EmailTemplateEnum.LAST_NAME);
            firstLine.append(COMMA_DELIMITER);
            firstLine.append(EmailTemplateEnum.EMAIL);
            bw.write(firstLine.toString());
            bw.newLine();

            for (Customer customer : errorlist)
            {
                StringBuffer oneLine = new StringBuffer();
                oneLine.append(customer.getTitle() == null ? "" :customer.getTitle());
                oneLine.append(COMMA_DELIMITER);
                oneLine.append(customer.getFirstName() == null ? "" :customer.getFirstName());
                oneLine.append(COMMA_DELIMITER);
                oneLine.append(customer.getLastName() == null ? "" :customer.getLastName());
                oneLine.append(COMMA_DELIMITER);
                oneLine.append(customer.getEmail() == null ? "" : customer.getEmail());
                bw.write(oneLine.toString());
                bw.newLine();
            }
            bw.flush();
            bw.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void WriteToFile(String data, String path){
        try(FileWriter fileWriter = new FileWriter(path)){

            fileWriter.write(data);

        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static List<Customer> getListCustomer(List<List<String>> dataCustomer){
        List<Customer> listCustomer = new ArrayList<Customer>();
        for (int i = 1; i < dataCustomer.size() ; i++) {
            List<String> element = dataCustomer.get(i);
            Customer customer = new Customer();

            customer.setTitle(element.size() > EmailTemplateEnum.TITLE.ordinal() ? element.get(EmailTemplateEnum.TITLE.ordinal()) : null );
            customer.setFirstName(element.size() > EmailTemplateEnum.FIRST_NAME.ordinal() ? element.get(EmailTemplateEnum.FIRST_NAME.ordinal()) : null);
            customer.setLastName(element.size() > EmailTemplateEnum.LAST_NAME.ordinal() ? element.get(EmailTemplateEnum.LAST_NAME.ordinal()) : null);
            customer.setEmail(element.size() > EmailTemplateEnum.EMAIL.ordinal() ? element.get(EmailTemplateEnum.EMAIL.ordinal()) : null);

            listCustomer.add(customer);
        }
        return listCustomer;
    }

    public static List<String> getRecordFromLine(String line) {
        List<String> values = new ArrayList<String>();
        try (Scanner rowScanner = new Scanner(line)) {
            rowScanner.useDelimiter(COMMA_DELIMITER);
            while (rowScanner.hasNext()) {
                values.add(rowScanner.next());
            }
        }
        return values;
    }

    public static List<List<String>> readFileCSV(String path){
        List<List<String>> records = new ArrayList<>();
        try (Scanner scanner = new Scanner(new FileReader(path))) {
            while (scanner.hasNextLine()) {
                records.add(getRecordFromLine(scanner.nextLine()));
            }
        }catch(Exception err){
            err.printStackTrace();
        }
        return records;
    }

    public static EmailTemplate readFileEmailTemplate(String path){
        EmailTemplate emailTemplate = new EmailTemplate();
        JSONParser parser = new JSONParser();
        try(FileReader fileReader = new FileReader(path)) {
            JSONObject email = (JSONObject) parser.parse(fileReader);
            emailTemplate.setFrom(email.get("from").toString());
            emailTemplate.setTo(email.get("to") != null ? email.get("to").toString() : "");
            emailTemplate.setSubject(email.get("subject").toString());
            emailTemplate.setMimeType(email.get("mimeType").toString());
            emailTemplate.setBody(email.get("body").toString());
        }catch (Exception e){
            e.printStackTrace();
        }

        return emailTemplate;
    }
}
