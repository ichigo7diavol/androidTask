package com.example.arhon.app;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.ListMenuPresenter;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.future.ResponseFuture;
import com.scalified.fab.ActionButton;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class CreationActivity extends AppCompatActivity {

    Bundle saved;
    Boolean isSaving;
    ArrayList<Integer> catId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creation);

        ViewPump.init(ViewPump.builder()
                .addInterceptor(new CalligraphyInterceptor(
                        new CalligraphyConfig.Builder()
                                .setDefaultFontPath("fonts/OpenSans-Regular.ttf")
                                .setFontAttrId(R.attr.fontPath)
                                .build()))
                .build());

        catId = new ArrayList<Integer>();
        ImageButton button = findViewById(R.id.back_button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }

        });

        ImageButton cr_button = findViewById(R.id.create_button);
        cr_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                isSaving = true;
                postServer();
                finish();
            }
        });
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getDelegate().onPostCreate(savedInstanceState);

        ListView lv = (ListView)findViewById(R.id.categoryList);
        saved = getIntent().getExtras();

        ArrayList<String> tmp = saved.getStringArrayList("category");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.category_cell);

        for (Iterator<String> it = tmp.iterator();it.hasNext();) {
            adapter.add(it.next());
            catId.add(Integer.valueOf(it.next()));
        }

        ((ListView)findViewById(R.id.categoryList)).setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                adapterView.setSelection(i);
            }});


        lv.setAdapter(adapter);

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        getDelegate().onPostResume();

        isSaving = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getDelegate().onDestroy();
    }

    public void postServer () {

        if (!isSaving)
            return;

        if (((EditText)findViewById(R.id.editText2)).getText().toString().isEmpty())
            return;

        if (((ListView)findViewById(R.id.categoryList)).getCheckedItemPosition() == -1)
            return;

        JsonObject main = new JsonObject();

        JsonObject req = new JsonObject();
        req.addProperty("text",((EditText)findViewById(R.id.editText2)).getText().toString());
        req.addProperty("project_id", String.valueOf(catId.get(((ListView)findViewById(R.id.categoryList)).getCheckedItemPosition())));

        main.add("todo", req);
        main.addProperty("ContentType", "application/x-www-form-urlencoded; charset=UTF-8");
        ResponseFuture<JsonObject> waiter = Ion.with(this)
                .load(getString(R.string.kCreateRequest))
                .setJsonObjectBody(main)
                .asJsonObject();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }
}
