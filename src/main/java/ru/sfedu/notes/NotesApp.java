package ru.sfedu.notes;

import org.apache.log4j.Logger;
import ru.sfedu.notes.db.DatabaseManager;
import ru.sfedu.notes.models.Note;

import java.util.List;
import java.util.Scanner;

/*
Основной класс приложения
 */
public class NotesApp {
    private final DatabaseManager dbManager;
    private final Scanner scanner;

    private static final Logger log = Logger.getLogger(NotesApp.class);

    public NotesApp() {
        dbManager = new DatabaseManager();
        scanner = new Scanner(System.in);
    }

    /*
    Главный цикл приложения
     */
    public void run() {
        initializeDefaultNote();

        while (true) {
            showMenu();
            String input = scanner.nextLine();

            try {
                int choice = Integer.parseInt(input);
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
                        scanner.close();
                        System.exit(0);
                    default:
                        log.warn("Неверный кейс!");
                        break;
                }
            } catch (NumberFormatException e) {
                log.warn("Введите число!");
            }
        }
    }

    /*
    Отображает меню приложения в консоли
     */
    private void showMenu() {
        System.out.println(
                "\n1. Показать все заметки.\n" +
                "2. Создать заметку.\n" +
                "3. Редактировать заметку.\n" +
                "4. Удалить заметку.\n" +
                "5. Выход.");
    }

    /*
    Отображет существующие заметки
     */
    private void showNotes() {
        List<Note> notes = dbManager.getAllNotes();
        if (notes.isEmpty()) {
            System.out.println("Вы еще не создали заметку");
        } else {
            for (Note note : notes) {
                System.out.println(note.toString());
            }
        }
    }

    /*
    Создание заметки пользователем
     */
    private void createNote() {
        System.out.println("Введите текст: ");
        String text = scanner.nextLine();
        dbManager.createNote(text);
    }

    /*
    Редактирование заметки пользователем
     */
    private void editNote() {
        showNotes();
        System.out.println("Введите id заметки: ");
        try {
            int id = Integer.parseInt(scanner.nextLine());
            System.out.println("Введите новый текст: ");
            String newText = scanner.nextLine();
            if (!dbManager.checkExistence(id)) {
                log.warn("Заметка с id " + id + " не найдена");
                return;
            }
            dbManager.updateNote(id, newText);
        } catch (NumberFormatException e) {
            log.warn("Введите число!");
        }

    }

    /*
    Удаление заметки пользователем
     */
    private void deleteNote() {
        showNotes();
        System.out.println("Введите id заметки: ");
        try{
            int id = Integer.parseInt(scanner.nextLine());
            if (!dbManager.checkExistence(id)) {
                log.warn("Заметка с id " + id + " не найдена");
                return;
            }
            dbManager.deleteNote(id);
        } catch (NumberFormatException e) {
            log.warn("Введите число!");
        }

    }

    /*
    Инициализация заметки по умолчанию при первом запуске приложения спустой БД
     */
    private void initializeDefaultNote() {
        if (dbManager.getAllNotes().isEmpty()) {
            dbManager.createNote("Начни создавать свои заметки (Заметка по умолчанию)");
        }
    }
}
