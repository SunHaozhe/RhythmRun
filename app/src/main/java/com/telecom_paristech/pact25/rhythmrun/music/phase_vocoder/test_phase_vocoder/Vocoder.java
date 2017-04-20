package com.telecom_paristech.pact25.rhythmrun.music.phase_vocoder.test_phase_vocoder;

import com.telecom_paristech.pact25.rhythmrun.music.waveFileReaderLib.WavFile;

import org.apache.commons.math3.complex.Complex;

import java.io.File;

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
     *
     * @param N voir formule
     * @return le tableau
     */
    private double[] hanning(int N) {
        double[] tab = new double[N];
        for (int i = 0; i < N; i++) tab[i] = 0.5 * (1 - Math.cos((2 * Math.PI * i) / N));
        return tab;
    }


    /**
     * Copie le tableau de décimal et le renvoie sous la forme d'un tableau d'entiers
     *
     * @param tabDouble
     * @return le tableau d'entiers correspondant
     */
    private int[] copie(double[] tabDouble) {
        int tabLength = tabDouble.length;
        int[] tabInt = new int[tabLength];
        for (int i = 0; i < tabLength; i++) tabInt[i] = (int) tabDouble[i];
        return tabInt;
    }

    private int rate = 44100;
    private int buffer_time = 1; //on traite le morceau par intervalles de 1s (0.5 serait mieux en java parce qu'on calcule 2 buffers a l'avance, mais on peut changer)
    private int buffer_size = rate * buffer_time;

    private double ratio = 0;
    private double ratioMin = 0;
    private double ratioMax = 0;
    private double ratioTemp = 0;
    private int shiftIn, shiftOut, shiftOutMin;
    private String path;
    private int indexNumberInWav, windowSize, ta, ts, len_signal_in;
    private boolean song_ended;
    private double[] wa, lastPhaseIn, lastPhaseOut, windowIn, signal_in, bufferSynthesis;
    private Complex[] tfWindowOut;
    private int[] signal;

    /**
     * CONSTRUCTOR
     */
    public Vocoder(String path) {

        //TODO : Valeurs à modifier
        ratio = 2.0;
        ratioMin = 0.5;
        ratioMax = 2;
        ratioTemp = 0;
        this.path = path;
        indexNumberInWav = 0;

        //TODO : gérer mieux le WavFile
        WavFile wav = null;
        try {
            wav = WavFile.openWavFile(new File(path));
        } catch (Exception e) {
            e.printStackTrace();
        }

        signal_in = wav.read(this.path);
        this.len_signal_in = signal_in.length;

        song_ended = false;


        windowSize = 1024; //puissance de 2 pour les fft, 1024 semble le mieux en qualite

        shiftIn = windowSize / 4; //au max 1/4 (1/8=>mieux mais plus de calculs; windowSize est un multiple de 4 donc pas besoin de reflechir a des problemes comme ca)
        shiftOut = (int) (shiftIn / ratio);

        this.ta = 0;
        this.ts = 0;
        this.wa = hanning(windowSize); //tout le monde utilise hann

        this.bufferSynthesis = new double[buffer_size + windowSize]; // Tous les éléments sont à 0
        //on prevoie windowSize en plus parce qu'on va depasser. A la fin du vocodage il faut donc recopier ce qui depasse au debut du prochain bufferSynthesis.

        shiftOutMin = (int) (shiftIn / ratioMax); //variable temporaire pour le calcul de la ligne suivante

        signal = new int[(((buffer_size - 1) / shiftOutMin) * shiftIn + windowSize)];
        //on y mettra le signal en entree. On met la taille maximale dont on peut avoir besoin en fonction du ratioMax.

        // TODO : modifier findNextSignal pour que ça marche
        this.findNextSignal(signal, windowSize - shiftIn, 0);
            /*à chaque fois qu'on voudra calculer un buffer de 1s, on mettre dans signal des donnees qui serviront 
            la prochaine fois (car les fenetres se superposent), et on recopie ce petit bout au debut. 
            On met ici artificielement un petit bout dans signal pour ne pas avoir a implementer un 
            comportement different selon que c'est le tout premier buffer ou pas.
             */

        this.lastPhaseIn = new double[windowSize];
        this.lastPhaseOut = new double[windowSize];
        windowIn = new double[windowSize];
        this.tfWindowOut = new Complex[windowSize];
    }

    private void findNextSignal(double[] buffer, int n, int n0) {
        if (indexNumberInWav + n >= this.len_signal_in) {
            song_ended = true; //si on a tout lu
            for (int i = indexNumberInWav; i < this.len_signal_in; i++)
                buffer[n0 + i - indexNumberInWav] = (float) (signal_in[i]); //double pour les calculs
        } else {
            for (int i = indexNumberInWav; i < indexNumberInWav + n; i++)
                buffer[n0 + i - indexNumberInWav] = (float) (signal_in[i]);
        }

        indexNumberInWav += n;
    }

    /**
     * Determine si le morceau est fini ou non
     *
     * @return true si on a fini de jouer le morceau, false sinon
     */
    private boolean hasSongEnded() {
        return song_ended;
    }

    //def m_get_wav(this): //pour les tests
    //        return this.sr,signal_in

    /**
     * Retourne la prochaine seconde traitée avec le vocodeur
     *
     * @return un tableau contenant ce qu'il y a à lire la prochaine seconde
     */
    private double[] m_next_buffer() {
        ratio = ratioTemp; //on met a jour le ratio
        shiftOut = (int) (shiftIn / ratio); //si le ratio change
        if (this.hasSongEnded()) {
            return new double[buffer_size]; // Bien ?
        }
        for (int i = buffer_size; i < buffer_size + windowSize; i++)
            this.bufferSynthesis[i - buffer_size] = this.bufferSynthesis[i];//on recopie le bout qui depassait au debut

        for (int i = windowSize; i < buffer_size + windowSize; i++)
            this.bufferSynthesis[i] = 0; //Puis on met le reste à 0
        this.ta = 0;

        int fenetres_requises = (int) ((buffer_size + shiftOut - 1 - this.ts) / shiftOut); //de combien de fenetres a-t-on besoin
        // TODO : Vérifier que la division ci-dessus renvoie bien un entier

        this.findNextSignal(signal, (int) ((fenetres_requises - 1) * shiftIn + windowSize - (windowSize - shiftIn)), (int) (windowSize - shiftIn));
        double Q = 0;
        for (int k = 0; k < fenetres_requises; k++) {
            for (int i = 0; i < windowSize; i++)
                windowIn[i] = signal[this.ta + i] * this.wa[i] //fenetrage


            Complex[] tf_windowIn = fft(windowIn);
            for (int i = 0; i < windowSize; i++)
                this.tfWindowOut[i] = new Complex(tf_windowIn[i].abs(), 0);
            for (int i = 0; i < windowSize; i++) {
                float nu_i = (float) i / windowSize;

                Q = (tf_windowIn[i].getArgument() - this.lastPhaseIn[i] - 2 * Math.PI * nu_i * shiftIn);
                Q %= 2 * Math.PI; // si ça ne marche pas, essayer Q -= 2*pi*floor((Q/(2*pi)))
                if (Q > Math.PI) //on se met dans -pi pi
                    Q -= 2 * Math.PI;

                double f_i = nu_i + Q / (2 * Math.PI * shiftIn);
                this.lastPhaseOut[i] = this.lastPhaseOut[i] + 2 * Math.PI * f_i * shiftOut;


                this.tfWindowOut[i] = tfWindowOut[i].multiply(new Complex(0, this.lastPhaseOut[i])).exp();
                this.lastPhaseIn[i] = tf_windowIn[i].getArgument();
            }


            Complex[] windowOutComplex = fft(fftReversed(this.tfWindowOut)); // TODO : résoudre le problème de types
            double[] windowOut = new double[windowSize];
            for (int i = 0; i < windowSize; i++)
                this.bufferSynthesis[this.ts + i] += this.wa[i] * windowOut[i];
            this.ta += shiftIn;
            this.ts += shiftOut;
        }

        //on prepare le prochain traitement du signal
        this.ts %= buffer_size;
        for (int k = this.ta; k < this.ta + windowSize - shiftIn)
            signal[k - this.ta] = signal[k];
        for (int k = windowSize - shiftIn; k < signal.length; k++)
            signal[k] = 0;

        return copie(this.bufferSynthesis) // TODO : à modifier pour qu'on puisse utiliser des buffers à la place
        // on l'evitera en java (on utilisera plutot une poignee de buffers alloues pendant l'initialisation et on les fera tourner)

    }

    /**
     * Modifie le ratio en temps réel
     *
     * @param ratio nouveau ratio
     * @return False si le ratio n'a pas été modifié, true sinon
     */
    public boolean setMusicRatio(double ratio) {
        if (ratio >= ratioMin && ratio <= ratioMax) {
            ratioTemp = ratio;
            return true;
        }
        return false;
    }
}
}
