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
    private static final int NUMBER_OF_COLUMN = 4;

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

        List<String> lineErrors = new ArrayList<>();

        // Get list string from csv
        List<List<String>> dataCustomer = readFileCSV(customerFilePath,lineErrors);
        System.out.printf("Get %d lines valid from customers csv \n", dataCustomer.size());
        System.out.printf("Get %d lines errors from customers csv \n", lineErrors.size());

        // list customer from dataCustomer
        List<Customer> listCustomer = getListCustomer(dataCustomer);

        // Get email template
        EmailTemplate email = readFileEmailTemplate(emailTemplate);

        System.out.println("Write Output !!");
        //write output Email
        writeOutputEmail(email, listCustomer, outputEmailPath);

        if (!lineErrors.isEmpty()) {
            writeErrorsOutput(outputErrorPath,lineErrors);
        }

        System.out.println("Done !!");
    }
    /**
     * This method get input file path from customer
     * @param  extention  extention of file
     * @return      the string of file path
     */
    public static String inputPathFile(String extention) {
        Scanner input = new Scanner(System.in);
        String path = input.nextLine();

        while (!isValidPathFile(path) || !getFileExtension(path).equals(extention)) {
            System.err.print("ERROR Please enter a valid path file with extention ." + extention + " : ");
            path = input.nextLine();
        }
        return path;
    }

    /**
     * This method get extenstion of file
     * @param  filePath  the absolute file path from computer
     * @return  the extention ( csv, json, ... )
     */
    public static String getFileExtension(String filePath) {
        int lastIndexOf = filePath.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return ""; // empty extension
        }
        return filePath.substring(lastIndexOf + 1);
    }

    /**
     * This method get input directory path from customer
     * @return      the string of directory path
     */
    public static String inputPathDirectory() {
        Scanner input = new Scanner(System.in);
        String path = input.nextLine();

        while (!isValidPathDirectory(path)) {
            System.err.print("ERROR Please enter a valid path directory : ");
            path = input.nextLine();
        }
        return path;
    }

    /**
     * This method validate file path
     * @return boolean : true if  the string is file path
     *                   false if the string is not file path
     */
    public static boolean isValidPathFile(String path) {
        return Files.isRegularFile(Paths.get(path));
    }

    /**
     * This method validate directory path
     * @return boolean : true if  the string is directory path
     *                   false if the string is not directory path
     */
    public static boolean isValidPathDirectory(String path) {
        return Files.isDirectory(Paths.get(path));
    }

    /**
     *
     * This method generate json file each valid customer
     *
     * @param email template from json file
     * @param listCustomer list customer valid
     * @param outputEmailPath output directory to save valid json file
     *
     */
    public static void writeOutputEmail(EmailTemplate email, List<Customer> listCustomer, String outputEmailPath) {
        SimpleDateFormat today = new SimpleDateFormat("dd MMM yyyy");
        for (Customer customer : listCustomer) {
            EmailTemplate saveEmail = new EmailTemplate();

            saveEmail.setTo(customer.getEmail());

            saveEmail.setFrom(email.getFrom());
            saveEmail.setMimeType(email.getMimeType());
            saveEmail.setSubject(email.getSubject());

            String newBody = email.getBody().replace(TITLE_REPLACE, customer.getTitle())
                    .replace(FIRST_NAME_REPLACE, customer.getFirstName())
                    .replace(LAST_NAME_REPLACE, customer.getLastName())
                    .replace(TODAY_REPLACE, today.format(Calendar.getInstance().getTime()));
            saveEmail.setBody(newBody);

            JSONObject emailOutput = saveEmail.toJSON();

            String outputPath = outputEmailPath + "/" + customer.getFirstName().toLowerCase() + customer.getLastName().toLowerCase() + ".json";

            //write to file
            WriteToFile(emailOutput.toJSONString(), outputPath);

        }

    }

    /**
     *
     * This method write list of line errors from file csv
     *
     * @param outputErrorPath absolute path to error file (csv) ex: errors.csv
     * @param lineErrors list of line errors from csv file
     *
     */
    public static void writeErrorsOutput(String outputErrorPath,List<String> lineErrors) {
        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputErrorPath), "UTF-8"));

            StringBuffer firstLine = new StringBuffer();
            for (EmailTemplateEnum emailTemplateEnum : EmailTemplateEnum.values()) {
                firstLine.append(emailTemplateEnum.toString());
                if (emailTemplateEnum.ordinal() != EmailTemplateEnum.values().length - 1) {
                    firstLine.append(COMMA_DELIMITER);
                }
            }

            bw.write(firstLine.toString());
            bw.newLine();

            for (String errLine : lineErrors) {
                bw.write(errLine);
                bw.newLine();
            }
            bw.flush();
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * This method write data to file
     *
     * @param data data need to write
     * @param path path to file
     *
     */
    public static void WriteToFile(String data, String path) {
        try (FileWriter fileWriter = new FileWriter(path)) {

            fileWriter.write(data);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * This method convert data customer string to list customer object
     *
     * @param dataCustomer list data customer string from csv file
     *
     * @return list customer object
     */
    public static List<Customer> getListCustomer(List<List<String>> dataCustomer) {
        List<Customer> listCustomer = new ArrayList<Customer>();
        for (int i = 1; i < dataCustomer.size(); i++) {
            List<String> element = dataCustomer.get(i);
            Customer customer = new Customer();

            customer.setTitle(element.size() > EmailTemplateEnum.TITLE.ordinal() ? element.get(EmailTemplateEnum.TITLE.ordinal()) : null);
            customer.setFirstName(element.size() > EmailTemplateEnum.FIRST_NAME.ordinal() ? element.get(EmailTemplateEnum.FIRST_NAME.ordinal()) : null);
            customer.setLastName(element.size() > EmailTemplateEnum.LAST_NAME.ordinal() ? element.get(EmailTemplateEnum.LAST_NAME.ordinal()) : null);
            customer.setEmail(element.size() > EmailTemplateEnum.EMAIL.ordinal() ? element.get(EmailTemplateEnum.EMAIL.ordinal()) : null);

            listCustomer.add(customer);
        }
        return listCustomer;
    }

    /**
     *
     * This method will read each row of customer csv file and validate each row
     * if email is missing or number of column line is over csv columns then that line
     * will add to lineErrors list
     *
     * @param line line of data csv
     * @param lineErrors list of line errors
     *
     * @return list string data of line
     */
    public static List<String> getRecordFromLine(String line,List<String> lineErrors) {
        List<String> values = new ArrayList<String>();
        String[] arrs = line.split(COMMA_DELIMITER, -1);
        if (arrs.length == NUMBER_OF_COLUMN) {
            String email = arrs[EmailTemplateEnum.EMAIL.ordinal()];
            if (line != null && email != null && !email.isBlank() && !email.isEmpty()) {
                for (String el : arrs) {
                    values.add(el);
                }
                return values;
            }
        }
        lineErrors.add(line);
        return values;
    }

    /**
     *
     * This method will read file csv
     *
     * @param path path of csv file
     * @param lineErrors list of line errors
     *
     * @return list of list string data
     */
    public static List<List<String>> readFileCSV(String path,List<String> lineErrors) {
        List<List<String>> records = new ArrayList<>();
        try (Scanner scanner = new Scanner(new FileReader(path))) {
            while (scanner.hasNextLine()) {
                List<String> recordFromLine = getRecordFromLine(scanner.nextLine(),lineErrors);
                if (!recordFromLine.isEmpty()) {
                    records.add(recordFromLine);
                }
            }
        } catch (Exception err) {
            err.printStackTrace();
        }
        return records;
    }

    /**
     *
     * This method will read email template json file
     *
     * @param path path of json file
     *
     * @return EmailTemplate object
     */
    public static EmailTemplate readFileEmailTemplate(String path) {
        EmailTemplate emailTemplate = new EmailTemplate();
        JSONParser parser = new JSONParser();
        try (FileReader fileReader = new FileReader(path)) {
            JSONObject email = (JSONObject) parser.parse(fileReader);
            emailTemplate.setFrom(email.get("from").toString());
            emailTemplate.setTo(email.get("to") != null ? email.get("to").toString() : "");
            emailTemplate.setSubject(email.get("subject").toString());
            emailTemplate.setMimeType(email.get("mimeType").toString());
            emailTemplate.setBody(email.get("body").toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return emailTemplate;
    }
}
