package com.example.dx_printing;

import androidx.annotation.NonNull;

import com.lvrenyang.io.NETPrinting;
import com.lvrenyang.io.Pos;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

/** DxPrintingPlugin */
public class DxPrintingPlugin implements FlutterPlugin, MethodCallHandler {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private MethodChannel channel;

  ExecutorService es = Executors.newScheduledThreadPool(30);
  Pos mPos = new Pos();
  NETPrinting mNet = new NETPrinting();

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "dx_printing");
    channel.setMethodCallHandler(this);
  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    if (call.method.equals("getPlatformVersion")) {
      result.success("Android " + android.os.Build.VERSION.RELEASE);
    }
    else if(call.method.equals("Open")){
      es.submit(new OpenPrinterConnection());
      result.success("Success");
    }
    else {
      result.notImplemented();
    }
  }

  private class OpenPrinterConnection implements Runnable{
    @Override
    public void run() {
      mNet.Open("192.168.0.12", 9100, 5000, getApplicationContext());
    }
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
  }
}
