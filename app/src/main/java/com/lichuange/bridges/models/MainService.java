package com.lichuange.bridges.models;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainService {
    public static final int MSG_LOGIN_SUCCESS = 0x1001;
    public static final int MSG_LOGIN_FAILED = 0x1002;
    public static final int MSG_LOGOUT_SUCCESS = 0x1003;
    public static final int MSG_LOGOUT_FAILED = 0x1004;
    public static final int MSG_QUERY_PROJECTS_SUCCESS = 0x2001;
    public static final int MSG_QUERY_PROJECTS_FAILED = 0x2002;
    public static final int MSG_QUERY_PROJECT_DETAIL_SUCCESS = 0x3001;
    public static final int MSG_QUERY_PROJECT_DETAIL_FAILED = 0x3002;
    public static final int MSG_UPDATE_PROJECT_DATUM_SUCCESS = 0x4001;
    public static final int MSG_UPDATE_PROJECT_DATUM_FAILED = 0x4002;
    public static final int MSG_SEND_SIGN_CHECK_SUCCESS = 0x5001;
    public static final int MSG_SEND_SIGN_CHECK_FAILED = 0x5002;
    public static final int MSG_GET_SIGN_CHECK_SUCCESS = 0x6001;
    public static final int MSG_GET_SIGN_CHECK_FAILED = 0x6002;
    public static final int MSG_DELETE_SENSOR_CHECK_SUCCESS = 0x7001;
    public static final int MSG_DELETE_SENSOR_CHECK_FAILED = 0x7002;
    public static final int MSG_SEND_EXPLORE_SUCCESS = 0x7001;
    public static final int MSG_SEND_EXPLORE_FAILED = 0x7002;

    public static final int MSG_LOAD_EXPLORE_PARAMS_META_SUCCESS = 0x8001;

    public static final String serverBaseUrl = "http://139.196.200.114:8888";

    private OkHttpClient httpClient;
    private LoginModel loginModel;
    private ProjectsModel projectsModel;
    private HashMap<String, ProjectModel> cachedProjects;
    private List<ExploreModel> exploreList;

    private static MainService ourInstance = new MainService();

    public static MainService getInstance() {
        return ourInstance;
    }

    private MainService() {
        httpClient = new OkHttpClient();
        cachedProjects = new HashMap<>();
        exploreList = new ArrayList<>();
    }

    public LoginModel getLoginModel() {
        return loginModel;
    }

    public void setLoginModel(LoginModel loginModel) {
        this.loginModel = loginModel;
    }

    public ProjectsModel getProjectsModel() {
        return projectsModel;
    }

    public void setProjectsModel(ProjectsModel projectsModel) {
        this.projectsModel = projectsModel;
    }

    public List<ExploreModel> getExploreList() {
        return exploreList;
    }

    public void setExploreList(List<ExploreModel> exploreList) {
        this.exploreList = exploreList;
    }

    public ExploreModel findExploreModel(String modelId) {
        if (modelId == null || modelId.length() == 0) {
            return null;
        }

        for (int i = 0; i < getExploreList().size(); ++i) {
            ExploreModel model = getExploreList().get(i);
            if (model.getModelId().equals(modelId)) {
                return model;
            }

        }

        return null;
    }

    public void deleteExploreModel(ExploreModel model) {
        try {
            getExploreList().remove(model);
        }
        catch (Exception e) {
            Log.e("MainService", e.toString());
        }
    }

    public String addExploreModel() {
        String modelId = Utils.getUniqueModelId();
        ExploreModel model = new ExploreModel();
        model.setModelId(modelId);
        getExploreList().add(model);
        return modelId;
    }

    public ProjectModel findDetailForProjectId(String projectId) {
        if (projectId == null || projectId.length() == 0) {
            return null;
        }

        return cachedProjects.get(projectId);
    }

    private String responsePrevProcess(String inString) {
        if (inString != null && inString.length() > 0) {
            if (inString.startsWith("(")) {
                inString = inString.substring(1);
                if (inString.endsWith(")")) {
                    inString = inString.substring(0, inString.length() - 1);
                }
            }
            String outString = inString.replace("\\\"", "\"");
            return outString;
        }
        else {
            return "";
        }
    }

    private void notifyMsg(Handler handler, int msgCode) {
        if (handler != null) {
            Message msg = new Message();
            msg.what = msgCode;
            handler.sendMessage(msg);
        }
    }

    private void notifyMsg(Handler handler, int msgCode, MsgResponseBase model) {
        if (handler != null) {
            Message msg = new Message();
            msg.what = msgCode;
            msg.obj = model;
            handler.sendMessage(msg);
        }
    }

    public boolean login(final String username, final String password, final Handler handler) {
        if (username.length() == 0 || password.length() == 0) {
            return false;
        }

        if (getLoginModel() != null) {
            getLoginModel().setToken("");
        }

        Runnable networkTask = new Runnable() {

            @Override
            public void run() {
                try {
                    String url = serverBaseUrl + "/Maintain/APP.ashx?Type=Login";

                    FormBody body = new FormBody.Builder()
                            .add("Username", username)
                            .add("Password", password)
                            .build();

                    Request request = new Request.Builder()
                            .url(url)
                            .post(body)
                            .build();

                    Response response = httpClient.newCall(request).execute();
                    if (response.isSuccessful()) {
                        String responseStr = response.body().string();

                        Log.i("MainService", responseStr);

                        responseStr = responsePrevProcess(responseStr);
                        Gson gson = new Gson();
                        setLoginModel(gson.fromJson(responseStr, LoginModel.class));
                        getLoginModel().setUserName(username);

                        if (getLoginModel() != null
                                && getLoginModel().isLoginSuccess()) {
                            Message msg = new Message();
                            msg.what = MSG_LOGIN_SUCCESS;
                            msg.obj = getLoginModel();
                            handler.sendMessage(msg);
                        }
                        else {
                            notifyLoginFailed(handler);
                        }
                    }
                    else {
                        notifyLoginFailed(handler);
                    }
                }
                catch (IOException e) {
                    notifyLoginFailed(handler);
                }
            }
        };

        new Thread(networkTask).start();

        return true;
    }

    public boolean logout() {
        if (!getLoginModel().isLoginSuccess()) {
            return false;
        }

        final String token = getLoginModel().getToken();

        Runnable networkTask = new Runnable() {

            @Override
            public void run() {
                try {
                    String url = serverBaseUrl + "/Maintain/APP.ashx?Type=Loginout";

                    FormBody body = new FormBody.Builder()
                            .add("Token", token)
                            .build();

                    Request request = new Request.Builder()
                            .url(url)
                            .post(body)
                            .build();

                    getLoginModel().setToken("");
                    Response response = httpClient.newCall(request).execute();
                    if (response.isSuccessful()) {
                        String responseStr = response.body().string();
                        Log.i("MainService", responseStr);
                    }
                    else {
                    }
                }
                catch (IOException e) {
                }
            }
        };

        new Thread(networkTask).start();

        return true;
    }

    private void notifyLoginFailed(Handler handler) {
        if (handler != null) {
            Message msg = new Message();
            msg.what = MSG_LOGIN_FAILED;
            handler.sendMessage(msg);
        }
    }

    public boolean sendProjectsQuery(final Handler handler) {
        if (getLoginModel() == null || !getLoginModel().isLoginSuccess()) {
            return false;
        }

        String url = serverBaseUrl + "/Maintain/APP.ashx?Type=GetProjectList";
        BGRequest req = new BGRequest() {
            @Override
            public void success(MsgResponseBase res) {
                setProjectsModel((ProjectsModel)res);
                notifyMsg(handler, MSG_QUERY_PROJECTS_SUCCESS);
            }

            @Override
            public void failed(MsgResponseBase res) {
                notifyMsg(handler, MSG_QUERY_PROJECTS_FAILED);
            }
        };
        return req.addParam("Token", getLoginModel().getToken())
                .addParam("ProjectType", "0")
                .send(url, ProjectsModel.class);
    }

    public boolean sendPlanProjectsQuery(final Handler handler) {
        if (getLoginModel() == null || !getLoginModel().isLoginSuccess()) {
            return false;
        }

        String url = serverBaseUrl + "/Maintain/APP.ashx?Type=GetProjectList";
        BGRequest req = new BGRequest() {
            @Override
            public void success(MsgResponseBase model) {
                notifyMsg(handler, MSG_QUERY_PROJECTS_SUCCESS, model);
            }

            @Override
            public void failed(MsgResponseBase model) {
                notifyMsg(handler, MSG_QUERY_PROJECTS_FAILED, model);
            }
        };
        return req.addParam("Token", getLoginModel().getToken())
                .addParam("ProjectType", "1")
                .send(url, ProjectsModel.class);
    }

    public boolean sendPlanProjectDetailQuery(final String ProjectID, final Handler handler) {
        if (getLoginModel() == null || !getLoginModel().isLoginSuccess()) {
            return false;
        }

        if (ProjectID.length() == 0) {
            return false;
        }

        String url = serverBaseUrl + "/Maintain/APP.ashx?Type=GetSchemeDetail";
        BGRequest req = new BGRequest() {
            @Override
            public void success(MsgResponseBase model) {
                notifyMsg(handler, MSG_QUERY_PROJECT_DETAIL_SUCCESS, model);
            }

            @Override
            public void failed(MsgResponseBase model) {
                notifyMsg(handler, MSG_QUERY_PROJECT_DETAIL_FAILED, model);
            }
        };
        return req.addParam("Token", getLoginModel().getToken())
                .addParam("ProjectID", ProjectID)
                .send(url, PlanDetailModel.class);
    }

    public boolean sendProjectDetailQuery(final String ProjectID, final Handler handler) {
        if (getLoginModel() == null || !getLoginModel().isLoginSuccess()) {
            return false;
        }

        if (ProjectID.length() == 0) {
            return false;
        }

        Runnable networkTask = new Runnable() {

            @Override
            public void run() {
                try {
                    String url = serverBaseUrl + "/Maintain/APP.ashx?Type=GetProjectDetail";

                    FormBody body = new FormBody.Builder()
                            .add("Token", getLoginModel().getToken())
                            .add("ProjectID", ProjectID)
                            .build();

                    Request request = new Request.Builder()
                            .url(url)
                            .post(body)
                            .build();

                    Response response = httpClient.newCall(request).execute();
                    if (response.isSuccessful()) {
                        String responseStr = response.body().string();

                        Log.i("MainService", responseStr);

                        responseStr = responsePrevProcess(responseStr);

                        //String mockStr = "{\"Status\":0,\"Msg\":\"OK\",\"ProjectName\":\"杭州到宁波\",\"LineName\":\"杭千高速\",\"ControlStartStack\":\"K0+-760\",\"ControlEndStack\":\"K2+720\",\"GZ1\":\"K1+100\",\"GZ1Lon\":\"\",\"GZ1Lat\":\"\",\"SchemeImage\":\"http://139.196.200.114:80/Maintain/rule/SchemeImage/1-12.png\",\"SignCount\":\"12\",\"items\":[{\"SignNumber\":\"b32\",\"SignName\":\"解除限速60\",\"SignImageURL\":\"http://139.196.200.114:80/Maintain/rule/SignImage/WEB/A-1-12-5.png\",\"StackNumber\":\"K0+-760\"},{\"SignNumber\":\"b38\",\"SignName\":\"解除禁止超车\",\"SignImageURL\":\"http://139.196.200.114:80/Maintain/rule/SignImage/WEB/A-1-14.png\",\"StackNumber\":\"K0+-760\"},{\"SignNumber\":\"b3\",\"SignName\":\"施工长度标志\",\"SignImageURL\":\"http://139.196.200.114:80/Maintain/rule/SignImage/WEB/A-1-3.png\",\"StackNumber\":\"K1+100\"},{\"SignNumber\":\"b47\",\"SignName\":\"附设警示灯的路栏\",\"SignImageURL\":\"http://139.196.200.114:80/Maintain/rule/SignImage/WEB/A-3-6.png\",\"StackNumber\":\"K1+100\"},{\"SignNumber\":\"b14\",\"SignName\":\"向右导向\",\"SignImageURL\":\"http://139.196.200.114:80/Maintain/rule/SignImage/WEB/A-1-7-2.png\",\"StackNumber\":\"K1+160\"},{\"SignNumber\":\"b5\",\"SignName\":\"两车道向右变一车道\",\"SignImageURL\":\"http://139.196.200.114:80/Maintain/rule/SignImage/WEB/A-1-5-1.png\",\"StackNumber\":\"K1+595\"},{\"SignNumber\":\"b36\",\"SignName\":\"解除限速20\",\"SignImageURL\":\"http://139.196.200.114:80/Maintain/rule/SignImage/WEB/A-1-12-9.png\",\"StackNumber\":\"K1+595\"},{\"SignNumber\":\"b50\",\"SignName\":\"夜间语音提示设施\",\"SignImageURL\":\"http://139.196.200.114:80/Maintain/rule/SignImage/WEB/A-3-9.png\",\"StackNumber\":\"K1+595\"},{\"SignNumber\":\"b54\",\"SignName\":\"警示频闪灯\",\"SignImageURL\":\"http://139.196.200.114:80/Maintain/rule/SignImage/WEB/A-3-11.png\",\"StackNumber\":\"K1+595\"},{\"SignNumber\":\"b23\",\"SignName\":\"限速60\",\"SignImageURL\":\"http://139.196.200.114:80/Maintain/rule/SignImage/WEB/A-1-11-5.png\",\"StackNumber\":\"K1+770\"},{\"SignNumber\":\"b21\",\"SignName\":\"限速80\",\"SignImageURL\":\"http://139.196.200.114:80/Maintain/rule/SignImage/WEB/A-1-11-3.png\",\"StackNumber\":\"K1+970\"},{\"SignNumber\":\"b2\",\"SignName\":\"施工距离标志\",\"SignImageURL\":\"http://139.196.200.114:80/Maintain/rule/SignImage/WEB/A-1-2.png\",\"StackNumber\":\"K2+720\"}]}";
                        Gson gson = new Gson();
                        GetProjectDetailResponse res = gson.fromJson(responseStr, GetProjectDetailResponse.class);

                        if (res != null && res.isSuccess()) {
                            // 更新数据
                            ProjectModel detailModel = cachedProjects.get(ProjectID);
                            if (detailModel == null) {
                                detailModel = new ProjectModel();
                                cachedProjects.put(ProjectID, detailModel);
                            }
                            detailModel.setDetail(res);

                            // 通知UI
                            notifyMsg(handler, MSG_QUERY_PROJECT_DETAIL_SUCCESS);
                        }
                        else {
                            notifyMsg(handler, MSG_QUERY_PROJECT_DETAIL_FAILED);
                        }
                    }
                    else {
                        notifyMsg(handler, MSG_QUERY_PROJECT_DETAIL_FAILED);
                    }
                }
                catch (IOException e) {
                    notifyMsg(handler, MSG_QUERY_PROJECT_DETAIL_FAILED);
                }
            }
        };

        new Thread(networkTask).start();
        return true;
    }

    public boolean sendUpdateProjectDatum(final String ProjectID, final String longitude, final String latitude, final Handler handler) {
        if (getLoginModel() == null || !getLoginModel().isLoginSuccess()) {
            return false;
        }

        if (ProjectID.length() == 0) {
            return false;
        }

        if (longitude.length() == 0 || latitude.length() == 0) {
            return false;
        }

        Runnable networkTask = new Runnable() {

            @Override
            public void run() {
                try {
                    String url = serverBaseUrl + "/Maintain/APP.ashx?Type=UpdateProjectDatum";

                    FormBody body = new FormBody.Builder()
                            .add("Token", getLoginModel().getToken())
                            .add("ProjectID", ProjectID)
                            .add("Lon", longitude)
                            .add("Lat", latitude)
                            .build();

                    Request request = new Request.Builder()
                            .url(url)
                            .post(body)
                            .build();

                    Response response = httpClient.newCall(request).execute();
                    if (response.isSuccessful()) {
                        String responseStr = response.body().string();

                        Log.i("MainService", responseStr);

                        responseStr = responsePrevProcess(responseStr);

                        Gson gson = new Gson();
                        MsgResponseBase res = gson.fromJson(responseStr, MsgResponseBase.class);

                        if (res != null && res.isSuccess()) {
                            // 更新数据
                            ProjectModel detailModel = cachedProjects.get(ProjectID);
                            if (detailModel != null && detailModel.getDetail() != null) {
                                detailModel.getDetail().setGZ1Lat(latitude);
                                detailModel.getDetail().setGZ1Lon(longitude);
                                notifyMsg(handler, MSG_UPDATE_PROJECT_DATUM_SUCCESS);
                            }
                            else {
                                notifyMsg(handler, MSG_UPDATE_PROJECT_DATUM_FAILED);
                            }
                        }
                        else {
                            notifyMsg(handler, MSG_UPDATE_PROJECT_DATUM_FAILED);
                        }
                    }
                    else {
                        notifyMsg(handler, MSG_UPDATE_PROJECT_DATUM_FAILED);
                    }
                }
                catch (IOException e) {
                    notifyMsg(handler, MSG_UPDATE_PROJECT_DATUM_FAILED);
                }
            }
        };

        new Thread(networkTask).start();
        return true;
    }

    public boolean sendSignCheck(final String ProjectID,
                                 final String longitude,
                                 final String latitude,
                                 final String signCode,
                                 final Handler handler) {
        if (getLoginModel() == null || !getLoginModel().isLoginSuccess()) {
            return false;
        }

        if (ProjectID.length() == 0) {
            return false;
        }

        if (longitude.length() == 0 || latitude.length() == 0) {
            return false;
        }

        if (signCode.length() == 0) {
            return false;
        }

        Runnable networkTask = new Runnable() {

            @Override
            public void run() {
                try {
                    String url = serverBaseUrl + "/Maintain/APP.ashx?Type=SignCheck";

                    FormBody body = new FormBody.Builder()
                            .add("Token", getLoginModel().getToken())
                            .add("ProjectID", ProjectID)
                            .add("Lon", longitude)
                            .add("Lat", latitude)
                            .add("QRCode", signCode)
                            .build();

                    Request request = new Request.Builder()
                            .url(url)
                            .post(body)
                            .build();

                    Response response = httpClient.newCall(request).execute();
                    if (response.isSuccessful()) {
                        String responseStr = response.body().string();

                        Log.i("MainService", responseStr);

                        responseStr = responsePrevProcess(responseStr);

                        Gson gson = new Gson();
                        MsgResponseBase res = gson.fromJson(responseStr, MsgResponseBase.class);

                        if (res != null && res.isSuccess()) {
                            notifyMsg(handler, MSG_SEND_SIGN_CHECK_SUCCESS);
                        }
                        else {
                            notifyMsg(handler, MSG_SEND_SIGN_CHECK_FAILED);
                        }
                    }
                    else {
                        notifyMsg(handler, MSG_SEND_SIGN_CHECK_FAILED);
                    }
                }
                catch (IOException e) {
                    notifyMsg(handler, MSG_SEND_SIGN_CHECK_FAILED);
                }
            }
        };

        new Thread(networkTask).start();
        return true;
    }

    public boolean sendGetSignCheck(final String ProjectID,
                                 final Handler handler) {
        if (getLoginModel() == null || !getLoginModel().isLoginSuccess()) {
            return false;
        }

        if (ProjectID.length() == 0) {
            return false;
        }

        Runnable networkTask = new Runnable() {

            @Override
            public void run() {
                try {
                    String url = serverBaseUrl + "/Maintain/APP.ashx?Type=GetSignCheck";

                    FormBody body = new FormBody.Builder()
                            .add("Token", getLoginModel().getToken())
                            .add("ProjectID", ProjectID)
                            .build();

                    Request request = new Request.Builder()
                            .url(url)
                            .post(body)
                            .build();

                    Response response = httpClient.newCall(request).execute();
                    if (response.isSuccessful()) {
                        String responseStr = response.body().string();

                        Log.i("MainService", responseStr);

                        responseStr = responsePrevProcess(responseStr);

                        Gson gson = new Gson();
                        GetSignCheckResponse res = gson.fromJson(responseStr, GetSignCheckResponse.class);

                        if (res != null && res.isSuccess()) {
                            // 更新数据
                            ProjectModel detailModel = cachedProjects.get(ProjectID);
                            if (detailModel != null) {
                                detailModel.setSignItems(res.getSignItems());
                                detailModel.setSensorItems(res.getSensorItems());
                                notifyMsg(handler, MSG_GET_SIGN_CHECK_SUCCESS);
                            }
                            else {
                                notifyMsg(handler, MSG_GET_SIGN_CHECK_FAILED);
                            }
                        }
                        else {
                            notifyMsg(handler, MSG_GET_SIGN_CHECK_FAILED);
                        }
                    }
                    else {
                        notifyMsg(handler, MSG_GET_SIGN_CHECK_FAILED);
                    }
                }
                catch (IOException e) {
                    notifyMsg(handler, MSG_GET_SIGN_CHECK_FAILED);
                }
            }
        };

        new Thread(networkTask).start();
        return true;
    }

    public boolean sendDeleteSensorCheck(final String ProjectID,
                                         final String SensorNumber,
                                    final Handler handler) {
        if (getLoginModel() == null || !getLoginModel().isLoginSuccess()) {
            return false;
        }

        if (ProjectID.length() == 0 || SensorNumber.length() == 0) {
            return false;
        }

        ProjectModel detailModel = cachedProjects.get(ProjectID);
        if (detailModel == null) {
            return false;
        }

        final String sensorId = detailModel.findSensorIdForSensorNumber(SensorNumber);
        if (sensorId == null || sensorId.length() == 0) {
            return false;
        }

        Runnable networkTask = new Runnable() {

            @Override
            public void run() {
                try {
                    String url = serverBaseUrl + "/Maintain/APP.ashx?Type=DeleteSensorCheck";

                    FormBody body = new FormBody.Builder()
                            .add("Token", getLoginModel().getToken())
                            .add("ProjectID", ProjectID)
                            .add("ProjectSensorID", sensorId)
                            .build();

                    Request request = new Request.Builder()
                            .url(url)
                            .post(body)
                            .build();

                    Response response = httpClient.newCall(request).execute();
                    if (response.isSuccessful()) {
                        String responseStr = response.body().string();

                        Log.i("MainService", responseStr);

                        responseStr = responsePrevProcess(responseStr);

                        Gson gson = new Gson();
                        MsgResponseBase res = gson.fromJson(responseStr, MsgResponseBase.class);

                        if (res != null && res.isSuccess()) {
                            // 更新数据
                            ProjectModel detailModel = cachedProjects.get(ProjectID);
                            if (detailModel != null) {
                                detailModel.deleteSensorForSensorNumber(SensorNumber);
                                notifyMsg(handler, MSG_DELETE_SENSOR_CHECK_SUCCESS);
                            }
                            else {
                                notifyMsg(handler, MSG_DELETE_SENSOR_CHECK_FAILED);
                            }
                        }
                        else {
                            notifyMsg(handler, MSG_DELETE_SENSOR_CHECK_FAILED);
                        }
                    }
                    else {
                        notifyMsg(handler, MSG_DELETE_SENSOR_CHECK_FAILED);
                    }
                }
                catch (IOException | JsonSyntaxException e) {
                    notifyMsg(handler, MSG_DELETE_SENSOR_CHECK_FAILED);
                }
            }
        };

        new Thread(networkTask).start();
        return true;
    }

    static final String[] ExploreItemKeys = {"ProjectName", "LineName", "ExplorDate", "Lon1", "Lat1",
            "Lon2", "Lat2", "GZ1", "GZ2", "ObjectType", "RoadType", "DesignSpeed",
            "LaneNumber", "TrafficQ", "DownSlope", "Radius", "WindingType",
            "CloseType", "LaneWidth", "CloseLaneWidth", "CenterDisp", "LateralBuffer",
            "WindingStart", "BridgeType", "BridgeZH1", "BridgeZH2", "IsRushHour", "SquearType",
            "TunnelType", "CrossType", "TransitEquipment", "MaintenanceType", "QZH1", "QZH2" , "SZH1", "SZH2" // 文档多的参数
    };

    public boolean sendExplor(ExploreModel model, final Handler handler) {
        if (getLoginModel() == null || !getLoginModel().isLoginSuccess()) {
            return false;
        }

        if (model == null) {
            return false;
        }

        String exploredInfo = "";

        for (String itemKey : ExploreItemKeys) {
            String value = model.getParams().get(itemKey);
            if (value == null) {
                value = "";
            }

            String exploreItemInfo = itemKey + "=" + value + "\n";

            exploredInfo += exploreItemInfo;
        }

        Log.i("MainService", exploredInfo);

        final String exploredInfoToSend = exploredInfo;

        Runnable networkTask = new Runnable() {

            @Override
            public void run() {
                try {
                    String url = serverBaseUrl + "/Maintain/APP.ashx?Type=Explor";

                    Map<String, String> fileHeader = new HashMap<>();
                    fileHeader.put("Content-Disposition", "form-data; name=\"File1\"; filename=\"takan.txt\"");

                    //MediaType textPlain = MediaType.parse("text/plain; charset=utf-8");
                    MediaType textPlain = MediaType.parse("text/plain; charset=gb2312");

                    RequestBody body = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addPart(Headers.of("Content-Disposition", "form-data; name=\"Token\""),
                                    RequestBody.create(null, getLoginModel().getToken()))
                            .addPart(Headers.of(fileHeader),
                                    RequestBody.create(textPlain, exploredInfoToSend))
                            .build();

                    Request request = new Request.Builder()
                            .url(url)
                            .post(body)
                            .build();

                    Response response = httpClient.newCall(request).execute();
                    if (response.isSuccessful()) {
                        String responseStr = response.body().string();

                        Log.i("MainService", responseStr);

                        responseStr = responsePrevProcess(responseStr);

                        Gson gson = new Gson();
                        MsgResponseBase res = gson.fromJson(responseStr, MsgResponseBase.class);

                        if (res != null && res.isSuccess()) {
                            notifyMsg(handler, MSG_SEND_EXPLORE_SUCCESS);
                        }
                        else {
                            notifyMsg(handler, MSG_SEND_EXPLORE_FAILED);
                        }
                    }
                    else {
                        notifyMsg(handler, MSG_SEND_EXPLORE_FAILED);
                    }
                }
                catch (IOException | JsonSyntaxException e) {
                    notifyMsg(handler, MSG_SEND_EXPLORE_FAILED);
                }
            }
        };

        new Thread(networkTask).start();
        return true;
    }
}
