package com.corey.sample;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MyAdapter adapter = new MyAdapter();
        final GridView gridview = (GridView) findViewById(R.id.gridview);
        gridview.setAdapter(adapter);

        gridview.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setAdapterViewFocus(gridview);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        gridview.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                setAdapterViewFocus(gridview);
            }
        });
    }

    class MyAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return 12;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null){
                RoundedImageView imageView = new RoundedImageView(MainActivity.this);
                imageView.setImageResource(R.mipmap.focus_img);
                imageView.setCornerRadius(3);
                AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                imageView.setLayoutParams(layoutParams);
                convertView = imageView;
            }
            return convertView;
        }
    }
}
