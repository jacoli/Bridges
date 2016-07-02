package com.lichuange.bridges.activities;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import com.lichuange.bridges.R;
import com.lichuange.bridges.models.ExploreModel;
import com.lichuange.bridges.models.ExploreParamsMetaInfo;
import com.lichuange.bridges.models.MainService;
import com.lichuange.bridges.models.Utils;
import com.lichuange.bridges.views.MyToast;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

public class ExploreDetailActivity extends MyBaseActivity {

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

    final public static String EXPLORE_EXTRA_MODEL_ID = "model_id";

    private ExploreModel model;
    private ExploreParamsMetaInfo paramsModel;

    private Location curLocation;
    final private String titleBarTitle = "踏勘详情";

    private void setTitleBarStatus(String status) {
        titleBar.setTitle(titleBarTitle + "\n" + status);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore_detail);
        createTitleBar();
        titleBar.setTitle(titleBarTitle);

        // 设置参数元信息
        paramsModel = ExploreParamsMetaInfo.getParamsModel();
        if (paramsModel == null) {
            MyToast.showMessage(getBaseContext(), "参数初始化...");


            Runnable networkTask = new Runnable() {

                @Override
                public void run() {
                    ExploreParamsMetaInfo.setNewParamsModel(getFromAssets("explore_params_meta"));

                    Message msg = new Message();
                    msg.what = MainService.MSG_LOAD_EXPLORE_PARAMS_META_SUCCESS;
                    handler.sendMessage(msg);
                }
            };

            new Thread(networkTask).start();
        }

        paramsModel = ExploreParamsMetaInfo.getParamsModel();


        // 加载数据模型
        Intent intent = getIntent();
        String modelId = intent.getStringExtra(EXPLORE_EXTRA_MODEL_ID);
        if (modelId == null || modelId.length() == 0) {
            modelId = MainService.getInstance().addExploreModel();
        }
        model = MainService.getInstance().findExploreModel(modelId);
        model.setExplorDate(Utils.getCurrentDateStr());

        // 暂存按钮
        Button saveBtn = (Button)findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (persistAllExplores()) {
                    MyToast.showMessage(getBaseContext(), "暂存成功");
                }
                else {
                    MyToast.showMessage(getBaseContext(), "暂存失败");
                }
            }
        });

        // 上传按钮
        Button uploadBtn = (Button)findViewById(R.id.uploadBtn);
        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainService.getInstance().sendExplor(model, handler);
            }
        });

        // 删除按钮
        Button deleteBtn = (Button)findViewById(R.id.deleteBtn);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainService.getInstance().deleteExploreModel(model);
                MyToast.showMessage(getBaseContext(), "删除成功");
                finish();
            }
        });

        // 更新视图
        updateMainInfoViews();
        reloadParamItemsViews();

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
    }

    private boolean persistAllExplores() {
        boolean ret = false;
        // 数据模型持久化
        try {
            List<ExploreModel> list = MainService.getInstance().getExploreList();
            FileOutputStream stream = this.openFileOutput("explore_list.s", MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(stream);
            oos.writeObject(list);
            oos.close();
            stream.close();
            ret = true;
        }
        catch (Exception e) {
            Log.e("", e.toString());
        }

        return ret;
    }

    @Override
    protected void onStop() {
        super.onStop();

        persistAllExplores();
    }

    private void updateProjectNameView() {
        LinearLayout layout = (LinearLayout)findViewById(R.id.projectNameLayout);

        final EditText editText = (EditText)layout.findViewById(R.id.edit_text);

        editText.setText(model.getProjectName());

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                model.setProjectName(s.toString());
            }
        });

        editText.clearFocus();
    }

    private void updateLineNameView() {
        LinearLayout layout = (LinearLayout)findViewById(R.id.lineNameLayout);

        final EditText editText = (EditText)layout.findViewById(R.id.edit_text);

        editText.setText(model.getLineName());

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                model.setLineName(s.toString());
            }
        });

        editText.clearFocus();
    }

    private void updateEploreDateView() {
        TextView textView = (TextView)findViewById(R.id.exploreDateText);
        textView.setText(model.getExplorDate());
    }

    private void updateStartWorkLocationView() {
        LinearLayout layout = (LinearLayout)findViewById(R.id.WorkStartStackLayout);
        final EditText editText = (EditText)layout.findViewById(R.id.edit_text);
        final TextView workStartStackText = (TextView)layout.findViewById(R.id.WorkStartStackText);
        final Button workStartStackBtn = (Button)layout.findViewById(R.id.WorkStartStackBtn);


        String startWorkStack = model.getParams().get("GZ1");
        if (startWorkStack == null || startWorkStack.length() == 0) {
            startWorkStack = "K";
        }

        editText.setText(startWorkStack);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                model.getParams().put("GZ1", s.toString());
            }
        });

        editText.clearFocus();

        String lon = model.getParams().get("Lon1");
        String lat = model.getParams().get("Lat1");

        if (lon != null && lon.length() > 0 && lat != null && lat.length() > 0) {
            workStartStackText.setText("经度：" + lon + "，纬度：" + lat);
            workStartStackBtn.setText("修改定位");
        }
        else {
        }

        workStartStackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (curLocation != null) {
                    double precision = 1000000;
                    double longitude = (long)(curLocation.getLongitude() * precision) / precision;
                    double latitude = (long)(curLocation.getLatitude() * precision) / precision;
                    String locationDesc = "东经：" + longitude + "，北纬：" + latitude;

                    workStartStackText.setText(locationDesc);
                    workStartStackBtn.setText("修改定位");

                    model.getParams().put("Lon1", Double.toString(longitude));
                    model.getParams().put("Lat1", Double.toString(latitude));
                }
                else  {
                    Toast.makeText(getBaseContext(), "定位失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateRoadDirectionView() {
        LinearLayout layout = (LinearLayout)findViewById(R.id.RoadDirectionLayout);
        final TextView RoadDirectionText = (TextView)layout.findViewById(R.id.RoadDirectionText);
        final Button RoadDirectionBtn = (Button)layout.findViewById(R.id.RoadDirectionBtn);

        String lon = model.getParams().get("Lon2");
        String lat = model.getParams().get("Lat2");

        if (lon != null && lon.length() > 0 && lat != null && lat.length() > 0) {
            RoadDirectionText.setText("经度：" + lon + "，纬度：" + lat);
            RoadDirectionBtn.setText("修改定位");
        }
        else {
        }

        RoadDirectionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (curLocation != null) {
                    double precision = 1000000;
                    double longitude = (long)(curLocation.getLongitude() * precision) / precision;
                    double latitude = (long)(curLocation.getLatitude() * precision) / precision;
                    String locationDesc = "东经：" + longitude + "，北纬：" + latitude;

                    RoadDirectionText.setText(locationDesc);
                    RoadDirectionBtn.setText("修改定位");

                    model.getParams().put("Lon2", Double.toString(longitude));
                    model.getParams().put("Lat2", Double.toString(latitude));
                }
                else  {
                    Toast.makeText(getBaseContext(), "定位失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateEndWorkLocationView() {
        LinearLayout layout = (LinearLayout)findViewById(R.id.WorkEndStackLayout);
        final EditText editText = (EditText)layout.findViewById(R.id.edit_text);

        String endWorkStack = model.getParams().get("GZ2");
        if (endWorkStack == null || endWorkStack.length() == 0) {
            endWorkStack = "K";
        }

        editText.setText(endWorkStack);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                model.getParams().put("GZ2", s.toString());
            }
        });

        editText.clearFocus();
    }

    private void updateMainInfoViews() {
        updateProjectNameView();
        updateLineNameView();
        updateEploreDateView();
        updateStartWorkLocationView();
        updateRoadDirectionView();
        updateEndWorkLocationView();
    }

    private void reloadParamItemsViews() {
        if (paramsModel == null) {
            return;
        }

        LinearLayout container = (LinearLayout)findViewById(R.id.paramItemsContainer);
        container.removeAllViews();

        List<ExploreParamsMetaInfo.ExploreParamItemModel> itemsList = paramsModel.getItems();
        for (ExploreParamsMetaInfo.ExploreParamItemModel item : itemsList) {

            String selectionMode = item.getSelectionMode();
            if (selectionMode != null
                    && selectionMode.equals(ExploreParamsMetaInfo.ExploreParamItemModel.SELECTION_MODE_EDIT_NUM)) {
                addEditableNumberParamView(container, item);
            }
            else if (selectionMode != null
                    && selectionMode.equals(ExploreParamsMetaInfo.ExploreParamItemModel.SELECTION_MODE_EDIT_TEXT)) {
                addEditableTextParamView(container, item);
            }
            else {
                addSelectableParamView(container, item);
            }
        }
    }

    public void addEditableNumberParamView(ViewGroup container, final ExploreParamsMetaInfo.ExploreParamItemModel item) {
        // 生成参数项视图
        LinearLayout paramLayout = (LinearLayout)getLayoutInflater().inflate(R.layout.list_item_explore_param_edit, null);
        container.addView(paramLayout);
        TextView paramName = (TextView)paramLayout.findViewById(R.id.paramName);
        TextView valueRangeText = (TextView)paramLayout.findViewById(R.id.valueRangeText);
        EditText editText = (EditText)paramLayout.findViewById(R.id.edit_text);

        // 参数名
        paramName.setText(item.getDisplayName());


        String valueRange = "" + item.getUnit() + ", 范围:" + item.getMinValue() + "-" + item.getMaxValue();
        valueRangeText.setText(valueRange);

        String value = model.getParams().get(item.getItemKey());
        if (value == null || value.length() == 0) {
            value = item.getDefaultValue();
            model.getParams().put(item.getItemKey(), item.getDefaultValue());
        }

        editText.setText(value);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
//                String text = s.toString();
//
//                boolean isValid = true;
//
//                try {
//                    double value = Double.valueOf(text);
//
//                    if (item.getMinValue() != null) {
//                        double minValue = Double.valueOf(item.getMinValue());
//                        if (value < minValue) {
//                            isValid = false;
//                        }
//                    }
//
//                    if (item.getMaxValue() != null) {
//                        double maxValue = Integer.valueOf(item.getMaxValue());
//                        if (value > maxValue) {
//                            isValid = false;
//                        }
//                    }
//
//                } catch (NumberFormatException e) {
//                    Log.e("", e.toString());
//                    isValid = false;
//                } finally {
//                }
//
//                if (isValid) {
                model.getParams().put(item.getItemKey(), s.toString());
//                } else {
//                    MyToast.showMessage(getBaseContext(), "参数错误");
//                }
            }
        });

        editText.clearFocus();
    }

    public void addEditableTextParamView(ViewGroup container, final ExploreParamsMetaInfo.ExploreParamItemModel item) {
        // 生成参数项视图
        LinearLayout paramLayout = (LinearLayout)getLayoutInflater().inflate(R.layout.list_item_explore_param_edit, null);
        container.addView(paramLayout);
        TextView paramName = (TextView)paramLayout.findViewById(R.id.paramName);
        TextView valueRangeText = (TextView)paramLayout.findViewById(R.id.valueRangeText);
        EditText editText = (EditText)paramLayout.findViewById(R.id.edit_text);

        // 参数名
        paramName.setText(item.getDisplayName());

        String textAllowed = findValueRangeKey(item);
        if (textAllowed != null && textAllowed.length() > 0) {

            String valueRange = "格式:" + item.getRegEx();
            valueRangeText.setText(valueRange);


            String value = model.getParams().get(item.getItemKey());
            if (value == null || value.length() == 0) {
                value = item.getDefaultValue();
                //model.getParams().put(item.getItemKey(), item.getDefaultValue());
            }

            editText.setText(value);
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    model.getParams().put(item.getItemKey(), s.toString());
                }
            });

            editText.clearFocus();

        }
        else {
            valueRangeText.setText("");
            paramLayout.removeView(editText);
        }
    }

    public void addSelectableParamView(ViewGroup container, final ExploreParamsMetaInfo.ExploreParamItemModel item) {
        // 生成参数项视图
        LinearLayout paramLayout = (LinearLayout)getLayoutInflater().inflate(R.layout.list_item_explore_param, null);
        container.addView(paramLayout);
        TextView paramName = (TextView)paramLayout.findViewById(R.id.paramName);
        Spinner spinner = (Spinner)paramLayout.findViewById(R.id.spinner);

        // 参数名
        paramName.setText(item.getDisplayName());



        // 计算取值范围
        String valueRangeKey = findValueRangeKey(item);

        // 存在可选值,设置选项
        if (valueRangeKey != null && valueRangeKey.length() > 0 && item.getValueRanges().get(valueRangeKey) != null) {

            // 取值范围
            List<ExploreParamsMetaInfo.ExploreParamValueModel> paramValues = item.getValueRanges().get(valueRangeKey);

            // 更新前次的选择
            String choosedValue = model.getParams().get(item.getItemKey());
            boolean isChoosedValueAllowed = false;
            int choosedPosition = -1;
            if (choosedValue != null && choosedValue.length() > 0) {
                for (ExploreParamsMetaInfo.ExploreParamValueModel paramValue : paramValues) {
                    if (paramValue.getValue().equals(choosedValue)) {
                        isChoosedValueAllowed = true;
                        choosedPosition = paramValues.indexOf(paramValue);
                        break;
                    }
                }
            }



            final List<Map<String, String>> spinnerDataSource = new ArrayList<>();
            Map<String, String> spinnerItemDefault = new HashMap<>();
            spinnerItemDefault.put("ValueName", "--请选择--");
            spinnerItemDefault.put("Value", "");
            spinnerDataSource.add(spinnerItemDefault);



            if (isChoosedValueAllowed) {
                choosedPosition++;
            }
            else {
                model.getParams().put(item.getItemKey(), "");
                choosedPosition = 0;
            }

            for (ExploreParamsMetaInfo.ExploreParamValueModel paramValue : paramValues) {
                Map<String, String> spinnerItem = new HashMap<>();
                String value = "(" + paramValue.getValue() + ")";
                String displayName = paramValue.getDisplayName() + item.getUnit();
                spinnerItem.put("ValueName", displayName);
                spinnerItem.put("Value", value);
                spinnerDataSource.add(spinnerItem);
            }

            SimpleAdapter simpleAdapter = new SimpleAdapter(this,
                    spinnerDataSource,
                    R.layout.spinner_item_explor_param,
                    new String[]{"ValueName"},
                    new int[]{R.id.paramName});

            spinner.setAdapter(simpleAdapter);
            if (choosedPosition >= 0) {
                spinner.setSelection(choosedPosition);
            }

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String prevValue = model.getParams().get(item.getItemKey());
                    String curValue = spinnerDataSource.get(position).get("Value");

                    if (curValue.startsWith("(")) {
                        curValue = curValue.substring(1);
                        if (curValue.endsWith(")")) {
                            curValue = curValue.substring(0, curValue.length() - 1);
                        }
                    }

                    if (prevValue != null && curValue != null && prevValue.equals(curValue)) {
                    }
                    else {
                        model.getParams().put(item.getItemKey(), curValue);
                        reloadParamItemsViews();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
        }
        // 取值范围为空,按钮置灰,不可选
        else {
            model.getParams().put(item.getItemKey(), "");

            List<Map<String, String>> spinnerDataSource = new ArrayList<>();
            Map<String, String> spinnerItem = new HashMap<>();
            spinnerItem.put("ValueName", "**不可选**");
            spinnerItem.put("Value", "");
            spinnerDataSource.add(spinnerItem);

            SimpleAdapter simpleAdapter = new SimpleAdapter(this,
                    spinnerDataSource,
                    R.layout.spinner_item_explor_param,
                    new String[]{"ValueName", "Value"},
                    new int[]{R.id.paramName, R.id.paramValue});

            spinner.setAdapter(simpleAdapter);
            spinner.setSelection(0);
            spinner.setEnabled(false);
        }
    }

    public String findValueRangeKey(ExploreParamsMetaInfo.ExploreParamItemModel item) {
        List<ExploreParamsMetaInfo.ExploreParamConditionModel> conditions = item.getConditions();

        for (ExploreParamsMetaInfo.ExploreParamConditionModel condition : conditions) {
            String values = findValuesWithCondition(condition);
            if (values != null && values.length() > 0) {
                return values;
            }
        }

        return item.getDefaultValueRange();
    }

    public String findValuesWithCondition(ExploreParamsMetaInfo.ExploreParamConditionModel condition) {
        // find from self
        String paramValue = model.getParams().get(condition.getConditionKey());
        List<String> conditionValues = condition.getConditionValue();

        if (conditionValues != null && conditionValues.size() > 0) {
            if (paramValue != null && conditionValues.contains(paramValue)) {

                // find from sub conditions
                List<ExploreParamsMetaInfo.ExploreParamConditionModel> subConditions = condition.getSubConditions();
                for (ExploreParamsMetaInfo.ExploreParamConditionModel subCondition : subConditions) {
                    String values = findValuesWithCondition(subCondition);
                    if (values != null && values.length() > 0) {
                        return values;
                    }
                }

                return condition.getItemValue();
            }
        }

        return null;
    }

    @Override
    public void onResponse(int msgCode) {
        switch (msgCode) {
            case MainService.MSG_SEND_EXPLORE_SUCCESS:
                MyToast.showMessage(getBaseContext(), "上传踏勘数据成功");
                MainService.getInstance().deleteExploreModel(model);
                finish();
                break;
            case MainService.MSG_SEND_EXPLORE_FAILED:
                MyToast.showMessage(getBaseContext(), "上传踏勘数据失败");
                break;
            case MainService.MSG_LOAD_EXPLORE_PARAMS_META_SUCCESS:
                MyToast.showMessage(getBaseContext(), "参数初始化成功");
                paramsModel = ExploreParamsMetaInfo.getParamsModel();
                reloadParamItemsViews();
                break;
            default:
                break;
        }
    }

    public String getFromAssets(String fileName){
        try {
            InputStreamReader inputReader = new InputStreamReader(getResources().getAssets().open(fileName) );
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line="";
            String Result="";
            while((line = bufReader.readLine()) != null)
                Result += line;
            return Result;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
