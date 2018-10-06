package com.byteshaft.hafizconstructionworks;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.byteshaft.hafizconstructionworks.gettersetters.Quarters;
import com.byteshaft.requests.HttpRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ListView mListView;
    private QuarterAdapter adapter;
    private ArrayList<Quarters> quartersList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        mListView = findViewById(R.id.quarter_list);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        quartersList = new ArrayList<>();
        getAllQuarters();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addQuarterDialog();
            }
        });
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Quarters singleQuarter = quartersList.get(position);
                Intent intent = new Intent(MainActivity.this, QuarterDetailActivity.class);
                intent.putExtra("q_id", singleQuarter.getId());
                intent.putExtra("q_name", singleQuarter.getName());
                startActivity(intent);
            }
        });

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Quarters singleQuarter = quartersList.get(position);
                deleteDialog(singleQuarter.getId());
                return true;
            }
        });
    }

    private void deleteDialog(int quarterId) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilder.setTitle("Delete");
        alertDialogBuilder.setMessage("Do you want to delete this item?")
                .setCancelable(false).setPositiveButton(getString(R.string.yes),
                (dialog, id) -> {
                    dialog.dismiss();
                    deleteQuarter(quarterId);
                });
        alertDialogBuilder.setNegativeButton(getString(R.string.no), (dialogInterface, i) -> dialogInterface.dismiss());
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void deleteQuarter(int quarterId) {
        HttpRequest request = new HttpRequest(getApplicationContext());
        request.setOnReadyStateChangeListener(new HttpRequest.OnReadyStateChangeListener() {
            @Override
            public void onReadyStateChange(HttpRequest request, int state) {
                switch (state) {
                    case HttpRequest.STATE_DONE:
                        switch (request.getStatus()) {
                            case HttpURLConnection.HTTP_OK:
                                quartersList.clear();
                                getAllQuarters();
                                adapter.notifyDataSetChanged();
                                Toast.makeText(MainActivity.this, "Item Removed", Toast.LENGTH_SHORT).show();
                        }
                }
            }
        });
        request.open("DELETE", String.format("%sdelete/quarter/%s", AppGlobals.SERVER_IP, quarterId));
        request.send();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    private void addQurter(String quarterName) {
        HttpRequest request = new HttpRequest(getApplicationContext());
        request.setOnReadyStateChangeListener(new HttpRequest.OnReadyStateChangeListener() {
            @Override
            public void onReadyStateChange(HttpRequest request, int readyState) {
                switch (readyState) {
                    case HttpRequest.STATE_DONE:
                        switch (request.getStatus()) {
                            case HttpURLConnection.HTTP_CREATED:
                                quartersList.clear();
                                getAllQuarters();

                        }
                }
            }
        });
        request.setOnErrorListener(new HttpRequest.OnErrorListener() {
            @Override
            public void onError(HttpRequest httpRequest, int i, short i1, Exception e) {

            }
        });
        request.open("POST", String.format("%screate_quarter", AppGlobals.SERVER_IP));
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("owner", quarterName);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        request.send(jsonObject.toString());
    }

    private void getAllQuarters() {
        final HttpRequest request = new HttpRequest(getApplicationContext());
        request.setOnReadyStateChangeListener(new HttpRequest.OnReadyStateChangeListener() {
            @Override
            public void onReadyStateChange(HttpRequest httpRequest, int readyState) {
                switch (readyState) {
                    case HttpRequest.STATE_DONE:
                        switch (httpRequest.getStatus()) {
                            case HttpURLConnection.HTTP_OK:
                                Log.wtf("quarters ", request.getResponseText());
                                try {
                                    JSONArray jsonArray = new JSONArray(request.getResponseText());
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        Quarters quarters = new Quarters();
                                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                                        quarters.setId(jsonObject.getInt("id"));
                                        quarters.setName(jsonObject.getString("owner"));
                                        Log.wtf("Names..:", jsonObject.getString("owner"));
                                        quartersList.add(quarters);
                                    }
                                    adapter = new QuarterAdapter(getApplicationContext(), quartersList);
                                    mListView.setAdapter(adapter);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                        }
                }

            }
        });
        request.setOnErrorListener(new HttpRequest.OnErrorListener() {
            @Override
            public void onError(HttpRequest httpRequest, int i, short i1, Exception e) {

            }
        });
        request.open("GET", String.format("%squarters", AppGlobals.SERVER_IP));
        request.send();
    }

    private void addQuarterDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setTitle("Add Quarter");
        alertDialog.setMessage("");
        FrameLayout container = new FrameLayout(MainActivity.this);
        final EditText input = (EditText) getLayoutInflater().inflate(R.layout.dialog_editext, null);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.leftMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);
        lp.rightMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);
        lp.bottomMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);
        container.addView(input);
        input.setLayoutParams(lp);
        alertDialog.setView(container);
        alertDialog.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                addQurter(input.getText().toString());
            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();
    }

    private class QuarterAdapter extends BaseAdapter {

        private ViewHolder viewHolder;
        private Context context;
        private ArrayList<Quarters> quarters;

        private QuarterAdapter(Context context, ArrayList<Quarters> quarters) {
            this.context = context;
            this.quarters = quarters;
        }

        @Override
        public int getCount() {
            return quarters.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater()
                        .inflate(R.layout.delegate_quarter, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.quarterTitle = convertView.findViewById(R.id.text_view_wish_list_title);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            Quarters singleQuarter = quarters.get(position);
            viewHolder.quarterTitle.setText(singleQuarter.getName());

            return convertView;
        }

        private class ViewHolder {
            private TextView quarterTitle;
        }
    }
}
