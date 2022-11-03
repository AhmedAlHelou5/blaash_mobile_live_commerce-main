import 'dart:async';
import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:http/http.dart' as http;
import 'package:loading_animation_widget/loading_animation_widget.dart';
import 'package:otp_text_field/otp_field.dart';
import 'package:otp_text_field/otp_field_style.dart';
import 'package:otp_text_field/style.dart';

import '../../fitness_app/fitness_app_home_screen.dart';
import '../../hotel_booking/hotel_app_theme.dart';

class WelcomeView extends StatefulWidget {
  // final AnimationController animationController;

  WelcomeView({Key? key}) : super(key: key);

  Map<String, dynamic> getTokenParams() {
    print(_WelcomeViewState.tokenParameters);
    return _WelcomeViewState.tokenParameters;
  }

  @override
  State<WelcomeView> createState() => _WelcomeViewState();
}

class _WelcomeViewState extends State<WelcomeView> {
  late bool enableOtpButton = true;
  late bool enableOtpField = false;
  late bool signInEligible = false;
  late bool isTimerVisible = false;
  late bool otpLoading = false;
  late String otpFromServer;
  late bool isOtpCorrect = false;
  late bool isEmailValid = true;
  late int _start = 180;
  late String emailValidation = "Please enter a valid E-mail Id";
  late String optButtonText = "Generate OTP";
  final emailController = TextEditingController();
  OtpFieldController otpController = OtpFieldController();
  static Map<String, dynamic> tokenParameters = {};

  void _validateOtp(String pin) {
    if (int.parse(pin) == int.parse(otpFromServer)) {
      setState(() {
        isOtpCorrect = true;
        _timer.cancel();
        isTimerVisible = false;
      });
    } else {
      setState(() {
        isOtpCorrect = false;
      });
    }
  }

  void generateOtp() async {
    FocusManager.instance.primaryFocus?.unfocus();
    bool emailRegEx = RegExp(
            "[a-z0-9!#\$%&'*+/=?^_`{|}~-]+(?:\.[a-z0-9!#\$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?")
        .hasMatch(emailController.text);
    if (emailRegEx) {
      setState(() {
        _start = 180;
        isTimerVisible = false;
        enableOtpField = false;
        otpLoading = true;
        optButtonText = "Generating OTP";
      });
      MethodChannel config = MethodChannel("flutter/goLive");
      String tenantKey = await config.invokeMethod("configTenant");
      String apiKey = await config.invokeMethod("configApiKey");
      Map<String, String> header = {
        "x-tenant-key": tenantKey,
        "x-api-key": apiKey
      };

      String url =
          "https://frg6g6wml9.execute-api.ap-south-1.amazonaws.com/Prod/api/idty/getUserOTP?email=" +
              emailController.text;
      // print("URL = " + url);

      try {
        http.Response response = await http
            .get(Uri.parse(url), headers: header)
            .timeout(const Duration(seconds: 30));
        // print("Response = " + response.body);
        final res = json.decode(response.body.toString());
        try {
          String otp = res["data"]["OTP"];
          // print("OTP = " + otp);
          setState(() {
            // _start = res["data"]["OTPValidUptoSeconds"];
            _start = 180;
            enableOtpButton = false;
            isTimerVisible = true;
            otpFromServer = otp;
            enableOtpField = true;
            otpLoading = false;
            signInEligible = true;
            startTimer();
            tokenParameters = {
              "portal_customerId": res["data"]["UserID"].toString(),
              "first_name": res["data"]["FirstName"].toString(),
              "last_name": res["data"]["LastName"].toString(),
              "emailId": emailController.text.toString(),
            };
            print(tokenParameters);
          });
        } catch (e) {
          setState(() {
            isTimerVisible = false;
            otpLoading = false;
          });
          ScaffoldMessenger.of(context).showSnackBar(SnackBar(
              content: Text(
                  "User not found, Please complete your Sign Up process through Web")));
        }
      } on TimeoutException catch (_) {
        FocusManager.instance.primaryFocus?.unfocus();

        setState(() {
          otpLoading = false;
          optButtonText = "Resend OTP";
        });
        ScaffoldMessenger.of(context).showSnackBar(SnackBar(
            content: Text("Unable to generate OTP, please try again later")));
      } on Exception catch (_) {
        setState(() {
          otpLoading = false;
          optButtonText = "Resend OTP";
        });
        ScaffoldMessenger.of(context).showSnackBar(SnackBar(
            content: Text("Unable to generate OTP, please try again later")));
      }
    } else {
      setState(() {
        otpLoading = false;
        isEmailValid = false;
      });
    }
  }

  late Timer _timer = new Timer.periodic(Duration(seconds: 1), (timer) {});

  void startTimer() {
    const oneSec = const Duration(seconds: 1);
    _timer = new Timer.periodic(
      oneSec,
      (Timer timer) {
        if (_start == 0) {
          setState(() {
            timer.cancel();
            optButtonText = "Resend OTP";
            enableOtpButton = true;
            otpFromServer = "xyz";
            isTimerVisible = false;
            enableOtpField = false;
            otpLoading = false;
            signInEligible = false;
          });
        } else {
          setState(() {
            _start--;
          });
        }
      },
    );
  }

  String formatTime(int seconds) {
    int minutes = int.parse((seconds / 60).truncate().toString());
    int minutesStr = int.parse((seconds % 60).truncate().toString());
    return "$minutes:$minutesStr";
  }

  void emailChangeDetected() {
    bool emailRegEx = RegExp(
            "[a-z0-9!#\$%&'*+/=?^_`{|}~-]+(?:\.[a-z0-9!#\$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?")
        .hasMatch(emailController.text);
    setState(() {
      optButtonText = "Generate OTP";
      enableOtpButton = true;
      isEmailValid = emailRegEx;
      otpFromServer = "xyz";
      signInEligible = false;
      enableOtpField = false;
      otpLoading = false;
      isTimerVisible = false;
      _timer.cancel();
    });
  }

  void signIn() async {
    MethodChannel methodChannel = MethodChannel("flutter/goLive");
      await methodChannel.invokeMethod("returnUrl", tokenParameters);
  }

  void signInUser() {
    FocusManager.instance.primaryFocus?.unfocus();
    if (isOtpCorrect) {
      signIn();
    } else {
      ScaffoldMessenger.of(context)
          .showSnackBar(SnackBar(content: Text("Enter a valid OTP")));
    }
  }

  @override
  void dispose() {
    _timer.cancel();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return (Container(
        decoration: const BoxDecoration(
            image: DecorationImage(
                image: AssetImage("assets/login_bg.png"), fit: BoxFit.cover)),
        child: Scaffold(
            resizeToAvoidBottomInset: false,
            backgroundColor: Colors.transparent,
            body: Container(
                child: Column(
                    mainAxisAlignment: MainAxisAlignment.start,
                    children: [
                  Column(
                    children: [
                      Container(
                        child: Padding(
                          padding: const EdgeInsets.only(
                              left: 70, right: 70, top: 40),
                          child: Image.asset(
                            'assets/introduction_animation/logo.png',
                            fit: BoxFit.contain,
                            width: 200,
                          ),
                        ),
                      ),
                      Container(
                        child: Padding(
                          padding: const EdgeInsets.only(
                              left: 70, right: 70, top: 40),
                          child: TextFormField(
                            controller: emailController,
                            onChanged: (String txt) {
                              emailChangeDetected();
                            },
                            style: const TextStyle(
                              fontSize: 18,
                            ),
                            cursorColor:
                                HotelAppTheme.buildLightTheme().primaryColor,
                            decoration: InputDecoration(
                              enabledBorder: OutlineInputBorder(
                                  borderRadius: BorderRadius.circular(10),
                                  borderSide: BorderSide(
                                      width: 1,
                                      color: Color.fromRGBO(30, 30, 30, 1))),
                              focusedBorder: OutlineInputBorder(
                                  borderRadius: BorderRadius.circular(10),
                                  borderSide: BorderSide(
                                      width: 2,
                                      color: Color.fromRGBO(63, 81, 181, 1))),
                              hintText: 'Email ID',
                            ),
                          ),
                        ),
                      ),
                      Visibility(
                          visible: !isEmailValid,
                          child: Padding(
                            padding: EdgeInsets.only(top: 20, bottom: 20),
                            child: Text(emailValidation),
                          )),
                      Visibility(
                          visible: enableOtpField,
                          child: Container(
                            child: Padding(
                                padding: const EdgeInsets.only(
                                    left: 70, right: 70, top: 40),
                                child: OTPTextField(
                                  hasError: !isOtpCorrect,
                                  controller: otpController,
                                  length: 5,
                                  width: MediaQuery.of(context).size.width,
                                  fieldWidth: 40,
                                  style: TextStyle(fontSize: 18),
                                  textFieldAlignment:
                                      MainAxisAlignment.spaceAround,
                                  fieldStyle: FieldStyle.box,
                                  otpFieldStyle: OtpFieldStyle(
                                      errorBorderColor: Colors.deepOrangeAccent,
                                      focusBorderColor: Colors.blueAccent),
                                  onCompleted: (pin) {
                                    _validateOtp(pin);
                                  },
                                )),
                          )),
                      Visibility(
                          visible: enableOtpButton,
                          child: Padding(
                            padding: const EdgeInsets.only(
                                left: 70, right: 70, top: 40),
                            child: Column(
                              children: [
                                FloatingActionButton.extended(
                                  // label: Text(optButtonText),
                                  label: Row(
                                    children: [
                                      Text(optButtonText),
                                      Visibility(
                                          visible: otpLoading,
                                          child: Padding(
                                            padding: EdgeInsets.only(left: 20),
                                            child:
                                                LoadingAnimationWidget.waveDots(
                                                    color: Colors.white,
                                                    size: 20),
                                          ))
                                    ],
                                  ),
                                  backgroundColor: Colors.green,
                                  foregroundColor: Colors.white,
                                  onPressed: () => {generateOtp()},
                                  // onPressed: () => { signIn()},
                                ),
                              ],
                            ),
                          )),
                      Visibility(
                          visible: isTimerVisible,
                          child: Padding(
                            padding: EdgeInsets.only(top: 30),
                            child: Text("Resend OTP in " + formatTime(_start)),
                          )),
                    ],
                  ),
                  Visibility(
                    visible: signInEligible,
                    child: Padding(
                      padding: EdgeInsets.only(bottom: 20),
                      child: Row(
                        mainAxisAlignment: MainAxisAlignment.center,
                        children: [
                          Padding(
                              padding: EdgeInsets.only(top: 40),
                              child: FloatingActionButton.extended(
                                label: Row(
                                  children: [
                                    Text("SIGN IN"),
                                    Icon(Icons.arrow_forward_rounded,
                                        color: Colors.white),
                                  ],
                                ),
                                onPressed: () {
                                  signInUser();
                                },
                                backgroundColor: Color(0xff3f51b5),
                              ))
                        ],
                      ),
                    ),
                  ),
                ])))));
  }
}
