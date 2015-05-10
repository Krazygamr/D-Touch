/*
 * Copyright (C) 2009 jeyries@yahoo.fr
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
package com.beloko.idtech.shadow;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.opengles.GL10;

import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.EGLConfigChooser;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.bda.controller.Controller;
import com.bda.controller.ControllerListener;
import com.bda.controller.StateEvent;
import com.beloko.idtech.AppSettings;
import com.beloko.idtech.CDAudioPlayer;
import com.beloko.idtech.FPSLimit;
import com.beloko.idtech.GD;
import com.beloko.idtech.GD.IDGame;
import com.beloko.idtech.QuakeControlInterpreter;
import com.beloko.idtech.QuakeCustomCommands;
import com.beloko.idtech.QuakeTouchControlsSettings;
import com.beloko.idtech.Utils;
import com.beloko.libsdl.SDLLib;

public class Server extends Activity 

{
	String LOG = "PrBoomServer";


	Controller mogaController = null;

	private String args;
	private String doomPath;


	Activity act;


	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);           

		act = this;

		AppSettings.setGame(IDGame.Doom);
		AppSettings.reloadSettings(getApplication());

		args = getIntent().getStringExtra("args");
		doomPath  = getIntent().getStringExtra("doom_path");


		(new HelloThread()).start();
	}

	public class HelloThread extends Thread {

		public void run() {
			NativeLib.loadLibraries(true);
			args = " -altdeath";
			String[] args_array = Utils.creatArgs(args);

			NativeLib.startserver(args_array, doomPath);
		}


	}
}


