
package com.beloko.idtech.doom3;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.opengles.GL10;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

import com.bda.controller.Controller;
import com.bda.controller.ControllerListener;
import com.bda.controller.StateEvent;
import com.beloko.idtech.AppSettings;
import com.beloko.idtech.CDAudioPlayer;
import com.beloko.idtech.GD;
import com.beloko.idtech.GD.IDGame;
import com.beloko.idtech.NoSwapBestEglChooser;
import com.beloko.idtech.NoSwapGLSurfaceView;
import com.beloko.idtech.NoSwapGLSurfaceView.EGLConfigChooser;
import com.beloko.idtech.QuakeControlInterpreter;
import com.beloko.idtech.QuakeCustomCommands;
import com.beloko.idtech.QuakeTouchControlsSettings;
import com.beloko.idtech.ShowKeyboard;
import com.beloko.idtech.Utils;
import com.beloko.ssetup.SSetup;

public class Game extends Activity 
implements Handler.Callback
{
	static String LOG = "Quake3";

	private QuakeControlInterpreter controlInterp;

	private final MogaControllerListener mogaListener = new MogaControllerListener();
	Controller mogaController = null;

	private static String args;
	private static String gamePath;

	private QuakeView mGLSurfaceView = null;
	private QuakeRenderer mRenderer = new QuakeRenderer();
	static Activity act;

	static int surfaceWidth;

	static int surfaceHeight;


	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);           

		act = this;

		AppSettings.setGame(IDGame.JK3);
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

		GD.init(getApplicationContext());

		String s = "Please stop hacking this and posting online. It has a MASSIVE effect on sales which means I can not afford to port more games. It costs less than $2 on Google Play!!! Thank you.";
		Log.i(LOG,s);


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


	class QuakeEGLConfigChooser implements EGLConfigChooser {

		public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display) {

			Log.i( "Quake2.java", "chooseConfig");

			int EGL_OPENGL_ES2_BIT = 4;

			int[] mConfigSpec  = {
					EGL10.EGL_RED_SIZE, 4,
					EGL10.EGL_GREEN_SIZE, 4,
					EGL10.EGL_BLUE_SIZE, 4,
					EGL10.EGL_ALPHA_SIZE, 4,
					EGL10.EGL_DEPTH_SIZE, 16,
					EGL10.EGL_RENDERABLE_TYPE, EGL_OPENGL_ES2_BIT,
					//EGL10.EGL_STENCIL_SIZE, 0,
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

			return configs[3];
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

	} // end of Qu

	public void start_quake2() {


		NativeLib engine = new NativeLib();

		NativeLib.loadLibraries(false);

		SSetup.load(getApplicationContext(),this);

		controlInterp = new QuakeControlInterpreter(engine,AppSettings.game,AppSettings.gamePadControlsFile,AppSettings.gamePadEnabled);

		QuakeTouchControlsSettings.setup(act, engine);
		QuakeTouchControlsSettings.loadSettings(act);
		QuakeTouchControlsSettings.sendToQuake();

		QuakeCustomCommands.setup(act, engine,getIntent().getStringExtra("main_qc"),getIntent().getStringExtra("mod_qc"));


		// Create our Preview view and set it as the content of our
		// Activity
		mGLSurfaceView = new QuakeView(this);

		mGLSurfaceView.setEGLContextClientVersion(2); // enable OpenGL 2.0

		mGLSurfaceView.setEGLConfigChooser( new NoSwapBestEglChooser(getApplicationContext()) );
		//mGLSurfaceView.setEGLConfigChooser( new QuakeEGLConfigChooser() );
		
		mGLSurfaceView.setRenderer(mRenderer);

		// This will keep the screen on, while your view is visible. 
		mGLSurfaceView.setKeepScreenOn(true);

		mGLSurfaceView.setPreserveEGLContextOnPause(true);
		
		ShowKeyboard.setup(act, mGLSurfaceView);

		setContentView(mGLSurfaceView);
		mGLSurfaceView.requestFocus();
		mGLSurfaceView.setFocusableInTouchMode(true);
		
		//mGLSurfaceView.getHolder().setFixedSize(1280/2, 720/2);

	}


	@Override
	protected void onPause() {
		Log.i( "Quake2.java", "onPause" );
		CDAudioPlayer.onPause();
		mGLSurfaceView.onPause();
		mogaController.onPause();
		super.onPause();
	}

	@Override
	protected void onResume() {

		Log.i( "Quake2.java", "onResume" );
		CDAudioPlayer.onResume();

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

	class QuakeView extends NoSwapGLSurfaceView {

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
			//Log.d(LOG,"ont touch "+ event.getX() + " " + event.getY());
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

	private static void init( int width, int height ){

		Log.i( LOG, "screen size : " + width + "x"+ height);

		AppSettings.setIntOption(act, "max_fps", 0);  

		NativeLib.setScreenSize(width,height);

		Utils.copyPNGAssets(act,AppSettings.graphicsDir);   


		Log.i(LOG, "Quake2Init start args = " + args);

		String[] args_array = Utils.creatArgs(args);


		int ret = NativeLib.init(AppSettings.graphicsDir,args_array,gamePath,act.getApplicationInfo().nativeLibraryDir);

		Log.i(LOG, "Quake2Init done");

	}

	

	///////////// GLSurfaceView.Renderer implementation ///////////

	class QuakeRenderer implements NoSwapGLSurfaceView.Renderer {



		public void onSurfaceCreated(GL10 gl, EGLConfig config) {

			Log.d("Renderer", "onSurfaceCreated");

		}


		private void init( int width, int height ){

			Log.i( LOG, "screen size : " + width + "x"+ height);

			NativeLib.setScreenSize(width,height);

			Utils.copyPNGAssets(getApplicationContext(),AppSettings.graphicsDir);   

			Log.i(LOG, "Quake2Init start args = " + args);

			String[] args_array = Utils.creatArgs(args);


			int ret = NativeLib.init(AppSettings.graphicsDir,args_array,gamePath,getApplicationInfo().nativeLibraryDir);

			Log.i(LOG, "Quake2Init done");

		}



		//// new Renderer interface
		int notifiedflags;

		boolean inited = false;

		private int counter_fps=0;
		private long tprint_fps= 0;

		public void onDrawFrame(GL10 gl) {

			if (!inited)
			{
				final int div = 2;
				AppSettings.setIntOption(getApplicationContext(), "max_fps", 0);  

				inited = true;
				init( surfaceWidth/div, surfaceHeight/div);
				
				handlerUI.post(new Runnable() {				
					@Override
					public void run() {
						mGLSurfaceView.getHolder().setFixedSize( surfaceWidth/div, surfaceHeight/div);					
					}
				});
			}

			//while (true)
			{
				NativeLib.frame();
				
				//Log.d(LOG,"onDrawFrame");
				long tnow = SystemClock.uptimeMillis(); 
				// compute FPS
				if ( (tnow-tprint_fps) >= 1000){

					Log.i(LOG,String.format( "FPS= %d",counter_fps));
					tprint_fps = tnow;
					counter_fps = 0;        	
				}			
				counter_fps ++;
			}

		}




		@SuppressLint("NewApi")
		public void onSurfaceChanged(GL10 gl, int width, int height) {
			Log.d("Renderer", String.format("onSurfaceChanged %dx%d", width,height) );

			
			Display display = ((WindowManager) act.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
			Point size = new Point();
			display.getSize(size);
			controlInterp.setScreenSize(size.x,size.y);

			surfaceWidth = width;
			surfaceHeight = height;
			runOnUiThread(new Runnable() {				
				@Override
				public void run() {
					//mGLSurfaceView.getHolder().setFixedSize(640, 480);				
				}
			});
		
		}


	} // end of QuakeRenderer


}


