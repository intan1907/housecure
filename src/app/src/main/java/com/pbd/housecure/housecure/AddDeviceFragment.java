package com.pbd.housecure.housecure;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddDeviceFragment extends Fragment {
    public AddDeviceFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_device, container, false);
        Button button = (Button) view.findViewById(R.id.add);
        button.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
                String url = getResources().getString(R.string.api_host) + "/add";

                EditText editRoom = (EditText) getView().findViewById(R.id.room);
                EditText editDeviceId = (EditText) getView().findViewById(R.id.device_id);
                String room = editRoom.getText().toString();
                String device_id = editDeviceId.getText().toString();

                JSONObject obj = new JSONObject();
                try {
                    obj.put("room", room);
                    obj.put("device_id", device_id);
                } catch (Exception e) {
                    Log.e("VOLLEY", e.toString());
                };
                JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, obj,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.d("VOLLEY", response.toString());
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
        });
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
