package ru.sfedu.notes.db;

import org.apache.log4j.Logger;
import ru.sfedu.notes.models.Note;

import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class DatabaseManager {
    private Connection connection;

    private static final Logger log = Logger.getLogger(DatabaseManager.class);

    public DatabaseManager() {
        Properties properties = new Properties();
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("configDB.properties")) {
            if (in == null) {
                log.error("Файл конфигурации отсутствует");
                return;
            }
            properties.load(in);
            String url = properties.getProperty("db.url");
            String username = properties.getProperty("db.username");
            String password = properties.getProperty("db.password");
            connection = DriverManager.getConnection(url, username, password);
            log.info("Успешное подключение к БД");
        } catch (Exception e) {
            log.error("ОШИБКА ПОДКЛЮЧЕНИЯ:" + e.getMessage());
        }
    }

    public void createNote(String text) {
        if (connection == null) {
            log.error("Нет соединения с БД");
            return;
        }

        String sql = "INSERT INTO notes (text) VALUES (?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, text);
            preparedStatement.executeUpdate();
            log.info("Заметка создана успешно");
        } catch (SQLException e) {
            log.error("ОШИБКА СОЗДАНИЯ ЗАМЕТКИ: " + e.getMessage());
        }
    }

    public List<Note> getAllNotes() {
        if (connection == null) {
            log.error("Нет соединения с БД");
            return List.of();
        }
        List<Note> notes = new ArrayList<>();

        String sql = "SELECT * FROM notes";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String text = rs.getString("text");
                Note note = new Note(id, text);
                notes.add(note);
            }
            log.info("Успешное получение заметок");
        } catch (Exception e) {
            log.error("ОШИБКА ПОЛУЧЕНИЯ ЗАМЕТОК: " + e.getMessage());
        }

        return notes;
    }

    public void updateNote(int id, String newText) {
        if (connection == null) {
            log.error("Нет соединения с БД");
            return;
        }

        String sql = "UPDATE notes SET text = ? WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, newText);
            preparedStatement.setInt(2, id);
            preparedStatement.executeUpdate();
            log.info("Заметка успешно обновлена");
        } catch (SQLException e) {
            log.error("ОШИБКА ОБНОВЛЕНИЯ ЗАМЕТКИ: " + e.getMessage());
        }
    }

    public void deleteNote(int id) {
        if (connection == null) {
            log.error("Нет соединения с БД");
            return;
        }

        String sql = "DELETE FROM notes WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
            log.info("Заметка успешно удалена");
        } catch (SQLException e) {
            log.error("ОШИБКА УДАЛЕНИЯ ЗАМЕТКИ: " + e.getMessage());
        }
    }

    public boolean checkExistence(int id) {

        String sql = "SELECT COUNT(*) FROM notes WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            ResultSet rs = preparedStatement.executeQuery();
            log.info("Успешная проверка существования заметки");
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            log.error("Ошибка проверки существования заметки" + e.getMessage());
        }
        return false;
    }

    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                log.info("Отключение бд");
            } catch (SQLException e) {
                log.error("ОШИБКА ЗАКРЫТИЯ СОЕДИНЕНИЯ: " + e.getMessage());
            }
        }
    }
}
