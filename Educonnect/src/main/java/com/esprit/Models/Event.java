package com.esprit.Models;

import com.esprit.Entities.User;
import java.io.File;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class Event {
    private int id;
    private String title;
    private LocalDateTime startDatetime;
    private LocalDateTime endDatetime;
    private String location;
    private String description;
    private int duration;
    private int maxParticipants;
    private int currentParticipants;
    private String imagePath;
    private File imageFile;
    private Category category;
    private Set<User> registeredUsers = new HashSet<>();

    public Event() {}

    public Event(String title, LocalDateTime startDatetime, LocalDateTime endDatetime,
                 String location, String description, int duration,
                 int maxParticipants, String imagePath, Category category) {
        this.title = title;
        this.startDatetime = startDatetime;
        this.endDatetime = endDatetime;
        this.location = location;
        this.description = description;
        this.duration = duration;
        this.maxParticipants = maxParticipants;
        this.imagePath = imagePath;
        this.category = category;
        this.currentParticipants = 0;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public LocalDateTime getStartDatetime() { return startDatetime; }
    public void setStartDatetime(LocalDateTime startDatetime) { this.startDatetime = startDatetime; }

    public LocalDateTime getEndDatetime() { return endDatetime; }
    public void setEndDatetime(LocalDateTime endDatetime) { this.endDatetime = endDatetime; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }

    public int getMaxParticipants() { return maxParticipants; }
    public void setMaxParticipants(int maxParticipants) { this.maxParticipants = maxParticipants; }

    public int getCurrentParticipants() { return currentParticipants; }
    public void setCurrentParticipants(int currentParticipants) { this.currentParticipants = currentParticipants; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public File getImageFile() { return imageFile; }
    public void setImageFile(File imageFile) { this.imageFile = imageFile; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    // Event registration methods
    public boolean registerUser(User user) {
        if (currentParticipants < maxParticipants && !registeredUsers.contains(user)) {
            registeredUsers.add(user);
            currentParticipants++;
            return true;
        }
        return false;
    }

    public boolean unregisterUser(User user) {
        if (registeredUsers.contains(user)) {
            registeredUsers.remove(user);
            currentParticipants--;
            return true;
        }
        return false;
    }

    public Set<User> getRegisteredUsers() { return new HashSet<>(registeredUsers); }
    public boolean isUserRegistered(User user) { return registeredUsers.contains(user); }
    public int getAvailableSpots() { return maxParticipants - currentParticipants; }

    public boolean hasTimeConflict(Event other) {
        return !(this.endDatetime.isBefore(other.startDatetime) ||
                this.startDatetime.isAfter(other.endDatetime));
    }

    @Override
    public String toString() { return title; }
}