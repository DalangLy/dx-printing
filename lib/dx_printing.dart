
import 'dart:async';

import 'package:flutter/services.dart';

class DxPrinting {
  static const MethodChannel _channel = MethodChannel('dx_printing');

  static Future<String?> get platformVersion async {
    final String? version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  Future<String> print() async{
    return await _channel.invokeMethod("Print");
  }
}
