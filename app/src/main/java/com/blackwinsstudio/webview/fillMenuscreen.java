package com.blackwinsstudio.webview;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;

import android.app.Application;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class fillMenuscreen extends AppCompatActivity {

    LinearLayoutCompat LabelLayout;
    LinearLayout ProductLayout;
    Spinner SlotColValues , SlotRowValues;
    List<String> SlotColList = new ArrayList<String> () ;
    List<String> SlotRowList = new ArrayList<String> () ;
    List<Integer> QtyList = new ArrayList<Integer> () ;
    Button DoneButton, AddProductDB, AddRefillProduct, CurrentMachineStatus;
    FillProductDetails productDetails = new FillProductDetails ();
    OrderedItemModel orderedItemModel = new OrderedItemModel ();
    MqttAndroidClient client;
    Spinner Quantity;
    Tray trayDetails = new Tray ();
    ArrayList<Tray> trays;

    {
        trays = new ArrayList<> ();
    }
    Arrays ProductID;
    TextInputLayout productSKU;
    String SetStatus="Processing";
    String SelectedSlot , SelectedRow;
    int productQty =0;

    //Variables used for API call

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_fill_menuscreen);

        // initializing our views
        LabelLayout = findViewById (R.id.labellayout);
        ProductLayout = findViewById (R.id.productlayout);
        productSKU = findViewById (R.id.productID);
        SlotColValues =  findViewById (R.id.selectcolumn);
        SlotRowValues =  findViewById (R.id.selectrow);
        DoneButton =  findViewById (R.id.done);
        AddRefillProduct =  findViewById (R.id.Add_RefillProduct);
        AddProductDB = findViewById (R.id.AddNewProduct);
        CurrentMachineStatus = findViewById (R.id.CurrentMachineStatus);
        Quantity = findViewById (R.id.Quantity);
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



        /* MQTT client Start Here*/
        //String clientId = MqttClient.generateClientId();
        String clientId = "Suthar_Electronics";
        client = new MqttAndroidClient(this.getApplicationContext(), "tcp://broker.hivemq.com:1883",
                        clientId);


        try {
            IMqttToken token = client.connect();
            client.acknowledgeMessage ("Sent");
            token.setActionCallback(new IMqttActionListener () {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Log.d(ApplicationConstant.MQTT_TAG, "onSuccess");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Log.d(ApplicationConstant.MQTT_TAG, "onFailure");

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
        /* MQTT client Ends Here */


        /*Post Custom Order Item starts here*/
        // Rewritten the done functionality below in line no:294
        /*DoneButton.setOnClickListener (new View.OnClickListener () {
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
        });*/
        /*so commenting above done click function here*/

        /*Post Custom Order Item starts here*/
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


        // Get Current Machine Status from API call
        CurrentMachineStatus.setOnClickListener (new View.OnClickListener () {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                AttributeMethods methods = RetrofitClient.getRetrofitInstance ().create (AttributeMethods.class);
                //Call<List<AttributesModel>> call = methods.getMachineInPlaceData (Helper.getAuthToken());
                Call<List<OrdersModel>> call = methods.getAllOrders (Helper.getAuthToken());
                // Orders API invoking part
                call.enqueue (new Callback<List<OrdersModel>> () {
                    @Override
                    public void onResponse(Call<List<OrdersModel>> call, Response<List<OrdersModel>> response) {
                        Log.i (ApplicationConstant.TAG, "OnResponse Code"+ response.code ());
                        List<OrdersModel> orderResponse = response.body ();
                        //Log.i(ApplicationConstant.TAG,String.valueOf (orderResponse.size ()));
                        Log.i(ApplicationConstant.TAG,"Order_Id :"+ String.valueOf (orderResponse.get (0).id)+ "Status :" + orderResponse.get (0).getStatus ());
                        // CHECKING STATUS == "PROCESSING" IF STATUS IS "COMPLETED" DONT RETRIEVE THE DATA FURTHER
                        if(orderResponse.get (0).getStatus ().equals (ApplicationConstant.ORDER_STATUS_PROCESSING)){
                            //Forming orderedItemModel data here
                            orderedItemModel.order_id = orderResponse.get (0).id;
                            orderedItemModel.status =  orderResponse.get (0).getStatus ();
                            //Log.i(ApplicationConstant.TAG,"Product_Id : "+String.valueOf (orderResponse.get (0).line_items.get (0).product_id)+ "Quantity :" +String.valueOf (orderResponse.get (0).line_items.get (0).quantity) );
                            //Log.i(ApplicationConstant.TAG,"Product_Id : "+String.valueOf (orderResponse.get (0).line_items.get (1).product_id)+ "Quantity :" +String.valueOf (orderResponse.get (0).line_items.get (1).quantity) );
                            for(LineItem lineItems: orderResponse.get(0).line_items){
                                Log.i(ApplicationConstant.TAG,String.valueOf (lineItems.product_id)+ "Quantity :" + String.valueOf (lineItems.quantity));
                                orderedItemModel.product_id = lineItems.product_id;
                                String productConvert_id = String.valueOf(orderedItemModel.product_id);
                                productSKU.getEditText ().setText(productConvert_id);
                                orderedItemModel.quantity = lineItems.quantity;
                                GetTheProductPosition(String.valueOf (orderedItemModel.product_id),orderedItemModel.quantity);
                            }
                            Toast.makeText (fillMenuscreen.this, "Order Information received successfully", Toast.LENGTH_LONG).show ();
                        }
                        /*for(OrdersModel eachorder: orderResponse){
                            Log.i(ApplicationConstant.TAG,"Order_Id :"+String.valueOf (eachorder.id)+ "Status :" + eachorder.status);
                            // CHECKING STATUS == "PROCESSING" IF STATUS IS "COMPLETED" DONT RETRIEVE THE DATA FURTHER
                            if(eachorder.status.equals (ApplicationConstant.ORDER_STATUS_PROCESSING)){
                                //Log.i(ApplicationConstant.TAG,"Product_Id : "+String.valueOf (orderResponse.get (0).line_items.get (0).product_id)+ "Quantity :" +String.valueOf (orderResponse.get (0).line_items.get (0).quantity) );
                                //Log.i(ApplicationConstant.TAG,"Product_Id : "+String.valueOf (orderResponse.get (0).line_items.get (1).product_id)+ "Quantity :" +String.valueOf (orderResponse.get (0).line_items.get (1).quantity) );
                                for(LineItem lineItems: orderResponse.get(0).line_items){
                                    Log.i(ApplicationConstant.TAG,String.valueOf (lineItems.product_id)+ String.valueOf (lineItems.quantity));
                                    GetTheProductPosition(lineItems.product_id,lineItems.quantity);
                                }

                                //GetTheProductPosition();
                            }
                        /*else if(orderResponse.get(0).status.equals (ApplicationConstant.ORDER_STATUS_COMPLETED)){
                            Toast.makeText (fillMenuscreen.this,"Recent Order Status : "+ApplicationConstant.ORDER_STATUS_COMPLETED, Toast.LENGTH_LONG).show ();
                        }*/
                    }

                    @Override
                    public void onFailure(Call<List<OrdersModel>> call, Throwable t) {
                        Log.e (ApplicationConstant.TAG,"onfailure"+ t.getMessage ());
                    }
                });
                /*attribute API invoking part
                call.enqueue (new Callback<List<AttributesModel>> () {
                    @Override
                    public void onResponse(Call<List<AttributesModel>> call, Response<List<AttributesModel>> response) {
                        Log.i (ApplicationConstant.TAG, "OnResponse Code"+ response.code ());
                        //Log.i (ApplicationConstant.TAG, "OnResponse Code"+ response.body ().toString ());
                        List<AttributesModel> responseData = response.body ();
                        for(AttributesModel data:responseData){
                            Log.i(ApplicationConstant.TAG, "Name: " +data.getName () +", " +"ID: "+ data.getID ());
                        }
                    }

                    @Override
                    public void onFailure(Call<List<AttributesModel>> call, Throwable t) {
                        Log.e (ApplicationConstant.TAG,"onfailure"+ t.getMessage ());
                    }
                });*/
            }
        });

        // Calling a method to post the data and passing ordereditem object to custom API
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
                //postData();
                //publishMQTTMessage();
            }
        });
    }

    private void publishMQTTMessage() {
        Gson orderedJsonObject = new Gson ();
        String payload =orderedJsonObject.toJson (orderedItemModel);
        //orderedJsonObject.
        //String topic = "VM_NI/SE/Testing";
        String topic ="vending/1";
       // String payload = "orderid-1234";
        byte[] encodedPayload = new byte[0];
        try {
            encodedPayload = payload.getBytes("UTF-8");
            MqttMessage message = new MqttMessage(encodedPayload);
            IMqttDeliveryToken pubToken = client.publish(topic, message);
            if(pubToken.isComplete ()){
                Log.d(ApplicationConstant.MQTT_TAG, "Published");
            }
            //Log.d(TAG, "Published");

        } catch (UnsupportedEncodingException | MqttException e) {
            e.printStackTrace();
        }
    }


    /* MQTT cloud Information
    client name: deployment-h611068d
    MQTT Client Id : ec50aa24-fc62-481b-ba01-cb66ef4d57e2
    Protocol :mqtt/tcp
    Host: mqtt://h611068d.us-east-1.emqx.cloud:15484
    username: blackwinstech
    password:bws24
     */
    /*private void publishMQTTMessage() {
        Gson orderedJsonObject = new Gson ();
        String payload =orderedJsonObject.toJson (orderedItemModel);
        //orderedJsonObject.
        String topic = "vending 1";
        // String payload = "orderid-1234";
        byte[] encodedPayload = new byte[0];
        try {
            encodedPayload = payload.getBytes("UTF-8");
            MqttMessage message = new MqttMessage(encodedPayload);
            IMqttDeliveryToken pubToken = client.publish(topic, message);
            if(pubToken.isComplete ()){
                Log.d(ApplicationConstant.MQTT_TAG, "Published");
            }
            //Log.d(TAG, "Published");

        } catch (UnsupportedEncodingException | MqttException e) {
            e.printStackTrace();
        }
    }*/

    private void postData() {
            if(orderedItemModel.getOrder_id ()!=0){
                AttributeMethods bwt_methods = BwtRetrofitClient.getRetrofitInstance ().create (AttributeMethods.class);
                /* POST THE APIcalls Data*/
                //Call<OrderedItemModel> bwt_call = bwt_methods.postOrderedItems (orderedItemModel.getOrder_id (), orderedItemModel.getStatus (), orderedItemModel.getProduct_id (), orderedItemModel.getQuantity (), orderedItemModel.getOptions ());
                /* POST THE App Data*/
                Call<OrderedItemModel> bwt_call = bwt_methods.postOrderedItems (orderedItemModel.getOrder_id (), orderedItemModel.getStatus (), Integer.parseInt(productDetails.getProductSKU ()), trayDetails.getProductQuantity (), trayDetails.getProductColumn());
                bwt_call.enqueue (new Callback<OrderedItemModel> () {
                    @Override
                    public void onResponse(Call<OrderedItemModel> call, Response<OrderedItemModel> response) {
                        Log.i (ApplicationConstant.TAG, "OnResponse Code :"+ response.code ());
                        Log.i (ApplicationConstant.TAG, "OnResponse Message :"+ response.message ());
                        Toast.makeText (fillMenuscreen.this, response.message (), Toast.LENGTH_LONG).show ();
                    }

                    @Override
                    public void onFailure(Call<OrderedItemModel> call, Throwable t) {
                        Log.e (ApplicationConstant.TAG,"onfailure"+ t.getMessage ());
                    }
                });
            }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void GetTheProductPosition(String product_id, int quantity) {
        AttributeMethods methods = RetrofitClient.getRetrofitInstance ().create (AttributeMethods.class);
        Call<ProductInformation> pcall = methods.getProductById (Helper.getAuthToken(),product_id);
        pcall.enqueue (new Callback<ProductInformation> () {
            @Override
            public void onResponse(Call<ProductInformation> call, Response<ProductInformation> response) {
                Log.i (ApplicationConstant.TAG, "OnResponse Code :"+ response.code ());
                Log.i(ApplicationConstant.TAG,"options :" + response.body ().attributes.get (0).options.get (0));
                orderedItemModel.options = response.body ().attributes.get (0).options.get (0);
            }

            @Override
            public void onFailure(Call<ProductInformation> call, Throwable t) {
                Log.e (ApplicationConstant.TAG,"onfailure"+ t.getMessage ());
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