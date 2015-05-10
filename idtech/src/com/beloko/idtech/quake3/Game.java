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
package com.beloko.idtech.quake3;

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
import com.beloko.idtech.BestEglChooser;
import com.beloko.idtech.CDAudioPlayer;
import com.beloko.idtech.FPSLimit;
import com.beloko.idtech.GD.IDGame;
import com.beloko.idtech.QuakeControlInterpreter;
import com.beloko.idtech.QuakeCustomCommands;
import com.beloko.idtech.QuakeTouchControlsSettings;
import com.beloko.idtech.ShowKeyboard;
import com.beloko.idtech.Utils;
import com.beloko.libsdl.SDLLib;

public class Game extends Activity 
implements Handler.Callback
{
	String LOG = "Quake3";

	private QuakeControlInterpreter controlInterp;

	private final MogaControllerListener mogaListener = new MogaControllerListener();
	Controller mogaController = null;

	private String args;
	private String gamePath;

	private QuakeView mGLSurfaceView = null;
	private QuakeRenderer mRenderer = new QuakeRenderer();
	Activity act;

	int surfaceWidth,surfaceHeight;


	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);           

		act = this;

		AppSettings.setGame(IDGame.Quake3);
		AppSettings.reloadSettings(getApplication());

		args = getIntent().getStringExtra("args");
		gamePath  = getIntent().getStringExtra("game_path");

		handlerUI  = new Handler(this);         

		mogaController = Controller.getInstance(this);
		mogaController.init();
		mogaController.setListener(mogaListener,new Handler());

		//Log.i( LOG, "version : " + getVersion());

		// fullscreen
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		// keep screen on 
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


		Utils.setImmersionMode(this);

		
		start_quake2();   
	}
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		Utils.onWindowFocusChanged(this, hasFocus);
	}

	/// Handler for asynchronous message
	/// => showDialog

	private Handler handlerUI ;

	public static final int MSG_SHOW_DIALOG = 1;


	// implements Handler.Callback
	@Override
	public boolean handleMessage(Message msg) {

		Log.i( LOG, String.format("handleMessage %d %d", msg.what, msg.arg1));

		switch( msg.what ){

		case MSG_SHOW_DIALOG:
			showDialog(msg.arg1);
			break;

		}

		return true;

	}


	/////////////////////////////




	public void start_quake2() {

	
		NativeLib engine = new NativeLib();
		
		NativeLib.loadLibraries(false);

		controlInterp = new QuakeControlInterpreter(engine,AppSettings.game,AppSettings.gamePadControlsFile,AppSettings.gamePadEnabled);

		QuakeTouchControlsSettings.setup(act, engine);
		QuakeTouchControlsSettings.loadSettings(act);
		QuakeTouchControlsSettings.sendToQuake();

		QuakeCustomCommands.setup(act, engine,getIntent().getStringExtra("main_qc"),getIntent().getStringExtra("mod_qc"));


		// Create our Preview view and set it as the content of our
		// Activity
		mGLSurfaceView = new QuakeView(this);


		//mGLSurfaceView.setEGLConfigChooser( new QuakeEGLConfigChooser() );
		mGLSurfaceView.setEGLConfigChooser( new BestEglChooser(this) );

		mGLSurfaceView.setRenderer(mRenderer);

		// This will keep the screen on, while your view is visible. 
		mGLSurfaceView.setKeepScreenOn(true);
		
		ShowKeyboard.setup(act, mGLSurfaceView);

		setContentView(mGLSurfaceView);
		mGLSurfaceView.requestFocus();
		mGLSurfaceView.setFocusableInTouchMode(true);

	}

	class QuakeEGLConfigChooser implements EGLConfigChooser {




		public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display) {

			Log.i( "Quake2.java", "chooseConfig");


			int[] mConfigSpec  = {
					EGL10.EGL_RED_SIZE, 8,
					EGL10.EGL_GREEN_SIZE, 8,
					EGL10.EGL_BLUE_SIZE, 8,
					EGL10.EGL_ALPHA_SIZE, 8,
					EGL10.EGL_DEPTH_SIZE, 16,
					//EGL10.EGL_RENDERABLE_TYPE,4, //GLES 2
					//EGL10.EGL_STENCIL_SIZE, 8,
					EGL10.EGL_NONE};



			int[] num_config = new int[1];
			egl.eglChooseConfig(display, mConfigSpec, null, 0, num_config);

			int numConfigs = num_config[0];

			Log.i( "Quake2.java", "numConfigs="+numConfigs);

			if (numConfigs <= 0) {
				throw new IllegalArgumentException(
						"No EGL configs match configSpec");
			}

			EGLConfig[] configs = new EGLConfig[numConfigs];
			egl.eglChooseConfig(display, mConfigSpec, configs, numConfigs,
					num_config);


			for(EGLConfig config : configs) {
				Log.i( "Quake2.java", "found EGL config : " + printConfig(egl,display,config));       	
			}


			// best choice : select first config
			Log.i( "Quake2.java", "selected EGL config : " + printConfig(egl,display,configs[0]));

			return configs[0];
		}


		private  String printConfig(EGL10 egl, EGLDisplay display,
				EGLConfig config) {

			int r = findConfigAttrib(egl, display, config,
					EGL10.EGL_RED_SIZE, 0);
			int g = findConfigAttrib(egl, display, config,
					EGL10.EGL_GREEN_SIZE, 0);
			int b = findConfigAttrib(egl, display, config,
					EGL10.EGL_BLUE_SIZE, 0);
			int a = findConfigAttrib(egl, display, config,
					EGL10.EGL_ALPHA_SIZE, 0);
			int d = findConfigAttrib(egl, display, config,
					EGL10.EGL_DEPTH_SIZE, 0);
			int s = findConfigAttrib(egl, display, config,
					EGL10.EGL_STENCIL_SIZE, 0);

			/*
			 * 
			 * EGL_CONFIG_CAVEAT value 

         #define EGL_NONE		       0x3038	
         #define EGL_SLOW_CONFIG		       0x3050	
         #define EGL_NON_CONFORMANT_CONFIG      0x3051	
			 */

			return String.format("EGLConfig rgba=%d%d%d%d depth=%d stencil=%d", r,g,b,a,d,s)
					+ " native=" + findConfigAttrib(egl, display, config, EGL10.EGL_NATIVE_RENDERABLE, 0)
					+ " buffer=" + findConfigAttrib(egl, display, config, EGL10.EGL_BUFFER_SIZE, 0)
					+ String.format(" caveat=0x%04x" , findConfigAttrib(egl, display, config, EGL10.EGL_CONFIG_CAVEAT, 0))
					;




		}

		private int findConfigAttrib(EGL10 egl, EGLDisplay display,
				EGLConfig config, int attribute, int defaultValue) {

			int[] mValue = new int[1];
			if (egl.eglGetConfigAttrib(display, config, attribute, mValue)) {
				return mValue[0];
			}
			return defaultValue;
		}

	} // end of QuakeEGLConfigChooser

	//protected void onStart();




	@Override
	protected void onPause() {
		Log.i( "Quake2.java", "onPause" );
		CDAudioPlayer.onPause();
		SDLLib.onPause();
		mogaController.onPause();
		super.onPause();
	}

	@Override
	protected void onResume() {

		Log.i( "Quake2.java", "onResume" );
		CDAudioPlayer.onResume();
		SDLLib.onResume();
		mogaController.onResume();

		super.onResume();
		mGLSurfaceView.onResume();

	}

	@Override
	protected void onRestart() {
		Log.i( "Quake2.java", "onRestart" );
		super.onRestart();
	}




	@Override
	protected void onStop() {
		Log.i( "Quake2.java", "onStop" );
		super.onStop();	
	}

	@Override
	protected void onDestroy() {
		Log.i( "Quake2.java", "onDestroy" ); 
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
			// TODO Auto-generated method stub
			Log.d(LOG,"onGenericMotionEvent " + event.toString());
			controlInterp.onGenericMotionEvent(event);
		}

		@Override
		public void onStateEvent(StateEvent event) {
			Log.d(LOG,"onStateEvent " + event.getState());
		}
	}

	class QuakeView extends GLSurfaceView {

		/*--------------------
		 * Event handling
		 *--------------------*/


		public QuakeView(Context context) {
			super(context);

		}

		//@Override
		//public boolean dispatchTouchEvent(MotionEvent event) {
			//return controlInterp.onTouchEvent(event);
		//}

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
			Log.d(LOG,"onKeyDown " + keyCode);
			//SDLLib.onNativeKeyDown(keyCode);
			return controlInterp.onKeyDown(keyCode, event);
		}

		@Override
		public boolean onKeyUp(int keyCode, KeyEvent event)
		{
			//SDLLib.onNativeKeyUp(keyCode);
			return controlInterp.onKeyUp(keyCode, event);
		} 

	}  // end of QuakeView




	///////////// GLSurfaceView.Renderer implementation ///////////

	class QuakeRenderer implements GLSurfaceView.Renderer {



		public void onSurfaceCreated(GL10 gl, EGLConfig config) {

			Log.d("Renderer", "onSurfaceCreated");

		}


		private void init( int width, int height ){

			Log.i( LOG, "screen size : " + width + "x"+ height);

			NativeLib.setScreenSize(width,height);

			Utils.copyPNGAssets(getApplicationContext(),AppSettings.graphicsDir);   

			fpsLimit = new FPSLimit(getApplicationContext());

			Log.i(LOG, "Quake2Init start");

			String[] args_array = Utils.creatArgs(args);

			int ret = NativeLib.init(AppSettings.graphicsDir,64,args_array,0,gamePath);

			Log.i(LOG, "Quake2Init done");

		}



		//// new Renderer interface
		int notifiedflags;

		FPSLimit fpsLimit;

		boolean inited = false;

		public void onDrawFrame(GL10 gl) {



			//Log.d(LOG,"onDrawFrame");

			if (!inited)
			{
				AppSettings.setIntOption(getApplicationContext(), "max_fps", 0);  

				inited = true;
				init( surfaceWidth, surfaceHeight );
			}

			fpsLimit.tick();

			int flags =  NativeLib.frame();

			if (flags != notifiedflags)
			{
				if (((flags ^ notifiedflags) & 1) != 0)
				{
					Log.d(LOG,"show keyboard");
					final int fl = flags;
					Runnable r = new Runnable() 
					{	//doing this on the ui thread because android sucks.
						public void run()
						{
							InputMethodManager im = (InputMethodManager) act.getSystemService(Context.INPUT_METHOD_SERVICE);
							if (im != null)
							{
								if ((fl & 1) != 0)
								{
									//								getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
									im.showSoftInput(mGLSurfaceView, 0);//InputMethodManager.SHOW_FORCED);
								}
								else
								{
									//								getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
									//im.hideSoftInputFromWindow(mGLSurfaceView.getWindowToken(), 0);
								}
							}
							else
								android.util.Log.i("FTEDroid", "IMM failed");
						}
					};
					act.runOnUiThread(r);
				}
				else if (((flags ^ notifiedflags) & 128) != 0)
				{
					Log.d(LOG,"QUIT");
					finish();
				}
				notifiedflags = flags;
			}

		}




		public void onSurfaceChanged(GL10 gl, int width, int height) {


			Log.d("Renderer", String.format("onSurfaceChanged %dx%d", width,height) );

			SDLLib.nativeInit(false);

			SDLLib.surfaceChanged(PixelFormat.RGBA_8888, width, height);
			//SDLLib.surfaceChanged(PixelFormat.RGBA_8888, 320, 240);

			controlInterp.setScreenSize(width, height);

			surfaceWidth = width;
			surfaceHeight = height;

		}


	} // end of QuakeRenderer


}


