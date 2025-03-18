package ru.sfedu.notes.db;

import ru.sfedu.notes.models.Note;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private Connection connection;

    public DatabaseManager() {
        try {
            connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/notes", "postgres", "02052004");
        } catch (SQLException e) {
            System.out.println("ОШИБКА ПОДКЛЮЧЕНИЯ:" + e.getMessage());
        }
    }

    public void createNote(String text) {
        try {
            String sql = "INSERT INTO notes (text) VALUES (?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, text);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("ОШИБКА СОЗДАНИЯ ЗАМЕТКИ: " + e.getMessage());
        }
    }

    public List<Note> getAllNotes() {
        List<Note> notes = new ArrayList<>();

        try {
            String sql = "SELECT * FROM notes";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String text = rs.getString("text");
                Note note = new Note(id, text);
                notes.add(note);
            }
        } catch (Exception e) {
            System.out.println("ОШИБКА ПОЛУЧЕНИЯ ЗАМЕТОК: " + e.getMessage());
        }

        return notes;
    }

    public void updateNote(int id, String newText) {
        try {
            String sql = "UPDATE notes SET text = ? WHERE id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, newText);
            preparedStatement.setInt(2, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("ОШИБКА ОБНОВЛЕНИЯ ЗАМЕТКИ: " + e.getMessage());
        }
    }

    public void deleteNote(int id) {
        try {
            String sql = "DELETE FROM notes WHERE id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("ОШИБКА УДАЛЕНИЯ ЗАМЕТКИ: " + e.getMessage());
        }
    }

    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.out.println("ОШИБКА ЗАКРЫТИЯ СОЕДИНЕНИЯ: " + e.getMessage());
            }
        }
    }
}
