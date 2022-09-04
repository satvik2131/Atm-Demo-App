package com.work.atm.repository;

import com.work.atm.model.ATMMachine;

import java.util.List;

public interface ATMMachineCallBack {
    void onAllATMMachineCallback(List<ATMMachine> atmMachineList);

    void onSingleAtmMachineCallback(ATMMachine atmMachine);
}
