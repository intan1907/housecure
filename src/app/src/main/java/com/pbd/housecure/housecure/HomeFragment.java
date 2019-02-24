package com.pbd.housecure.housecure;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {


    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        checkStatus();
        return inflater.inflate(R.layout.fragment_home, container, false);
    }



    private void checkStatus() {
        RequestQueue queue = Volley.newRequestQueue(getContext());
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
