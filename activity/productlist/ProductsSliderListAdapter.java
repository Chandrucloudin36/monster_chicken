package com.cloudin.monsterchicken.activity.productlist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.cloudin.monsterchicken.R;
import com.cloudin.monsterchicken.utils.NativeUtils;
import com.cloudin.monsterchicken.utils.autoimageslider.SliderViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class ProductsSliderListAdapter extends
        SliderViewAdapter<ProductsSliderListAdapter.SliderAdapterVH> {

    private Context context;
    private List<String> mSliderItems = new ArrayList<>();

    public ProductsSliderListAdapter(Context context) {
        this.context = context;
    }

    public void renewItems(List<String> sliderItems) {
        this.mSliderItems = sliderItems;
        notifyDataSetChanged();
    }

    public void deleteItem() {
        this.mSliderItems.removeAll(mSliderItems);
        notifyDataSetChanged();
    }

    public void addItem(List<String> sliderItem) {
        this.mSliderItems.addAll(sliderItem);
        notifyDataSetChanged();
    }

    @Override
    public SliderAdapterVH onCreateViewHolder(ViewGroup parent) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_product_images_slider, null);
        return new SliderAdapterVH(inflate);
    }

    @Override
    public void onBindViewHolder(SliderAdapterVH viewHolder, final int position) {
        String sliderItem = mSliderItems.get(position);
        NativeUtils.INSTANCE.setLoadImage(viewHolder.ivProductImages, sliderItem);
    }

    @Override
    public int getCount() {
        return mSliderItems.size();
    }

    static class SliderAdapterVH extends ViewHolder {

        ImageView ivProductImages;

        public SliderAdapterVH(View itemView) {
            super(itemView);
            ivProductImages = itemView.findViewById(R.id.ivProductImages);
            this.itemView = itemView;
        }
    }

}
