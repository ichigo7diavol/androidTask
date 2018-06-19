package com.example.arhon.app;

import java.util.ArrayList;
import java.util.TreeSet;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.CheckBox;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

class CustomAdapter extends BaseAdapter {

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_SEPARATOR = 1;

    private ArrayList<JsonElement> mData = new ArrayList<JsonElement>();

    private TreeSet<Integer> sectionHeader = new TreeSet<Integer>();

    private LayoutInflater mInflater;

    public CustomAdapter(Context context) {
        mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void addItem(final JsonElement item) {
        mData.add(item);
        notifyDataSetChanged();
    }

    public void addSectionHeaderItem(final JsonElement item) {
        mData.add(item);
        sectionHeader.add(mData.size() - 1);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return sectionHeader.contains(position) ? TYPE_SEPARATOR : TYPE_ITEM;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public JsonElement getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        int rowType = getItemViewType(position);

        if (convertView == null) {
            holder = new ViewHolder();
            switch (rowType) {
                case TYPE_ITEM:
                    convertView = mInflater.inflate(R.layout.list_header, null);
                    holder.textView = (TextView) convertView.findViewById(R.id.text);
                    holder.checkBox = (CheckBox) convertView.findViewById(R.id.checkbox);
                    holder.checkBox.setTag(mData.get(position));
                    Log.i("My",mData.get(position).getAsJsonObject().get("isCompleted").toString());

                    if((!mData.get(position).getAsJsonObject().get("isCompleted").isJsonNull()) &&
                            mData.get(position).getAsJsonObject().get("isCompleted").getAsBoolean())
                        holder.checkBox.setChecked(true);

                    holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                            if (((JsonElement)buttonView.getTag()).getAsJsonObject().get("isCompleted").isJsonNull() && !isChecked ||
                                    !((JsonElement)buttonView.getTag()).getAsJsonObject().get("isCompleted").isJsonNull() &&
                                            ((JsonElement)buttonView.getTag()).getAsJsonObject().get("isCompleted").getAsBoolean() == isChecked) return;

                            JsonObject tmp = new JsonObject();
                            tmp.add("todo", (JsonElement)buttonView.getTag());
                            tmp.get("todo").getAsJsonObject().add("pr_id", ((JsonObject)buttonView.getTag()).get("project_id"));
                            tmp.addProperty("isCompleted", isChecked);

                            Ion.with(buttonView.getContext())
                                    .load("PATCH", buttonView.getContext().getString(R.string.kCreateRequest) + "/" + tmp.get("todo").getAsJsonObject().get("id").getAsString())
                                    .setJsonObjectBody(tmp)
                                    .asJsonObject()
                                    .setCallback(new FutureCallback<JsonObject>() {
                                        @Override
                                        public void onCompleted(Exception e, JsonObject result) { }});
                        }
                    });

                    break;
                case TYPE_SEPARATOR:
                    convertView = mInflater.inflate(R.layout.list_row, null);
                    holder.textView = (TextView) convertView.findViewById(R.id.textSeparator);

                    break;
            }
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (sectionHeader.contains(position)) {
            holder.textView.setText(mData.get(position).getAsJsonObject().get("title").getAsString());
        } else {
            holder.textView.setText(mData.get(position).getAsJsonObject().get("text").getAsString());
        }

        return convertView;
    }

    public ArrayList<String> getHeaders () {

        ArrayList<String> headers = new ArrayList<String>();

        for(int index : sectionHeader) {
            headers.add(mData.get(index).getAsJsonObject().get("title").getAsString());
            headers.add(mData.get(index).getAsJsonObject().get("id").getAsString());
        }
        return headers;
    }

    public void sendUpdate (JsonElement obj) {

    }

    public static class ViewHolder {
        public TextView textView;
        public CheckBox checkBox;
    }

}