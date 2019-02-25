package com.pbd.housecure.housecure;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


public class HomeFragment extends Fragment {
    RequestQueue queue;
    Timer timer;

    public HomeFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        timer = new Timer();
        queue = Volley.newRequestQueue(getContext());
        schedule();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        queue.cancelAll("status_request");
        timer.cancel();
    }

    private void schedule() {
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                checkStatus();
            }
        };

        timer.schedule(timerTask, 0,5000);
    }


    private void checkStatus() {
        String url = getResources().getString(R.string.api_host) + "/status";

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        changeImage(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("VOLLEY", error.toString());
            }
        }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json; charset=UTF-8");
                params.put("Authorization", FirebaseAuth.getInstance().getCurrentUser().getUid());
                return params;
            }
        };
        jsonRequest.setTag("status_request");
        queue.add(jsonRequest);
    }


    public void changeImage(JSONObject response) {
        boolean status = false;
        try {
            status = response.getBoolean("safe");
        } catch (JSONException e) {
            return;
        }

        ImageView imageView = getView().findViewById(R.id.status_image);
        TextView textView = getView().findViewById(R.id.status_text);
        Button dangerButton = getView().findViewById(R.id.danger_house);

        if (status) {
            imageView.setImageDrawable(getResources().getDrawable(R.drawable.safe));
            textView.setText(getString(R.string.safe_text));
            textView.setTextColor(getResources().getColor(R.color.colorAccent));
        } else {
            imageView.setImageDrawable(getResources().getDrawable(R.drawable.intruder_alert));
            textView.setText(getString(R.string.alert_text));
            textView.setTextColor(getResources().getColor(R.color.colorDanger));
        }
    }


}
