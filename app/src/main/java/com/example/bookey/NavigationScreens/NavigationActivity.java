package com.example.bookey.NavigationScreens;

import androidx.appcompat.app.AppCompatActivity;
import androidx.collection.ArraySet;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.example.bookey.Authentication.Login;
import com.example.bookey.Instances.BaseActivity;
import com.example.bookey.Instances.RoomAdapter;
import com.example.bookey.Objects.Room;
import com.example.bookey.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;


public class NavigationActivity extends BaseActivity {

    private ArrayList<Room> roomList = new ArrayList<>();
    private ArrayList<Room> reservedRooms = new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private RoomAdapter bookingAdapter;
    private RoomAdapter keyAdapter;

    private ArraySet<Room> rooms = new ArraySet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.action_booking); // Set booking tab as the default selected tab

        FrameLayout container = findViewById(R.id.container);
        View bookingView = getLayoutInflater().inflate(R.layout.view_booking, container, false);
        ListView bookingListView = bookingView.findViewById(R.id.lv_hotel);

        View keyView = getLayoutInflater().inflate(R.layout.view_key, container, false);
        ListView keyListView = keyView.findViewById(R.id.key_list_view);

        View settingsView = getLayoutInflater().inflate(R.layout.view_settings, container, false);

        bookingAdapter = new RoomAdapter(NavigationActivity.this, roomList, true, NavigationActivity.this);
        bookingListView.setAdapter(bookingAdapter);

        keyAdapter = new RoomAdapter(NavigationActivity.this, reservedRooms, false, NavigationActivity.this);
        keyListView.setAdapter(keyAdapter);



        container.addView(bookingView);

        loadUnreservedRooms(bookingAdapter); // Load the unreserved rooms from Firestore

        Log.d("Firestore", "Data retrieval successful");
        Toast.makeText(NavigationActivity.this, "Data retrieval successful", Toast.LENGTH_SHORT).show();


        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            // Reset all items to default background
            for (int i = 0; i < bottomNavigationView.getMenu().size(); i++) {
                MenuItem menuItem = bottomNavigationView.getMenu().getItem(i);
                View view = bottomNavigationView.findViewById(menuItem.getItemId());
                view.setBackgroundColor(getResources().getColor(android.R.color.black));
            }

            // Perform your desired action here based on the selected item
            switch (item.getItemId()) {
                case R.id.action_booking:
                    // Handle booking tab click
                    // Show the booking view
                    container.removeAllViews();
                    bookingListView.setAdapter(bookingAdapter);
                    loadUnreservedRooms(    bookingAdapter);
                    container.addView(bookingView);
                    return true;

                case R.id.action_key:
                    container.removeAllViews();
                    container.addView(keyView);

                    // Load reserved rooms based on username
                    String username = getUsernameFromSharedPreferences(); // Implement your logic to get the username from SharedPreferences
                    loadReservedRoomsByUser(username, keyAdapter);

                    keyListView.setAdapter(keyAdapter);
                    return true;

                case R.id.action_settings:
                    // Handle settings tab click
                    // Show the settings view
                    container.removeAllViews();
                    Button logoutButton = settingsView.findViewById(R.id.logout);
                    logoutButton.setOnClickListener(view -> {
                        // Clear the username from SharedPreferences
                        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.clear();
                        editor.apply();
                        // jump to login screne
                        Intent intent = new Intent(NavigationActivity.this, Login.class);

                        // Go back to the login screen
                        finish();
                    });

                    container.addView(settingsView);
                    return true;

                default:
                    return false;
            }
        });
    }


    private String getUsernameFromSharedPreferences() {

        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);

        return sharedPreferences.getString("username", "");
    }


    private void loadReservedRoomsByUser(String username, RoomAdapter adapter) {
        reservedRooms.clear(); // Clear the existing reservedRooms set

        db.collection("Rooms")
                .whereEqualTo("reservedBy", username)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            Room room = document.toObject(Room.class);
                            reservedRooms.add(room);
                        }
                        adapter.notifyDataSetChanged(); // Notify the bookinglist adapter
                        if(reservedRooms.isEmpty()){
                            Toast.makeText(NavigationActivity.this, "No rooms reserved", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Log.e("Firestore", "Error retrieving reserved rooms: " + task.getException());
                        Toast.makeText(NavigationActivity.this, "Error retrieving reserved rooms", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    public void showCancelReservationDialog(Room room) {
        AlertDialog.Builder builder = new AlertDialog.Builder(NavigationActivity.this);
        builder.setTitle("Cancel Reservation")
                .setMessage("Do you want to cancel the room reservation?")
                .setPositiveButton("Yes", (dialogInterface, i) -> {
                    // Perform cancellation logic here
                   cancelReservation(room);
                })
                .setNegativeButton("No", (dialogInterface, i) -> {
                    //  dismiss the dialog
                    dialogInterface.dismiss();
                })
                .show();
    }

    private void cancelReservation(Room room) {
        int roomId = room.getRoomNumber(); // Assuming you have a method to retrieve the room ID

        // Search for the room in the database using the room ID
        db.collection("Rooms")
                .whereEqualTo("roomNumber", roomId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        // Get the document reference for the found room
                        DocumentSnapshot document = task.getResult().getDocuments().get(0);

                        // Update the fields
                        document.getReference().update(
                                "arrivalDate", "",
                                "departureDate", "",
                                "reservationStatus", false,
                                "reservedBy", ""
                        ).addOnSuccessListener(aVoid -> {
                            // Successful update
                            Toast.makeText(NavigationActivity.this, "Reservation canceled successfully", Toast.LENGTH_SHORT).show();

                            // Refresh the reserved rooms list
                            String username = getUsernameFromSharedPreferences();
                            loadReservedRoomsByUser(username, keyAdapter);
                        }).addOnFailureListener(e -> {
                            // Error occurred while updating
                            Log.e("Firestore", "Error canceling reservation: " + e.getMessage());
                            Toast.makeText(NavigationActivity.this, "Error canceling reservation", Toast.LENGTH_SHORT).show();
                        });
                    }
                    else {
                        // Room not found in the database
                        Toast.makeText(NavigationActivity.this, "Room not found", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void loadUnreservedRooms(RoomAdapter adapter) {
        roomList.clear(); // Clear the existing roomList
        rooms.clear();
        db.collection("Rooms").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot document : task.getResult()) {
                    Room room = document.toObject(Room.class);


                    if (!room.getReservationStatus()){
                        rooms.add(room);
                    }

                    // print to console
                    Log.d("Firestore", document.getId() + " => " + document.getData());
                }

                // Update roomList with the retrieved data
                roomList.addAll(rooms);

                if (roomList.isEmpty()) {
                    // Show a message if the roomList is empty
                    Toast.makeText(NavigationActivity.this, "No rooms found", Toast.LENGTH_SHORT).show();
                }

                if (roomList.isEmpty()) {
                    // Show a message if the roomList is empty
                    Toast.makeText(NavigationActivity.this, "No rooms found", Toast.LENGTH_SHORT).show();
                }

                // Notify the adapter that the data set has changed
                adapter.notifyDataSetChanged();

                // Log a success message or show a toast

            } else {
                // Log an error message or show a toast
                Log.e("Firestore", "Error retrieving data: " + task.getException());
                Toast.makeText(NavigationActivity.this, "Error retrieving data", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void bookRoom(Room room, String arrivalDate, String departureDate) {
        int roomId = room.getRoomNumber(); // Assuming you have a method to retrieve the room ID

        // Search for the room in the database using the room ID
        db.collection("Rooms")
                .whereEqualTo("roomNumber", roomId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        // Get the document reference for the found room
                        DocumentSnapshot document = task.getResult().getDocuments().get(0);

                        // Update the fields
                        document.getReference().update(
                                "arrivalDate", arrivalDate,
                                "departureDate", departureDate,
                                "reservationStatus", true,
                                "reservedBy", getUsernameFromSharedPreferences()
                        ).addOnSuccessListener(aVoid -> {
                            // Successful update
                            Toast.makeText(NavigationActivity.this, "Room booked successfully", Toast.LENGTH_SHORT).show();

                            // Refresh the unreserved rooms list
                            loadUnreservedRooms(bookingAdapter);
                        }).addOnFailureListener(e -> {
                            // Error occurred while updating
                            Log.e("Firestore", "Error booking room: " + e.getMessage());
                            Toast.makeText(NavigationActivity.this, "Error booking room", Toast.LENGTH_SHORT).show();
                        });
                    } else {
                        // Room not found in the database
                        Toast.makeText(NavigationActivity.this, "Room not found", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
