package com.example.bioauthentication.user;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bioauthentication.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

public class NewUserActivity extends AppCompatActivity {

    FirebaseDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);

        db = FirebaseDatabase.getInstance();

        final EditText fullName = findViewById(R.id.new_user);
        Button addUser = findViewById(R.id.add_user_button);
        addUser.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 final String userName = fullName.getText().toString();

                 if(userName.isEmpty()) {
                     Toast.makeText(getApplicationContext(),R.string.required_value,Toast.LENGTH_SHORT).show();
                     return;
                 }
                 DatabaseReference usersRef = db.getReference("users");
                 usersRef.runTransaction(new Transaction.Handler() {
                     @NonNull
                     @Override
                     public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                         int mayor = 0;
                         for (MutableData currentMutable : mutableData.getChildren()) {
                             User user = currentMutable.getValue(User.class);
                             assert user != null;
                             if ((user.getName().trim()).equalsIgnoreCase(userName.trim())){
                                 Log.d("TAG", "EQUALS ");
                                 Transaction.abort();
                             }
                             mayor = Math.max(user.getUid(), mayor);
                             Log.d("TAG", "doTransaction: "+user.getName()+"user"+ userName);
                         }
                         final User newUser = new User();
                         newUser.setName(userName);
                         newUser.setUid(mayor+1);
                         mutableData.child(newUser.getUid()+"").setValue(newUser);
                         return Transaction.success(mutableData);
                     }

                     @Override
                     public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                         Log.d("TAG", "onComplete: "+databaseError);
                     }
                 });
             }
         });

    }

}
