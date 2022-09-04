package com.work.atm.repository;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.work.atm.model.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserRepository {

    public final static String TABLE_NAME = "user";

    private String[] defaultUserEmails = {
            "abc@gmail.com",
            "def@gmail.com",
            "ghi@gmail.com"
    };

    private Map<String, Boolean> adminEmails = new HashMap<>();

    private static UserRepository userRepository;

    private UserRepository() {
        adminEmails.put("project.exec2021@gmail.com", true);
    }

    public static UserRepository getInstance() {
        if(userRepository == null)
            userRepository = new UserRepository();
        return userRepository;
    }

    public boolean isAdmin(User user) {
        return user.getAdmin();
    }

    public Boolean findIfAdmin(String email) {
        return adminEmails.containsKey(email);
    }

    public User create (User user) {

        DatabaseReference mDatabase;
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.child(TABLE_NAME).child(user.getUserId()).setValue(user);
        return user;
    }


//    public User create(FirebaseUser fbUser) {
//        User user = new User(fbUser.getUid(), fbUser.getEmail(),
//                fbUser.getDisplayName() == null ? fbUser.getEmail().substring(0, fbUser.getEmail().indexOf("@")) :
//                        fbUser.getDisplayName(),
//                null, 0L, findIfAdmin(fbUser.getEmail()));
//        return create(user);
//    }

    /**
     * get logged in user record from firebase auth
     * retrieve corresponding record from User table
     * @return
     */
    public void fetchLoggedInUser(UserCallBack userCallBack) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser == null){
            userCallBack.onLoggedInUserCallback(null);
            return;
        }

        DatabaseReference mDatabase;
        mDatabase = FirebaseDatabase.getInstance().getReference();

        DatabaseReference ref2;
        ref2 = mDatabase.child(TABLE_NAME).child(currentUser.getUid());

        Query query = ref2;

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                User user = dataSnapshot.getValue(User.class);
                userCallBack.onLoggedInUserCallback(user);

                /*
                if(!(dataSnapshot instanceof Map)) {
                    userCallBack.onLoggedInUserCallback(null);
                    return;
                }



                String userId, String email, String displayName, String bankAccount, Boolean admin

                Map map = (Map) dataSnapshot;
                User user = new User(map.get("userId"))
                userCallBack.onLoggedInUserCallback(user);

                for(Object entry : ((HashMap) dataSnapshot.getValue()).entrySet()) {
                    System.out.println(((Map.Entry)entry).getKey() + " => " + ((Map.Entry)entry).getValue());
                }

                if(!dataSnapshot.hasChildren()) {
                    userCallBack.onLoggedInUserCallback(create(currentUser));
                    return;
                }
                // Result will be holded Here
                for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                    User user = dsp.getValue(User.class);
                    userCallBack.onLoggedInUserCallback(user);
                    break;
                }
                */

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    public void read(UserCallBack userCallBack) {

        DatabaseReference mDatabase;
        mDatabase = FirebaseDatabase.getInstance().getReference();

        DatabaseReference ref2;
        ref2 = mDatabase.child(TABLE_NAME);

        Query query = ref2;

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                List<User> userList = new ArrayList<>();
                // Result will be holded Here
                for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                    User user = dsp.getValue(User.class);
                    userList.add(user);
                }
                Collections.sort(userList, new Comparator<User>() {
                    @Override
                    public int compare(User o1, User o2) {
                        if(o1 == null || o1.getEmail() == null || o2 == null || o2.getEmail() == null)
                            return 0;
                        return o1.getEmail().compareTo(o2.getEmail());
                    }
                });
                userCallBack.onAllUserCallback(userList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void update(User user) {DatabaseReference mDatabase;
        if(user == null || user.getUserId() == null)
            return;

        mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.child(TABLE_NAME).child(user.getUserId()).setValue(user);

    }


    public void delete(User user) {

        if (user == null || user.getUserId() == null)
            return;

        DatabaseReference mDatabase;
        mDatabase = FirebaseDatabase.getInstance().getReference();

        DatabaseReference ref2;
        ref2 = mDatabase.child(TABLE_NAME).child(user.getUserId());
        ref2.removeValue();
    }

    /**
     * firebase auth maintains a list of users registered in it
     * get all those registered users
     * @return
     * @throws Exception
     */
    /*
    public List<ExportedUserRecord> fetchUsersFromAuth() throws Exception {

        List<ExportedUserRecord> list = new ArrayList<>();

        // buffering no more than 1000 users in memory at a time.
        ListUsersPage page = FirebaseAuth.getInstance().listUsers(null);
        for (ExportedUserRecord user : page.iterateAll()) {
            System.out.println("User: " + user.getEmail());
            list.add(user);
        }

        return list;
    }

    **
     * create default users into firebase auth system
     * ignore exception if email already exist
     *
    public void injectDefaultUsers() throws FirebaseAuthException {
        for(String email : defaultUserEmails) {
            try {
                String displayName = email.substring(0, email.indexOf("@"));
                UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                        .setEmail(email)
                        .setEmailVerified(false)
                        .setPassword("a")
                        .setDisplayName(displayName)
                        .setDisabled(false);

                UserRecord userRecord = FirebaseAuth.getInstance().createUser(request);
            } catch(Exception ex) {
                throw ex;
            }
        }
    }

    **
     * go through list of users in firebase auth system
     * and create corresponding User record in local table
     *
    public void createUserRecords() throws Exception {
        List<ExportedUserRecord> authUsers = fetchUsersFromAuth();

        for(ExportedUserRecord authUser : authUsers) {
            User user = new User(authUser.getUid(), authUser.getEmail(), authUser.getDisplayName(),
                null, findIfAdmin(authUser.getEmail()));
            create(user);
        }
    }



    public void resetUserTable() throws Exception {
        try {
            injectDefaultUsers();
            createUserRecords();
        } catch(Exception ex) {
            System.err.println("Unable to create users into UserTable - " + ex.getMessage());
            throw ex;
        }
    }

    */
}
