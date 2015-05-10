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
package com.beloko.idtech.vavoom;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.opengles.GL10;

import android.app.Activity;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.EGLConfigChooser;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.os.Vibrator;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.beloko.idtech.AppSettings;
import com.beloko.idtech.CDAudioPlayer;
import com.beloko.idtech.FPSLimit;
import com.beloko.idtech.GD;
import com.beloko.idtech.GD.IDGame;
import com.beloko.idtech.QuakeControlInterpreter;
import com.beloko.idtech.QuakeCustomCommands;
import com.beloko.idtech.QuakeTouchControlsSettings;
import com.beloko.idtech.Utils;

public class Vavoom extends Activity 
implements Handler.Callback
{
	String LOG = "Quake2";

	private QuakeControlInterpreter controlInterp;

	private String args;


	private QuakeView mGLSurfaceView = null;
	private QuakeRenderer mRenderer = new QuakeRenderer();
	Activity act;

	private Vibrator vibrator;

	private boolean please_exit = false;

	// android settings - saved as preferences
	private boolean debug = false,	
			invert_roll = false,
			enable_audio = true,
			enable_sensor = true,
			enable_vibrator = false,
			enable_ecomode = false;

	private long tstart;
	private int timelimit = 0; //4*60000;

	private String error_message;
	private int overlay = 0;

	public static final String version = "1.91" ;


	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);           

		Log.i( "Quake2.java", "onCreate " + version);
		
		act = this;
		
		AppSettings.setGame(IDGame.Quake2);//Always Quake
		AppSettings.reloadSettings(getApplication());

		args = getIntent().getStringExtra("args");

		handlerUI  = new Handler(this);         

		//Log.i( "Quake2", "version : " + getVersion());

		// fullscreen
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		// keep screen on 
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		start_quake2();   

	}


	/// Handler for asynchronous message
	/// => showDialog

	private Handler handlerUI ;

	public static final int MSG_SHOW_DIALOG = 1;


	// implements Handler.Callback
	@Override
	public boolean handleMessage(Message msg) {

		Log.i( "Quake2", String.format("handleMessage %d %d", msg.what, msg.arg1));

		switch( msg.what ){

		case MSG_SHOW_DIALOG:
			showDialog(msg.arg1);
			break;

		}

		return true;

	}


	/////////////////////////////




	public void start_quake2() {



		VavoomLib engine = new VavoomLib();
		controlInterp = new QuakeControlInterpreter(engine,GD.IDGame.Quake2,AppSettings.gamePadControlsFile,AppSettings.gamePadEnabled);

		QuakeTouchControlsSettings.setup(act, engine);
		QuakeTouchControlsSettings.loadSettings(act);
		QuakeTouchControlsSettings.sendToQuake();

		QuakeCustomCommands.setup(act, engine,getIntent().getStringExtra("main_qc"),getIntent().getStringExtra("mod_qc"));
		
		
		mRenderer.speed_limit = enable_ecomode ? 40 : 0;



		vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);


		// Create our Preview view and set it as the content of our
		// Activity
		mGLSurfaceView = new QuakeView(this);
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

			Log.i( "Quake2.java", "chooseConfig");


			int[] mConfigSpec  = {
					EGL10.EGL_RED_SIZE, 8,
					EGL10.EGL_GREEN_SIZE, 8,
					EGL10.EGL_BLUE_SIZE, 8,
					EGL10.EGL_ALPHA_SIZE, 8,
					EGL10.EGL_DEPTH_SIZE, 16,
					EGL10.EGL_STENCIL_SIZE, 0,
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


			if (debug)
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
		super.onPause();
	}

	@Override
	protected void onResume() {

		Log.i( "Quake2.java", "onResume" );
		CDAudioPlayer.onResume();
		
		super.onResume();
		if (mRenderer.state!=mRenderer.STATE_RESET){
			mGLSurfaceView.onResume();
		}
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
		System.exit(0);
	}






	static final int DIALOG_EXIT_ID = 0,
			DIALOG_ABOUT_ID = 1,
			DIALOG_PAK_NOT_FOUND = 2,
			DIALOG_ERROR = 3,
			DIALOG_LOADING = 4,
			DIALOG_CHECK_UPDATE = 5;

	

	class QuakeView extends GLSurfaceView {

		/*--------------------
		 * Event handling
		 *--------------------*/

	
		public QuakeView(Context context) {
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

	class QuakeRenderer implements GLSurfaceView.Renderer {


		private static final int 
		STATE_RESET=0,
		STATE_SURFACE_CREATED=1,
		STATE_RUNNING=2,
		STATE_ERROR=100;



		private int state = STATE_RESET; 


		// deprecated ... use setEGLConfigChooser
		//public int[] getConfigSpec() {


		public void onSurfaceCreated(GL10 gl, EGLConfig config) {


			Log.d("Renderer", "onSurfaceCreated");

			switch(state){

			case STATE_RESET:
				state=STATE_SURFACE_CREATED;
				break;

			default:
				throw new Error("wrong state");

			}

/*
			gl.glDisable(GL10.GL_DITHER);

			gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT,
					//GL10.GL_FASTEST);
					GL10.GL_NICEST);
*/
		}





		private void init( int width, int height ){

			Log.i( "Quake2", "screen size : " + width + "x"+ height);

			VavoomLib.Quake2SetScreenSize(width,height);
		
			Utils.copyPNGAssets(getApplicationContext(),AppSettings.graphicsDir);   

			fpsLimit = new FPSLimit(getApplicationContext());

			Log.i("Quake2", "Quake2Init start");

			//String[] args_array = Utils.creatArgs(args);
			String[] args_array= {"test"};

			
			int ret = VavoomLib.init(AppSettings.graphicsDir,64,args_array,0);

			Log.i("Quake2", "Quake2Init done");

			if (ret!=0){

				error_message = String.format("initialisation error detected (code %d)\nworkaround : reinstall APK or reboot phone.", ret) ;
				Log.e( "Quake2", error_message   );

				//System.exit(1);

				state = STATE_ERROR;
				// error, wrong thread ...
				//showDialog(DIALOG_ERROR);


				Message.obtain(handlerUI, MSG_SHOW_DIALOG, DIALOG_ERROR, 0 )  			   
				.sendToTarget();


				return;
			}
			tstart = SystemClock.uptimeMillis();
		}




		private int counter_fps=0;
		private long tprint_fps= 0;
		private int framenum=0;

		// speed limit : 10 FPS
		private int speed_limit = 0; 
		//40;
		//100;
		//200;

		private int vibration_duration = //0;
				100;

		private boolean vibration_running = false;
		private long vibration_end;

		private long tprev = 0;
		private boolean paused = false;

		private boolean audio_initalised = false;
		//// new Renderer interface
		int notifiedflags;
		
		FPSLimit fpsLimit;
		
		boolean inited = false;
		
		public void onDrawFrame(GL10 gl) {

			//fpsLimit.tick();
			
			//Log.d(LOG,"onDrawFrame");
			
			if (!inited)
			{
				inited = true;
				init( 640, 480 );
			}
			
			switch(state){

			case STATE_RUNNING:
				// nothing
				break;

			case STATE_ERROR:
			{
				long s = SystemClock.uptimeMillis();

				gl.glClearColor(((s>>10)&1)*1.0f,((s>>11)&1)*1.0f,((s>>12)&1)*1.0f,1.0f);

				gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

				gl.glFinish();
			}
			return;

			default:
				throw new Error("wrong state");

			}

			long tnow = SystemClock.uptimeMillis(); 
			int tdelta = (int)(tnow-tprev) ;
			if ( tprev == 0 ) tdelta = 0;
			tprev = tnow;

			if ( timelimit!=0 && (tnow-tstart)>= timelimit){
				Log.i( "Quake2.java", "Timer expired. exiting");
				finish();
				timelimit = 0;
			}

			// compute FPS
			if ( (tnow-tprint_fps) >= 1000){
				if (debug)
					Log.i("Quake2",String.format( "FPS= %d",counter_fps));

				tprint_fps = tnow;
				counter_fps = 0;        	
			}
			counter_fps ++;



			/*
			 * Usually, the first thing one might want to do is to clear
			 * the screen. The most efficient way of doing this is to use
			 * glClear().
			 */

			int vibration = 0;

			//Log.i("Quake2", "Quake2Frame start");

			//if (framenum < 30)
			//	Log.i("Quake2", String.format("frame %d",framenum));


			//while( sQuake2Frame()==0 );  
			
			//gl.glClearColor(1f,0,0,1.0f);

			//gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

			
			int flags =  VavoomLib.Quake2Frame();
			
			if (flags != notifiedflags)
			{
				if (((flags ^ notifiedflags) & 1) != 0)
				{
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
									im.hideSoftInputFromWindow(mGLSurfaceView.getWindowToken(), 0);
								}
							}
							else
								android.util.Log.i("FTEDroid", "IMM failed");
						}
					};
					act.runOnUiThread(r);
				}
				notifiedflags = flags;
			}
			
			
			
			
			framenum ++;

		

		}




		public void onSurfaceChanged(GL10 gl, int width, int height) {


			Log.d("Renderer", String.format("onSurfaceChanged %dx%d", width,height) );

			controlInterp.setScreenSize(width, height);


			//AndroidRenderer.renderer.set_gl(gl);
			//AndroidRenderer.renderer.set_size(width,height);

			//gl.glViewport(0, 0, width, height);

			switch(state){

			case STATE_SURFACE_CREATED:
				//init( width, height );
				state=STATE_RUNNING;
				break;

			case STATE_RUNNING:
				//nothing
				break;

			default:
				throw new Error("wrong state");
			}


		}


	} // end of QuakeRenderer



	private static Object quake2Lock = new Object();


	private static int sQuake2Quit(){
		int ret;
		synchronized(quake2Lock) { 	
			Log.i( "Quake2.java", "Quake2Quit" );
			ret = VavoomLib.Quake2Quit();
		}
		return ret;
	}

	

	


}


