package com.example.dx_printing;

import androidx.annotation.NonNull;

import com.lvrenyang.io.NETPrinting;
import com.lvrenyang.io.Pos;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

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

  private Context context;

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "dx_printing");
    channel.setMethodCallHandler(this);

    context = flutterPluginBinding.getApplicationContext();
  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    if (call.method.equals("getPlatformVersion")) {
      result.success("Android " + android.os.Build.VERSION.RELEASE);
    }
    else if(call.method.equals("Open")){
      final String ip = call.argument("ip");
      final int port = call.argument("port");
      final int timeout = call.argument("timeout");
      es.submit(new OpenPrinterConnection(ip, port, timeout));
      result.success("Success Ip Is "+ ip);
    }
    else if(call.method.equals("CutPaper")){
      es.submit(new CutPaper());
      result.success("Cut Success");
    }
    else if(call.method.equals("PrintPicture")){
      final byte[] bytes = call.argument("bytes");
      final int width = call.argument("width");
      final boolean cutPaper = call.argument("cutPaper");
      es.submit(new PrintPicture(bytes, width, cutPaper));
      result.success("Print Image Success");
    }
    else if(call.method.equals("KickDrawer")){
      final int nDrawerIndex = call.argument("nDrawerIndex");
      final int nPulseTime = call.argument("nPulseTime");
      es.submit(new KickDrawer(nDrawerIndex, nPulseTime));
      result.success("Kick Drawer Success");
    }
    else {
      result.notImplemented();
    }
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
  }

  private class OpenPrinterConnection implements Runnable{
    private String ip;
    private int port;
    private int timeout;

    private OpenPrinterConnection(String ip, int port, int timeout) {
      this.ip = ip;
      this.port = port;
      this.timeout = timeout;
    }

    @Override
    public void run(){
      mNet.Open(ip, port, timeout, context);
      mPos.Set(mNet);
    }
  }

  private class CutPaper implements Runnable{
    @Override
    public void run() {
      mPos.POS_CutPaper();
    }
  }

  private class  PrintPicture implements Runnable{
    final byte[] bytes;
    final int width;
    final boolean cutPaper;

    private PrintPicture(byte[] bytes, int width, boolean cutPaper) {
      this.bytes = bytes;
      this.width = width;
      this.cutPaper = cutPaper;
    }

    @Override
    public void run() {
      try {
        InputStream is = new ByteArrayInputStream(bytes);
        Bitmap image = BitmapFactory.decodeStream(is);
        is.close();
        mPos.POS_PrintPicture(image,width, 0, 0);
        if(cutPaper){
          mPos.POS_CutPaper();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  private class KickDrawer implements Runnable{
    final int nDrawerIndex;
    final int nPulseTime;

    private KickDrawer(int nDrawerIndex, int nPulseTime) {
      this.nDrawerIndex = nDrawerIndex;
      this.nPulseTime = nPulseTime;
    }

    @Override
    public void run() {
      mPos.POS_KickDrawer(nDrawerIndex, nPulseTime);
    }
  }
}
