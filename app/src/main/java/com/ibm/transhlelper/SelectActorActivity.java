package com.ibm.transhlelper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class SelectActorActivity extends Activity {

    private Button btnOk;
    private Spinner spinner1;
    private List<String> languages1 = new ArrayList<String>();
    private ArrayAdapter<String> adapter1;
    public String targetStr;

    private Spinner spinner2;
    private List<String> languages2 = new ArrayList<String>();
    private ArrayAdapter<String> adapter2;
    public String sourceStr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//隐藏标题
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置全屏
        setContentView(R.layout.activity_select_actor);
        initView();
        getData();
        setListener();

    }

    private void initView() {
        spinner1 =(Spinner)findViewById(R.id.spinneractor1);
        spinner2 = (Spinner)findViewById(R.id.spinneractor2);
        btnOk =(Button)findViewById(R.id.btnok);

    }
    private void getData(){
        languages1.add("Enlish");
        languages1.add("Spanish");
        languages1.add("French");
        languages1.add("Portuguese");
        languages1.add("Arabic");
        adapter1 = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, languages1);
        // 为适配器设置下拉列表下拉时的菜单样式。
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // 将适配器添加到下拉列表上
        spinner1.setAdapter(adapter1);


        languages2.add("Enlish");
        languages2.add("Spanish");
        languages2.add("French");
        languages2.add("Portuguese");
        languages2.add("Arabic");
        adapter2 = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, languages2);
        // 为适配器设置下拉列表下拉时的菜单样式。
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // 将适配器添加到下拉列表上
        spinner2.setAdapter(adapter2);

    }
    private void setListener(){

        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // TODO Auto-generated method stub
                String itemStr = adapter1.getItem(position);
                switch(itemStr){
                    case "Enlish":
                        sourceStr = "en";
                       // Toast.makeText(getApplicationContext(),targetStr,Toast.LENGTH_SHORT).show();
                        break;
                    case "French":
                        sourceStr = "fr";
                      //  Toast.makeText(getApplicationContext(),targetStr,Toast.LENGTH_SHORT).show();
                        break;
                    case "Spanish":
                        sourceStr = "es";
                        // Toast.makeText(getApplicationContext(),"目标转换语言是"+sourceStr,Toast.LENGTH_SHORT).show();
                        break;
                    case "Portuguese":
                        sourceStr = "pt";
                      //  Toast.makeText(getApplicationContext(),targetStr,Toast.LENGTH_SHORT).show();
                        break;
                    case "Arabic":
                        sourceStr = "ar";
                       // Toast.makeText(getApplicationContext(),targetStr,Toast.LENGTH_SHORT).show();
                        break;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });


        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // TODO Auto-generated method stub
                String itemStr = adapter2.getItem(position);
                switch(itemStr){
                    case "Enlish":
                        targetStr = "en";
                      //  Toast.makeText(getApplicationContext(),"目标转换语言是"+sourceStr,Toast.LENGTH_SHORT).show();
                        break;
                    case "French":
                        targetStr = "fr";
                      //  Toast.makeText(getApplicationContext(),"目标转换语言是"+sourceStr,Toast.LENGTH_SHORT).show();
                        break;
                    case "Spanish":
                        targetStr = "es";
                        // Toast.makeText(getApplicationContext(),"目标转换语言是"+sourceStr,Toast.LENGTH_SHORT).show();
                        break;
                    case "Portuguese":
                        targetStr = "pt";
                       // Toast.makeText(getApplicationContext(),"目标转换语言是"+sourceStr,Toast.LENGTH_SHORT).show();
                        break;
                    case "Arabic":
                        targetStr = "ar";
                      //  Toast.makeText(getApplicationContext(),"目标转换语言是"+sourceStr,Toast.LENGTH_SHORT).show();
                        break;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Toast.makeText(getApplicationContext(),sourceStr+"目标"+targetStr,Toast.LENGTH_LONG).show();
                Intent it = new Intent();
                it.setClass(SelectActorActivity.this,ChatActivity.class);
                it.putExtra("source", sourceStr);
                it.putExtra("target",targetStr);
                startActivity(it);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_select_actor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
