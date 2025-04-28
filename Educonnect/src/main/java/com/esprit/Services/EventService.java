package com.esprit.Services;

import com.esprit.Connexion.DatabaseConnection;
import com.esprit.Entities.User;
import com.esprit.Models.Category;
import com.esprit.Models.Event;
import com.esprit.exceptions.ValidationException;
import com.esprit.utils.FileUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
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
    // In EventService.java
    public Map<String, Long> getEventCountByLocation() {
        return getAllEvents().stream()
                .collect(Collectors.groupingBy(
                        Event::getLocation,
                        Collectors.counting()
                ));
    }

    public Map<Integer, Long> getEventCountByMonth() {
        return getAllEvents().stream()
                .collect(Collectors.groupingBy(
                        event -> event.getStartDatetime().getMonthValue(),
                        Collectors.counting()
                ));
    }

    public Map<String, Long> getEventCountByParticipantRange() {
        return getAllEvents().stream()
                .collect(Collectors.groupingBy(
                        event -> {
                            int max = event.getMaxParticipants();
                            if (max < 50) return "Small (<50)";
                            if (max < 100) return "Medium (50-99)";
                            return "Large (100+)";
                        },
                        Collectors.counting()
                ));
    }
    public boolean registerUserForEvent(Event event, User user) {
        if (event.getCurrentParticipants() >= event.getMaxParticipants()) {
            return false;
        }

        if (isUserRegistered(event.getId(), user.getId())) {
            return false;
        }

        if (hasTimeConflict(user.getId(), event)) {
            return false;
        }

        String query = "INSERT INTO event_registrations (event_id, user_id) VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, event.getId());
            statement.setInt(2, user.getId());
            statement.executeUpdate();

            // Update current participants count
            updateParticipantCount(event.getId(), 1);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean unregisterUserFromEvent(Event event, User user) {
        String query = "DELETE FROM event_registrations WHERE event_id = ? AND user_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, event.getId());
            statement.setInt(2, user.getId());
            int affectedRows = statement.executeUpdate();

            if (affectedRows > 0) {
                updateParticipantCount(event.getId(), -1);
                return true;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void updateParticipantCount(int eventId, int delta) {
        String query = "UPDATE events SET current_participants = current_participants + ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, delta);
            statement.setInt(2, eventId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean isUserRegistered(int eventId, int userId) {
        String query = "SELECT 1 FROM event_registrations WHERE event_id = ? AND user_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, eventId);
            statement.setInt(2, userId);
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean hasTimeConflict(int userId, Event newEvent) {
        String query = "SELECT e.* FROM events e " +
                "JOIN event_registrations er ON e.id = er.event_id " +
                "WHERE er.user_id = ? AND NOT (e.end_datetime <= ? OR e.start_datetime >= ?)";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);
            statement.setTimestamp(2, Timestamp.valueOf(newEvent.getStartDatetime()));
            statement.setTimestamp(3, Timestamp.valueOf(newEvent.getEndDatetime()));

            try (ResultSet rs = statement.executeQuery()) {
                return rs.next(); // If any rows returned, there's a conflict
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Event> getRegisteredEventsForUser(int userId) {
        List<Event> events = new ArrayList<>();
        String query = "SELECT e.* FROM events e " +
                "JOIN event_registrations er ON e.id = er.event_id " +
                "WHERE er.user_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    events.add(createEventFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return events;
    }

}