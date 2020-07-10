/*
 * Copyright 2013 Thomas Hoffmann
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.faizi.pedometerdemo.widget;

import android.content.Intent;

import com.google.android.apps.dashclock.api.DashClockExtension;
import com.google.android.apps.dashclock.api.ExtensionData;

import com.faizi.pedometerdemo.Database;
import com.faizi.pedometerdemo.R;
import com.faizi.pedometerdemo.ui.fragment.PedoMeterFragment;
import com.faizi.pedometerdemo.util.Util;

import de.j4velin.pedometer.ui.Activity_Main;

/**
 * Class for providing a DashClock (https://code.google.com/p/dashclock)
 * extension
 */
public class DashClock extends DashClockExtension {

    @Override
    protected void onUpdateData(int reason) {
        ExtensionData data = new ExtensionData();
        Database db = Database.getInstance(this);
        int steps = Math.max(db.getCurrentSteps().getStep() + db.getSteps(Util.getToday()).getStep(), 0);
        data.visible(true).status(PedoMeterFragment.formatter.format(steps))
                .icon(R.drawable.ic_dashclock)
                .clickIntent(new Intent(DashClock.this, Activity_Main.class));
        db.close();
        publishUpdate(data);
    }

}
