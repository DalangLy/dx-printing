import 'dart:async';
import 'dart:typed_data';

import 'package:flutter/services.dart';

class DxPrinting {
  static const MethodChannel _channel = MethodChannel('dx_printing');

  static Future<String?> get platformVersion async {
    final String? version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  Future<String> open({required String ip, required int port, required int timeout,}) async{
    return await _channel.invokeMethod("Open", <String, dynamic>{
      'ip': ip,
      'port': port,
      'timeout': timeout,
    });
  }

  Future<String> cutPaper() async{
    return await _channel.invokeMethod('CutPaper');
  }

  Future<String> printPicture({required Uint8List bytes, required double width,}) async{
    return await _channel.invokeMethod("PrintPicture", <String, dynamic>{
      'bytes': bytes,
      'width': width,
    });
  }
}
