package com.example.babybuy1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.babybuy1.Adapter.ItemsAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    FloatingActionButton fab;
    private ArrayList<Item> items;
    private DatabaseHelper databaseHelper;
    private RecyclerView itemRecyclerView;
    private ItemsAdapter itemsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                items.get(position))
        ));
        itemRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        itemRecyclerView.setAdapter(itemsAdapter);
    }
}