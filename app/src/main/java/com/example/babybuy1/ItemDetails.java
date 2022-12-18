package com.example.babybuy1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class ItemDetails extends AppCompatActivity {
    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String PRICE = "price";
    public static final String DESCRIPTION = "description";
    public static final String IMAGE = "image";

    public static Intent getIntent(Context context, Item item) {
        Intent intent = new Intent(context, ItemDetails.class);
        intent.putExtra(ID, item.getId());
        intent.putExtra(NAME, item.getName());
        intent.putExtra(PRICE, item.getPrice().toString());
        intent.putExtra(DESCRIPTION, item.getDescription());
        intent.putExtra(IMAGE, item.getImage().toString());

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        ImageView imageView = findViewById(R.id.imageViewItem);
        TextView textViewName = findViewById(R.id.textViewName);
        TextView textViewPrice = findViewById(R.id.textViewPrice);
        TextView textViewDescription = findViewById(R.id.textViewDescription);
        Button buttonEdit = findViewById(R.id.buttonEditItem);

        Bundle bundle = getIntent().getExtras();
        long id = bundle.getLong(ItemDetails.ID);
        String name = bundle.getString(ItemDetails.NAME);
        String price = bundle.getString(ItemDetails.PRICE);
        String description = bundle.getString(ItemDetails.DESCRIPTION);

        Uri imageUri = Uri.EMPTY;
        try {
            imageUri = Uri.parse(bundle.getString(ItemDetails.IMAGE));
        } catch (NullPointerException e) {
            Toast.makeText(
                    this,
                    "Error occurred in identifying image resource!",
                    Toast.LENGTH_SHORT
            ).show();
        }
        if (!imageUri.equals(Uri.EMPTY)){
            imageView.setImageURI(imageUri);
        }
        textViewName.setText(name);
        textViewPrice.setText(price);
        textViewDescription.setText(description);
        buttonEdit.setOnClickListener(this::startEditItemActivity);
    }

    private void startEditItemActivity(View view) {
//        startActivity();
    }
}