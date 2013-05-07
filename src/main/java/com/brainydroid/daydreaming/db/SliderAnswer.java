package com.brainydroid.daydreaming.db;

import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import com.brainydroid.daydreaming.R;
import com.brainydroid.daydreaming.ui.Config;
import com.brainydroid.daydreaming.ui.QuestionViewAdapter;
import com.google.gson.annotations.Expose;
import com.google.inject.Inject;

import java.util.ArrayList;
import java.util.HashMap;

public class SliderAnswer implements Answer {

    private static String TAG = "SliderAnswer";

    @Expose @Inject HashMap<String,Integer> sliders;

    @Override
    public void getAnswersFromLayout(LinearLayout questionLinearLayout) {

        //Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] getAnswersFromLayout");
        }

        ArrayList<View> subQuestions = QuestionViewAdapter.getViewsByTag(questionLinearLayout, "subQuestion");

        for (View subQuestion : subQuestions) {
            SeekBar seekBar = (SeekBar)subQuestion.findViewById(
                    R.id.question_slider_seekBar);
            TextView mainTextView = (TextView)subQuestion.findViewById(R.id.question_slider_mainText);
            String mainText = mainTextView.getText().toString();
            addAnswer(mainText, seekBar.getProgress());
        }
    }

    private void addAnswer(String questionString, int answer) {

        //Debug
        if (Config.LOGD) {
            Log.d(TAG, "[fn] addAnswer");
        }

        sliders.put(questionString, answer);
    }

}
