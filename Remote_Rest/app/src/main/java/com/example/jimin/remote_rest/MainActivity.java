package com.example.jimin.remote_rest;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    Button btn;
    ListView listView;
    CustomAdapter adapter;

    //어답터가 사용하는 배열값
    ArrayList<String> datas = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.listView);
        adapter = new CustomAdapter(this);
        listView.setAdapter(adapter);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getData();
            }
        });

    }


    class CustomAdapter extends BaseAdapter {

        LayoutInflater inflater;

        public CustomAdapter(Context context) {

            inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);


        }

        @Override
        public int getCount() {
            return datas.size();
        }

        @Override
        public Object getItem(int position) {
            return datas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null)
                convertView = inflater.inflate(R.layout.list_item, null);
            TextView tv = (TextView) convertView.findViewById(R.id.textView);
            tv.setText(datas.get(position));

            return convertView;
        }
    }


    private void getData() {
        new AsyncTask<Void, Void, String>() {

            ProgressDialog progress;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progress = new ProgressDialog(MainActivity.this);
                progress.setTitle("댜운로드");
                progress.setMessage("downloading...");
                progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progress.setCancelable(false);
                progress.show();
            }


            @Override
            protected String doInBackground(Void... params) {
                String result = "";
                try {
                    result = Remote.getData("http://192.168.0.175/sub/requestJSON.jsp?skip=5");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return result;
            }


            @Override
            protected void onPostExecute(String jsonString) {
                super.onPostExecute(jsonString);

                try {

                    //전체 문자열을 JSON오브젝트로 변환
                    JSONObject json = new JSONObject(jsonString);

                    // 특정 키를 가진 배열형태의 값을 꺼낸다
                    JSONArray rows = json.getJSONArray("root");

                    int rows_count = rows.length();
                    for (int i = 0; i < rows_count; i++) {
                        //배열을 반복문을 돌면서 배열의 index로 값을 꺼낸다
                        JSONObject result = (JSONObject) rows.get(i);
                        //최종적으로 각열의 컬럼의 키 이름에 해당되는 값을 꺼낸다
                        String value = result.getString("key");
                        datas.add(value);
                    }

                    adapter.notifyDataSetChanged();

                } catch (Exception e) {

                    e.printStackTrace();
                }


                //tv.setText(sb.toString());
                progress.dismiss();

            }
        }.execute(); //doInBackground()호출

    }

}

