package com.make.reqs;

import android.util.Log;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class ReqsPlugin extends CordovaPlugin {
    private final OkHttpClient OK_HTTP = new OkHttpClient();

    @Override
    public boolean execute(final String acts, final JSONArray data, final CallbackContext back) {
        if (acts.equals("make")) {
            try {
                String type = data.getString(0);
                String path = data.getString(1);
                String body = data.getString(2);
                JSONObject head = data.getJSONObject(3);

                OK_HTTP.setConnectTimeout(60, TimeUnit.SECONDS);

                OK_HTTP.setReadTimeout(60, TimeUnit.SECONDS);

                Request.Builder http = new Request.Builder();

                if (body.equals("")) {
                    http.method(type, null);
                } else {
                    String mode = "application/x-www-form-urlencoded";

                    if (head.has("content-type")) {
                        mode = head.getString("content-type");
                    }

                    http.post(RequestBody.create(MediaType.parse(mode), body));
                }

                http.url(path);

                if(head != null && head.length() > 0 ) {
                    JSONArray list = head.names();

                    for (int i = 0; i < list.length(); i++) {
                        String name = list.getString(i);
                        String text = head.getString(name);

                        http.addHeader(name, text);
                    }
                }

                Request reqs = http.build();

                OK_HTTP.setFollowRedirects(false);

                OK_HTTP.newCall(reqs).enqueue(new Callback() {
                    @Override
                    public void onFailure(Request reqs, IOException fail) {
                        back.sendPluginResult(new PluginResult(PluginResult.Status.ERROR, fail.getMessage()));
                    }

                    @Override
                    public void onResponse(Response resp) throws IOException {
                        JSONObject send = new JSONObject();

                        try {
                            Headers head = resp.headers();

                            send.put("path", resp.request().urlString());
                            send.put("code", resp.code());
                            send.put("data", resp.body().string());

                            if (head != null) {
                                send.put("head", head.toString());
                            } else {
                                send.put("head", "");
                            }
                        } catch (Exception fail) {
                            fail.printStackTrace();
                        }

                        back.sendPluginResult(new PluginResult(PluginResult.Status.OK, send));
                    }
                });
            } catch (JSONException fail) {
                back.error(fail.getMessage());
            }
        } else {
            back.sendPluginResult(new PluginResult(PluginResult.Status.INVALID_ACTION));
            return false;
        }

        return true;
    }
}
