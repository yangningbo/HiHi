/*
 * Copyright (C) 2010 Moduad Co., Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.gaopai.guiren.service;

import xmpp.push.sns.ConnectionListener;
import android.content.Intent;
import android.util.Log;

import com.gaopai.guiren.BaseActivity;
import com.gaopai.guiren.DamiApp;
import com.gaopai.guiren.DamiCommon;
import com.gaopai.guiren.FeatureFunction;
import com.gaopai.guiren.R;
import com.gaopai.guiren.activity.MainActivity;

public class PersistentConnectionListener implements ConnectionListener {

    private static final String LOGTAG = "PersissstentConnectionListener";

    private final XmppManager xmppManager;

    public PersistentConnectionListener(XmppManager xmppManager) {
        this.xmppManager = xmppManager;
    }

    @Override
    public void connectionClosed() {
        Log.i(LOGTAG, "connectionClosed()...");
        try {
        	if(DamiCommon.verifyNetwork(xmppManager.getSnsService()) 
        			&& xmppManager.getSnsService().isServiceRunState())
        	{
				xmppManager.startReconnectionThread();
			}
		} catch (Exception e) {
		}
    }

    @Override
    public void connectionClosedOnError(Exception e) {
        Log.i(LOGTAG, "connectionClosedOnError()..." + " " + e.getMessage());
        if(e.getMessage().contains("stream:error (conflict)")){
        	Intent toastIntent = new Intent(MainActivity.ACTION_SHOW_TOAST);
        	toastIntent.putExtra("toast_msg", DamiApp.getInstance().getString(R.string.openfire_login_prompt));
        	DamiApp.getInstance().sendBroadcast(toastIntent);
        	DamiCommon.saveLoginResult(DamiApp.getInstance(), null);
			DamiCommon.setUid("");
			DamiCommon.setToken("");
			DamiApp.getInstance().sendBroadcast(new Intent(BaseActivity.ACTION_FINISH));
			FeatureFunction.stopService(DamiApp.getInstance());
			DamiApp.getInstance().sendBroadcast(new Intent(MainActivity.ACTION_LOGIN_OUT));
        }else {
        	try {
            	xmppManager.getConnection().disconnect();
    		} catch (Exception e2) {
    			e.printStackTrace();
    			e2.printStackTrace();
    		}
    		try {
    			if(DamiCommon.verifyNetwork(xmppManager.getSnsService()) && xmppManager.getSnsService().isServiceRunState()){
    				xmppManager.startReconnectionThread();
    			}
    		} catch (Exception e2) {
    		}
		}
        
    }

    @Override
    public void reconnectingIn(int seconds) {
        Log.i(LOGTAG, "reconnectingIn()...");
    }

    @Override
    public void reconnectionFailed(Exception e) {
        Log.i(LOGTAG, "reconnectionFailed()...");
    }

    @Override
    public void reconnectionSuccessful() {
        Log.i(LOGTAG, "reconnectionSuccessful()...");
    }

}
