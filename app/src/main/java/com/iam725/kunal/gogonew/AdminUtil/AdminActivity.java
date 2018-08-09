package com.iam725.kunal.gogonew.AdminUtil;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.iam725.kunal.gogonew.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AdminActivity extends AppCompatActivity
implements AdminRecyclerAdapter.AdminClickListener{

    public static final String CHECK_ACTION = "approved";
    public static final String CANCEL_ACTION = "rejected";
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private DatabaseReference userListDatabaseReference;
    private ChildEventListener userListChildEventListener;
    private ChildEventListener mChildEventListener;
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    AdminRecyclerAdapter mAdapter;
    ArrayList<AdminItemData> arrayList = new ArrayList<AdminItemData>();
    AdminItemData tempAdminItemData;
    String uid;
    Map<String, String> map = new HashMap<>();

    SharedPreferences loginPrefs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_page);


        Toolbar toolbar = findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference().child("Requests");
        userListDatabaseReference = mFirebaseDatabase.getReference().child("userList");
        auth = FirebaseAuth.getInstance();
        loginPrefs = getSharedPreferences("userId", MODE_PRIVATE);
        setUserListListener();

        mAdapter = new AdminRecyclerAdapter(arrayList, this);
        RecyclerView recyclerView = findViewById(R.id.admin_page_recycler);
        recyclerView.setAdapter(mAdapter);
        fetchData();
        Log.v("AdminActivity", "arrayListCreated");
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home :
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void fetchData(){

        if(mDatabaseReference == null){
            return;
        }

        mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.v("AdminActivity", dataSnapshot.toString());
                AdminItemData adminItemData = new AdminItemData(dataSnapshot.getKey(),
                        (String)dataSnapshot.getValue());
                arrayList.add(adminItemData);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                String email = dataSnapshot.getKey();
                String status = (String)dataSnapshot.getValue();
                Log.v("AdminActivity", "Status Approved1"+status);
                if(status.toLowerCase().equals("approved")){
                    Log.v("AdminActivity", "Status Approved2"+status);
                    UserListItem userListItem = new UserListItem(email, false);
                    userListDatabaseReference.child(map.get(userListItem.getEmail())).setValue(userListItem);
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                tempAdminItemData = null;
                String key = dataSnapshot.getKey();
                Log.v("AdminActivity", "DATA removed "+key);
                for(int i = 0; i<arrayList.size(); i++){
                    tempAdminItemData = arrayList.get(i);
                    if(tempAdminItemData.getEncodedKey().equals(key)){
                        arrayList.remove(i);
                        mAdapter.notifyItemRemoved(i);
                        break;
                    }
                }
                if (((String) dataSnapshot.getValue()).toLowerCase().equals("approved")) {
                    Toast.makeText(AdminActivity.this, tempAdminItemData.getKey()+" Approved",
                            Toast.LENGTH_SHORT).show();
                    Log.v("AdminActivity", "RemovedFromRequests");
                    return;
                }
                CoordinatorLayout coordinatorLayout = findViewById(R.id.admin_coordinator_layout);
                Snackbar snackbar = Snackbar.make(coordinatorLayout, "request is rejected", Snackbar.LENGTH_SHORT);
                snackbar.setAction(R.string.undo_string, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDatabaseReference.child(tempAdminItemData.getKey()).setValue(tempAdminItemData.getRequest());
                    }
                });
                snackbar.show();
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        mDatabaseReference.addChildEventListener(mChildEventListener);
    }

    @Override
    public void onClickAction(final int pos, String action) {
        switch (action){
            case CHECK_ACTION:
                Log.v("AdminActivity", "CHECK_ACTION"+arrayList.get(pos).getKey());
                Toast.makeText(this, "Approving", Toast.LENGTH_SHORT).show();
                final String password = GenerateRandomPassword.randomString(4);
                auth.createUserWithEmailAndPassword(arrayList.get(pos).getKey(),password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(pos>arrayList.size()) return;
                        if(task.isSuccessful()){
                            Log.v("AdminActivity", "UserAuthenticated1"+password);
//                            UserListItem userListItem = new UserListItem(arrayList.get(pos).getEncodedKey(), false);
//                            arrayList.get(pos).setPassword(password);
                            uid = auth.getCurrentUser().getUid();
                            String to = arrayList.get(pos).getKey();
                            map.put(to, uid);
                            Log.v("AdminActivity", "UserAuthenticated2"+uid);
//                            String password = arrayList.get(pos).getPassword();
                            String subject = "Login Credentials for GoGo";
                            String message = "Login Credentials for GoGo\n Email-id: "+to+"\nPassword: "+password;;
                            Intent email = new Intent(Intent.ACTION_SEND);
                            email.putExtra(Intent.EXTRA_EMAIL, new String[]{to});
                            email.putExtra(Intent.EXTRA_SUBJECT, subject);
                            email.putExtra(Intent.EXTRA_TEXT, message);
                            email.setType("message/rfc822");
                            startActivity(Intent.createChooser(email, "Choose an Email client :"));
                            mDatabaseReference.child(arrayList.get(pos).getEncodedKey()).setValue("Approved");
                        }
                        else {
                            Log.v("AdminActivity", "UserNotAuthenticated");
                            if(task.getException()instanceof FirebaseAuthUserCollisionException){
                                Log.v("AdminActivity", "UserNotAuthenticated1");
                                uid = auth.getCurrentUser().getUid();
                                mDatabaseReference.child(arrayList.get(pos).getEncodedKey())
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                Log.v("AdminActivity", "UserNotAuthenticated2"+dataSnapshot.getValue());
                                                String status = (String)dataSnapshot.getValue();
                                                if(status == null){
                                                    Toast.makeText(AdminActivity.this, "Connection failed, try again", Toast.LENGTH_SHORT).show();
                                                    return;
                                                }
                                                map.put(arrayList.get(pos).getKey(), uid);
                                                if(status.toLowerCase().equals("approved")){
                                                    Log.v("AdminActivity", "UserNotAuthenticated4");
                                                    UserListItem userListItem = new UserListItem(arrayList.get(pos).getEncodedKey(), false);
                                                    uid = auth.getCurrentUser().getUid();
                                                    userListDatabaseReference.child(uid).setValue(userListItem);
                                                    Log.v("AdminActivity", "UserNotAuthenticated5"+dataSnapshot.getKey());
                                                    mDatabaseReference.child((String)(dataSnapshot.getKey())).removeValue();
                                                }
                                                else{
                                                    Log.v("AdminActivity", "UserNotAuthenticated3");
                                                    mDatabaseReference.child(arrayList.get(pos).getEncodedKey()).setValue("Approved");
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                            }
                        }
                    }
                });
                return;
            case CANCEL_ACTION:
                Log.v("AdminActivity", "CANCEL_ACTION");
                String key = arrayList.get(pos).getEncodedKey();
                mDatabaseReference.child(key).removeValue();
                Log.v("AdminActivity", "key "+ key);
                return;
        }
    }

    private void setUserListListener(){
        userListChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                if(dataSnapshot.child("email").getValue()!=null){
                    Log.v("AdminActivity", "Added to User List"+dataSnapshot.child("email").getValue());
                    mDatabaseReference.child(((String)dataSnapshot.child("email").getValue()).replace(".",",")).removeValue();
                }
//            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        userListDatabaseReference.addChildEventListener(userListChildEventListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mChildEventListener!=null) mDatabaseReference.removeEventListener(mChildEventListener);
    }
//    ODM643d35f1
}
