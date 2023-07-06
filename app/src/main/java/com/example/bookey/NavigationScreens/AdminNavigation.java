package com.example.bookey.NavigationScreens;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.bookey.Instances.BaseActivity;
import com.example.bookey.Instances.RecyclerRoomAdapter;

import com.example.bookey.Objects.Room;
import com.example.bookey.R;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class AdminNavigation extends BaseActivity {

    private RecyclerRoomAdapter recyclerAdapter;
    private List<Room> roomList;

    private CollectionReference roomCollection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_navigation);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Initialize Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        roomCollection = db.collection("Rooms");

        // Initialize roomList and roomAdapter
        roomList = new ArrayList<>();
        recyclerAdapter = new RecyclerRoomAdapter(roomList);

        // Initialize roomRecyclerView
        RecyclerView roomRecyclerView = findViewById(R.id.roomRecyclerView);
        roomRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        roomRecyclerView.setAdapter(recyclerAdapter);

        // Initialize addRoomButton and set onClickListener
        ImageView addRoomButton = findViewById(R.id.addRoomButton);
        addRoomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show dialog box to add a room
                showDialogToAddRoom();
            }
        });

        // Load rooms from the database
        loadRoomsFromDatabase();
    }

    private void showDialogToAddRoom() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View addroomdialogview = getLayoutInflater().inflate(R.layout.addroomdialogview, null);
        builder.setView(addroomdialogview);
        builder.setTitle("Add a room");

        // Set positive button to add the room
        builder.setPositiveButton("Add", (dialogInterface, i) -> {
            // Get the room number from the dialog
            String roomNumber = ((EditText) addroomdialogview.findViewById(R.id.roomNumberEditText)).getText().toString();
            // Get the room type from the dialog
            String type = ((EditText) addroomdialogview.findViewById(R.id.roomTypeEditText)).getText().toString();
            // Get the room price from the dialog
            String pricePerNight = ((EditText) addroomdialogview.findViewById(R.id.roomPriceEditText)).getText().toString();

            // Create a new room object
            Room room = new Room(Integer.parseInt(roomNumber), Double.parseDouble(pricePerNight), type, false);

            // Add the room to the database
            roomCollection.add(room)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(this, "Room added successfully", Toast.LENGTH_SHORT).show();
                        loadRoomsFromDatabase(); // Refresh the room list after adding the room
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to add room", Toast.LENGTH_SHORT).show();
                    });
        });

        // Set negative button to cancel the dialog
        builder.setNegativeButton("Cancel", (dialogInterface, i) -> {
            dialogInterface.dismiss();
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void loadRoomsFromDatabase() {

        roomCollection.get().addOnSuccessListener(queryDocumentSnapshots -> {
            roomList.clear();
            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                Room room = documentSnapshot.toObject(Room.class);
                roomList.add(room);
            }
            recyclerAdapter.notifyDataSetChanged();
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Failed to load rooms", Toast.LENGTH_SHORT).show();
        });
    }
}
