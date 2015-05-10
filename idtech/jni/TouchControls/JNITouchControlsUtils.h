
#ifdef __cplusplus
extern "C"
{
#endif
#include <jni.h>

//void setUIJNIEnv( JNIEnv* env);

void setTCJNIEnv( JavaVM* jvm);


void showCustomCommands();
void showTouchSettings();
void showEditButtons();


void toggleKeyboard();
void showKeyboard(int val);

void ChangeDroidMusic(int action,int param1,int param2);

void setControlsContainer(touchcontrols::TouchControlsContainer * cc);

JNIEnv * getEnv();

#ifdef __cplusplus
}
#endif