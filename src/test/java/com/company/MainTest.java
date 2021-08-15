package com.company;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {

    private List<Customer> listTest,listError;
    private EmailTemplate emailTemplateTest;
    List<List<String>> dataCustomerTest;

    @BeforeEach
    public void init() {
        listTest = new ArrayList<>();
        listError = new ArrayList<>();
        dataCustomerTest = new ArrayList<>();

        dataCustomerTest.add(new ArrayList<String>(Arrays.asList("title", "firstname","lastname","email")));
        dataCustomerTest.add(new ArrayList<String>(Arrays.asList("mr", "nguyen","loc","")));
        dataCustomerTest.add(new ArrayList<String>(Arrays.asList("mis", "Nam","Nguyen","name@gmail.com")));
        dataCustomerTest.add(new ArrayList<String>(Arrays.asList("mr", "nam","")));
        dataCustomerTest.add(new ArrayList<String>(Arrays.asList("", "","","")));

        listTest.add(new Customer("Mr", "Loc", "Nguyen", "locnx1105@gmail.com"));
        listTest.add(new Customer("Miss", "Nhu", "Nguyen", "nhunguyen@gmail.com"));
        listTest.add(new Customer("Mrs", "Nam", "Nguyen", ""));
        listTest.add(new Customer("Mrs", "Mhung", "Nguyen", ""));
        listTest.add(new Customer("", "", "", ""));

        listError.add(new Customer("Mrs", "Manh", "Nguyen", ""));


        emailTemplateTest = new EmailTemplate("The Marketing Team<marketing@example.com",
                "to", "subject", "mimeType", "Hi {{TITLE}} {{FI {{LAST_NAME}},\nToday, {{TODAY}}, we would like to tell you that... Sincerely, \nThe Marketing Team");
    }

    @Test
    void testWriteOutputEmail() {

        Main.writeOutputEmail(emailTemplateTest, listTest, "D:\\BETEST", "D:\\BETEST\\errors.csv");
        List<List<String>> dataCustomer = Main.readFileCSV("D:\\BETEST\\errors.csv");
        EmailTemplate emailTemplateTest = Main.readFileEmailTemplate("D:\\BETEST\\nhunguyen.json");

        assertEquals(4,dataCustomer.size());

        Assertions.assertEquals("nhunguyen@gmail.com",emailTemplateTest.getTo());

    }

    @Test
    void testWriteErrorsOutput() {
        Main.writeErrorsOutput(listError,"D:\\BETEST\\errors.csv");
        List<List<String>> dataCustomer = Main.readFileCSV("D:\\BETEST\\errors.csv");

        assertEquals(2,dataCustomer.size());
    }

    @Test
    void testGetListCustomer() {
        List<Customer> customers = Main.getListCustomer(dataCustomerTest);

        assertEquals("",customers.get(0).getEmail());
        assertEquals("nguyen",customers.get(0).getFirstName());
        assertEquals("nam",customers.get(2).getFirstName());
        assertEquals(null,customers.get(2).getEmail());
        assertEquals(4,customers.size());
    }

    @Test
    void testGetRecordFromLine() {
        String lineTest = "Loc,Nguyen,Phuoc";

        List<String> listString = Main.getRecordFromLine(lineTest);

        assertEquals("Loc",listString.get(0));
        assertEquals("Nguyen",listString.get(1));

    }

    @Test
    void testReadFileCSVWithData() {
        List<List<String>> testCSV = Main.readFileCSV("D:\\BETEST\\errors.csv");

        assertEquals(2,testCSV.size());
    }

    @Test
    void testReadFileEmailTemplateWithData() {
        EmailTemplate emailTemplateTest = Main.readFileEmailTemplate("D:\\BETEST\\nhunguyen.json");

        assertEquals("nhunguyen@gmail.com",emailTemplateTest.getTo());
    }

    @Test
    void testInvalidPathWithTrue() {
        assertTrue(Main.isValidPath("D:\\"));
        assertTrue(Main.isValidPath("D:\\BETEST"));
        assertTrue(Main.isValidPath("D:/"));
    }

    @Test
    void testInvalidPathWithFalse() {
        assertFalse(Main.isValidPath("D"));
        assertFalse(Main.isValidPath("basT"));
        assertFalse(Main.isValidPath("224"));
    }

}