package com.example.bookey.Objects;

import android.view.View;

import java.util.Date;

public class Room {

    private int roomNumber;
    private boolean reservationStatus;
    private String reservedBy;
    private String arrivalDate;
    private String departureDate;
    private String type;
    private double pricePerNight;

    public Room() {
        // No-argument constructor
        // This constructor is necessary for Firestore deserialization
    }

    public Room(int roomNumber, double pricePerNight, String type, boolean reservationStatus) {
        this.roomNumber = roomNumber;
        this.pricePerNight = pricePerNight;
        this.type = type;
        this.reservationStatus = reservationStatus;
        this.reservedBy = null;
        this.arrivalDate = null;
        this.departureDate = null;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public boolean getReservationStatus() {
        return reservationStatus;
    }

    public String getReservedBy() {
        return reservedBy;
    }

    public String getArrivalDate() {
        return arrivalDate;
    }

    public String getDepartureDate() {
        return departureDate;
    }

    public double getPricePerNight() {
        return pricePerNight;
    }

    public void reserve(String reservedBy, String arrivalDate, String departureDate) {
        if (!reservationStatus) {
            this.reservationStatus = true;
            this.reservedBy = reservedBy;
            this.arrivalDate = arrivalDate;
            this.departureDate = departureDate;
            System.out.println("Room " + roomNumber + " reserved by " + reservedBy);
        } else {
            System.out.println("Room " + roomNumber + " is already reserved.");
        }
    }



    @Override
    public String toString() {
        return
                "Room Number: " + roomNumber + "\n" +
                        "Status: " + (reservationStatus ? "Reserved" : "Available") + "\n" +
                        "Reserved By: " + reservedBy + "\n" +
                        "Arrival Date: " + arrivalDate + "\n" +
                        "Departure Date: " + departureDate + "\n" +
                        "Price per Night: " + pricePerNight;
    }
}
