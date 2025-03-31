package ru.sfedu.notes.db;

import org.apache.log4j.Logger;
import ru.sfedu.notes.models.Note;

import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


/* Класс для взаимодйствия с БД PostgreSQL
   содержит CRUD операции для управления заметками (Note)
 */
public class DatabaseManager {
    private Connection connection;

    private static final Logger log = Logger.getLogger(DatabaseManager.class);

    /*
    Конструктор для подключения к БД через конфиг файл
     */
    public DatabaseManager() {
        Properties properties = new Properties();
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("configDB.properties")) {
            if (in != null) {
                properties.load(in);
                log.info("Загружен configDB.properties");
            } else {
                log.warn("Файл конфигурации отсутствует, используются переменные окружения");
            }

            String url = System.getenv("DB_URL") != null ? System.getenv("DB_URL") : properties.getProperty("db.url");
            String username = System.getenv("DB_USERNAME") != null ? System.getenv("DB_USERNAME") : properties.getProperty("db.username");
            String password = System.getenv("DB_PASSWORD") != null ? System.getenv("DB_PASSWORD") : properties.getProperty("db.password");

            connection = DriverManager.getConnection(url, username, password);
            log.info("Успешное подключение к БД: " + url);

            // Создание таблицы, если она не существует
            String createTableSQL = "CREATE TABLE IF NOT EXISTS notes (id SERIAL PRIMARY KEY, text VARCHAR(255) NOT NULL)";
            try (Statement stmt = connection.createStatement()) {
                stmt.execute(createTableSQL);
                log.info("Таблица notes создана или уже существует");
            }
        } catch (Exception e) {
            log.error("ОШИБКА ПОДКЛЮЧЕНИЯ: " + e.getMessage());
        }
    }

//    public DatabaseManager() {
//        this("configDB.properties"); // По умолчанию основной файл
//    }


    /*
    Метод для создания заметки
     */
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

    /*
    Получение списка всех заметок
     */
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
        } catch (SQLException e) {
            log.error("ОШИБКА ПОЛУЧЕНИЯ ЗАМЕТОК: " + e.getMessage());
        }

        return notes;
    }

    /*
    Обновление заметки по заданному id
     */
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

    /*
    Удаление заметки по заданному id
     */
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

    /*
    Проверяет существование заметки по id
     */
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

    /*
    Закрывает соединение с БД
     */
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
