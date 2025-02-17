package com.example.pddc.ui.classes;

public class BatchesModel {
    String batchId;
    String batchName;
    String batchDate;
    String batchAge;
    String chicksNo;
    Boolean isAvailable;


    public BatchesModel( String batchName, String batchDate, String batchAge, String chicksNo, boolean isAvailable) {
//        this.batchId = batchId;
        this.batchName = batchName;
        this.batchAge = batchAge;
        this.chicksNo = chicksNo;
        this.batchDate = batchDate;
        this.isAvailable = isAvailable;
    }

//    public String getBatchId() {
//        return batchId;
//    }

    public String getBatchName() {
        return batchName;
    }

    public String getBatchAge() {
        return batchAge;
    }

    public String getChicksNo() { return chicksNo;}
    public String getBatchDate() {
        return batchDate;
    }


    public boolean isAvailable() {
        return isAvailable;
    }
}
