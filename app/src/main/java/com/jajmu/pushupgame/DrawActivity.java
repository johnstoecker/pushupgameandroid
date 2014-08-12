package com.jajmu.pushupgame;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
//import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class DrawActivity extends Activity {
    public com.jajmu.pushupgame.Deck deck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw);
        deck = new com.jajmu.pushupgame.Deck();
        System.out.println("created");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.draw, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void drawCard(View v){
        Card card = deck.drawCard();
        TextView view = (TextView) findViewById(R.id.cardResult);
        view.setText(card.getName());
    }

    public void completeTurn(View v){
        Intent intent = new Intent(this, OpponentTurnActivity.class);
        startActivity(intent);
//		NetClient netClient = ((PushUpApplication) getApplication()).getNetClient();
//		netClient.completeTurn();
        RequestQueue queue = ((PushUpApplication) getApplication()).getRequestQueue();
        String url ="http://107.170.43.64:4000/finishTurn";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        ((TextView) findViewById(R.id.cardResult)).setText("Response is: "+ response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ((TextView) findViewById(R.id.cardResult)).setText("That didn't work!");
            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);

    }

    public void incompleteTurn(View v){
        NetClient netClient = ((PushUpApplication) getApplication()).getNetClient();
        netClient.completeTurn();
    }

    @Override
    public void onPause(){
        super.onPause();
        System.out.println("paused");
    }

    @Override
    public void onResume(){
        super.onResume();
        System.out.println("resumed");
    }

    @Override
    public void onStart(){
        super.onStart();
        System.out.println("started");

    }

    @Override
    public void onRestart(){
        super.onRestart();
        System.out.println("restarted");
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        System.out.println("destroyed");
    }

    /**
     * save state to memory
     */
    @Override
    public void onStop(){
        super.onStop();
        System.out.println("stopped");
    }
}
