package org.zezutom.capstone.android.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;

import org.zezutom.capstone.android.util.BitmapCache;
import org.zezutom.capstone.android.R;
import org.zezutom.capstone.android.model.NavigationItem;

import java.util.List;

public class MenuItemAdapter extends ArrayAdapter<NavigationItem> {

    private List<NavigationItem> navigationItems;

    private RequestQueue requestQueue;

    private ImageLoader imageLoader;

    public MenuItemAdapter(Context context, List<NavigationItem> navigationItems) {
        super(context, (navigationItems == null) ? 0 : navigationItems.size());
        this.navigationItems = navigationItems;
        this.requestQueue = Volley.newRequestQueue(context);
        this.imageLoader = new ImageLoader(requestQueue, new BitmapCache(4));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            final NavigationItem item = navigationItems.get(position);
            final boolean hasImageUrl = hasImageUrl(item);
            final int layoutId = hasImageUrl ?
                    R.layout.row_navigation_image_download : R.layout.row_navigation;

            LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layoutId, null);

            TextView textView = (TextView) convertView.findViewById(R.id.row_menu_title);
            textView.setText(item.getTitle());

            if (hasImage(item)) {
                ImageView imageView = (ImageView) convertView.findViewById(R.id.row_menu_image);

                if (hasImageUrl) {
                    NetworkImageView.class.cast(imageView).setImageUrl(item.getImageUrl(), imageLoader);
                } else {
                    imageView.setImageResource(item.getImageId());
                }
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

    private boolean hasImageUrl(NavigationItem item) {
        return !TextUtils.isEmpty(item.getImageUrl());
    }

    private boolean hasImage(NavigationItem item) {
        return item.getImageId() != null || hasImageUrl(item);
    }

}
