package ru.sfedu.notes;

import ru.sfedu.notes.db.DatabaseManager;

public class Main {
    public static void main(String[] args) {
        System.out.println("Приложение Твои заметки");

        NotesApp app = new NotesApp();
        app.run();

//        DatabaseManager db = new DatabaseManager();
//        db.createNote("Тестовая заметка");
//        db.getAllNotes().forEach(note -> System.out.println("ID: " + note.getId() + ", Текст: " + note.getText()));
//        db.closeConnection();
    }
}