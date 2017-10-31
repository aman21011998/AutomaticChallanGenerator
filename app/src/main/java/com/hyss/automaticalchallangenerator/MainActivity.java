package com.hyss.automaticalchallangenerator;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

public class MainActivity extends AppCompatActivity {


    public Bitmap bitmap;
    String carNumber;
    ImageView imageView;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    TextView nameTextView,phoneno,email,vnumber;

    String name,emailid,phone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseReference=FirebaseDatabase.getInstance().getReference();

        nameTextView= (TextView) findViewById(R.id.name);
        phoneno=(TextView)findViewById(R.id.phone);
        email=(TextView)findViewById(R.id.email);
        vnumber=(TextView)findViewById(R.id.vnumber);
        //textView= (TextView) findViewById(R.id.);
        imageView=(ImageView)findViewById(R.id.imageView);
        //Toast.makeText(getApplicationContext(),generate().toString(),Toast.LENGTH_LONG).show();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Chalan Generator");
        getSupportActionBar().show();
        Toast.makeText(this, "FIRST STEP", Toast.LENGTH_SHORT).show();
        Toast.makeText(this, "DB abcd: "+databaseReference.toString(), Toast.LENGTH_SHORT).show();
//        databaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                String name = dataSnapshot.child("key").getValue().toString();
//                Toast.makeText(MainActivity.this, "NAME FROM DB: "+name, Toast.LENGTH_SHORT).show();
//                //if(name==null)
////                    {
////                        nameTextView.setText("----------");
////                    }
////                    else {
////                        Toast.makeText(MainActivity.this, String.valueOf(carNumber.length()), Toast.LENGTH_SHORT).show();
////                        nameTextView.setText(name);
////                    }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });

//
//        databaseReference.addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                pojo thispojo = dataSnapshot.getValue(pojo.class);
//                Toast.makeText(MainActivity.this, thispojo.getKey(), Toast.LENGTH_SHORT).show();
//
//            }
//
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onChildRemoved(DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                if (checkSelfPermission(Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA},
                            1);
                }

                else {
                    Intent cameraintent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraintent, 1);
                }
            }
        });
    }
    private void recognize(final Bitmap bitmap)
    {


        TextRecognizer recognizer = new TextRecognizer.Builder(getApplicationContext()).build();
        if (!recognizer.isOperational()) {
            Log.w("Error", "Dependencies are not yet avalilable");
        }
        else
        {
            Frame frame=new Frame.Builder().setBitmap(bitmap).build();

            SparseArray<TextBlock> items=recognizer.detect(frame);
            StringBuilder stringBuilder=new StringBuilder();

            for(int i=0;i<items.size();++i)
            {
                TextBlock item=items.valueAt(i);
                stringBuilder.append(item.getValue());
            }
            carNumber=stringBuilder.toString();
            if(carNumber.length()>5) {
                vnumber.setText(carNumber);
                Toast.makeText(this, "Length : "+carNumber.substring(carNumber.length()-4).trim(), Toast.LENGTH_SHORT).show();



                databaseReference.child(carNumber.substring(carNumber.length()-4)).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        name = dataSnapshot.child("Name").getValue().toString();
                        emailid=dataSnapshot.child("Email").getValue().toString();
                        phone=dataSnapshot.child("Phone").getValue().toString();

                        nameTextView.setText(name);
                        email.setText(emailid);
                        phoneno.setText(phone);
                        //Toast.makeText(MainActivity.this, "NAME FROM DB: "+name, Toast.LENGTH_SHORT).show();

                        sendemail(name,emailid);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });            }
            else {
                Toast.makeText(this, "INVALID : "+String.valueOf(carNumber.length()), Toast.LENGTH_SHORT).show();
            }
            Toast.makeText(this, "DR: "+databaseReference.toString(), Toast.LENGTH_SHORT).show();


        }
    }

    private void sendemail(String name, String emailid)
    {

        Log.i("Send email", "");
        String[] TO = {emailid};

        Intent emailIntent = new Intent(Intent.ACTION_SEND);

        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Challan For Violating Rule");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Mr."+name+" u have been fined Rs.500 for violating the traffic rule.Please deposit the fees to the district court within 5 days"+"\n Chalan number="+generate());

        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            finish();
            Log.i("Finished sending email.", "");
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(MainActivity.this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    protected  void onActivityResult(int requestcode,int resultcode,Intent data)
    {
        bitmap=(Bitmap)data.getExtras().get("data");
        imageView.setImageBitmap(bitmap);
        //  image.setImageBitmap(bitmap);
        recognize(bitmap);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



     public int[] generate()
     {
         Random r=new Random();
         int n[] = new int[5];
         for(int i=0;i<5;i++)
         {
             n[i]=r.nextInt(10);
         }

         return n;
     }
}
