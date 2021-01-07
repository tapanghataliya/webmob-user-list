package com.wmt.TapanGhataliya;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;
import com.wmt.TapanGhataliya.activity.RegistrationActivity;
import com.wmt.TapanGhataliya.controller.Global;
import com.wmt.TapanGhataliya.model.Bean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    SharedPreferences sharedPreferences;
    RequestQueue requestQueue;
    RelativeLayout progressBar;
    String USER_NAME, EMAIL_ADDRESS, USER_ID, U_TOKEN, TOTAL,PER_PAGE,PAGE,LAST_PAGE;
    RecyclerView rcUserList;
    ImageView imgLogout;
    ProgressBar marker_progress;

    Bean.User_Model user_model;
    ArrayList<Bean.User_Model> user_ArrayList = new ArrayList<>();
    UserAdapter userAdapter;
    LinearLayoutManager layoutManager;
    boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Initilization();
    }

    private void Initilization() {

        sharedPreferences = getSharedPreferences(Global.MyApplication, MODE_PRIVATE);
        requestQueue = Volley.newRequestQueue(MainActivity.this);

        progressBar = (RelativeLayout) findViewById(R.id.progressBar);
        imgLogout = (ImageView) findViewById(R.id.imgLogout);

        USER_ID = sharedPreferences.getString(Global.id, "");
        USER_NAME = sharedPreferences.getString(Global.username, "");
        EMAIL_ADDRESS = sharedPreferences.getString(Global.email, "");
        U_TOKEN = sharedPreferences.getString(Global.token, "");

        Log.d("USER_ID", USER_ID);
        Log.d("USER_NAME", USER_NAME);
        Log.d("EMAIL_ADDRESS", EMAIL_ADDRESS);
        Log.d("U_TOKEN", U_TOKEN);

        rcUserList = (RecyclerView) findViewById(R.id.rcUserList);
        rcUserList.setHasFixedSize(true);
        rcUserList.setNestedScrollingEnabled(false);
        layoutManager = new LinearLayoutManager(MainActivity.this);
        rcUserList.setLayoutManager(layoutManager);
        rcUserList.setItemAnimator(new DefaultItemAnimator());

        marker_progress = (ProgressBar)findViewById(R.id.marker_progress);

        imgLogout.setOnClickListener(this);

        getUserList();

//        initScrollListener();
    }

    private void getUserList() {

        progressBar.setVisibility(View.VISIBLE);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, Global.BASE_URL + "list",
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        progressBar.setVisibility(View.GONE);
                        Log.i("USER_response", response.toString());
                        // TODO Auto-generated method stub

                        try {

                            user_ArrayList.clear();
                            String META = response.getString("meta");
                            Log.i("REGISTRATIONL_NAME", META);
                            JSONObject jsonobject = response.getJSONObject("meta");

                            String status = jsonobject.getString("status");

                            if (status.equals("ok")) {

                                JSONObject jsonobject2 = response.getJSONObject("data");
                                Log.d("jsonobject2", jsonobject2.toString());

                                JSONArray jsonArray = jsonobject2.getJSONArray("users");
                                Log.d("UserjsonArray", jsonArray.toString());

                                for (int i = 0; i < jsonArray.length(); i++) {

                                    JSONObject obj = jsonArray.getJSONObject(i);

                                    user_model = new Bean.User_Model();
                                    user_model.setId(obj.getString("id"));
                                    user_model.setProfile_pic(obj.getString("profile_pic"));
                                    user_model.setUsername(obj.getString("username"));
                                    user_model.setEmail(obj.getString("email"));
                                    user_model.setCreated_at(obj.getString("created_at"));
                                    user_model.setUpdated_at(obj.getString("updated_at"));

                                    user_ArrayList.add(user_model);
                                    Log.d("ARRAY_LIST", user_ArrayList.toString());
                                }

                                if (user_ArrayList.size() > 0) {

                                    userAdapter = new UserAdapter(MainActivity.this, user_ArrayList);
                                    rcUserList.setAdapter(userAdapter);
                                }

                                JSONObject jsonobject3 = jsonobject2.getJSONObject("pagination");
                                Log.d("jsonobject3", jsonobject2.toString());

                                TOTAL = jsonobject3.getString("total");
                                PER_PAGE = jsonobject3.getString("perPage");
                                PAGE = jsonobject3.getString("page");
                                LAST_PAGE = jsonobject3.getString("lastPage");

                                Log.d("TOTAL",TOTAL);
                                Log.d("PER_PAGE",PER_PAGE);
                                Log.d("PAGE",PAGE);
                                Log.d("LAST_PAGE",LAST_PAGE);

                            } else if (status.equals("false")) {

                                rcUserList.setAdapter(null);
                            }

                        } catch (JSONException e) {

                            e.printStackTrace();
                        }


                    }

                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                progressBar.setVisibility(View.GONE);
                NetworkResponse networkResponse = error.networkResponse;

                if (networkResponse != null && networkResponse.statusCode == 404) {
                    Toast.makeText(MainActivity.this, "No Record Found.", Toast.LENGTH_SHORT).show();
                } else if (error instanceof TimeoutError) {
                    Toast.makeText(MainActivity.this, "Time Out,Network is slow.", Toast.LENGTH_SHORT).show();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(MainActivity.this, "Please Check Your Internet Connection.", Toast.LENGTH_SHORT).show();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(MainActivity.this, "Authentication Error.", Toast.LENGTH_SHORT).show();
                } else if (error instanceof ServerError) {
                    Toast.makeText(MainActivity.this, "Server Not Connected.", Toast.LENGTH_SHORT).show();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(MainActivity.this, "Network Error.", Toast.LENGTH_SHORT).show();
                } else if (error instanceof ParseError) {
                    Toast.makeText(MainActivity.this, "Parse Error.", Toast.LENGTH_SHORT).show();
                }

                Log.i("error --> ", error.toString());
            }

        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", "Bearer " + U_TOKEN);
                return headers;
            }

        };
        requestQueue.getCache().clear();
        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(jsObjRequest);
    }

    private void initScrollListener() {

        rcUserList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                if (!isLoading){
                    if ((visibleItemCount + firstVisibleItemPosition) >=
                            totalItemCount && firstVisibleItemPosition >= 0){

                        int LASTPOSITION = user_ArrayList.size()-1;
                        Log.d("LASTPOSITION", String.valueOf(LASTPOSITION));

                        loadMore();
                        isLoading = true;
                        marker_progress.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }

    private void loadMore() {
        user_ArrayList.add(null);
        userAdapter.notifyItemInserted(user_ArrayList.size() - 1);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                user_ArrayList.remove(user_ArrayList.size() - 1);
                int scrollPosition = user_ArrayList.size();
                userAdapter.notifyItemRemoved(scrollPosition);

                int TOTAL_COUNT = Integer.parseInt(TOTAL);
                int REQUEST_OFFSET = Integer.parseInt(PAGE);
                int TOTAL_LIMIT = Integer.parseInt(PER_PAGE);
                int RESPONSE_OFFSET = Integer.parseInt(LAST_PAGE);

                int NEXT_VIEW = TOTAL_LIMIT + RESPONSE_OFFSET;
                Log.d("NEXT_VIEW", String.valueOf(NEXT_VIEW));

                Log.d("TOTAL_COUNT", String.valueOf(TOTAL_COUNT));
                Log.d("RESPONSE_OFFSET", String.valueOf(RESPONSE_OFFSET));
                Log.d("REQUEST_OFFSET", String.valueOf(REQUEST_OFFSET));
                Log.d("TOTAL_LIMIT", String.valueOf(TOTAL_LIMIT));

                if (TOTAL_LIMIT - 1 < TOTAL_COUNT) {

                    getUserListLoadMore();

                }


            }
        }, 2000);
    }

    private void getUserListLoadMore() {
        progressBar.setVisibility(View.VISIBLE);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, Global.BASE_URL + "list=?page=1",
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        Log.i("USER_response2", response.toString());
                        // TODO Auto-generated method stub

                        try {

                            String META = response.getString("meta");
                            Log.i("REGISTRATIONL_NAME", META);
                            JSONObject jsonobject = response.getJSONObject("meta");

                            String status = jsonobject.getString("status");

                            if (status.equals("ok")) {

                                JSONObject jsonobject2 = response.getJSONObject("data");
                                Log.d("jsonobject2", jsonobject2.toString());

                                JSONArray jsonArray = jsonobject2.getJSONArray("users");
                                Log.d("UserjsonArray", jsonArray.toString());

                                for (int i = 0; i < jsonArray.length(); i++) {

                                    JSONObject obj = jsonArray.getJSONObject(i);

                                    user_model = new Bean.User_Model();
                                    user_model.setId(obj.getString("id"));
                                    user_model.setProfile_pic(obj.getString("profile_pic"));
                                    user_model.setUsername(obj.getString("username"));
                                    user_model.setEmail(obj.getString("email"));
                                    user_model.setCreated_at(obj.getString("created_at"));
                                    user_model.setUpdated_at(obj.getString("updated_at"));

                                    user_ArrayList.add(user_model);
                                    Log.d("ARRAY_LIST", user_ArrayList.toString());
                                }

                                if (user_ArrayList.size() > 0) {

                                    userAdapter = new UserAdapter(MainActivity.this, user_ArrayList);
                                    rcUserList.setAdapter(userAdapter);
                                }

                                JSONObject jsonobject3 = jsonobject2.getJSONObject("pagination");
                                Log.d("jsonobject3", jsonobject2.toString());

                                TOTAL = jsonobject3.getString("total");
                                PER_PAGE = jsonobject3.getString("perPage");
                                PAGE = jsonobject3.getString("page");
                                LAST_PAGE = jsonobject3.getString("lastPage");

                                Log.d("TOTAL",TOTAL);
                                Log.d("PER_PAGE",PER_PAGE);
                                Log.d("PAGE",PAGE);
                                Log.d("LAST_PAGE",LAST_PAGE);

                            } else if (status.equals("false")) {

                                rcUserList.setAdapter(null);
                            }

                        } catch (JSONException e) {

                            e.printStackTrace();
                        }


                    }

                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                progressBar.setVisibility(View.GONE);
                NetworkResponse networkResponse = error.networkResponse;

                if (networkResponse != null && networkResponse.statusCode == 404) {
                    Toast.makeText(MainActivity.this, "No Record Found.", Toast.LENGTH_SHORT).show();
                } else if (error instanceof TimeoutError) {
                    Toast.makeText(MainActivity.this, "Time Out,Network is slow.", Toast.LENGTH_SHORT).show();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(MainActivity.this, "Please Check Your Internet Connection.", Toast.LENGTH_SHORT).show();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(MainActivity.this, "Authentication Error.", Toast.LENGTH_SHORT).show();
                } else if (error instanceof ServerError) {
                    Toast.makeText(MainActivity.this, "Server Not Connected.", Toast.LENGTH_SHORT).show();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(MainActivity.this, "Network Error.", Toast.LENGTH_SHORT).show();
                } else if (error instanceof ParseError) {
                    Toast.makeText(MainActivity.this, "Parse Error.", Toast.LENGTH_SHORT).show();
                }

                Log.i("error --> ", error.toString());
            }

        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", "Bearer " + U_TOKEN);
                return headers;
            }

        };
        requestQueue.getCache().clear();
        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(jsObjRequest);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgLogout:
                SharedPreferences preferences = getSharedPreferences(Global.MyApplication, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.clear();
                editor.apply();
                finish();
                Intent i_logout = new Intent(MainActivity.this, RegistrationActivity.class);
                startActivity(i_logout);
                finish();
                break;
        }
    }

    private class UserAdapter extends RecyclerView.Adapter<UserAdapter.MyHolder> {

        List<Bean.User_Model> usersList = null;
        Context mContext;

        public UserAdapter(Context context, ArrayList<Bean.User_Model> arrayList) {

            this.mContext = context;
            this.usersList = arrayList;
        }

        @NonNull
        @Override
        public UserAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_list_detail_activity, parent, false);
            MainActivity.UserAdapter.MyHolder viewHolder = new MainActivity.UserAdapter.MyHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull UserAdapter.MyHolder holder, int position) {

            final Bean.User_Model ReportModel = usersList.get(position);

            if (ReportModel.getUsername().equalsIgnoreCase("") || ReportModel.getUsername().equalsIgnoreCase("null") || ReportModel.getUsername().equalsIgnoreCase(null)) {

                holder.txt_name.setText("");

            } else {

                holder.txt_name.setText(ReportModel.getUsername());
            }

            if (ReportModel.getEmail().equalsIgnoreCase("") || ReportModel.getEmail().equalsIgnoreCase("null") || ReportModel.getEmail().equalsIgnoreCase(null)) {

                holder.txt_email.setText("");

            } else {

                holder.txt_email.setText(ReportModel.getEmail());
            }

            Picasso.with(mContext)
                    .load(ReportModel.getProfile_pic())
                    .noFade()
                    .into(holder.img_product);

        }

        @Override
        public int getItemCount() {
            return usersList.size();
        }

        public class MyHolder extends RecyclerView.ViewHolder {

            ImageView img_product;
            TextView txt_name, txt_email;

            public MyHolder(@NonNull View itemView) {
                super(itemView);

                img_product = (ImageView) itemView.findViewById(R.id.img_product);

                txt_name = (TextView) itemView.findViewById(R.id.txt_name);
                txt_email = (TextView) itemView.findViewById(R.id.txt_email);
            }
        }
    }

}