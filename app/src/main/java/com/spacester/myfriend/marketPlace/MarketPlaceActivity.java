package com.spacester.myfriend.marketPlace;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.spacester.myfriend.NightMode;
import com.spacester.myfriend.R;
import com.spacester.myfriend.adapter.AdapterProduct;
import com.spacester.myfriend.model.ModelProduct;

import java.util.ArrayList;
import java.util.List;

public class MarketPlaceActivity extends AppCompatActivity {

    RecyclerView productList;
    AdapterProduct adapterProduct;
    List<ModelProduct> modelProducts;

    NightMode sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = new NightMode(this);
        if (sharedPref.loadNightModeState()){
            setTheme(R.style.DarkTheme);
        }else setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market_place);

        productList = findViewById(R.id.products);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
        productList.setLayoutManager(gridLayoutManager);
        modelProducts = new ArrayList<>();
        getAllProducts();

    }

    private void getAllProducts() {
        FirebaseDatabase.getInstance().getReference("Product").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                modelProducts.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelProduct modelUser = ds.getValue(ModelProduct.class);
                    modelProducts.add(modelUser);
                    adapterProduct = new AdapterProduct(MarketPlaceActivity.this, modelProducts);
                    productList.setAdapter(adapterProduct);
                    findViewById(R.id.progressBar).setVisibility(View.GONE);
                    if (adapterProduct.getItemCount() == 0){
                        findViewById(R.id.progressBar).setVisibility(View.GONE);
                        findViewById(R.id.products).setVisibility(View.GONE);
                        findViewById(R.id.nothing).setVisibility(View.VISIBLE);
                    }else {
                        findViewById(R.id.progressBar).setVisibility(View.GONE);
                        findViewById(R.id.products).setVisibility(View.VISIBLE);
                        findViewById(R.id.nothing).setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}