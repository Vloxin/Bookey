package com.example.bookey.Instances;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookey.Objects.Room;
import com.example.bookey.R;

import java.util.List;

public class RecyclerRoomAdapter extends RecyclerView.Adapter<RecyclerRoomAdapter.RoomViewHolder> {

    private List<Room> roomList;

    public RecyclerRoomAdapter(List<Room> roomList) {
        this.roomList = roomList;
    }

    @NonNull
    @Override
    public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_room, parent, false);
        return new RoomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomViewHolder holder, int position) {
        Room room = roomList.get(position);
        // Set the image based on room ID (room ID is the same as the room number) room_roomnumber.png
        holder.roomImageView.setImageResource(holder.itemView.getContext().getResources().getIdentifier("room_" + room.getRoomNumber(), "drawable", holder.itemView.getContext().getPackageName()));

        // Set the room data to the views
        holder.roomNumberTextView.setText("Room:  " + room.getRoomNumber());
        holder.pricePerNightTextView.setText("Price:  " + room.getPricePerNight() + "€");
        holder.typeTextView.setText("Type:  " + room.getType());

        // Set additional room details
        holder.reservedByTextView.setText("Reserved by:  " + room.getReservedBy());
        holder.departureDateTextView.setText("Departure date:  " + room.getDepartureDate());
        holder.arrivalDateTextView.setText("Arrival date:  " + room.getArrivalDate());

    }

    @Override
    public int getItemCount() {
        return roomList.size();
    }

    public static class RoomViewHolder extends RecyclerView.ViewHolder {
        TextView roomNumberTextView;
        TextView pricePerNightTextView;
        TextView typeTextView;
        TextView reservedByTextView;
        TextView departureDateTextView;
        TextView arrivalDateTextView;
        ImageView roomImageView;

        public RoomViewHolder(@NonNull View itemView) {
            super(itemView);
            roomNumberTextView = itemView.findViewById(R.id.roomNumberTextView);
            pricePerNightTextView = itemView.findViewById(R.id.pricePerNightTextView);
            typeTextView = itemView.findViewById(R.id.typeTextView);
            reservedByTextView = itemView.findViewById(R.id.reservedBy);
            departureDateTextView = itemView.findViewById(R.id.departerdate);
            arrivalDateTextView = itemView.findViewById(R.id.arrivaldate);
            roomImageView = itemView.findViewById(R.id.admin_room_item_pic);
        }
    }
}
