package com.example.capston_voisk;

import android.content.Context;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;


public class GridViewActivity extends AppCompatActivity {
    private String TAG = GridViewActivity.class.getSimpleName();

    private GridView gridview = null;
    private GridViewAdapter adapter = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid);

        gridview = findViewById(R.id.gridview);
        adapter = new GridViewAdapter();

        //Adapter 안에 아이템의 정보 담기
        adapter.addItem(new Item("1", "불고기 버거", R.drawable.ic_baseline_keyboard_voice_24));
        adapter.addItem(new Item("2", "치킨 버거", R.drawable.ic_baseline_keyboard_voice_24));
        adapter.addItem(new Item("3", "새우 버거", R.drawable.ic_baseline_keyboard_voice_24));
        adapter.addItem(new Item("4", "데리 버거", R.drawable.ic_baseline_keyboard_voice_24));
        adapter.addItem(new Item("5", "치즈 버거", R.drawable.ic_baseline_keyboard_voice_24));

        //리스트뷰에 Adapter 설정
        gridview.setAdapter(adapter);

    }

    /* 그리드뷰 어댑터 */
    class GridViewAdapter extends BaseAdapter {
        ArrayList<Item> items = new ArrayList<Item>();

        @Override
        public int getCount() {
            return items.size();
        }

        public void addItem(Item item) {
            items.add(item);
        }

        @Override
        public Object getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            final Context context = viewGroup.getContext();
            final Item Item = items.get(position);

            if(convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.gridview_list_item, viewGroup, false);

                TextView tv_num = convertView.findViewById(R.id.tv_num);
                TextView tv_name = convertView.findViewById(R.id.tv_name);
                ImageView iv_icon = convertView.findViewById(R.id.iv_icon);

                tv_num.setText(Item.getNum());
                tv_name.setText(Item.getName());
                iv_icon.setImageResource(Item.getResId());
                Log.d(TAG, "getView() - [ "+position+" ] "+Item.getName());

            } else {
                View view = new View(context);
                view = (View) convertView;
            }

            //각 아이템 선택 event
            convertView.setOnClickListener(view -> Toast.makeText(context, Item.getNum()+" 번 - "+Item.getName()+" 입니다 ", Toast.LENGTH_SHORT).show());

            return convertView;  //뷰 객체 반환
        }
    }

}
