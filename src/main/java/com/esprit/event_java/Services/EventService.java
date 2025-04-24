package com.esprit.event_java.Services;

import com.esprit.event_java.Models.Event;
import com.esprit.event_java.Models.Category;
import com.esprit.event_java.exceptions.ValidationException;
import com.esprit.event_java.Connexion.DatabaseConnection;
import com.esprit.event_java.utils.FileUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

public class EventService {
    private final Connection connection;
    private final CategoryService categoryService;
    private static final String UPLOAD_DIR = "uploads/events/";

    public EventService() {
        this.connection = DatabaseConnection.getConnection();
        this.categoryService = new CategoryService();
    }

    public void addEvent(Event event) throws ValidationException {
        handleImageUpload(event);
        String query = "INSERT INTO events (title, start_datetime, end_datetime, location, " +
                "description, duration, max_participants, image_path, category_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            setEventStatementParameters(statement, event);
            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    event.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            if (event.getImagePath() != null) {
                FileUtils.deleteFile(UPLOAD_DIR + event.getImagePath());
            }
            throw new ValidationException("Failed to add event: " + e.getMessage());
        }
    }

    public void updateEvent(Event event) throws ValidationException {
        Event oldEvent = event.getId() > 0 ? getEventById(event.getId()) : null;
        String oldImagePath = oldEvent != null ? oldEvent.getImagePath() : null;

        handleImageUpload(event);

        String query = "UPDATE events SET title = ?, start_datetime = ?, end_datetime = ?, " +
                "location = ?, description = ?, duration = ?, max_participants = ?, " +
                "image_path = ?, category_id = ? WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            setEventStatementParameters(statement, event);
            statement.setInt(10, event.getId());
            statement.executeUpdate();

            if (oldImagePath != null && !oldImagePath.equals(event.getImagePath())) {
                FileUtils.deleteFile(UPLOAD_DIR + oldImagePath);
            }
        } catch (SQLException e) {
            if (event.getImagePath() != null && !event.getImagePath().equals(oldImagePath)) {
                FileUtils.deleteFile(UPLOAD_DIR + event.getImagePath());
            }
            throw new ValidationException("Failed to update event: " + e.getMessage());
        }
    }

    private void handleImageUpload(Event event) throws ValidationException {
        if (event.getImageFile() != null) {
            String extension = FileUtils.getFileExtension(event.getImageFile().getName());
            String newFilename = System.currentTimeMillis() + "." + extension;
            String destinationPath = UPLOAD_DIR + newFilename;

            FileUtils.saveFile(event.getImageFile(), destinationPath);
            event.setImagePath(newFilename);
        }
    }

    public void deleteEvent(int id) throws ValidationException {
        String query = "DELETE FROM events WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new ValidationException("Failed to delete event: " + e.getMessage());
        }
    }

    public ObservableList<Event> getAllEvents() {
        ObservableList<Event> events = FXCollections.observableArrayList();
        String query = "SELECT * FROM events";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                events.add(createEventFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return events;
    }

    public Event getEventById(int id) {
        String query = "SELECT * FROM events WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return createEventFromResultSet(resultSet);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Event createEventFromResultSet(ResultSet resultSet) throws SQLException {
        Event event = new Event();
        event.setId(resultSet.getInt("id"));
        event.setTitle(resultSet.getString("title"));
        event.setStartDatetime(resultSet.getTimestamp("start_datetime").toLocalDateTime());
        event.setEndDatetime(resultSet.getTimestamp("end_datetime").toLocalDateTime());
        event.setLocation(resultSet.getString("location"));
        event.setDescription(resultSet.getString("description"));
        event.setDuration(resultSet.getInt("duration"));
        event.setMaxParticipants(resultSet.getInt("max_participants"));
        event.setImagePath(resultSet.getString("image_path"));

        int categoryId = resultSet.getInt("category_id");
        event.setCategory(categoryService.getCategoryById(categoryId));

        return event;
    }

    private void setEventStatementParameters(PreparedStatement statement, Event event) throws SQLException {
        statement.setString(1, event.getTitle());
        statement.setTimestamp(2, Timestamp.valueOf(event.getStartDatetime()));
        statement.setTimestamp(3, Timestamp.valueOf(event.getEndDatetime()));
        statement.setString(4, event.getLocation());
        statement.setString(5, event.getDescription());
        statement.setInt(6, event.getDuration());
        statement.setInt(7, event.getMaxParticipants());
        statement.setString(8, event.getImagePath());
        statement.setInt(9, event.getCategory().getId());
    }

    public ObservableList<Event> searchEvents(String searchTerm) {
        ObservableList<Event> events = FXCollections.observableArrayList();
        String query = "SELECT * FROM events WHERE " +
                "LOWER(title) LIKE ? OR " +
                "LOWER(location) LIKE ? OR " +
                "LOWER(description) LIKE ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            String likeTerm = "%" + searchTerm.toLowerCase() + "%";
            statement.setString(1, likeTerm);
            statement.setString(2, likeTerm);
            statement.setString(3, likeTerm);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    events.add(createEventFromResultSet(resultSet));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return events;
    }

    public ObservableList<Event> filterEventsByCategory(Category category) {
        if (category == null) {
            return getAllEvents();
        }

        ObservableList<Event> events = FXCollections.observableArrayList();
        String query = "SELECT * FROM events WHERE category_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, category.getId());

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    events.add(createEventFromResultSet(resultSet));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return events;
    }

    public ObservableList<Event> searchAndFilterEvents(String searchTerm, Category category) {
        if (searchTerm == null || searchTerm.isEmpty()) {
            return filterEventsByCategory(category);
        }
        if (category == null) {
            return searchEvents(searchTerm);
        }

        ObservableList<Event> events = FXCollections.observableArrayList();
        String query = "SELECT * FROM events WHERE " +
                "(LOWER(title) LIKE ? OR " +
                "LOWER(location) LIKE ? OR " +
                "LOWER(description) LIKE ?) " +
                "AND category_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            String likeTerm = "%" + searchTerm.toLowerCase() + "%";
            statement.setString(1, likeTerm);
            statement.setString(2, likeTerm);
            statement.setString(3, likeTerm);
            statement.setInt(4, category.getId());

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    events.add(createEventFromResultSet(resultSet));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return events;
    }

    public Map<Category, Long> getEventCountByCategory() {
        Map<Category, Long> eventCounts = getAllEvents().stream()
                .filter(event -> event.getCategory() != null)
                .collect(Collectors.groupingBy(
                        Event::getCategory,
                        Collectors.counting()
                ));

        categoryService.getAllCategories().forEach(category ->
                eventCounts.putIfAbsent(category, 0L)
        );

        return eventCounts;
    }
}