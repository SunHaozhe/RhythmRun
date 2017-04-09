#include <string.h>
#include <jni.h>
#include "kiss_fft.h"
#include "kiss_fftr.h"

JNIEXPORT jstring JNICALL Java_com_telecom_1paristech_pact25_rhythmrun_music_Main2Activity_nativeTest(JNIEnv* env, jobject thiz)
{
    return (*env)->NewStringUTF(env, "Test1212");
}