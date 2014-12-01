package org.zezutom.capstone.android.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.zezutom.capstone.android.R;
import org.zezutom.capstone.android.model.NavigationItem;

import java.util.List;

public class NavigationItemAdapter extends ArrayAdapter<NavigationItem> {

    private List<NavigationItem> navigationItems;

    public NavigationItemAdapter(Context context, List<NavigationItem> navigationItems) {
        super(context, (navigationItems == null) ? 0 : navigationItems.size());
        this.navigationItems = navigationItems;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            final NavigationItem item = navigationItems.get(position);
            final int layoutId = R.layout.row_navigation;

            LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layoutId, null);

            TextView textView = (TextView) convertView.findViewById(R.id.row_menu_title);
            textView.setText(item.getTitle());

            if (item.getImageId() != null) {
                ImageView imageView = (ImageView) convertView.findViewById(R.id.image);
                imageView.setImageResource(item.getImageId());
            }
        }
        return convertView;
    }

    @Override
    public int getCount() {
        return navigationItems.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public NavigationItem getItem(int position) {
        return navigationItems.get(position);
    }
}
