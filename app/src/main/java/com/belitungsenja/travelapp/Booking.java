/*
 * Created by Android Rion on 9/15/19 10:28 PM
 * Copyright © 2019 . All rights reserved.
 * Last modified 9/15/19 10:22 PM
 * Kunjungi androidrion.com untuk tutorial Android Studio
 */

package com.belitungsenja.travelapp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;

public class Booking extends AppCompatActivity {

    Calendar myCalendar;
    DatePickerDialog.OnDateSetListener date;

    ImageButton btnplus, btnminus;
    TextView textmybalance, textJumlah, textTotal, namaPaket, durasiPaket;
    EditText fullname, email, phonenumber, datedeparture, timedeparture;
    ImageView gambarPaket, notice_balance;
    Button btn_reservasi;
    Integer mybalance = 0;
    Integer valuejumlahtiket = 1;
    Integer valuehargapaket = 0;
    Integer valuetotalharga = 0;
    Integer sisabalance = 0;

    DatabaseReference reference, reference2, reference3, reference4;

    String USERNAME_KEY = "usernamekey";
    String username_key = "";
    String username_key_new = "";

    Integer nomor_transaksi = new Random().nextInt();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Book This Trip");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getUsernameLocal();

        Bundle bundle = getIntent().getExtras();
        final String jenis_tiket_baru = bundle.getString("jenis_paket");

        gambarPaket = findViewById(R.id.gambarPaket);
        namaPaket = findViewById(R.id.namaPaket);
        durasiPaket = findViewById(R.id.durasiPaket);
        textmybalance = findViewById(R.id.balanceUser);
        notice_balance = findViewById(R.id.notice_balance);
        fullname = findViewById(R.id.fullname);
        email = findViewById(R.id.email);
        phonenumber = findViewById(R.id.phonenumber);
        btnplus = findViewById(R.id.tambahi);
        btnminus = findViewById(R.id.kurangi);
        textJumlah = findViewById(R.id.jumlahtamu);
        textTotal = findViewById(R.id.total);
        datedeparture = findViewById(R.id.datedeparture);
        timedeparture = findViewById(R.id.timedeparture);
        btn_reservasi = findViewById(R.id.btn_reservasi);

        textJumlah.setText(valuejumlahtiket.toString());

        btnminus.animate().alpha(0).setDuration(300);
        btnminus.setEnabled(false);
        notice_balance.setVisibility(View.GONE);

        reference2 = FirebaseDatabase.getInstance().getReference().child("Users").child(username_key_new);
        reference2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mybalance = Integer.valueOf(dataSnapshot.child("balance").getValue().toString());
                textmybalance.setText(mybalance.toString());
                fullname.setText(dataSnapshot.child("fullname").getValue().toString());
                email.setText(dataSnapshot.child("email").getValue().toString());
                phonenumber.setText(dataSnapshot.child("phonenumber").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        reference = FirebaseDatabase.getInstance().getReference().child("Paket").child(jenis_tiket_baru);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                namaPaket.setText(dataSnapshot.child("nama_paket").getValue().toString());
                durasiPaket.setText(dataSnapshot.child("durasi").getValue().toString());
                Picasso.with(Booking.this).load(dataSnapshot.child("url_thumbnail").getValue().toString()).centerCrop().fit().into(gambarPaket);
                valuehargapaket = Integer.valueOf(dataSnapshot.child("harga").getValue().toString());
                valuetotalharga = valuejumlahtiket * valuehargapaket;
                textTotal.setText(valuetotalharga.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        btnplus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                valuejumlahtiket += 1;
                textJumlah.setText(valuejumlahtiket.toString());
                if (valuejumlahtiket > 1) {
                    btnminus.animate().alpha(1).setDuration(300).start();
                    btnminus.setEnabled(true);
                }
                if (valuejumlahtiket >= 15) {
                    btnplus.animate().alpha(0).setDuration(300);
                    btnplus.setEnabled(false);
                }
                valuetotalharga = valuehargapaket * valuejumlahtiket;
                textTotal.setText(valuetotalharga.toString());
                if (valuetotalharga > mybalance) {
                    btn_reservasi.animate().translationY(250).alpha(0).setDuration(350).start();
                    btn_reservasi.setEnabled(false);
                    textmybalance.setTextColor(Color.parseColor("#FFCC0000"));
                    notice_balance.setVisibility(View.VISIBLE);
                }
            }
        });

        btnminus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                valuejumlahtiket -= 1;
                textJumlah.setText(valuejumlahtiket.toString());
                if (valuejumlahtiket < 2) {
                    btnminus.animate().alpha(0).setDuration(300);
                    btnminus.setEnabled(false);
                }
                if (valuejumlahtiket < 15) {
                    btnplus.animate().alpha(1).setDuration(300).start();
                    btnplus.setEnabled(true);
                }
                valuetotalharga = valuehargapaket * valuejumlahtiket;
                textTotal.setText(valuetotalharga.toString());
                if (valuetotalharga < mybalance) {
                    btn_reservasi.animate().translationY(0).alpha(1).setDuration(350).start();
                    btn_reservasi.setEnabled(true);
                    textmybalance.setTextColor(Color.parseColor("#FF99CC00"));
                    notice_balance.setVisibility(View.GONE);
                }
            }
        });

        myCalendar = Calendar.getInstance();
        date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }
        };

        datedeparture.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(Booking.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        timedeparture.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(Booking.this, new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        timedeparture.setText(selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

            }
        });

        btn_reservasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reference3 = FirebaseDatabase.getInstance()
                        .getReference().child("Tickets").child(username_key_new)
                        .child(namaPaket.getText().toString() + nomor_transaksi);
                reference3.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        reference3.getRef().child("id_ticket").setValue(namaPaket.getText().toString() + nomor_transaksi);
                        reference3.getRef().child("nama_paket").setValue(namaPaket.getText().toString());
                        reference3.getRef().child("durasi").setValue(durasiPaket.getText().toString());
                        reference3.getRef().child("nama").setValue(fullname.getText().toString());
                        reference3.getRef().child("email").setValue(email.getText().toString());
                        reference3.getRef().child("phonenumber").setValue(phonenumber.getText().toString());
                        reference3.getRef().child("jumlah_tamu").setValue(valuejumlahtiket.toString());
                        reference3.getRef().child("date").setValue(datedeparture.getText().toString());
                        reference3.getRef().child("time").setValue(timedeparture.getText().toString());
                        reference3.getRef().child("total").setValue(textTotal.getText().toString());

                        Intent intent = new Intent(Booking.this, SuccessBooking.class);
                        startActivity(intent);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                reference4 = FirebaseDatabase.getInstance().getReference().child("Users").child(username_key_new);
                reference4.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        sisabalance = mybalance - valuetotalharga;
                        reference4.getRef().child("balance").setValue(sisabalance);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return false;
    }

    private void updateLabel() {
        EditText tanggal = findViewById(R.id.datedeparture);
        String myFormat = "dd-MMMM-yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        tanggal.setText(sdf.format(myCalendar.getTime()));
    }

    public void getUsernameLocal() {
        SharedPreferences sharedPreferences = getSharedPreferences(USERNAME_KEY, MODE_PRIVATE);
        username_key_new = sharedPreferences.getString(username_key, "");
    }

}
