package ru.sfedu.notes.models;

public class Note {
    private int id;
    private String text;

    public Note(int id, String text) {
        this.id = id;
        this.text = text;
    }

    public int getId() {
        return id;
    }


    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String toString() {
        return "Заметка №-" + id + ":\n" + text;
    }
}
