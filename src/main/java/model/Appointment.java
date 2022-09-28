package model;

import dao.DBCustomers;
import dao.DBUsers;
import java.util.List;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;

/**
 This class defines Appointment objects.
 @author Sean
 */
public class Appointment {

    private int appointmentId;
    private String title;
    private String description;
    private String location;
    private Contact contact;

    private LocalDateTime start;
    private LocalDateTime end;
    private int customerId;
    private int userId;

    public Appointment(int appointmentId, String title, String description, String location, Contact contact, LocalDateTime start, LocalDateTime end, int customerId, int userId) {
        this.appointmentId = appointmentId;
        this.title = title;
        this.description = description;
        this.location = location;
        this.contact = contact;

        this.start = start;
        this.end = end;
        this.customerId = customerId;
        this.userId = userId;
    }

    // no ID, for adding/creating Appointments
    public Appointment(String title, String description, String location, Contact contact, LocalDateTime start, LocalDateTime end, int customerId, int userId) {
        this.title = title;
        this.description = description;
        this.location = location;
        this.contact = contact;

        this.start = start;
        this.end = end;
        this.customerId = customerId;
        this.userId = userId;

    }

    public int getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public void setEnd(LocalDateTime end) {
        this.end = end;
    }

    public int getUserId() {
        return this.userId;
    }

    public User getUser(int userId) {
        return DBUsers.getAllUsers().stream().filter(user -> user.getId() == userId).findAny().orElseThrow();
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public Customer getCustomer(int customerId) {

        List<Customer> customersList = DBCustomers.getAllCustomers();

        for (Customer customer : customersList) {
            if (customer.getId() == customerId) return customer;
        }

        throw new NoSuchElementException();
    }
}
