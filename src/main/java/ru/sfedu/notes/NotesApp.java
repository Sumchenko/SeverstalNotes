package ru.sfedu.notes;

import ru.sfedu.notes.db.DatabaseManager;
import ru.sfedu.notes.models.Note;

import java.util.List;
import java.util.Scanner;

public class NotesApp {
    private DatabaseManager dbManager;
    private Scanner scanner;

    public NotesApp() {
        dbManager = new DatabaseManager();
        scanner = new Scanner(System.in);
    }

    public void run() {
        initializeDefaultNote();

        while (true) {
            showMenu();
            String in = scanner.nextLine();
            int choice = Integer.parseInt(in);

            switch (choice) {
                case 1:
                    showNotes();
                    break;
                case 2:
                    createNote();
                    break;
                case 3:
                    editNote();
                    break;
                case 4:
                    deleteNote();
                    break;
                case 5:
                    dbManager.closeConnection();
                    break;
                default:
                    System.out.println("Неыерный кейс!");
                    break;
            }
        }
    }

    public void showMenu() {
        System.out.println(
                "1. Показать все заметки.\n" +
                "2. Создать заметку.\n" +
                "3. Редактировать заметку.\n" +
                "4. Удалить заметку.\n" +
                "5. Выход.");
    }

    public void showNotes() {
        List<Note> notes = dbManager.getAllNotes();
        if (notes.isEmpty()) {
            System.out.println("Вы еще не создали заметку");
        } else {
            for (Note note : notes) {
                System.out.println(note.toString());
            }
        }
    }

    public void createNote() {
        System.out.println("Введите текст: ");
        String text = scanner.nextLine();
        dbManager.createNote(text);
    }

    public void editNote() {
        showNotes();
        System.out.println("Введите id заметки: ");
        int id = Integer.parseInt(scanner.nextLine());
        System.out.println("Введите новый текст: ");
        String newText = scanner.nextLine();
        dbManager.updateNote(id, newText);
    }

    public void deleteNote() {
        showNotes();
        System.out.println("Введите id заметки: ");
        int id = Integer.parseInt(scanner.nextLine());
        dbManager.deleteNote(id);
    }

    public void initializeDefaultNote() {
        if (dbManager.getAllNotes().isEmpty()) {
            dbManager.createNote("Начни создавать свои заметки");
        }
    }
}
