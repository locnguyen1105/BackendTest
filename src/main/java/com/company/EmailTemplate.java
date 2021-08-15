package com.company;

import org.json.simple.JSONObject;

public class EmailTemplate {
    private String from,to,subject,mimeType,body;

    public EmailTemplate() {
    }

    public EmailTemplate(String from, String to, String subject, String mimeType, String body) {
        this.from = from;
        this.to = to;
        this.subject = subject;
        this.mimeType = mimeType;
        this.body = body;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public JSONObject toJSON() {

        JSONObject jo = new JSONObject();

        jo.put("from", from);
        jo.put("to", to);
        jo.put("subject", subject);
        jo.put("mimeType", mimeType);
        jo.put("body", body);

        return jo;
    }
}
