package com.beloko.idtech.doom3;

import java.nio.ByteBuffer;

import android.app.Activity;
import android.util.Log;
import android.view.KeyEvent;

import com.beloko.idtech.QuakeControlInterface;

import com.beloko.libsdl.SDLLib;

public class NativeLib implements QuakeControlInterface{


	public static void loadLibraries(boolean gles2)
	{

		try {
			Log.i("JNI", "Trying to load libraries");

			System.loadLibrary("touchcontrols_gles2");
			//System.loadLibrary("openal");

			System.loadLibrary("openal");

			System.loadLibrary("ogg");
			System.loadLibrary("vorbis");
			System.loadLibrary("doom3");
		}
		catch (UnsatisfiedLinkError ule) {
			Log.e("JNI", "WARNING: Could not load shared library: " + ule.toString());
		}

	}

	public static native int init(String graphics_dir,String[] args,String game_path,String lib_path);

	public static native void setScreenSize( int width, int height );

	public static native int frame();

	public static native boolean touchEvent( int action, int pid, float x, float y);
	public static native void keypress(int down, int qkey, int unicode);
	public static native void doAction(int state, int action);
	public static native void analogFwd(float v);
	public static native void analogSide(float v);
	public static native void analogPitch(int mode,float v);
	public static native void analogYaw(int mode,float v);
	public static native void setTouchSettings(float alpha,float strafe,float fwd,float pitch,float yaw,int other);

	public static native void quickCommand(String command);


	public static native void startBackend();


	@Override
	public void quickCommand_if(String command)
	{
		quickCommand(command);
	}

	@Override
	public boolean touchEvent_if(int action, int pid, float x, float y) {
		return touchEvent(  action,  pid,  x,  y);
	}
	@Override
	public void keyPress_if(int down, int qkey, int unicode) {
		keypress(down,qkey,unicode);

	}
	@Override
	public void doAction_if(int state, int action) {
		doAction(state,action);
	} 

	@Override
	public void analogFwd_if(float v) {
		analogFwd(v);
	}
	@Override
	public void analogSide_if(float v) {
		analogSide(v);
	}
	@Override
	public void  analogPitch_if(int mode,float v)
	{
		analogPitch(mode,v);
	}
	@Override
	public void  analogYaw_if(int mode,float v)
	{
		analogYaw(mode,v);
	}

	@Override
	public void setTouchSettings_if(float alpha,float strafe, float fwd, float pitch,
			float yaw, int other) {
		setTouchSettings(alpha,strafe, fwd, pitch, yaw, other);

	}

	public static final int KEY_PRESS = 1;
	public static final int KEY_RELEASE = 0;


	public static final int K_TAB = 9;
	public static final int K_ENTER = 13;
	public static final int K_ESCAPE = 27;
	public static final int K_SPACE = 32;

	public static final int  K_BACKSPACE = 127;

	public  int mapKey(int acode,  int unicode)
	{
		switch(acode)
		{
		case KeyEvent.KEYCODE_TAB:
			return K_TAB;
		case KeyEvent.KEYCODE_DPAD_CENTER:
		case KeyEvent.KEYCODE_ENTER:
			return K_ENTER;
		case KeyEvent.KEYCODE_ESCAPE:
		case KeyEvent.KEYCODE_BACK:
			return K_ESCAPE;
		case KeyEvent.KEYCODE_SPACE:
			return K_SPACE;
		case KeyEvent.KEYCODE_DEL:
			return K_BACKSPACE;
		default:
			if (unicode < 128)
				return Character.toLowerCase(unicode);
		}
		return 0;
	} 
}
