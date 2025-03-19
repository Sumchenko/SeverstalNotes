package ru.sfedu.notes.db;

import org.checkerframework.checker.units.qual.N;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.sfedu.notes.models.Note;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import static org.junit.Assert.*;

public class DatabaseManagerTest {
    private DatabaseManager dbManager;
    private Connection connection;

    @Before
    public void setUp() throws SQLException {
        connection = DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/notes",
                "postgres",
                "02052004"
        );
        try (Statement statement = connection.createStatement()) {
            statement.execute("TRUNCATE TABLE notes RESTART IDENTITY");
        }
        dbManager = new DatabaseManager();
    }

    @After
    public void finish() throws SQLException {
        dbManager.closeConnection();
        if (connection != null) {
            connection.close();
        }
    }

    @Test
    public void testCreateNote() {
        String text = "Тест заметка";
        dbManager.createNote(text);

        List<Note> notes = dbManager.getAllNotes();
        assertEquals(1, notes.size());
        assertEquals(text, notes.get(0).getText());
    }

    @Test
    public void testCreateNoteNullConnection() throws SQLException{
        dbManager.closeConnection();
        try (Statement statement = connection.createStatement()) {
            statement.execute("UPDATE notes SET text = 'заметка тест'");
        }

        dbManager.createNote("заметка");

        List<Note> notes = dbManager.getAllNotes();
        assertTrue(notes.isEmpty());
    }

    @Test
    public void testGetAllNotes() {
        dbManager.createNote("Заметка тест 1");
        dbManager.createNote("Заметка тест 2");

        List<Note> notes = dbManager.getAllNotes();

        assertEquals(2, notes.size());
        assertTrue(notes.stream().anyMatch(note -> note.getText().equals("Заметка тест 1")));
        assertTrue(notes.stream().anyMatch(note -> note.getText().equals("Заметка тест 2")));
    }

    @Test
    public void testGetAllNotesEmpty() {
        List<Note> notes = dbManager.getAllNotes();

        assertTrue(notes.isEmpty());
    }

    @Test
    public void testUpdateNote() {
        dbManager.createNote("Заметка v1");
        int id = dbManager.getAllNotes().get(0).getId();
        String newText = "Заметка v2";
        dbManager.updateNote(id, newText);

        List<Note> notes = dbManager.getAllNotes();
        assertEquals(1, notes.size());
        assertEquals(newText, notes.get(0).getText());
    }

    @Test
    public void testUpdateNoteNotExistence() {
        int notExId = 10000;

        dbManager.updateNote(notExId, "Обновленная заметка");

        List<Note> notes = dbManager.getAllNotes();
        assertTrue(notes.isEmpty());
    }

    @Test
    public void testDeleteNot() {
        dbManager.createNote("Заметка");
        int id = dbManager.getAllNotes().get(0).getId();
        dbManager.deleteNote(id);

        List<Note> notes = dbManager.getAllNotes();
        assertTrue(notes.isEmpty());
    }

    @Test
    public void testDeleteNoteNotExistence() {
        int notExId = 10000;

        dbManager.deleteNote(notExId);

        List<Note> notes = dbManager.getAllNotes();
        assertTrue(notes.isEmpty());
    }

    @Test
    public void testCheckExistence() {
        dbManager.createNote("Заметка");
        int id = dbManager.getAllNotes().get(0).getId();

        assertTrue(dbManager.checkExistence(id));
    }

    @Test
    public void testCheckExistenceNotExistence() {
        int notExId = 10000;

        assertFalse(dbManager.checkExistence(notExId));
    }
}
