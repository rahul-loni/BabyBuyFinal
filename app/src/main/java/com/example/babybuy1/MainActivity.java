package com.example.babybuy1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.babybuy1.Adapter.ItemsAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    FloatingActionButton fab;
    private ArrayList<Item> items;
    private DatabaseHelper databaseHelper;
    private RecyclerView itemRecyclerView;
    private ItemsAdapter itemsAdapter;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Top Navigation View
        navigationView=findViewById(R.id.navigation);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int id = item.getItemId();
                switch (id) {
                    case R.id.home_menu:
                        Toast.makeText(MainActivity.this, "Clicked Home Page ", Toast.LENGTH_SHORT).show();
                        Intent homeintent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(homeintent);
                        break;

                    case R.id.about_menu:
                        Toast.makeText(MainActivity.this, "Clicked About Page ", Toast.LENGTH_SHORT).show();
                        Intent aboutintent = new Intent(getApplicationContext(), Login.class);
                        startActivity(aboutintent);
                        break;

                    case R.id.contact_menu:
                        Toast.makeText(MainActivity.this, "Clicked contact  Page ", Toast.LENGTH_SHORT).show();
                        Intent conintent = new Intent(getApplicationContext(), Login .class);
                        startActivity(conintent);
                        break;

                    case R.id.share_menu:
                        Intent shareintent = new Intent(Intent.ACTION_SEND);
                        shareintent.setType("text/plain");
                        shareintent.putExtra(Intent.EXTRA_TEXT, "https://www.facebook.com/ ");
                        shareintent.putExtra(Intent.EXTRA_SUBJECT, "Your Application link hare ");
                        startActivity(Intent.createChooser(shareintent, "share Via"));
                        break;
                }
                return false;
            }
        });
        final DrawerLayout drawerLayout = findViewById(R.id.drewer);
        findViewById(R.id.imagemenu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });


        // initialize data
        items = new ArrayList<>();
        databaseHelper = new DatabaseHelper(this);

        // recycler view setup
        itemRecyclerView = findViewById(R.id.orderRecycler);
        setupRecyclerView();

        // item touch helper setup
        setupItemTouchHelper();

        // initialize
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> startActivity(AddItems.getIntent(getApplicationContext())));

    }

    private void setupItemTouchHelper() {
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Item item = items.get(position);

                if (direction == ItemTouchHelper.LEFT) {
                    databaseHelper.delete(item.getId());

                    items.remove(position);
                    itemsAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());

                    Toast.makeText(
                            getApplicationContext(),
                            "Item deleted.",
                            Toast.LENGTH_SHORT
                    ).show();
                } else if (direction == ItemTouchHelper.RIGHT) {
                    item.setPurchased(true);

                    databaseHelper.update(
                            item.getId(),
                            item.getName(),
                            item.getPrice(),
                            item.getDescription(),
                            item.getImage().toString(),
                            item.isPurchased()
                    );
                    itemsAdapter.notifyItemChanged(position);

                    Toast.makeText(
                            getApplicationContext(),
                            "Item updated.",
                            Toast.LENGTH_SHORT
                    ).show();
                }
            }
        });

        itemTouchHelper.attachToRecyclerView(itemRecyclerView);
    }

    @Override
    protected void onStart() {
        super.onStart();
        retrieveData();
    }

    private void retrieveData() {
        Cursor cursor = databaseHelper.getAll();
        if (cursor == null) {
            return;
        }

        items.clear();
        while (cursor.moveToNext()) {
            Item item = new Item();
            item.setId(cursor.getInt(0));
            item.setName(cursor.getString(1));
            item.setPrice(cursor.getDouble(2));
            item.setDescription(cursor.getString(3));
            item.setImage(Uri.parse(cursor.getString(4)));

            items.add(cursor.getPosition(), item);
            itemsAdapter.notifyItemChanged(cursor.getPosition());
            Log.d("MainActivity", "Item: " + item.getId() + " added at " + cursor.getPosition());
        }
    }

    private void setupRecyclerView(
    ) {
        itemsAdapter = new ItemsAdapter(items, (view, position) -> startActivity(ItemDetails.getIntent(
                getApplicationContext(),
                items.get(position).getId())
        ));
        itemRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        itemRecyclerView.setAdapter(itemsAdapter);
    }

}