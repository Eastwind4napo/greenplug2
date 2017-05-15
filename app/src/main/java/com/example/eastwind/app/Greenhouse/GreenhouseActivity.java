package com.example.eastwind.app.Greenhouse;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.eastwind.app.Login;
import com.example.eastwind.app.R;
import com.example.eastwind.app.Tab_main;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class GreenhouseActivity extends AppCompatActivity {


    private RecyclerView mGreenList;

    private DatabaseReference databaseGreens;
    private DatabaseReference databaseUsers;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    private FloatingActionButton mFab;

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_greenhouse);

        toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        firebaseAuth=FirebaseAuth.getInstance();

        String uid = firebaseAuth.getInstance().getCurrentUser().getUid();
        databaseGreens= FirebaseDatabase.getInstance().getReference("Greenhouse");
        databaseGreens.child(uid).keepSynced(true);
        databaseUsers= FirebaseDatabase.getInstance().getReference("Users");
        databaseUsers.child(uid).keepSynced(true);

        mGreenList=(RecyclerView)findViewById(R.id.greenhouse_list);
        mGreenList.setHasFixedSize(true);
        mGreenList.setLayoutManager(new LinearLayoutManager(this));

        mFab = (FloatingActionButton) findViewById(R.id.fab);


        firebaseAuthListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser()==null)
                {
                    Intent mainIntent=new Intent(GreenhouseActivity.this,Login.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(mainIntent);
                }
            }
        };


        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent greenIntent=new Intent(GreenhouseActivity.this,RegisterGreenhouse.class);
                greenIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(greenIntent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        firebaseAuth.addAuthStateListener(firebaseAuthListener);

        final  String user_id=firebaseAuth.getCurrentUser().getUid();

        FirebaseRecyclerAdapter<Greenhouses,GreenViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<Greenhouses, GreenViewHolder>(
                Greenhouses.class,
                R.layout.greenhouse_row,
                GreenViewHolder.class,
                databaseGreens.child(user_id)
        ) {
            @Override
            protected void populateViewHolder(GreenViewHolder viewHolder, Greenhouses model, int position) {
                final String greenId = getRef(position).getKey();


                viewHolder.setName(model.getGreenName());
                viewHolder.setLocation(model.getGreenLocation());
                viewHolder.setDate(model.getGreenDate());
                viewHolder.setImage(getApplicationContext(), model.getGreenImage());


                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent greenIntent=new Intent(GreenhouseActivity.this,Tab_main.class);
                        //greenIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        greenIntent.putExtra("green_id",greenId);
                        startActivity(greenIntent);
                    }
                });


            }
        };
        mGreenList.setAdapter(firebaseRecyclerAdapter);
    }


    public static class GreenViewHolder extends RecyclerView.ViewHolder {

        View mView;
        Button button1;
        Button button2;

        public GreenViewHolder(View itemView) {
            super(itemView);
            mView=itemView;

        }
        public void setName(String name)
        {
            TextView post_name=(TextView)mView.findViewById(R.id.green_name);
            post_name.setText(name);
        }
        public void setLocation(String location)
        {
            TextView post_location=(TextView)mView.findViewById(R.id.green_location);
            post_location.setText(location);
        }
        public void setDate(String date)
        {
            TextView post_date=(TextView)mView.findViewById(R.id.green_date);
            post_date.setText(date);
        }
        public void setImage(final Context ctx, final String image)
        {
            final ImageView post_image=(ImageView) mView.findViewById(R.id.green_image);
            //Picasso.with(ctx).load(image).into(post_image);



            Picasso.with(ctx).load(image).networkPolicy(NetworkPolicy.OFFLINE).into(post_image, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    Picasso.with(ctx).load(image).into(post_image);
                }
            });

        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search_only,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.action_search)
        {

        }
        else if(item.getItemId()==R.id.action_logout)
        {
            logout();
        }

        return super.onOptionsItemSelected(item);
    }
    private void logout() {
        firebaseAuth.signOut();
    }

}
