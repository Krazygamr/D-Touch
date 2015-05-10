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
package com.beloko.idtech.avp;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

import com.bda.controller.Controller;
import com.bda.controller.ControllerListener;
import com.bda.controller.StateEvent;
import com.beloko.idtech.AppSettings;
import com.beloko.idtech.BestEglChooser;
import com.beloko.idtech.CDAudioPlayer;
import com.beloko.idtech.FPSLimit;
import com.beloko.idtech.GD.IDGame;
import com.beloko.idtech.MyGLSurfaceView;
import com.beloko.idtech.QuakeControlInterpreter;
import com.beloko.idtech.QuakeTouchControlsSettings;
import com.beloko.idtech.ShowKeyboard;
import com.beloko.idtech.Utils;
import com.beloko.libsdl.SDLLib;
import com.beloko.ssetup.SSetup;

public class Game extends Activity 
{
	String LOG = "Game";

	private QuakeControlInterpreter controlInterp;

	private final MogaControllerListener mogaListener = new MogaControllerListener();
	Controller mogaController = null;

	private String args;
	private String gamePath;
	private boolean setupLaunch; //True if the native setup program launched this

	private GameView mGLSurfaceView = null;
	private QuakeRenderer mRenderer = new QuakeRenderer();
	Activity act;

	int surfaceWidth,surfaceHeight;



	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);           

		act = this;

		AppSettings.setGame(IDGame.valueOf( getIntent().getStringExtra("game")));
		
		AppSettings.reloadSettings(getApplication());

		args = getIntent().getStringExtra("args");
		gamePath  = getIntent().getStringExtra("game_path");
		setupLaunch = getIntent().getBooleanExtra("setup_launch", false);
		
		mogaController = Controller.getInstance(this);
		mogaController.init();
		mogaController.setListener(mogaListener,new Handler());

		// fullscreen
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		// keep screen on 
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


		start_game();   
	}




	public void start_game() {

		NativeLib.loadLibraries(false);

		NativeLib engine = new NativeLib();

		SSetup.load(getApplicationContext(), act);
		
		controlInterp = new QuakeControlInterpreter(engine,AppSettings.game,AppSettings.gamePadControlsFile,AppSettings.gamePadEnabled);

		QuakeTouchControlsSettings.setup(act, engine);
		QuakeTouchControlsSettings.loadSettings(act);
		QuakeTouchControlsSettings.sendToQuake();


		mGLSurfaceView = new GameView(this);

		NativeLib.gv = mGLSurfaceView;

		ShowKeyboard.setup(act, mGLSurfaceView);

		mGLSurfaceView.setEGLConfigChooser( new BestEglChooser(getApplicationContext()) );

		mGLSurfaceView.setRenderer(mRenderer);

		// This will keep the screen on, while your view is visible. 
		mGLSurfaceView.setKeepScreenOn(true);

		setContentView(mGLSurfaceView);
		mGLSurfaceView.requestFocus();
		mGLSurfaceView.setFocusableInTouchMode(true);
	}




	@Override
	protected void onPause() {
		Log.i(LOG, "onPause" );
		CDAudioPlayer.onPause();
		SDLLib.onPause();
		mogaController.onPause();
		super.onPause();
	}

	@Override
	protected void onResume() {

		Log.i(LOG, "onResume" );
		CDAudioPlayer.onResume();
		SDLLib.onResume();
		mogaController.onResume();
		super.onResume();
		mGLSurfaceView.onResume();
	}


	@Override
	protected void onDestroy() {
		Log.i( LOG, "onDestroy" ); 
		super.onDestroy();
		mogaController.exit();
		System.exit(0);
	}

	class MogaControllerListener implements ControllerListener {


		@Override
		public void onKeyEvent(com.bda.controller.KeyEvent event) {
			//Log.d(LOG,"onKeyEvent " + event.getKeyCode());
			controlInterp.onMogaKeyEvent(event,mogaController.getState(Controller.STATE_CURRENT_PRODUCT_VERSION));
		}

		@Override
		public void onMotionEvent(com.bda.controller.MotionEvent event) {
			controlInterp.onGenericMotionEvent(event);
		}

		@Override
		public void onStateEvent(StateEvent event) {
			Log.d(LOG,"onStateEvent " + event.getState());
		}
	}

	class GameView extends MyGLSurfaceView {

		/*--------------------
		 * Event handling
		 *--------------------*/


		public GameView(Context context) {
			super(context);

		}

		@Override
		public boolean onGenericMotionEvent(MotionEvent event) {
			return controlInterp.onGenericMotionEvent(event);
		}
		@Override
		public boolean onTouchEvent(MotionEvent event)
		{
			return controlInterp.onTouchEvent(event);
		}

		@Override
		public boolean onKeyDown(int keyCode, KeyEvent event)
		{
			return controlInterp.onKeyDown(keyCode, event);
		}

		@Override
		public boolean onKeyUp(int keyCode, KeyEvent event)
		{
			return controlInterp.onKeyUp(keyCode, event);
		} 

	}  // end of QuakeView



	///////////// GLSurfaceView.Renderer implementation ///////////

	class QuakeRenderer implements MyGLSurfaceView.Renderer {



		public void onSurfaceCreated(GL10 gl, EGLConfig config) {
			Log.d("Renderer", "onSurfaceCreated");
		}

		private void init( int width, int height ){

			Log.i( LOG, "screen size : " + width + "x"+ height);

			NativeLib.setScreenSize(width,height);

			Utils.copyPNGAssets(getApplicationContext(),AppSettings.graphicsDir);   

			Log.i(LOG, "Quake2Init start");


			String[] args_array = Utils.creatArgs(args);

			int ret = NativeLib.init(AppSettings.graphicsDir,0,args_array,0,gamePath);

			Log.i(LOG, "Quake2Init done");

		}

		//// new Renderer interface
		int notifiedflags;

		FPSLimit fpsLimit;

		public void onDrawFrame(GL10 gl) {

			init( surfaceWidth, surfaceHeight );

		}


		public void onSurfaceChanged(GL10 gl, int width, int height) {
			Log.d("Renderer", String.format("onSurfaceChanged %dx%d", width,height) );

			SDLLib.nativeInit(false);

			SDLLib.surfaceChanged(PixelFormat.RGBA_8888, width, height);

			controlInterp.setScreenSize(width, height);

			surfaceWidth = width;
			surfaceHeight = height;
		}
	} // end of QuakeRenderer
}


