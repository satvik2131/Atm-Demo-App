package com.work.atm.repository;

import com.work.atm.model.User;

import java.util.List;

public interface UserCallBack {
    void onLoggedInUserCallback(User user);

    void onAllUserCallback(List<User> userList);
}
