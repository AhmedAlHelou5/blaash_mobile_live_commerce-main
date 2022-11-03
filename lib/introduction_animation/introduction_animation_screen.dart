import 'package:best_flutter_ui_templates/introduction_animation/components/care_view.dart';
import 'package:best_flutter_ui_templates/introduction_animation/components/center_next_button.dart';
import 'package:best_flutter_ui_templates/introduction_animation/components/mood_diary_vew.dart';
import 'package:best_flutter_ui_templates/introduction_animation/components/relax_view.dart';
import 'package:best_flutter_ui_templates/introduction_animation/components/splash_view.dart';
import 'package:best_flutter_ui_templates/introduction_animation/components/top_back_skip_view.dart';
import 'package:best_flutter_ui_templates/introduction_animation/components/welcome_view.dart';
import 'package:flutter/material.dart';

import '../fitness_app/fitness_app_home_screen.dart';

class IntroductionAnimationScreen extends StatefulWidget {
  const IntroductionAnimationScreen({Key? key}) : super(key: key);

  @override
  _IntroductionAnimationScreenState createState() =>
      _IntroductionAnimationScreenState();
}

class _IntroductionAnimationScreenState
    extends State<IntroductionAnimationScreen> with TickerProviderStateMixin {
  AnimationController? _animationController;

  @override
  void initState() {
    _animationController =
        AnimationController(vsync: this, duration: Duration(microseconds: 1));
    _animationController?.animateTo(1);
    super.initState();
  }

  @override
  void dispose() {
    _animationController?.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    print(_animationController?.value);
    return Scaffold(
      resizeToAvoidBottomInset: false,
      backgroundColor: Colors.white,
        body: (
            WelcomeView()
        ),
      );
  }
}
