package com.example.arhon.app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.scalified.fab.ActionButton;

import java.sql.Array;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class MainActivity extends AppCompatActivity {

    private CustomAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewPump.init(ViewPump.builder()
                .addInterceptor(new CalligraphyInterceptor(
                        new CalligraphyConfig.Builder()
                                .setDefaultFontPath("fonts/OpenSans-Regular.ttf")
                                .setFontAttrId(R.attr.fontPath)
                                .build()))
                .build());

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        myToolbar.setTitle("Задачи");

        setSupportActionBar(myToolbar);


        ActionButton button = findViewById(R.id.action_button);
        button.setImageDrawable(getResources().getDrawable(R.drawable.fab_plus_icon));

        button.setOnClickListener(
                new View.OnClickListener() {
                public void onClick(View v) {

                ListView lv = findViewById(R.id.mListView);

                Intent i = new Intent(MainActivity.this, CreationActivity.class);

                i.putStringArrayListExtra("category", ((CustomAdapter) lv.getAdapter()).getHeaders());

                startActivity(i);
            }
        });
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getDelegate().onPostCreate(savedInstanceState);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        getDelegate().onPostResume();
        mAdapter = new CustomAdapter(this);

        Ion.with(this)
                .load(getString(R.string.kIndexRequest))
                .asJsonArray()
                .setCallback(new FutureCallback<JsonArray>() {
                    @Override
                    public void onCompleted(Exception e, JsonArray result) {
                        if (result != null) {
                            for (final JsonElement projectJsonElement : result) {
                                JsonElement tmp = projectJsonElement.getAsJsonObject().remove("todos");
                                mAdapter.addSectionHeaderItem(projectJsonElement);
//
                                for (final JsonElement todo : tmp.getAsJsonArray()) {
                                    mAdapter.addItem(todo.getAsJsonObject());
                                }
                            }
                        }
                    }
                });

        ListView lv = findViewById(R.id.mListView);
        lv.setAdapter(mAdapter);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }
}
