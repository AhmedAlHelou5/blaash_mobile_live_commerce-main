import 'package:best_flutter_ui_templates/fitness_app/models/tabIcon_data.dart';
import 'package:best_flutter_ui_templates/fitness_app/training/training_screen.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:webview_flutter/webview_flutter.dart';

import 'bottom_navigation_view/bottom_bar_view.dart';
import 'fitness_app_theme.dart';
import 'my_diary/my_diary_screen.dart';
import 'package:loading_animation_widget/loading_animation_widget.dart';

class FitnessAppHomeScreen extends StatefulWidget {
  late Future<String> tkn;

  FitnessAppHomeScreen(Future<String> token) {
    this.tkn = token;
  }

  @override
  _FitnessAppHomeScreenState createState() => _FitnessAppHomeScreenState(tkn);
}

class _FitnessAppHomeScreenState extends State<FitnessAppHomeScreen>
    with TickerProviderStateMixin {
  AnimationController? animationController;
  late String token = "";
  late Future<String> tempTkn;

  late bool isLoading = true;

  _FitnessAppHomeScreenState(Future<String> token) {
    this.tempTkn = token;
    getToken();
    // token.then((value) => setState(() {
    //       this.token = value;
    //     }));
  }

  List<TabIconData> tabIconsList = TabIconData.tabIconsList;

  Widget tabBody = Container(
    color: FitnessAppTheme.background,
  );

  @override
  void initState() {
    tabIconsList.forEach((TabIconData tab) {
      tab.isSelected = false;
    });
    tabIconsList[0].isSelected = true;

    animationController = AnimationController(
        duration: const Duration(milliseconds: 600), vsync: this);
    tabBody = MyDiaryScreen(animationController: animationController);
    super.initState();
  }

  @override
  void dispose() {
    animationController?.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    // print("Loading UI!!");
    // print("http://demo-e-comm-integration.s3-website-us-east-1.amazonaws.com/?token=" +
    //     this.token +
    //     "&pid=78342");
    return Container(
      color: FitnessAppTheme.background,
      child: Scaffold(
        backgroundColor: Colors.transparent,
        body: FutureBuilder<String>(
          initialData: "Loading token...",
          future: getToken(),
          builder: (BuildContext context, AsyncSnapshot<String> snapshot) {
            return Stack(
              children: <Widget>[
                Padding(
                  padding: EdgeInsets.only(top: 40),
                  child: new WebView(
                    initialUrl:
                    "http://demo-e-comm-integration.s3-website-us-east-1.amazonaws.com/?token=" +
                        snapshot.data.toString() +
                        "&pid=78342",
                    javascriptMode: JavascriptMode.unrestricted,
                    onPageFinished: (finish) {
                      setState(() {
                        isLoading = false;
                      });
                    },
                  ),
                ),
                bottomBar(),
                isLoading ? Center(child: LoadingAnimationWidget.discreteCircle(
                          size: 50, color: Colors.blue,
                        )
                ) : Stack()
              ],
            );
          },
        ),
      ),
    );
  }

  Future<bool> getData() async {
    await Future<dynamic>.delayed(const Duration(seconds: 2000));
    return true;
  }

  Widget bottomBar() {
    return Column(
      children: <Widget>[
        const Expanded(
          child: SizedBox(),
        ),
        BottomBarView(
          tabIconsList: tabIconsList,
          addClick: () {},
          changeIndex: (int index) {
            if (index == 0 || index == 2) {
              animationController?.reverse().then<dynamic>((data) {
                if (!mounted) {
                  return;
                }
                setState(() {
                  tabBody =
                      MyDiaryScreen(animationController: animationController);
                });
              });
            } else if (index == 1 || index == 3) {
              animationController?.reverse().then<dynamic>((data) {
                if (!mounted) {
                  return;
                }
                setState(() {
                  tabBody =
                      TrainingScreen(animationController: animationController);
                });
              });
            }
          },
        ),
      ],
    );
  }

  Future<String> getToken() async {
    await tempTkn.then((value) => {
      setState(() {
        this.token = value;
      })
    });
    MethodChannel channel = MethodChannel("flutter/goLive");
    Map<String,dynamic> params = {
      "token" : this.token
    };
    await channel.invokeMethod("launchSite",params);
    return this.token;
  }
}
