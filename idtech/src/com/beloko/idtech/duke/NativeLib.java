package com.beloko.idtech.duke;

import android.util.Log;
import android.view.KeyEvent;

import com.beloko.idtech.QuakeControlInterface;
import com.beloko.idtech.duke.Game.QuakeView;
import com.beloko.libsdl.SDLLib;

public class NativeLib implements QuakeControlInterface{

	public static final int REND_SOFT = 0;
	public static final int REND_GL = 1;

	public static void loadLibraries(boolean gles2)
	{

		try {
			Log.i("JNI", "Trying to load duke.so");
			System.loadLibrary("touchcontrols");
			SDLLib.loadSDL();
			System.loadLibrary("duke");
		}
		catch (UnsatisfiedLinkError ule) {
			Log.e("JNI", "WARNING: Could not load prboom.so: " + ule.toString());
		}

	}

	public static native int init(String graphics_dir,int mem,String[] args,int game,String path);

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


	public static final int  KEYSC_ESC	  = 0x01;
	public static final int  KEYSC_1  	  = 0x02;
	public static final int  KEYSC_2  	  = 0x03;
	public static final int  KEYSC_3  	  = 0x04;
	public static final int  KEYSC_4  	  = 0x05;
	public static final int  KEYSC_5  	  = 0x06;
	public static final int  KEYSC_6  	  = 0x07;
	public static final int  KEYSC_7  	  = 0x08;
	public static final int  KEYSC_8  	  = 0x09;
	public static final int  KEYSC_9  	  = 0x0a;
	public static final int  KEYSC_0  	  = 0x0b;
	public static final int  KEYSC_DASH     = 0x0c;
	public static final int  KEYSC_EQUAL    = 0x0d;

	public static final int  KEYSC_BS 	  = 0x0e;
	public static final int  KEYSC_TAB	  = 0x0f;
	public static final int  KEYSC_Q  	  = 0x10;
	public static final int  KEYSC_W  	  = 0x11;
	public static final int  KEYSC_E  	  = 0x12;
	public static final int  KEYSC_R  	  = 0x13;
	public static final int  KEYSC_T  	  = 0x14;
	public static final int  KEYSC_Y  	  = 0x15;
	public static final int  KEYSC_U  	  = 0x16;
	public static final int  KEYSC_I  	  = 0x17;
	public static final int  KEYSC_O  	  = 0x18;
	public static final int  KEYSC_P  	  = 0x19;
	public static final int  KEYSC_LBRACK   = 0x1a;
	public static final int  KEYSC_RBRACK   = 0x1b;
	public static final int  KEYSC_ENTER    = 0x1c;
	;
	public static final int  KEYSC_LCTRL    = 0x1d;
	public static final int  KEYSC_A  	  = 0x1e;
	public static final int  KEYSC_S  	  = 0x1f;
	public static final int  KEYSC_D  	  = 0x20;
	public static final int  KEYSC_F  	  = 0x21;
	public static final int  KEYSC_G  	  = 0x22;
	public static final int  KEYSC_H  	  = 0x23;
	public static final int  KEYSC_J  	  = 0x24;
	public static final int  KEYSC_K  	  = 0x25;
	public static final int  KEYSC_L  	  = 0x26;
	public static final int  KEYSC_SEMI     = 0x27;
	public static final int  KEYSC_QUOTE    = 0x28;
	public static final int  KEYSC_BQUOTE   = 0x29;
	public static final int  KEYSC_TILDE    = 0x29;

	public static final int  KEYSC_LSHIFT   = 0x2a;
	public static final int  KEYSC_BSLASH   = 0x2b;
	public static final int  KEYSC_Z  	  = 0x2c;
	public static final int  KEYSC_X  	  = 0x2d;
	public static final int  KEYSC_C  	  = 0x2e;
	public static final int  KEYSC_V  	  = 0x2f;
	public static final int  KEYSC_B  	  = 0x30;
	public static final int  KEYSC_N  	  = 0x31;
	public static final int  KEYSC_M  	  = 0x32;
	public static final int  KEYSC_COMMA    = 0x33;
	public static final int  KEYSC_PERIOD   = 0x34;
	public static final int  KEYSC_SLASH    = 0x35;
	public static final int  KEYSC_RSHIFT   = 0x36;
	public static final int  KEYSC_gSTAR    = 0x37;

	public static final int  KEYSC_LALT     = 0x38;
	public static final int  KEYSC_SPACE    = 0x39;
	public static final int  KEYSC_CAPS     = 0x3a;

	public static final int  KEYSC_F1 	  = 0x3b;
	public static final int  KEYSC_F2 	  = 0x3c;
	public static final int  KEYSC_F3 	  = 0x3d;
	public static final int  KEYSC_F4 	  = 0x3e;
	public static final int  KEYSC_F5 	  = 0x3f;
	public static final int  KEYSC_F6 	  = 0x40;
	public static final int  KEYSC_F7 	  = 0x41;
	public static final int  KEYSC_F8 	  = 0x42;
	public static final int  KEYSC_F9 	  = 0x43;
	public static final int  KEYSC_F10	  = 0x44;
	;
	public static final int  KEYSC_gNUM     = 0x45;
	public static final int  KEYSC_SCROLL   = 0x46;

	public static final int  KEYSC_gHOME    = 0x47;
	public static final int  KEYSC_gUP	  = 0x48;
	public static final int  KEYSC_gPGUP    = 0x49;
	public static final int  KEYSC_gMINUS   = 0x4a;
	public static final int  KEYSC_gLEFT    = 0x4b;
	public static final int  KEYSC_gKP5     = 0x4c;
	public static final int  KEYSC_gRIGHT   = 0x4d;
	public static final int  KEYSC_gPLUS    = 0x4e;
	public static final int  KEYSC_gEND     = 0x4f;
	public static final int  KEYSC_gDOWN    = 0x50;
	public static final int  KEYSC_gPGDN    = 0x51;
	public static final int  KEYSC_gINS     = 0x52;
	public static final int  KEYSC_gDEL     = 0x53;

	public static final int  KEYSC_F11    = 0x57;
	public static final int  KEYSC_F12    = 0x58;

	public static final int  KEYSC_gENTER   = 0x9C;
	public static final int  KEYSC_RCTRL    = 0x9D;
	public static final int  KEYSC_gSLASH   = 0xB5;
	public static final int  KEYSC_RALT     = 0xB8;
	public static final int  KEYSC_PRTSCN   = 0xB7;
	public static final int  KEYSC_PAUSE    = 0xC5;
	public static final int  KEYSC_HOME     = 0xC7;
	public static final int  KEYSC_UP 	  = 0xC8;
	public static final int  KEYSC_PGUP     = 0xC9;
	public static final int  KEYSC_LEFT     = 0xCB;
	public static final int  KEYSC_RIGHT    = 0xCD;
	public static final int  KEYSC_END	  = 0xCF;
	public static final int  KEYSC_DOWN     = 0xD0;
	public static final int  KEYSC_PGDN     = 0xD1;
	public static final int  KEYSC_INSERT   = 0xD2;
	public static final int  KEYSC_DELETE   = 0xD3;




	public  int mapKey(int acode,  int unicode)
	{
		switch(acode)
		{
		case KeyEvent.KEYCODE_TAB:
			return KEYSC_TAB;
		case KeyEvent.KEYCODE_DPAD_CENTER:
		case KeyEvent.KEYCODE_ENTER:
			return KEYSC_ENTER;
		case KeyEvent.KEYCODE_ESCAPE:
		case KeyEvent.KEYCODE_BACK:
			return KEYSC_ESC;
		case KeyEvent.KEYCODE_SPACE:
			return KEYSC_SPACE;
		case KeyEvent.KEYCODE_DEL:
			return KEYSC_DELETE;
		case KeyEvent.KEYCODE_DPAD_UP:
			return KEYSC_UP;
		case KeyEvent.KEYCODE_DPAD_DOWN:
			return KEYSC_DOWN;
		case KeyEvent.KEYCODE_DPAD_LEFT:
			return KEYSC_LEFT;
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			return KEYSC_RIGHT;
		case KeyEvent.KEYCODE_ALT_LEFT:
			return KEYSC_LALT;
		case KeyEvent.KEYCODE_ALT_RIGHT:
			return KEYSC_RALT;
		case KeyEvent.KEYCODE_CTRL_LEFT:
			return KEYSC_LCTRL;
		case KeyEvent.KEYCODE_CTRL_RIGHT:
			return KEYSC_RCTRL;
		case KeyEvent.KEYCODE_SHIFT_LEFT:
			return KEYSC_LSHIFT;
		case KeyEvent.KEYCODE_SHIFT_RIGHT:
			return KEYSC_RSHIFT;
		case KeyEvent.KEYCODE_F1:
			return KEYSC_RSHIFT;
		case KeyEvent.KEYCODE_F2:
			return KEYSC_F2;
		case KeyEvent.KEYCODE_F3:
			return KEYSC_F3;
		case KeyEvent.KEYCODE_F4:
			return KEYSC_F4;
		case KeyEvent.KEYCODE_F5:
			return KEYSC_F5;
		case KeyEvent.KEYCODE_F6:
			return KEYSC_F6;
		case KeyEvent.KEYCODE_F7:
			return KEYSC_F7;
		case KeyEvent.KEYCODE_F8:
			return KEYSC_F8;
		case KeyEvent.KEYCODE_F9:
			return KEYSC_F9;
		case KeyEvent.KEYCODE_F10:
			return KEYSC_F10;
		case KeyEvent.KEYCODE_F11:
			return KEYSC_F11;
		case KeyEvent.KEYCODE_F12:
			return KEYSC_F12;	
		case KeyEvent.KEYCODE_FORWARD_DEL:
			return KEYSC_gDEL;
		case KeyEvent.KEYCODE_INSERT:
			return KEYSC_INSERT;
		case KeyEvent.KEYCODE_PAGE_UP:
			return KEYSC_PGUP;
		case KeyEvent.KEYCODE_PAGE_DOWN:
			return KEYSC_PGDN;
		case KeyEvent.KEYCODE_MOVE_HOME:
			return KEYSC_HOME;
		case KeyEvent.KEYCODE_MOVE_END:
			return KEYSC_END;
		case KeyEvent.KEYCODE_BREAK:
			return KEYSC_PAUSE;
		case KeyEvent.KEYCODE_0:
			return KEYSC_0;
		case KeyEvent.KEYCODE_1:
			return KEYSC_1;
		case KeyEvent.KEYCODE_2:
			return KEYSC_2;
		case KeyEvent.KEYCODE_3:
			return KEYSC_3;
		case KeyEvent.KEYCODE_4:
			return KEYSC_4;
		case KeyEvent.KEYCODE_5:
			return KEYSC_5;
		case KeyEvent.KEYCODE_6:
			return KEYSC_6;
		case KeyEvent.KEYCODE_7:
			return KEYSC_7;
		case KeyEvent.KEYCODE_8:
			return KEYSC_8;
		case KeyEvent.KEYCODE_9:
			return KEYSC_9;
		case KeyEvent.KEYCODE_A:
			return KEYSC_A;
		case KeyEvent.KEYCODE_B:
			return KEYSC_B;
		case KeyEvent.KEYCODE_C:
			return KEYSC_C;
		case KeyEvent.KEYCODE_D:
			return KEYSC_D;
		case KeyEvent.KEYCODE_E:
			return KEYSC_E;
		case KeyEvent.KEYCODE_F:
			return KEYSC_F;
		case KeyEvent.KEYCODE_G:
			return KEYSC_G;
		case KeyEvent.KEYCODE_H:
			return KEYSC_H;
		case KeyEvent.KEYCODE_I:
			return KEYSC_I;
		case KeyEvent.KEYCODE_J:
			return KEYSC_J;
		case KeyEvent.KEYCODE_K:
			return KEYSC_K;
		case KeyEvent.KEYCODE_L:
			return KEYSC_L;
		case KeyEvent.KEYCODE_M:
			return KEYSC_M;
		case KeyEvent.KEYCODE_N:
			return KEYSC_N;
		case KeyEvent.KEYCODE_O:
			return KEYSC_O;
		case KeyEvent.KEYCODE_P:
			return KEYSC_P;
		case KeyEvent.KEYCODE_Q:
			return KEYSC_Q;
		case KeyEvent.KEYCODE_R:
			return KEYSC_R;
		case KeyEvent.KEYCODE_S:
			return KEYSC_S;
		case KeyEvent.KEYCODE_T:
			return KEYSC_T;
		case KeyEvent.KEYCODE_U:
			return KEYSC_U;
		case KeyEvent.KEYCODE_V:
			return KEYSC_V;
		case KeyEvent.KEYCODE_W:
			return KEYSC_W;
		case KeyEvent.KEYCODE_X:
			return KEYSC_X;
		case KeyEvent.KEYCODE_Y:
			return KEYSC_Y;
		case KeyEvent.KEYCODE_Z:
			return KEYSC_Z;



		default:
			if (unicode < 128)
				return Character.toLowerCase(unicode);
		}
		return 0;
	} 

	private static final Object threadLock = new Object();
	private static boolean paused = false;

	public static QuakeView qv;
	

	static void swapBuffers()
	{
		boolean canDraw = false;
		do
		{
			qv.swapBuffers();
			canDraw = qv.setupSurface();
		}while (!canDraw);
	}
}
