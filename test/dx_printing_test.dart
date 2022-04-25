import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:dx_printing/dx_printing.dart';

void main() {
  const MethodChannel channel = MethodChannel('dx_printing');

  TestWidgetsFlutterBinding.ensureInitialized();

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('getPlatformVersion', () async {
    expect(await DxPrinting.platformVersion, '42');
  });
}
