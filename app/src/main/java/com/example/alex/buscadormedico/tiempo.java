package com.example.alex.buscadormedico;

/**
 * Created by Alex on 10/07/2015.
 */
/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

import android.app.Activity;
import android.os.Bundle;

import java.util.Timer;
import java.util.TimerTask;


/*
 * MainActivity class that loads MainFragment
 */
public class tiempo extends Activity {
    private Timer timer;
    /**
     * Called when the activity is first created.
     */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        timer=new Timer();
        SubTimerExample sub=new SubTimerExample();

        timer.schedule(sub,0,5000);

    }


    class SubTimerExample extends TimerTask{

        @Override
        public void run() {

        }
    }
}
