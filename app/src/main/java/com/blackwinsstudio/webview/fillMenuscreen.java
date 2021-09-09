package com.blackwinsstudio.webview;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;
import android.webkit.WebView;

import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

public class fillMenuscreen extends AppCompatActivity {
    LinearLayoutCompat LabelLayout;
    LinearLayout ProductLayout;
    Spinner SlotColValues , SlotRowValues;
    List<String> SlotColList = new ArrayList<String> () ;
    List<String> SlotRowList = new ArrayList<String> () ;
    List<Integer> QtyList = new ArrayList<Integer> () ;
    Button DoneButton, AddProductDB, AddRefillProduct;
    FillProductDetails productDetails = new FillProductDetails ();
    Spinner Quantity;
    Tray trayDetails = new Tray ();
    ArrayList<Tray> trays;

    {
        trays = new ArrayList<> ();
    }

    TextInputLayout productSKU;
    String SetStatus="Processing";
    String SelectedSlot , SelectedRow;
    int productQty =0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_fill_menuscreen);
        LabelLayout = (LinearLayoutCompat)findViewById (R.id.labellayout);
        ProductLayout = (LinearLayout)findViewById (R.id.productlayout);
        productSKU = (TextInputLayout)findViewById (R.id.productID);
        SlotColValues = (Spinner) findViewById (R.id.selectcolumn);
        SlotRowValues = (Spinner) findViewById (R.id.selectrow);
        DoneButton = (Button) findViewById (R.id.done);
        AddRefillProduct = (Button) findViewById (R.id.Add_RefillProduct);
        AddProductDB = (Button)findViewById (R.id.AddNewProduct);
        Quantity = (Spinner)findViewById (R.id.Quantity);
        // Spinner adapter for column ( A, B, ...E) slot values
        //Adding the SlotList
        SlotColList.add("A");
        SlotColList.add("B");
        SlotColList.add("C");
        SlotColList.add("D");
        SlotColList.add("E");
        ArrayAdapter<String> adapter = new ArrayAdapter<String> (this, android.R.layout.simple_spinner_item,SlotColList);
        adapter.setDropDownViewResource (android.R.layout.simple_spinner_dropdown_item);
        SlotColValues.setPrompt("Select your Slot!");
        SlotColValues.setAdapter (adapter);
        /*A spinner does not support item click events. Calling this method will raise an exception.*/
        SlotColValues.setOnItemSelectedListener (new AdapterView.OnItemSelectedListener () {
            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
                SelectedSlot = SlotColList.get (position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                    // show default values
            }
        });// End of column slot values mapping & Selection Logic

        // Spinner adapter for column ( A, B, ...E) slot values
        //Adding the SlotList
        SlotRowList.add("1");
        SlotRowList.add("2");
        SlotRowList.add("3");
        SlotRowList.add("4");
        SlotRowList.add("5");
        ArrayAdapter<String> rowadapter = new ArrayAdapter<String> (this, android.R.layout.simple_spinner_item,SlotRowList);
        rowadapter.setDropDownViewResource (android.R.layout.simple_spinner_dropdown_item);
        SlotRowValues.setPrompt("Select your Row!");
        SlotRowValues.setAdapter (rowadapter);

        SlotRowValues.setOnItemSelectedListener (new AdapterView.OnItemSelectedListener () {
            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
                SelectedRow = SlotRowList.get (position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // show default values
            }
        });// End of Row slot values mapping & Selection Logic

        //Data for Spinner Qty
        QtyList.add(1);
        QtyList.add(2);
        QtyList.add(3);
        QtyList.add(4);

        // Spinner adapter for Qty values

        ArrayAdapter<Integer> Qtyadapter = new ArrayAdapter<Integer> (this, android.R.layout.simple_spinner_item,QtyList);
        Qtyadapter.setDropDownViewResource (android.R.layout.simple_spinner_dropdown_item);
        Quantity.setPrompt ("Select the Quantity");
        Quantity.setAdapter (Qtyadapter);
        Quantity.setOnItemSelectedListener (new AdapterView.OnItemSelectedListener () {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int index, long l) {
                productQty = QtyList.get (index);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        DoneButton.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                productDetails.setProductSKU (productSKU.getEditText ().getText ().toString ());
                productDetails.setStatus (SetStatus);
                //trayDetails.setProductColumn ("A-11");
                trayDetails.setProductColumn (SelectedSlot + SelectedRow);
                //trayDetails.setProductQuantity (Integer.parseInt (Quantity.getText ().toString ()));
                trayDetails.setProductQuantity (productQty);
                trays.add (trayDetails);
                productDetails.setTrayDetails (trays);
                Log.i ("JSON RESPONSE", "onClick:"+productDetails.toString ());
                Toast.makeText (fillMenuscreen.this, productDetails.toString (), Toast.LENGTH_LONG).show ();
            }
        });

        AddProductDB.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                Opensastaamart();
            }
        });

        AddRefillProduct.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                //Show the label layout
                LabelLayout.setVisibility (View.VISIBLE);
                //Show the Product Input layout
                ProductLayout.setVisibility (View.VISIBLE);
                // Show the Done Button
                DoneButton.setVisibility (View.VISIBLE);
            }
        });

    }

    public void Opensastaamart(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra ("page", "fillmenuscreen");
        startActivity(intent);

    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    private void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    // Shows the system bars by removing all the flags
    // except for the ones that make the content appear under the system bars.
    private void showSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }
}