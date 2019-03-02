package edu.arizona.cs;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private DatabaseReference mDatabase ;
    Button addButton, deleteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(MainActivity.this);
        setContentView(R.layout.activity_main);


        mDatabase = FirebaseDatabase.getInstance().getReference("users");
        addButton = (Button) findViewById(R.id.add);
        deleteButton = (Button) findViewById(R.id.delete);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeToDataBase(mDatabase);
            }
        });
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeUser(mDatabase);
            }
        });
        System.out.println("Here!!!!!!!!!!!!!!");


        // NOTE: this is just examples to show how to use firebase API
        // These are just testing functions, can not be used directly for the project
        //uncomment the following one by one to test
//        readDataBase(mDatabase);
//        writeToDataBase(mDatabase);
//        updateDatabase(mDatabase);
//        removeData(mDatabase);

    }

    // read data from database once
    private void readDataBase(DatabaseReference database){
        if (database == null) {
            System.out.println("database is null");
            return;
        }

        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                // we can use dataSnapshot whatever we want
                System.out.println("------------------------------------------------");
                System.out.println(dataSnapshot);
                System.out.println(dataSnapshot.getValue());
                System.out.println(dataSnapshot.child("1"));
                System.out.println(dataSnapshot.child("1").child("songList").child("1").getValue());
//                String result = dataSnapshot.child("1").child("songList").child("1").getValue().toString();
//                System.out.println(result);
                System.out.println("------------------------------------------------");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    // this will fetch data whenever there is a change in data base
    private void readDataBaseDynamic(DatabaseReference database){
        if (database == null) {
            System.out.println("database is null");
            return;
        }
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //similar like above, we can use dataSnapshot
                System.out.println(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    // write something to database
    private void writeToDataBase(DatabaseReference database){
        //create a object
        String firsName = "Tom";
        String lastName = "Jerry";
        String email = "tj@arizona.edu";
        ArrayList<String> songList = new ArrayList<>();
        songList.add("mouse and cat");
        songList.add("paw patrol");
        User user = new User(firsName, lastName, email, songList);
        //write it to database
        // if "1" does not exist, it will create
        database.child("1").setValue(user);



    }

    //NOTE: we can also use setValue() directly
    private void updateDatabase(DatabaseReference database){
        ArrayList<String> songList = new ArrayList<>();
        songList.add("mouse and cat");
        songList.add("paw patrol");
        songList.add("the cat in the hat");
        database.child("1").child("songList").setValue(songList);
    }

    //remove data
    private void removeData(DatabaseReference database){
        database.child("1").child("songList").removeValue();
    }
    private  void removeUser(DatabaseReference database){
        database.child("1").removeValue();
    }
}
