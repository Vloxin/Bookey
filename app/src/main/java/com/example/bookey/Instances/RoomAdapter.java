package com.example.bookey.Instances;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.bookey.NavigationScreens.NavigationActivity;
import com.example.bookey.Objects.Room;
import com.example.bookey.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class RoomAdapter extends ArrayAdapter<Room> {
    private Context mContext;
    private boolean isBookingTab;
    private EditText etArrivalDate;
    private EditText etDepartureDate;

    private View dialogView;
    private DatePickerDialog arrivalDatePickerDialog;
    private DatePickerDialog departureDatePickerDialog;
    private SimpleDateFormat dateFormat;

    public RoomAdapter(Context context, ArrayList<Room> rooms, boolean isBookingTab, NavigationActivity activity) {
        super(context, 0, rooms);
        mContext = context;
        this.isBookingTab = isBookingTab;
        mActivity = activity;
        dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        // Initialize the DatePickerDialogs
        initDatePickerDialogs();
    }


    public void setBookingTab(boolean isBookingTab) {
        this.isBookingTab = isBookingTab;
    }

    private NavigationActivity mActivity;

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_room, parent, false);
        }

        Room currentRoom = getItem(position);

        // Set the room details to the views in your list item layout
        TextView roomNumberTextView = convertView.findViewById(R.id.tv_room_number);
        TextView roomTypeTextView = convertView.findViewById(R.id.tv_room_type);
        TextView priceTextView = convertView.findViewById(R.id.tv_price_per_night);
        TextView arrivalDateTextView = convertView.findViewById(R.id.tv_arrival);
        TextView departureDateTextView = convertView.findViewById(R.id.tv_dep);
        ImageView roomImageView = convertView.findViewById(R.id.roomImg);

        dialogView = LayoutInflater.from(mContext).inflate(R.layout.dialog_booking, null);

        etArrivalDate = dialogView.findViewById(R.id.et_arrival_date);
        etDepartureDate = dialogView.findViewById(R.id.et_departure_date);

        etArrivalDate.setOnClickListener(v -> arrivalDatePickerDialog.show());
        etDepartureDate.setOnClickListener(v -> departureDatePickerDialog.show());

        roomNumberTextView.setText("Number = " + currentRoom.getRoomNumber());
        roomTypeTextView.setText("Type = " + currentRoom.getType());

        // Set the image based on room ID
        int resourceId = mContext.getResources().getIdentifier(
                "room_" + currentRoom.getRoomNumber(), "drawable", mContext.getPackageName());
        roomImageView.setImageResource(resourceId);

        if (isBookingTab) {
            arrivalDateTextView.setVisibility(View.GONE);
            departureDateTextView.setVisibility(View.GONE);
            priceTextView.setText("Price = " + currentRoom.getPricePerNight());
            convertView.setOnClickListener(view -> {
                Room selectedRoom = getItem(position);
                showBookingDialog(selectedRoom);
            });
        } else {
            arrivalDateTextView.setVisibility(View.VISIBLE);
            departureDateTextView.setVisibility(View.VISIBLE);
            int numberOfDays = calculateNumberOfDays(currentRoom.getArrivalDate(), currentRoom.getDepartureDate());
            double totalPrice = currentRoom.getPricePerNight() * numberOfDays;

            priceTextView.setText("Full Price    = " + totalPrice);
            arrivalDateTextView.setText("Arrival = " + currentRoom.getArrivalDate());
            departureDateTextView.setText("Departure = " + currentRoom.getDepartureDate());
            convertView.setOnClickListener(view -> {
                Room selectedRoom = getItem(position);
                mActivity.showCancelReservationDialog(selectedRoom);

            });

        }


        return convertView;
    }

    private void initDatePickerDialogs() {
        Calendar calendar = Calendar.getInstance();

        DatePickerDialog.OnDateSetListener arrivalDateSetListener = (view, year, monthOfYear, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            String arrivalDate = dateFormat.format(calendar.getTime());
            etArrivalDate.setText(arrivalDate);
        };

        DatePickerDialog.OnDateSetListener departureDateSetListener = (view, year, monthOfYear, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            String departureDate = dateFormat.format(calendar.getTime());
            etDepartureDate.setText(departureDate);
        };

        arrivalDatePickerDialog = new DatePickerDialog(mContext, arrivalDateSetListener,
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        departureDatePickerDialog = new DatePickerDialog(mContext, departureDateSetListener,
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
    }

    private void showBookingDialog(Room room) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        builder.setView(dialogView);

        builder.setPositiveButton("Book", (dialogInterface, i) -> {
            String arrivalDate = etArrivalDate.getText().toString().trim();
            String departureDate = etDepartureDate.getText().toString().trim();

            if (isValidDates(arrivalDate, departureDate)) {
                // Book the room
                mActivity.bookRoom(room, arrivalDate, departureDate);

                // Notify the adapter that the data set has changed
                notifyDataSetChanged();

                // Show a toast or perform any other action to indicate successful booking
                Toast.makeText(mContext, "Room booked successfully", Toast.LENGTH_SHORT).show();
            } else {
                // Show an error message or perform any other action to handle invalid dates
                Toast.makeText(mContext, "Invalid dates", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialogInterface, i) -> {
            dialogInterface.dismiss();
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private boolean isValidDates(String arrivalDate, String departureDate) {
        try {
            Date arrival = dateFormat.parse(arrivalDate);
            Date departure = dateFormat.parse(departureDate);

            // Check if arrival date is before departure date and not equal
            return arrival != null && departure != null && arrival.before(departure);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    private int calculateNumberOfDays(String arrivalDate, String departureDate) {
        try {
            Date arrival = dateFormat.parse(arrivalDate);
            Date departure = dateFormat.parse(departureDate);
            long duration = departure.getTime() - arrival.getTime();
            return (int) TimeUnit.MILLISECONDS.toDays(duration);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
