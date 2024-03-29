package com.gongpingjia.carplay.activity.active;

import java.util.List;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.AMap.OnMapClickListener;
import com.amap.api.maps2d.AMap.OnMarkerClickListener;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.core.SuggestionCity;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.GeocodeSearch.OnGeocodeSearchListener;
import com.amap.api.services.geocoder.RegeocodeAddress;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.poisearch.PoiItemDetail;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.amap.api.services.poisearch.PoiSearch.OnPoiSearchListener;
import com.gongpingjia.carplay.R;
import com.gongpingjia.carplay.activity.CarPlayBaseActivity;
import com.gongpingjia.carplay.bean.MapEB;
import com.gongpingjia.carplay.view.ClearableEditText;

import de.greenrobot.event.EventBus;

/*
 *@author zhanglong
 *Email:1269521147@qq.com
 */
public class MapActivity extends CarPlayBaseActivity implements OnMarkerClickListener, OnPoiSearchListener,
        AMapLocationListener, OnMapClickListener, OnGeocodeSearchListener {

    private static final int REQUEST_KEY = 0;

    private MapView mMapView;

    private AMap aMap;

    private PoiSearch mPoiSearch;

    private PoiSearch.Query mQuery;

    private ClearableEditText mSearchEdit;

    private LocationManagerProxy mLocationManager;

    // private MarkerOptions mMarkerOptions;

    private boolean mIsFirstLocate = true;

    private TextView mLocDesText;

    private TextView mLocTitleText;

    private TextView mSelectText;

    private GeocodeSearch mGeoSearch;

    private LatLng mCurLatLng;

    private RegeocodeAddress mAddress;

    private Marker mMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        setTitle("地点选择");
        mMapView = (MapView) findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        setUpMap();

        mLocDesText = (TextView) findViewById(R.id.tv_loc_des);
        mLocTitleText = (TextView) findViewById(R.id.tv_loc_title);
        mSearchEdit = (ClearableEditText) findViewById(R.id.et_search);
        mSearchEdit.setInputType(InputType.TYPE_NULL);
        mSearchEdit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent it = new Intent(self, SearchPlaceActivity.class);
                self.startActivityForResult(it, REQUEST_KEY);
            }
        });

        // 选定位置
        mSelectText = (TextView) findViewById(R.id.tv_select);
        mSelectText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mAddress != null) {
                    Intent it = new Intent();
                    it.putExtra("city", mAddress.getCity());
                    it.putExtra("province", mAddress.getProvince());
                    it.putExtra("district", mAddress.getDistrict());
                    it.putExtra("address", mAddress.getFormatAddress());
                    it.putExtra("location", mLocTitleText.getText().toString());
                    if (mCurLatLng != null) {
                        it.putExtra("longitude", mCurLatLng.longitude);
                        it.putExtra("latitude", mCurLatLng.latitude);
                    } else {
                        showToast("请选择准确位置");
                        return;
                    }
                    setResult(RESULT_OK, it);
                    self.finish();
                    MapEB map = new MapEB();
                    map.setCity(mAddress.getCity());
                    map.setDistrict(mAddress.getDistrict());
                    EventBus.getDefault().post(map);
                } else {
                    showToast("请选择地点");
                }
            }
        });
        showProgressDialog("定位中...");
    }

    public void setUpMap() {
        aMap = mMapView.getMap();
        aMap.setOnMarkerClickListener(this);
        aMap.setOnMapClickListener(this);

        mLocationManager = LocationManagerProxy.getInstance(this);
        // 混合定位，默认开启gps定位
        mLocationManager.setGpsEnable(true);
        mLocationManager.requestLocationData(LocationProviderProxy.AMapNetwork, 20000, 10, this);

        // mMarkerOptions = new MarkerOptions();
        // mMarkerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_position));
        // mMarkerOptions.draggable(true);

        // 地址编码
        mGeoSearch = new GeocodeSearch(this);
        mGeoSearch.setOnGeocodeSearchListener(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        mMapView.onPause();
        if (mLocationManager != null) {
            mLocationManager.removeUpdates(this);
        }
        mLocationManager = null;
    }

    @Override
    protected void onDestroy() {
        mMapView.onDestroy();
        if (mLocationManager != null) {
            mLocationManager.removeUpdates(this);
            mLocationManager.destroy();
        }
        mLocationManager = null;
        super.onDestroy();
    }

    @Override
    public void onPoiItemDetailSearched(PoiItemDetail detail, int rCode) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onPoiSearched(PoiResult result, int rCode) {
        hidenProgressDialog();
        if (rCode == 0) {
            if (result != null && result.getQuery() != null) {
                if (result.getQuery().equals(mQuery)) {
                    // 是否同一条
                    List<PoiItem> poiItems = result.getPois();
                    // 当搜索不到poi item数据时，会返回含有搜索关键字的城市信息
                    List<SuggestionCity> suggestionCities = result.getSearchSuggestionCitys();
                    if (poiItems != null && poiItems.size() > 0) {
                        // aMap.clear();
                        // LatLonPoint firstPoint =
                        // poiItems.get(0).getLatLonPoint();
                        // aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new
                        // LatLng(firstPoint.getLatitude(),
                        // firstPoint.getLongitude()), 15));
                        // if (poiItems.size() == 1) {
                        // mLocTitleText.setText(poiItems.get(0).getTitle());
                        // mLocDesText.setText(mAddress.getFormatAddress());
                        // }

                        PoiItem item = poiItems.get(0);
                        RegeocodeQuery query = new RegeocodeQuery(item.getLatLonPoint(), 200, GeocodeSearch.AMAP);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
                        mGeoSearch.getFromLocationAsyn(query);// 设置同步逆地理编码请求
                        mCurLatLng = new LatLng(item.getLatLonPoint().getLatitude(), item.getLatLonPoint()
                                .getLongitude());
                        // if (mQuery.getPageSize() == 1) {
                        // // mLocTitleText.setText(item.getTitle());
                        // // mLocDesText.setText(mAddress.getFormatAddress());
                        // } else if (mQuery.getPageSize() == 10) {
                        // // LatLng ll = new
                        // // LatLng(item.getLatLonPoint().getLatitude(),
                        // // item.getLatLonPoint()
                        // // .getLongitude());
                        // // mCurLatLng = ll;
                        // // mLocTitleText.setText(item.getTitle());
                        // //
                        // mLocDesText.setText(item.getProvinceName()+item.getCityName()+item.get)
                        // }

                        // PoiOverlay poiOverlay = new PoiOverlay(aMap,
                        // poiItems);
                        // poiOverlay.removeFromMap();
                        // poiOverlay.addToMap();
                        // poiOverlay.zoomToSpan();
                    } else if (suggestionCities != null && suggestionCities.size() > 0) {
                        showToast("条件模糊，请重新搜索");
                    } else {
                        showToast("暂无搜索结果");
                    }
                }
            } else {
                showToast("暂无搜索结果");
            }
        } else if (rCode == 27) {
            showToast("网络出问题啦");
        } else if (rCode == 32) {
            showToast("暂无搜索结果");
        } else {
            showToast("暂无搜索结果");
        }

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        mCurLatLng = marker.getPosition();
        LatLonPoint latLonPoint = new LatLonPoint(mCurLatLng.latitude, mCurLatLng.longitude);
        RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200, GeocodeSearch.AMAP);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
        mGeoSearch.getFromLocationAsyn(query);// 设置同步逆地理编码请求
        return true;
    }

    @Override
    public void initView() {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onLocationChanged(AMapLocation location) {
        if (location != null) {
            LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
            // mMarkerOptions.position(ll);
            // mMarkerOptions.title(location.getPoiName());
            // mMarkerOptions.snippet(location.getDistrict());
            // aMap.addMarker(mMarkerOptions);

            mMarker = aMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f).icon(
                    BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

            if (mIsFirstLocate) {
                hidenProgressDialog();
                mCurLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                mIsFirstLocate = false;
                LatLonPoint latLonPoint = new LatLonPoint(ll.latitude, ll.longitude);
                RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200, GeocodeSearch.AMAP);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
                mGeoSearch.getFromLocationAsyn(query);// 设置同步逆地理编码请求
            }
        }
    }

    @Override
    public void onMapClick(LatLng ll) {
        // TODO Auto-generated method stub
        mCurLatLng = ll;
        LatLonPoint latLonPoint = new LatLonPoint(ll.latitude, ll.longitude);
        RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200, GeocodeSearch.AMAP);//
        // 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
        mGeoSearch.getFromLocationAsyn(query);// 设置同步逆地理编码请求
    }

    @Override
    public void onGeocodeSearched(GeocodeResult result, int arg1) {

    }

    // 逆地址编码
    @Override
    public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
        if (rCode == 0) {
            if (result != null && result.getRegeocodeAddress() != null
                    && result.getRegeocodeAddress().getFormatAddress() != null) {

                mAddress = result.getRegeocodeAddress();
                PoiItem item = mAddress.getPois().get(0);
                mLocTitleText.setText(item.getTitle());
                mLocDesText.setText(mAddress.getFormatAddress());

                aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mCurLatLng, 15));
                mMarker.setPosition(mCurLatLng);
                // mQuery = new PoiSearch.Query(mAddress.getFormatAddress(), "",
                // "");// 全国搜索
                // mQuery.setPageSize(1);// 每页查询1个
                // mQuery.setPageNum(0);// 设置查第一页
                //
                // mPoiSearch = new PoiSearch(MapActivity.this, mQuery);
                // mPoiSearch.setOnPoiSearchListener(MapActivity.this);
                // mPoiSearch.searchPOIAsyn();
            } else {
                showToast("暂无搜索结果");
            }
        } else if (rCode == 27) {
            showToast("网络错误");
        } else if (rCode == 32) {
            showToast("api key有误");
        } else {
            showToast("暂无搜索结果");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_KEY) {
                String key = data.getStringExtra("key");
                mSearchEdit.setText(key);
                showProgressDialog("正在搜索...");
                mQuery = new PoiSearch.Query(key, "", "");// 全国搜索
                mQuery.setPageSize(1);// 每页查询10个
                mQuery.setPageNum(0);// 设置查第一页

                mPoiSearch = new PoiSearch(MapActivity.this, mQuery);
                mPoiSearch.setOnPoiSearchListener(MapActivity.this);
                mPoiSearch.searchPOIAsyn();
            }
        }

    }

}