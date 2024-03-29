package com.spacester.myfriend.reel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.spacester.myfriend.R;
import com.spacester.myfriend.adapter.AdapterReel;
import com.spacester.myfriend.model.ModelReel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ReelActivity extends AppCompatActivity {

    ViewPager2 viewPager2,viewPager1;
    List<ModelReel> modelReels, modelReelList;
    List<String> idList;

    private static String type = "one";
    public static String getType() {
        return type;
    }
    public ReelActivity(){

    }

    @SuppressLint("ObsoleteSdkInt")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_reel);


        viewPager2 = findViewById(R.id.videoPager);
        viewPager1 = findViewById(R.id.videoPagerOne);

        modelReels = new ArrayList<>();
        modelReelList = new ArrayList<>();

        idList = new ArrayList<>();

        getAllReels();

        //TabLayout
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tabLayout.getSelectedTabPosition() == 0) {
                    getAllReels();
                    viewPager1.setVisibility(View.GONE);
                    viewPager2.setVisibility(View.VISIBLE);
                    type = "one";
                 } else if (tabLayout.getSelectedTabPosition() == 1) {
                    getFollowing();
                    viewPager1.setVisibility(View.VISIBLE);
                    viewPager2.setVisibility(View.GONE);
                    type = "two";
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


    }

    private void getFollowing() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Follow")
                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).child("Following");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                idList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    idList.add(snapshot.getKey());
                }
                FirebaseDatabase.getInstance().getReference("Reels").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds: dataSnapshot.getChildren()){
                            ModelReel modelUser = ds.getValue(ModelReel.class);
                            for (String id : idList) {
                                assert modelUser != null;
                                if (modelUser.getId().equals(id)){
                                    modelReelList.add(modelUser);
                                }
                                viewPager1.setAdapter(new AdapterReel(modelReelList));
                                if (getIntent().hasExtra("position")){
                                    String position  = getIntent().getStringExtra("position");
                                    String mType  = getIntent().getStringExtra("type");
                                    if (mType.equals("one")){
                                        viewPager2.setCurrentItem(Integer.parseInt(position));
                                    }else {
                                        viewPager1.setCurrentItem(Integer.parseInt(position));
                                    }
                                }
                                if (new AdapterReel(modelReelList).getItemCount() == 0){
                                    findViewById(R.id.no).setVisibility(View.VISIBLE);
                                    viewPager1.setVisibility(View.GONE);
                                }else {
                                    findViewById(R.id.no).setVisibility(View.GONE);
                                    viewPager1.setVisibility(View.VISIBLE);
                                }
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getAllReels() {
        FirebaseDatabase.getInstance().getReference("Reels").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    ModelReel modelUser = ds.getValue(ModelReel.class);
                    modelReels.add(modelUser);
                    viewPager2.setAdapter(new AdapterReel(modelReels));
                    if (getIntent().hasExtra("position")){
                        String position  = getIntent().getStringExtra("position");
                        String mType  = getIntent().getStringExtra("type");
                        if (mType.equals("one")){
                            viewPager2.setCurrentItem(Integer.parseInt(position));
                        }else {
                            viewPager1.setCurrentItem(Integer.parseInt(position));
                        }
                    }
                    if (new AdapterReel(modelReels).getItemCount() == 0){
                       findViewById(R.id.no).setVisibility(View.VISIBLE);
                       viewPager2.setVisibility(View.GONE);
                    }else {
                        findViewById(R.id.no).setVisibility(View.GONE);
                        viewPager2.setVisibility(View.VISIBLE);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}