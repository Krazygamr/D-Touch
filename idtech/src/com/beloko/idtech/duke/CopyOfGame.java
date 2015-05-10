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
package com.beloko.idtech.duke;

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
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.Window;
import android.view.WindowManager;

import com.beloko.idtech.AppSettings;
import com.beloko.idtech.CDAudioPlayer;
import com.beloko.idtech.FPSLimit;
import com.beloko.idtech.GD.IDGame;
import com.beloko.idtech.QuakeControlInterpreter;
import com.beloko.idtech.QuakeCustomCommands;
import com.beloko.idtech.QuakeTouchControlsSettings;
import com.beloko.idtech.ShowKeyboard;
import com.beloko.idtech.Utils;
import com.beloko.libsdl.SDLLib;

public class CopyOfGame extends Activity 
{
	String LOG = "eDuke32";

	private QuakeControlInterpreter controlInterp;

	private String args;
	private String doomPath;

	private QuakeView mGLSurfaceView = null;
	private QuakeRenderer mRenderer = new QuakeRenderer();
	Activity act;

	int surfaceWidth,surfaceHeight;

	int renderType;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);           

		act = this;

		AppSettings.setGame(IDGame.Duke3d);
		AppSettings.reloadSettings(getApplication());

		args = getIntent().getStringExtra("args");
		doomPath  = getIntent().getStringExtra("doom_path");


		//Log.i( "Quake2", "version : " + getVersion());

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

		renderType = getIntent().getIntExtra("renderer", NativeLib.REND_SOFT);
		renderType = NativeLib.REND_GL;

		NativeLib engine = new NativeLib();
		if ((renderType == NativeLib.REND_SOFT))
			NativeLib.loadLibraries(false);
		else
			NativeLib.loadLibraries(false);

		controlInterp = new QuakeControlInterpreter(engine,AppSettings.game,AppSettings.gamePadControlsFile,AppSettings.gamePadEnabled);

		QuakeTouchControlsSettings.setup(act, engine);
		QuakeTouchControlsSettings.loadSettings(act);
		QuakeTouchControlsSettings.sendToQuake();

		QuakeCustomCommands.setup(act, engine,getIntent().getStringExtra("main_qc"),getIntent().getStringExtra("mod_qc"));

		ShowKeyboard.setup(act, mGLSurfaceView);

		// Create our Preview view and set it as the content of our
		// Activity
		mGLSurfaceView = new QuakeView(this);


		//if (renderType == NativeLib.REND_SOFT) //SDL software mode uses gles2
		//	mGLSurfaceView.setEGLContextClientVersion(2); // enable OpenGL 2.0

		//mGLSurfaceView.setGLWrapper( new MyWrapper());
		//mGLSurfaceView.setDebugFlags(GLSurfaceView.DEBUG_CHECK_GL_ERROR | GLSurfaceView.DEBUG_LOG_GL_CALLS);
		//setEGLConfigChooser  (int redSize, int greenSize, int blueSize, int alphaSize, int depthSize, int stencilSize)
		//mGLSurfaceView.setEGLConfigChooser(8,8,8,0,16,0);
		mGLSurfaceView.setEGLConfigChooser( new QuakeEGLConfigChooser() );

		mGLSurfaceView.setRenderer(mRenderer);

		// This will keep the screen on, while your view is visible. 
		mGLSurfaceView.setKeepScreenOn(true);

		setContentView(mGLSurfaceView);
		mGLSurfaceView.requestFocus();
		mGLSurfaceView.setFocusableInTouchMode(true);

	}

	class QuakeEGLConfigChooser implements EGLConfigChooser {




		public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display) {

			Log.i( LOG, "chooseConfig");


			int[] mConfigSpec  = {
					EGL10.EGL_RED_SIZE, 8,
					EGL10.EGL_GREEN_SIZE, 8,
					EGL10.EGL_BLUE_SIZE, 8,
					EGL10.EGL_ALPHA_SIZE, 8,
					EGL10.EGL_DEPTH_SIZE, 16,
					//EGL10.EGL_RENDERABLE_TYPE,4, //GLES 2
					EGL10.EGL_STENCIL_SIZE, 8,
					EGL10.EGL_NONE};



			int[] num_config = new int[1];
			egl.eglChooseConfig(display, mConfigSpec, null, 0, num_config);

			int numConfigs = num_config[0];

			Log.i( LOG, "numConfigs="+numConfigs);

			if (numConfigs <= 0) {
				throw new IllegalArgumentException(
						"No EGL configs match configSpec");
			}

			EGLConfig[] configs = new EGLConfig[numConfigs];
			egl.eglChooseConfig(display, mConfigSpec, configs, numConfigs,
					num_config);


			for(EGLConfig config : configs) {
				Log.i( LOG, "found EGL config : " + printConfig(egl,display,config));       	
			}


			// best choice : select first config
			Log.i( LOG, "selected EGL config : " + printConfig(egl,display,configs[0]));

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
		Log.i( LOG, "onPause" );
		CDAudioPlayer.onPause();
		SDLLib.onPause();
		NativeLib.onPause();
		super.onPause();
	}

	@Override
	protected void onResume() {

		Log.i( LOG, "onResume" );
		CDAudioPlayer.onResume();
		SDLLib.onResume();
		NativeLib.onResume();
		super.onResume();
		mGLSurfaceView.onResume();

	}

	@Override
	protected void onRestart() {
		Log.i( LOG, "onRestart" );
		super.onRestart();
	}




	@Override
	protected void onStop() {
		Log.i( LOG, "onStop" );
		super.onStop();	
	}

	@Override
	protected void onDestroy() {
		Log.i( LOG, "onDestroy" ); 
		super.onDestroy();
		System.exit(0);
	}



	class QuakeView extends GLSurfaceView {

		/*--------------------
		 * Event handling
		 *--------------------*/


		public QuakeView(Context context) {
			super(context);

		}

		@Override
		public boolean dispatchTouchEvent(MotionEvent event) {
			return controlInterp.onTouchEvent(event);
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
			//Log.d(LOG,"onKeyDown " + keyCode);
			SDLLib.onNativeKeyDown(keyCode);
			return controlInterp.onKeyDown(keyCode, event);
		}

		@Override
		public boolean onKeyUp(int keyCode, KeyEvent event)
		{
			SDLLib.onNativeKeyUp(keyCode);
			return controlInterp.onKeyUp(keyCode, event);
		} 

	} 

	///////////// GLSurfaceView.Renderer implementation ///////////

	class QuakeRenderer implements GLSurfaceView.Renderer {

		

		@Override
		public void onSurfaceCreated(GL10 gl, EGLConfig config) {
			Log.d("Renderer", "onSurfaceCreated");
		}


		private void init( int width, int height ){

			Log.i( LOG, "screen size : " + width + "x"+ height);

			NativeLib.setScreenSize(width,height);

			Utils.copyAssets(getApplicationContext(),AppSettings.graphicsDir);   

			fpsLimit = new FPSLimit(getApplicationContext());

			Log.i(LOG, "Quake2Init start");

			String[] args_array = Utils.creatArgs(args);

			int ret = NativeLib.init(AppSettings.graphicsDir,64,args_array,renderType,doomPath);

			Log.i(LOG, "Quake2Init done");

		}



		//// new Renderer interface
		int notifiedflags;

		FPSLimit fpsLimit;

		boolean inited = false;

		public void onDrawFrame(GL10 gl) {


			if (!inited)
			{
				inited = true;
				init( surfaceWidth, surfaceHeight );
			}

			fpsLimit.tick();

		}



		@Override
		public void onSurfaceChanged(GL10 gl, int width, int height) {


			Log.d("Renderer", String.format("onSurfaceChanged %dx%d", width,height) );

			SDLLib.nativeInit(false);

			SDLLib.surfaceChanged(PixelFormat.RGBA_8888, width, height);
			//SDLLib.surfaceChanged(PixelFormat.RGBA_8888, 320, 240);

			controlInterp.setScreenSize(width, height);

			surfaceWidth = width;
			surfaceHeight = height;
		}


	} 

}


