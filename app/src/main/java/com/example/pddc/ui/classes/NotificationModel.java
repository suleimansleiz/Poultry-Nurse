package com.example.pddc.ui.classes;

public class NotificationModel {
    String aiName;
    String aiNote;
    String aiNoteBig;


    public NotificationModel(String aiName, String aiNote,String
            aiNoteBig) {
        this.aiName = aiName;
        this.aiNote = aiNote;
        this.aiNoteBig = aiNoteBig;
    }

    public String getAiName() {
        return aiName;
    }

    public String getAiNote() {
        return aiNote;
    }

    public String getAiNoteBig() {
        return aiNoteBig;
    }
}
