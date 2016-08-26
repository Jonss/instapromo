package br.com.instapromo.instapromo;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class ProductViewHolder extends RecyclerView.ViewHolder{

    private ImageView imageView;
    private TextView productDesc;
    private TextView productPrice;
    private TextView productStore;

    public ProductViewHolder(View view) {
        super(view);
        imageView = (ImageView) view.findViewById(R.id.product_image);
        productDesc = (TextView) view.findViewById(R.id.product_desc);
        productPrice = (TextView) view.findViewById(R.id.product_price);
        productStore = (TextView) view.findViewById(R.id.product_store);
    }

    public ImageView getImageView() {
        return imageView;
    }

    public TextView getProductDesc() {
        return productDesc;
    }

    public TextView getProductPrice() {
        return productPrice;
    }

    public TextView getProductStore() {
        return productStore;
    }

    public void setProductDesc(String desc) {
        this.productDesc.setText(desc);
    }

    public void setProductPrice(String price) {
        this.productPrice.setText(price);
    }

    public void setProductStore(String store) {
        this.productStore.setText(store);
    }
    public void setImageUrl(Context context, String url) {
        Glide.with(context).load(url).into(getImageView());
    }
}
