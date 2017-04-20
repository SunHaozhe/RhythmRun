package com.telecom_paristech.pact25.rhythmrun.music.phase_vocoder.test_phase_vocoder;

import com.telecom_paristech.pact25.rhythmrun.Android_activities.Song;

import org.apache.commons.math3.complex.Complex;

import java.util.ArrayList;

import static com.telecom_paristech.pact25.rhythmrun.music.phase_vocoder.FastFourierTransform.fft;
import static com.telecom_paristech.pact25.rhythmrun.music.phase_vocoder.FastFourierTransform.fftReversed;

/**
 * Created by Raphael Attali on 14/04/2017.
 * <p>
 * Project RythmRun
 */

public class Vocoder {
    
  


//penser a s'occuper de l'amplitude (pas important pour des petits ratios)

    /**
     * Renvoie un tableau avec la i-èmem case égale à 0,5*(1-cos(2*pi*i)/N)
     * @param N voir formule
     * @return le tableau
     */
    private double[] hanning(int N){
        double[] tab = new double[N];
        for (int i = 0; i<N ; i++) tab[i] = 0.5*(1-Math.cos((2*Math.PI*i)/N));
        return tab;
    }


    /**
     * Copie le tableau de décimal et le renvoie sous la forme d'un tableau d'entiers
     * @param tabDouble
     * @return le tableau d'entiers correspondant
     */
    private int[] copie(double[] tabDouble){
        int tabLength = tabDouble.length;
        int[] tabInt = new int[tabLength];
        for (int i = 0 ; i<tabLength ; i++) tabInt[i] = (int) tabDouble[i];
        return tabInt;
    }

    private int rate = 44100;
    private int buffer_time = 1; //on traite le morceau par intervalles de 1s (0.5 serait mieux en java parce qu'on calcule 2 buffers a l'avance, mais on peut changer)
    private int buffer_size = rate*buffer_time;

    private class SpeedChanger {


        
        private double ratio = 0;
        private double ratioMin = 0;
        private double ratioMax = 0;
        private double ratioTemp = 0;
        private int shiftIn, shiftOut, shiftOutMin;
        private String path;
        private int indexNumberInWav, windowSize, ta, ts, len_signal_in;
        private boolean song_ended;
        private double[] wa, lastPhaseIn, lastPhaseOut, windowIn;
        private Complex[] tfWindowOut;
        private int[] bufferSynthesis, signal;

        /**
         * CONSTRUCTOR
         */
        public SpeedChanger() {

            //TODO : Valeurs à modifier
            this.ratio =2.0;
            this.ratioMin =0.5;
            this.ratioMax =2;
            this.ratioTemp = 0;
            this.path ="guitare_mono_70bpm.wav";
            this.indexNumberInWav =0;

        //TODO : gérer mieux le WavFile
            (this.sr,this.signal_in)=wavfile.read(this.path);
            this.len_signal_in =  this.signal_in.length;

            this.song_ended = false;

            
            this.windowSize =1024; //puissance de 2 pour les fft, 1024 semble le mieux en qualite
        
            this.shiftIn =this.windowSize/4; //au max 1/4 (1/8=>mieux mais plus de calculs; windowSize est un multiple de 4 donc pas besoin de reflechir a des problemes comme ca)
            this.shiftOut = (int) (this.shiftIn/this.ratio);

            this.ta =0;
             this.ts =0;
             this.wa = hanning(this.windowSize); //tout le monde utilise hann

            this.bufferSynthesis = new int[buffer_size + this.windowSize]; // Tous les éléments sont à 0 
            //on prevoie windowSize en plus parce qu'on va depasser. A la fin du vocodage il faut donc recopier ce qui depasse au debut du prochain bufferSynthesis.
            
            shiftOutMin = (int) (this.shiftIn/this.ratioMax); //variable temporaire pour le calcul de la ligne suivante

            this.signal = new int[(int)(((buffer_size-1)/shiftOutMin)*this.shiftIn+this.windowSize)];
            //on y mettra le signal en entree. On met la taille maximale dont on peut avoir besoin en fonction du ratioMax.
            
            // TODO : modifier get_next_int pour que ça marche 
            this.m_get_next_sig(this.signal,this.windowSize-this.shiftIn,0);
            /*à chaque fois qu'on voudra calculer un buffer de 1s, on mettre dans signal des donnees qui serviront 
            la prochaine fois (car les fenetres se superposent), et on recopie ce petit bout au debut. 
            On met ici artificielement un petit bout dans signal pour ne pas avoir a implementer un 
            comportement different selon que c'est le tout premier buffer ou pas.
             */
            
        this.lastPhaseIn = new double[this.windowSize];
        this.lastPhaseOut = new double[this.windowSize];
        this.windowIn = new double[this.windowSize];
        this.tfWindowOut = new Complex[this.windowSize];
    }

        private void m_get_next_sig(int[] buffer, int n, int n0){
            if(this.indexNumberInWav+n >=this.len_signal_in) {
                this.song_ended = true; //si on a tout lu
                for (int i = this.indexNumberInWav ; i < this.len_signal_in ; i++)
                    buffer[n0 + i - this.indexNumberInWav] = (float) (this.signal_in[i]); //float pour les calculs
            }
		else
		    {
            for (int i = this.indexNumberInWav ; i < this.indexNumberInWav+n ; i++)
                buffer[n0+i-this.indexNumberInWav]= (float) (this.signal_in[i]);
		    }

            this.indexNumberInWav +=n;
        }

        private boolean m_song_ended(){return song_ended;}

        //def m_get_wav(this): //pour les tests
        //        return this.sr,this.signal_in

        /**
         * Retourne la prochaine seconde traitée avec le vocodeur
         * @return un tableau contenant ce qu'il y a à lire la prochaine seconde
         */
        private double[] m_next_buffer(){
            this.ratio =this.ratioTemp; //on met a jour le ratio
            this.shiftOut = (int) (this.shiftIn/this.ratio); //si le ratio change
            if(this.m_song_ended())
            {
                return new double[buffer_size]; // Bien ?
            }
            for (int i = buffer_size ; i < buffer_size + this.windowSize ; i++)
                this.bufferSynthesis[i-buffer_size]=this.bufferSynthesis[i];//on recopie le bout qui depassait au debut

            for (int i = windowSize ; i < buffer_size + this.windowSize ; i++)
                this.bufferSynthesis[i]=0; //Puis on met le reste à 0
            this.ta =0;

            int fenetres_requises = (int) ((buffer_size+this.shiftOut-1-this.ts)/ this.shiftOut); //de combien de fenetres a-t-on besoin
            // TODO : Vérifier que la division ci-dessus renvoie bien un entier

            this.m_get_next_sig(this.signal,(int)((fenetres_requises-1)*this.shiftIn+this.windowSize-(this.windowSize-this.shiftIn)),(int)(this.windowSize-this.shiftIn));
            double Q =0;
            for (int k = 0;  k<fenetres_requises ; k++)
            {
                for (int i = 0; i< windowSize ; i++)
                    this.windowIn[i]=this.signal[this.ta+i]*this.wa[i] //fenetrage


                Complex[] tf_windowIn = fft(this.windowIn);
                for (int i = 0; i< windowSize ; i++)
                    this.tfWindowOut[i]= new Complex(tf_windowIn[i].abs(),0);
                for (int i = 0; i< windowSize ; i++)
                {
                    float nu_i =  (float) i / this.windowSize;

                    Q = (tf_windowIn[i].getArgument() - this.lastPhaseIn[i] - 2 * Math.PI * nu_i * this.shiftIn);
                    Q %=2*Math.PI; // si ça ne marche pas, essayer Q -= 2*pi*floor((Q/(2*pi)))
                    if(Q >Math.PI) //on se met dans -pi pi
                        Q -=2*Math.PI;

                    double f_i = nu_i + Q / (2 * Math.PI * this.shiftIn);
                    this.lastPhaseOut[i]=this.lastPhaseOut[i]+2*Math.PI*f_i*this.shiftOut;


                    this.tfWindowOut[i]= tfWindowOut[i].multiply(new Complex(0,this.lastPhaseOut[i])).exp();
                    this.lastPhaseIn[i]=tf_windowIn[i].getArgument();
                }


                Complex[] windowOutComplex = fft(fftReversed(this.tfWindowOut)); // TODO : résoudre le problème de types
                double[] windowOut = new double[this.windowSize];
                for (int i = 0 ; i < this.windowSize ; i++)
                    this.bufferSynthesis[this.ts+i]+=this.wa[i]*windowOut[i];
                this.ta +=this.shiftIn;
                this.ts +=this.shiftOut;
            }

            //on prepare le prochain traitement du signal
            this.ts %=buffer_size;
            for (int k = this.ta ; k < this.ta + this.windowSize - this.shiftIn)
                this.signal[k-this.ta]=this.signal[k];
            for (int k = this.windowSize-this.shiftIn ; k < this.signal.length ; k++)
                this.signal[k]=0;

            return copie(this.bufferSynthesis) //on l'evitera en java (on utilisera plutot une poignee de buffers alloues pendant l'initialisation et on les fera tourner)

        }

        /**
         * Modifie le ratio en temps réel
         * @param ratio nouveau ratio
         * @return False si le ratio n'a pas été modifié, true sinon
         */
        public boolean m_set_ratio(double ratio){
            if(ratio>=this.ratioMin && ratio<=this.ratioMax)
            {
                this.ratioTemp =ratio;
                return true;
            }
            return false;
        }
    }
}
