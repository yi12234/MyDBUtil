package com.mydbutil;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.mydblibrary.DatabaseUtils;

public class MainActivity extends AppCompatActivity {

    private TextView tv1;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //必须先初始化
        DatabaseUtils.initHelper(this,"qweq");
        final Student student=new Student("张三","1001",12);

        tv1 = (TextView) findViewById(R.id.tv1);
        findViewById(R.id.btnadd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseUtils.getHelper().save(student);
                onResume();
                show();
            }
        });
        findViewById(R.id.btnaelete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseUtils.getHelper().delete(Student.class,1);
                onResume();
                show();
            }
        });
        findViewById(R.id.btnupdata).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseUtils.getHelper().udadta(Student.class,new Student("123231321","1001",12),3);
                onResume();
                show();
            }
        });
    }

    private void show() {
        tv1.setText(DatabaseUtils.getHelper().queryAll(Student.class)+"");
    }

    @Override
    protected void onResume() {
        super.onResume();
        show();
        listView = (ListView) findViewById(R.id.lv);
        listView.setAdapter(new MyAdapter(this,DatabaseUtils.getHelper().queryAll(Student.class)));
    }
}
