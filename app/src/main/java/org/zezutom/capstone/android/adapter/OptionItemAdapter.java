package org.zezutom.capstone.android.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.zezutom.capstone.android.R;

import java.util.List;

public class OptionItemAdapter extends ArrayAdapter<String> {

    private List<String> titles;

    public OptionItemAdapter(Context context, List<String> titles) {
        super(context, (titles == null) ? 0 : titles.size());
        this.titles = titles;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            String title = titles.get(position);

            LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.row_option, parent, false);

            TextView titleView = (TextView) convertView.findViewById(R.id.title);
            titleView.setText(title);
        }
        return convertView;
    }

    @Override
    public int getCount() {
        return titles.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public String getItem(int position) {
        return titles.get(position);
    }
}
