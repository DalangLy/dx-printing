import 'dart:io';
import 'dart:typed_data';

import 'package:dx_printing/dx_printing.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

class HomePage extends StatefulWidget {
  const HomePage({Key? key}) : super(key: key);

  @override
  State<HomePage> createState() => _HomePageState();
}

class _HomePageState extends State<HomePage> {
  final DxPrinting p = DxPrinting();
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: SafeArea(
        child: Center(
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              ElevatedButton(
                onPressed: () async{
                  print( await p.open(ip: '192.168.0.12', port: 9100, timeout: 5000));
                },
                child: const Text("Connect"),
              ),
              ElevatedButton(
                onPressed: (){
                  p.cutPaper();
                },
                child: const Text("Cut"),
              ),
              ElevatedButton(
                onPressed: () async{
                  const String imageUrl = 'https://static.remove.bg/remove-bg-web/5cc729f2c60683544f035949b665ce17223fd2ec/assets/start_remove-c851bdf8d3127a24e2d137a55b1b427378cd17385b01aec6e59d5d4b5f39d2ec.png';
                  final ByteData data = await NetworkAssetBundle(Uri.parse(imageUrl)).load(imageUrl);
                  Uint8List imagebytes = data.buffer.asUint8List();
                  print(await p.printPicture(bytes: imagebytes, width: 384, cutPaper: true));
                },
                child: const Text("Print Image"),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
