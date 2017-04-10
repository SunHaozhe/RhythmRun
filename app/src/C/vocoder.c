#include <string.h>
#include <jni.h>
#include <math.h>
#include "kiss_fft.h"
#include "kiss_fftr.h"
#include "_kiss_fft_guts.h"

#define module(x) sqrt(x.r*x.r+x.i*x.i)

float angle(kiss_fft_cpx x);

JNIEXPORT jstring JNICALL Java_com_telecom_1paristech_pact25_rhythmrun_music_Main2Activity_nativeTest(JNIEnv* env, jobject thiz)
{
    return (*env)->NewStringUTF(env, "Test1212");
}

int buffer_size;
int taille_fenetre;
int decalage_in;
int decalage_out;
int ta, ts;
float *wa;
float *buffer_synthese;
float *buffer_temp;//pour mettre ce qui depasse
int len_signal_in;
float *signal_in;
float *last_phase_in;
float *last_phase_out;
kiss_fft_cpx *fenetre_in;
kiss_fft_cpx *tf_fenetre_in;
kiss_fft_cpx *tf_fenetre_out;
kiss_fft_cpx *fenetre_out;
float Q, nu, f, phi, m;
int fenetres_requises;
kiss_fftr_cfg fft_directe;
kiss_fftr_cfg fft_inverse;
int k, i;
/*size_t memneeded;
void *mem;*/

JNIEXPORT void JNICALL Java_com_telecom_1paristech_pact25_rhythmrun_music_phase_1vocoder_NativeVocoder_initVocoder(JNIEnv* env, jobject thiz,
    jobject jsignal, jint jbuffer_size, jint jtaille_fenetre, jint jdecalage_in)
{
    signal_in = (float*)(*env)->GetDirectBufferAddress(env, jsignal);

    ta = 0;
    buffer_size = (int)jbuffer_size;
    taille_fenetre = (int)jtaille_fenetre;
    decalage_in = (int)jdecalage_in;
    int k;
    wa = malloc(taille_fenetre*sizeof(float));
	    for(k=0; k<taille_fenetre; k++)	wa[k] = 0.5*(1-cos((2*M_PI*k)/((float)taille_fenetre)));
	last_phase_in = malloc(taille_fenetre*sizeof(float));
	    for(k=0; k<taille_fenetre; k++)	last_phase_in[k] = 0;
	last_phase_out = malloc(taille_fenetre*sizeof(float));
	    for(k=0; k<taille_fenetre; k++)	last_phase_out[k] = 0;
	fenetre_in = malloc(taille_fenetre*sizeof(float));
	tf_fenetre_in = malloc(taille_fenetre*sizeof(kiss_fft_cpx));
    tf_fenetre_out = malloc(taille_fenetre*sizeof(kiss_fft_cpx));
	fenetre_out = malloc(taille_fenetre*sizeof(float));
	buffer_temp = malloc(taille_fenetre*sizeof(float));
	    for(k=0; k<taille_fenetre; k++)	buffer_temp[k] = 0;

    /*size_t subsize;
    kiss_fft_alloc (taille_fenetre, 0, NULL, &subsize);
	memneeded = sizeof(struct kiss_fftr_state) + subsize + sizeof(kiss_fft_cpx)*(taille_fenetre*3/2);
	mem = KISS_FFT_MALLOC(memneeded);
	fft_directe = NULL;
	fft_directe = kiss_fftr_alloc(taille_fenetre, 0, mem, &memneeded);
	if (fft_directe == NULL) return 42;
	fft_inverse = kiss_fftr_alloc(taille_fenetre, 1, mem, &memneeded);*/
	fft_directe = kiss_fft_alloc(taille_fenetre, 0, NULL, NULL);
	fft_inverse = kiss_fft_alloc(taille_fenetre, 1, NULL, NULL);
}

JNIEXPORT void JNICALL Java_com_telecom_1paristech_pact25_rhythmrun_music_phase_1vocoder_NativeVocoder_freeVocoder(JNIEnv* env, jobject thiz)
{
    free(wa);
    free(last_phase_in);
    free(last_phase_out);
    free(fenetre_in);
    free(tf_fenetre_in);
    free(tf_fenetre_out);
    free(fenetre_out);
    free(fft_directe);
    free(fft_inverse);
    free(buffer_temp);
    //free(mem);
}

JNIEXPORT jint JNICALL Java_com_telecom_1paristech_pact25_rhythmrun_music_phase_1vocoder_NativeVocoder_nextVocoder(JNIEnv* env, jobject thiz,
    jobject jbuffer_synthese, jint jdecalage_out, jint jts, jint jfenetres_requises)
{//retourne 1 si c'est fini
    buffer_synthese = (float*)(*env)->GetDirectBufferAddress(env, jbuffer_synthese);
    ts = (int)jts;
    decalage_out = (int)jdecalage_out;
    fenetres_requises = (int) jfenetres_requises;
    for(k=0; k<taille_fenetre; k++)
    		{
    			buffer_synthese[k] = buffer_temp[k];
    		}
    for(k=taille_fenetre; k<buffer_size+taille_fenetre; k++)
    		{
    			buffer_synthese[k] = 0;
    		}
    		ta = 0;
    for(k=0;k<fenetres_requises;k++)
    {
    			for(i=0; i<taille_fenetre; i++)
    			{
    				fenetre_in[i].r = wa[i]*(signal_in[ta+i]);
    				fenetre_in[i].i = 0;
    			}
    			kiss_fft(fft_directe, fenetre_in, tf_fenetre_in);

    			for(i=0;i<taille_fenetre;i++)
    			{
    				nu = ((float)i)/taille_fenetre;
    				phi = angle(tf_fenetre_in[i]);
    				Q = phi-last_phase_in[i]-2*M_PI*nu*decalage_in;
    				Q -= 2*M_PI*(float)floor((double)(Q/(2*M_PI)));
    				if(Q>M_PI) Q -= 2*M_PI;
    				f = nu+Q/(2*M_PI*decalage_in);
    				last_phase_out[i] += 2*M_PI*f*decalage_out;
    				m = module(tf_fenetre_in[i]);
    				tf_fenetre_out[i].r = m*(float)cos((double)last_phase_out[i]);
    				tf_fenetre_out[i].i = m*(float)sin((double)last_phase_out[i]);
    				last_phase_in[i] = phi;
    			}
    			kiss_fft(fft_inverse, tf_fenetre_out, fenetre_out);
    			for(i=0; i<taille_fenetre; i++)
    			{
    				buffer_synthese[ts+i] += (fenetre_out[i].r*wa[i])/taille_fenetre;
    			}
    			ta += decalage_in;
    			ts += decalage_out;
    	}
    		ts %= buffer_size;
    		for(k=0;k<taille_fenetre-decalage_in;k++)
    		{
    			signal_in[k] = signal_in[k+ta];
    		}
    		for(k=taille_fenetre-decalage_in;k<len_signal_in;k++) //on pourrait s'en passer en rajoutant un argument a la fonction...
    		{
    			signal_in[k] = 0;
    		}
    for(k=0;k<taille_fenetre;k++)
    {
        buffer_temp[k] = buffer_synthese[buffer_size+k];
    }
    return ts;
}

float angle(kiss_fft_cpx x)
{
	if(x.r == 0) //licite
	{
		return (x.i<0)?M_PI:0;
	}
	if(x.r>0)
	{
		return (float)atan((double)(x.i/x.r));
	}
	return M_PI+(float)atan((double)(x.i/x.r));
}

JNIEXPORT void JNICALL Java_com_telecom_1paristech_pact25_rhythmrun_music_phase_1vocoder_NativeVocoder_test(JNIEnv* env, jobject thiz,
    jobject jbuffer_out)//, jobject jbuffer_in
{
    //float *buffer_in = (float*)(*env)->GetDirectBufferAddress(env, jbuffer_in);
    float *buffer_out = (float*)(*env)->GetDirectBufferAddress(env, jbuffer_out);
    buffer_out[0] = signal_in[0];//buffer_in[0];
}