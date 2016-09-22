package br.com.instapromo.instapromo;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import br.com.instapromo.instapromo.adapters.ProductAdapter;
import br.com.instapromo.instapromo.connection.Back4AppAPI;
import br.com.instapromo.instapromo.gps.GeoLocation;
import br.com.instapromo.instapromo.model.Back4AppResponse;
import br.com.instapromo.instapromo.model.Product;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static br.com.instapromo.instapromo.R.string.permission_location_rationale;
import static br.com.instapromo.instapromo.commons.Constants.PERMISSIONS_LOCATION;
import static br.com.instapromo.instapromo.commons.Constants.REQUEST_LOCATION;
import static br.com.instapromo.instapromo.commons.Constants.TAG_TIMELINE;
import static br.com.instapromo.instapromo.commons.Constants.URL_NO_PROMOTION;
import static br.com.instapromo.instapromo.permission.PermissionMan.hasPermission;
import static br.com.instapromo.instapromo.permission.PermissionMan.requestWithSnack;

/**
 * Created by montanha on 9/21/16.
 */
public class TimelineActivity extends AppCompatActivity {

    private Back4AppAPI apiBack = new Back4AppAPI();

    private RecyclerView recyclerView;

    private ProductAdapter adapter;

    private LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

    private SwipeRefreshLayout mSwipeRefreshLayout;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_tab_timeline);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshItems();
            }
        });
    }

    void refreshItems() {
        Location location;

        View viewForSnack = findViewById(R.id.tab1);

        if (!hasPermission(this, ACCESS_COARSE_LOCATION) || !hasPermission(this, ACCESS_FINE_LOCATION)) {
            requestWithSnack(viewForSnack, this, PERMISSIONS_LOCATION, permission_location_rationale, REQUEST_LOCATION);
        } else {
            location = new GeoLocation(this).getLocation();

            rx.Observable<Back4AppResponse> promos = apiBack.get(location.getLatitude(), location.getLongitude(), 5);
            promos.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<Back4AppResponse>() {
                        @Override
                        public void onCompleted() {
                            Log.d(TAG_TIMELINE, "Completo");
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.e(TAG_TIMELINE, e.getMessage());
                        }

                        @Override
                        public void onNext(Back4AppResponse back4AppResponse) {
                            if (back4AppResponse.getResults().isEmpty()) {
                                Product productEmpty = new Product();
                                productEmpty.setDesc("");
                                productEmpty.setLocal("");
                                productEmpty.setPreco("");
                                productEmpty.setUrlImg(URL_NO_PROMOTION);

                                List<Product> list = new ArrayList();
                                list.add(productEmpty);

                                adapter = new ProductAdapter(TimelineActivity.this, list);
                            } else {
                                adapter = new ProductAdapter(TimelineActivity.this, back4AppResponse.getResults());
                            }

                            recyclerView = (RecyclerView) findViewById(R.id.timeline);
                            recyclerView.setLayoutManager(linearLayoutManager);
                            recyclerView.setAdapter(adapter);
                        }
                    });
        }

        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    protected void onResume() {
        super.onResume();

        refreshItems();
    }
}