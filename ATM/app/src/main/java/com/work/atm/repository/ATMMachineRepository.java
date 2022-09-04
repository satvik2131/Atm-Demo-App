package com.work.atm.repository;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.work.atm.model.ATMMachine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class ATMMachineRepository {

    public final static String TABLE_NAME = "ATMMachine";

    private static ATMMachineRepository atmMachineRepository = null;

    public static ATMMachineRepository getInstance() {
        if(atmMachineRepository == null) {
            atmMachineRepository = new ATMMachineRepository();
        }
        return atmMachineRepository;
    }

    public ATMMachine create (ATMMachine atmMachine) {

        DatabaseReference mDatabase;
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.child(TABLE_NAME).push().setValue(atmMachine);
        return atmMachine;
    }

    public static int numberIn(int start, int end) {
        return new Random().nextInt(end + 1 - start) + start;
    }

    public void generateATMMachines() {
        String names[] = {
          "West side",
          "Near water body",
                "Business zone",
                "Shopping center"

        };

        for (int i = 0; i < names.length; i++) {
            Long balance = Long.valueOf(numberIn(10, 20) * 1000);
            ATMMachine atmMachine = new ATMMachine(names[i], balance);
            create(atmMachine);
        }
    }

    /**
     * retrieve all atm machines
     * @param atmMachineCallBack
     */
    public void read(ATMMachineCallBack atmMachineCallBack) {

        DatabaseReference mDatabase;
        mDatabase = FirebaseDatabase.getInstance().getReference();

        DatabaseReference ref2;
        ref2 = mDatabase.child(TABLE_NAME);

        Query query = ref2;

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(!dataSnapshot.hasChildren()) {
                    generateATMMachines();
                    read(atmMachineCallBack);
                    return;
                }

                List<ATMMachine> atmMachineList = new ArrayList<>();
                // Result will be holded Here
                for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                    ATMMachine atmMachine = dsp.getValue(ATMMachine.class);
                    atmMachine.setAtmId(dsp.getKey());
                    atmMachineList.add(atmMachine);
                }
                Collections.sort(atmMachineList, new Comparator<ATMMachine>() {
                    @Override
                    public int compare(ATMMachine o1, ATMMachine o2) {
                        return o1.getAtmName().compareTo(o2.getAtmName());
                    }
                });

                System.out.println("in repo  going to call");
                atmMachineCallBack.onAllATMMachineCallback(atmMachineList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    public void findById(String id, ATMMachineCallBack atmMachineCallBack) {
        DatabaseReference mDatabase;
        mDatabase = FirebaseDatabase.getInstance().getReference();

        DatabaseReference ref2;
        ref2 = mDatabase.child(TABLE_NAME).child(id);

        ref2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                ATMMachine atmMachine = dataSnapshot.getValue(ATMMachine.class);
                atmMachine.setAtmId(dataSnapshot.getKey());

                atmMachineCallBack.onSingleAtmMachineCallback(atmMachine);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void update(ATMMachine atmMachine) {

        DatabaseReference mDatabase;
        if(atmMachine == null || atmMachine.getAtmId() == null)
            return;

        mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.child(TABLE_NAME).child(atmMachine.getAtmId()).setValue(atmMachine);

    }

    public void delete(ATMMachine atmMachine) {

        if (atmMachine == null || atmMachine.getAtmId() == null)
            return;

        DatabaseReference mDatabase;
        mDatabase = FirebaseDatabase.getInstance().getReference();

        DatabaseReference ref2;
        ref2 = mDatabase.child(TABLE_NAME).child(atmMachine.getAtmId());
        ref2.removeValue();
    }
}
