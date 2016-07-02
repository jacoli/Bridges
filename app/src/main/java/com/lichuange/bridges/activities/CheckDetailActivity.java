package com.lichuange.bridges.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.lichuange.bridges.models.Utils;
import com.lichuange.bridges.scan.scan.qrmodule.CaptureActivity;
import com.lichuange.bridges.R;
import com.lichuange.bridges.models.GetProjectDetailResponse;
import com.lichuange.bridges.models.MainService;
import com.lichuange.bridges.models.ProjectModel;
import com.lichuange.bridges.models.SensorItemModel;
import com.lichuange.bridges.models.SignItemModel;
import com.lichuange.bridges.views.MyToast;
import com.lichuange.bridges.views.TitleBar;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.internal.Util;

public class CheckDetailActivity extends MyBaseActivity {
    class MyLocationListener implements LocationListener {
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
    }

    private static final int REQUEST_CODE_QRCODE = 1001;
    private static final int REQUEST_CODE_QRCODE_FOR_DELETE_SENSOR = 1002;
    private String projectId;
    private Location curLocation;
    final private String titleBarTitle = "检查详情";

    private FrameLayout tmpDeleteBtnContainer;
    private Button tmpDeleteBtn;

    private Timer timerForRefleshSensorState;

    private int direction = 0; // -1为下行,0为未知,1为上行
    private String curScanedSignCode;

    private void createPopMenu() {
        // 更多菜单
        titleBar.setActionTextColor(Color.WHITE);
        titleBar.addAction(new TitleBar.TextAction("更多...") {
            @Override
            public void performAction(View view) {
                PopupMenu popupMenu = new PopupMenu(getApplicationContext(), view);
                getMenuInflater().inflate(R.menu.menu_check_detail, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.deleteSensorBtn:
                                onDeleteSenorBtnClicked();
                                break;
                            case R.id.recheckBtn:
                                MainService.getInstance().sendDeleteAllSensorCheck(projectId, handler);
                                break;
                            default:
                                break;
                        }
                        return true;
                    }
                });
                popupMenu.show();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_detail);
        createTitleBar();
        titleBar.setLeftText("项目列表");
        titleBar.setTitle(titleBarTitle);
        createPopMenu();

        Intent intent = getIntent();
        projectId = intent.getStringExtra("ProjectID");
        if (MainService.getInstance().sendProjectDetailQuery(projectId, handler)) {
            Toast.makeText(getBaseContext(), "获取项目详情中", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(getBaseContext(), "获取项目详情失败", Toast.LENGTH_SHORT).show();
        }

        Button locationBtn = (Button)findViewById(R.id.locationBtn);
        locationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationWorkStack();
            }
        });

        // 跳转到扫码界面
        Button scanBtn = (Button)findViewById(R.id.scanBtn);
        scanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (curLocation == null) {
                    Toast.makeText(getBaseContext(), "扫码前需要定位", Toast.LENGTH_SHORT).show();
                }
                else {
                    Intent intent = new Intent(CheckDetailActivity.this, CaptureActivity.class);
                    startActivityForResult(intent, REQUEST_CODE_QRCODE);
                }
            }
        });

        // 定位
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        LocationManager locManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        curLocation = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if (curLocation != null) {
            setTitleBarStatus("定位成功");
        }
        else {
            setTitleBarStatus("定位中，请稍后...确保GPS处于开启状态");
        }

        locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, new MyLocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                curLocation = location;
                setTitleBarStatus("定位成功");
            }
        });

        ScrollView scrollView = (ScrollView)findViewById(R.id.myScrollView);
        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                cleanTmpDeleteBtn();
                return false;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        // 启动定时器,定时刷新传感器状态
        long timeInterval = 5 * 60 *1000; // 5min

        timerForRefleshSensorState = new Timer();

        timerForRefleshSensorState.schedule(new TimerTask() {
                                                @Override
                                                public void run() {
                                                    MainService.getInstance().sendGetSignCheck(projectId, handler);
                                                }
                                            },
                timeInterval,
                timeInterval);
    }

    @Override
    protected void onStop() {
        super.onStop();

        timerForRefleshSensorState.cancel();
    }

    private void setTitleBarStatus(String status) {
        titleBar.setTitle(titleBarTitle + "\n" + status);
    }

    public void updateMainInfoViews() {
        ProjectModel model = MainService.getInstance().findDetailForProjectId(projectId);

        if (model == null) {
            return;
        }

        if (model.getDetail() == null) {
            return;
        }

        GetProjectDetailResponse detail = model.getDetail();

        // 项目基本信息
        TextView projectNameText = (TextView)findViewById(R.id.projectNameText);
        projectNameText.setText(detail.getProjectName());

        TextView lineNameText = (TextView)findViewById(R.id.LineNameText);
        lineNameText.setText(detail.getLineName());

        TextView controlStartStackText = (TextView)findViewById(R.id.ControlStartStackText);
        controlStartStackText.setText(detail.getControlStartStack());

        TextView controlEndStackText = (TextView)findViewById(R.id.ControlEndStackText);
        controlEndStackText.setText(detail.getControlEndStack());

        TextView workStartStackText = (TextView)findViewById(R.id.WorkStartStackText);
        workStartStackText.setText(detail.getGZ1());

        String locationDesc = "东经：" + detail.getGZ1Lon() + "，北纬：" + detail.getGZ1Lat();
        TextView workStackLocationText = (TextView)findViewById(R.id.WorkStackLocationText);
        workStackLocationText.setText(locationDesc);

        // 更新图片
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(getBaseContext()));
        String url = detail.getSchemeImage();
        if (url != null) {
            ImageView imageView = (ImageView)findViewById(R.id.imageView);
            imageLoader.displayImage(url, imageView);
        }

        // 更新道路维护方向
        direction = 0;
        String startStr = detail.getControlStartStack();
        String endStr = detail.getControlEndStack();
        if (startStr != null && endStr != null && startStr.length() > 0 && endStr.length() > 0) {
            long start = Utils.convertStackNumberToDistance(startStr);
            long end = Utils.convertStackNumberToDistance(endStr);
            if (start >= 0 && end >= 0) {
                if (start < end) {
                    direction = 1; // 上行
                }
                else {
                    direction = -1; // 下行
                }
            }
        }
    }

    public void updateSignItemsViews() {
        ProjectModel model = MainService.getInstance().findDetailForProjectId(projectId);

        if (model == null || model.getSignItems() == null) {
            return;
        }

        List<SignItemModel> items = model.getSignItems();

        int checkedCount = 0;
        for (SignItemModel item : items) {
            if (item.getActualStackNumber().length() > 0) {
                checkedCount++;
            }
        }

        cleanTmpDeleteBtn();

        // 标志牌检查
        TextView checkedCountText = (TextView)findViewById(R.id.CheckedItemsCount);
        String checkedCountStr = "" + checkedCount + "/" + items.size();
        checkedCountText.setText(checkedCountStr);


        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(getBaseContext()));
        LinearLayout boardsCheckLayout = (LinearLayout)findViewById(R.id.content);
        boardsCheckLayout.removeAllViews();


        for (int i = 0; i < items.size(); ++i) {
            final SignItemModel item = items.get(i);

            LinearLayout boardLayout = (LinearLayout)getLayoutInflater().inflate(R.layout.board_check_item_layout, null);

            TextView indexText = (TextView)boardLayout.findViewById(R.id.IndexText);
            TextView nameText = (TextView)boardLayout.findViewById(R.id.NameText);
            TextView designedStackText = (TextView)boardLayout.findViewById(R.id.DesignedStackText);
            final TextView realStackText = (TextView)boardLayout.findViewById(R.id.RealStackText);
            ImageView stackImageView = (ImageView)boardLayout.findViewById(R.id.ImageView);


            boardLayout.setLongClickable(true);
            boardLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    cleanTmpDeleteBtn();

                    if (item.getActualStackNumber() != null && item.getActualStackNumber().length() > 0) {
                        FrameLayout frameLayout = (FrameLayout)v.findViewById(R.id.RealStackTextFrameLayout);

                        Button button = (Button)getLayoutInflater().inflate(R.layout.button_delete, null);
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                cleanTmpDeleteBtn();
                                MainService.getInstance().sendDeleteSignCheck(projectId, item.getID(), handler);
                            }
                        });

                        frameLayout.addView(button);

                        tmpDeleteBtn = button;
                        tmpDeleteBtnContainer = frameLayout;
                    }
                }
            });


            String indexStr = Integer.toString(i + 1);
            indexText.setText(indexStr);
            nameText.setText(item.getSignName());
            designedStackText.setText(item.getDesignStackNumber());
            realStackText.setText(item.getActualStackNumber());
            imageLoader.displayImage(item.getSignImageFileName(), stackImageView);

            boardsCheckLayout.addView(boardLayout);

            // 如果标志牌的实际位置偏离设计值大于30m,则做相应提示.
            checkStackNumberAndPrompt(item);
        }

        ScrollView scrollView = (ScrollView)findViewById(R.id.myScrollView);
        scrollView.invalidate();
    }

    public void checkStackNumberAndPrompt(SignItemModel item) {
        // 如果标志牌的实际位置偏离设计值大于30m,则做相应提示.
        if (curScanedSignCode != null && curScanedSignCode.length() > 0
                && item.getSignCode() != null && item.getSignCode().equals(curScanedSignCode)) {
            if (direction != 0) {
                String actualStackStr = item.getActualStackNumber();
                String designedStackStr = item.getDesignStackNumber();
                if (actualStackStr != null && actualStackStr.length() > 0
                        && designedStackStr != null && designedStackStr.length() > 0) {
                    long actualStack = Utils.convertStackNumberToDistance(actualStackStr);
                    long designedStack = Utils.convertStackNumberToDistance(designedStackStr);
                    if (actualStack >= 0 && designedStack >= 0) {
                        String message = "";
                        if (actualStack - designedStack > 30) {
                            if (direction > 0) {
                                message = item.getSignCode() + "需要往后移动"
                                        + (Double.valueOf(actualStack - designedStack)).toString() + "米";
                            }
                            else {
                                message = item.getSignCode() + "需要往前移动"
                                        + (Double.valueOf(actualStack - designedStack)).toString() + "米";
                            }
                        }
                        else if (actualStack - designedStack < -30) {
                            if (direction > 0) {
                                message = item.getSignCode() + "需要往前移动"
                                        + (Double.valueOf(designedStack - actualStack)).toString() + "米";
                            }
                            else {
                                message = item.getSignCode() + "需要往后移动"
                                        + (Double.valueOf(designedStack - actualStack)).toString() + "米";
                            }
                        }

                        if (message.length() > 0) {
                            new AlertDialog.Builder(CheckDetailActivity.this).setTitle("提示")
                                    .setMessage(message)
                                    .setPositiveButton("确定", null).show();
                        }
                    }
                }
            }
        }
    }

    public void cleanTmpDeleteBtn() {
        if (tmpDeleteBtn != null) {

            tmpDeleteBtnContainer.removeView(tmpDeleteBtn);

            tmpDeleteBtn = null;
            tmpDeleteBtnContainer = null;
        }
    }

    public void updateSensorItemsViews() {
        ProjectModel model = MainService.getInstance().findDetailForProjectId(projectId);

        if (model == null || model.getSensorItems() == null) {
            return;
        }

        List<SensorItemModel> items = model.getSensorItems();

//        // 标志牌检查
        TextView StatusCheckCount = (TextView)findViewById(R.id.StatusCheckCount);
        String checkedCountStr = "" + items.size() + "/" + items.size();
        StatusCheckCount.setText(checkedCountStr);


        LinearLayout boardsCheckLayout = (LinearLayout)findViewById(R.id.sensorItemsContaner);
        boardsCheckLayout.removeAllViews();
        cleanTmpDeleteBtn();
        for (int i = 0; i < items.size(); ++i) {
            final SensorItemModel item = items.get(i);

            final LinearLayout boardLayout = (LinearLayout)getLayoutInflater().inflate(R.layout.board_status_item_layout, null);

            boardLayout.setLongClickable(true);
            boardLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    cleanTmpDeleteBtn();

                    FrameLayout frameLayout = (FrameLayout)v.findViewById(R.id.StatusFrameLayout);

                    Button button = (Button)getLayoutInflater().inflate(R.layout.button_delete, null);
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            MainService.getInstance().sendDeleteSensorCheck(projectId, item.getSensorNumber(), handler);
                            cleanTmpDeleteBtn();
                        }
                    });

                    frameLayout.addView(button);

                    tmpDeleteBtn = button;
                    tmpDeleteBtnContainer = frameLayout;
                }
            });

            TextView indexText = (TextView)boardLayout.findViewById(R.id.IndexText);
            TextView SensorNumber = (TextView)boardLayout.findViewById(R.id.SensorNumber);
            TextView StackNumber = (TextView)boardLayout.findViewById(R.id.StackNumber);
            TextView AddDate = (TextView)boardLayout.findViewById(R.id.AddDate);
            TextView Status = (TextView)boardLayout.findViewById(R.id.Status);
            ImageView imageView = (ImageView)boardLayout.findViewById(R.id.BatteryLevelImageView);

            // 如果传感器倾倒,弹窗提示.
            boolean isSenerFall = item.isSensorFall();
            if (isSenerFall) {
                String message = item.getStackNumber() + "处发生标志牌倾倒";
                new AlertDialog.Builder(CheckDetailActivity.this).setTitle("警告")
                        .setMessage(message)
                        .setPositiveButton("确定", null).show();
            }

            String indexStr = Integer.toString(i + 1);
            indexText.setText(indexStr);
            SensorNumber.setText(item.getSensorNumber());
            StackNumber.setText(item.getStackNumber());
            AddDate.setText(item.getAddDate());
            Status.setText(item.getStatus());

            String batteryLevelStr = item.getElectricity();
            if (batteryLevelStr != null && batteryLevelStr.length() > 0) {
                Integer batteryLevel = Integer.valueOf(batteryLevelStr);

                if (batteryLevel < 10) {
                    imageView.setImageResource(R.drawable.battery_level_0);
                }
                else if (batteryLevel < 50) {
                    imageView.setImageResource(R.drawable.battery_level_1);
                }
                else if (batteryLevel < 75) {
                    imageView.setImageResource(R.drawable.battery_level_2);
                }
                else {
                    imageView.setImageResource(R.drawable.battery_level_3);
                }
            }

            boardsCheckLayout.addView(boardLayout);
        }

        ScrollView scrollView = (ScrollView)findViewById(R.id.myScrollView);
        scrollView.invalidate();
    }

    public void updateLocationView(Location newLocation) {
        String locationDesc = "经度：NA，纬度：NA";
        if (newLocation != null)  {
            double precision = 1000000;

            double longitude = (long)(newLocation.getLongitude() * precision) / precision;
            double latitude = (long)(newLocation.getLatitude() * precision) / precision;
            locationDesc = "东经：" + longitude + "，北纬：" + latitude;
        }

        TextView workStackLocationText = (TextView)findViewById(R.id.WorkStackLocationText);
        workStackLocationText.setText(locationDesc);
    }

    public void locationWorkStack() {
        if (curLocation == null) {
            Toast.makeText(getBaseContext(), "定位失败", Toast.LENGTH_SHORT).show();
        }
        else {
            updateLocationView(curLocation);
            sendUpdateDatumLocation(curLocation);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_QRCODE: {
                if (resultCode == CaptureActivity.ZXING_SCAN_RESULT_CODE && data != null) {
                    final String contentUri = data.getStringExtra(CaptureActivity.ZXING_SCAN_CONTENT_DATA);
                    if (curLocation == null) {
                        Toast.makeText(getBaseContext(), "扫码成功，但定位失败", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getBaseContext(), ("扫码成功，二维码：" + contentUri), Toast.LENGTH_SHORT).show();
                        sendSignCheckWithLocation(curLocation, contentUri);
                        curScanedSignCode = contentUri;
                    }
                }
                break;
            }
            case REQUEST_CODE_QRCODE_FOR_DELETE_SENSOR: {
                if (resultCode == CaptureActivity.ZXING_SCAN_RESULT_CODE && data != null) {
                    final String contentUri = data.getStringExtra(CaptureActivity.ZXING_SCAN_CONTENT_DATA);
                    MainService.getInstance().sendDeleteSensorCheck(projectId, contentUri, handler);
                }
                break;
            }
            default:
                // ignored
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onResponse(int msgCode) {
        switch (msgCode) {
            case MainService.MSG_QUERY_PROJECT_DETAIL_SUCCESS:
                MyToast.showMessage(getBaseContext(), "获取项目详情成功");
                updateMainInfoViews();
                MainService.getInstance().sendGetSignCheck(projectId, handler);
                break;
            case MainService.MSG_QUERY_PROJECT_DETAIL_FAILED:
                Toast.makeText(getBaseContext(), "获取项目详情失败", Toast.LENGTH_SHORT).show();
                break;
            case MainService.MSG_GET_SIGN_CHECK_SUCCESS:
                MyToast.showMessage(getBaseContext(), "获取检查标志成功");
                updateSignItemsViews();
                updateSensorItemsViews();
                curScanedSignCode = null;
                break;
            case MainService.MSG_GET_SIGN_CHECK_FAILED:
                Toast.makeText(getBaseContext(), "获取检查标志失败", Toast.LENGTH_SHORT).show();
                curScanedSignCode = null;
                break;
            case MainService.MSG_UPDATE_PROJECT_DATUM_SUCCESS:
                Toast.makeText(getBaseContext(), "上传基点成功", Toast.LENGTH_SHORT).show();
                break;
            case MainService.MSG_UPDATE_PROJECT_DATUM_FAILED:
                Toast.makeText(getBaseContext(), "上传基点失败", Toast.LENGTH_SHORT).show();
                break;
            case MainService.MSG_SEND_SIGN_CHECK_SUCCESS:
                Toast.makeText(getBaseContext(), "上传检查成功", Toast.LENGTH_SHORT).show();
                MainService.getInstance().sendGetSignCheck(projectId, handler);
                break;
            case MainService.MSG_SEND_SIGN_CHECK_FAILED:
                Toast.makeText(getBaseContext(), "上传检查失败", Toast.LENGTH_SHORT).show();
                curScanedSignCode = null;
                break;
            case MainService.MSG_DELETE_SENSOR_CHECK_SUCCESS:
                Toast.makeText(getBaseContext(), "删除传感器成功", Toast.LENGTH_SHORT).show();
                updateSensorItemsViews();
                break;
            case MainService.MSG_DELETE_SENSOR_CHECK_FAILED:
                Toast.makeText(getBaseContext(), "删除传感器失败", Toast.LENGTH_SHORT).show();
                break;
            case MainService.MSG_DELETE_SIGN_CHECK_SUCCESS:
                Toast.makeText(getBaseContext(), "删除标志成功", Toast.LENGTH_SHORT).show();
                updateSignItemsViews();
                break;
            case MainService.MSG_DELETE_SIGN_CHECK_FAILED:
                Toast.makeText(getBaseContext(), "删除标志失败", Toast.LENGTH_SHORT).show();
                break;
            case MainService.MSG_DELETE_ALL_SENSOR_CHECK_SUCCESS:
                Toast.makeText(getBaseContext(), "重新检查成功", Toast.LENGTH_SHORT).show();
                updateSignItemsViews();
                updateSensorItemsViews();
                break;
            case MainService.MSG_DELETE_ALL_SENSOR_CHECK_FAILED:
                Toast.makeText(getBaseContext(), "重新检查失败", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

    public void sendUpdateDatumLocation(Location location) {
        double precision = 1000000;
        Double longitude = new Double ((long)(location.getLongitude() * precision) / precision);
        Double latitude = new Double ((long)(location.getLatitude() * precision) / precision);
        MainService.getInstance().sendUpdateProjectDatum(projectId, longitude.toString(), latitude.toString(), handler);
    }

    public void sendSignCheckWithLocation(Location location, String signCode) {
        double precision = 1000000;
        Double longitude = new Double ((long)(location.getLongitude() * precision) / precision);
        Double latitude = new Double ((long)(location.getLatitude() * precision) / precision);
        MainService.getInstance().sendSignCheck(projectId, longitude.toString(), latitude.toString(), signCode, handler);
    }

    public void onDeleteSenorBtnClicked() {
        Intent intent = new Intent(CheckDetailActivity.this, CaptureActivity.class);
        startActivityForResult(intent, REQUEST_CODE_QRCODE_FOR_DELETE_SENSOR);
    }
}
