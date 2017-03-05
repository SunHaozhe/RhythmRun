package com.telecom_paristech.pact25.rhythmrun.music.phase_vocoder;

/**
 * Created by Raphael Attali on 05/03/2017.
 * <p>
 * Project RythmRun
 */

import java.util.Arrays;

/**
 * Permet de faire une interpolation
 */
public class SelfMadeInterpolator {

    /**
     * Equivalent de interp1(x,y,xi,'linear') de Matlab
     * 
     * @param abscisses Le tableau d'abscisses (équivalent de x sous matlab)
     * @param ordonnees Le tableau d'ordonnées  (équivalent de y sous matlab)
     * @param abscissesInterp Les points d'interpolation (équivalent de xi sous matlab)
     * @return Le tableau d'ordonnées interpolées
     * @throws IllegalArgumentException
     */
    public static final double[] interp1(double[] abscisses, double[] ordonnees, double[] abscissesInterp) throws IllegalArgumentException {

        if (abscisses.length != ordonnees.length) {
            throw new IllegalArgumentException("Les 2 longueurs ne correspondent pas !");
        }
        if (abscisses.length == 1) {
            throw new IllegalArgumentException("Taille du tableau abscisses trop faible");
        }
        double[] dx = new double[abscisses.length - 1];
        double[] dy = new double[abscisses.length - 1];
        double[] slope = new double[abscisses.length - 1];
        double[] intercept = new double[abscisses.length - 1];

        // Calcul de l'équation de la ligne entre 2 points
        for (int i = 0; i < abscisses.length - 1; i++) {
            dx[i] = abscisses[i + 1] - abscisses[i];
            if (dx[i] <= 0) {
                throw new IllegalArgumentException("Le tableau d'abscisse ne convient pas");
            }
            
            dy[i] = ordonnees[i + 1] - ordonnees[i];
            slope[i] = dy[i] / dx[i];
            intercept[i] = ordonnees[i] - abscisses[i] * slope[i];
        }

        // Interpolation
        double[] yInterpole = new double[abscissesInterp.length];
        for (int i = 0; i < abscissesInterp.length; i++) {
            if ((abscissesInterp[i] > abscisses[abscisses.length - 1]) || (abscissesInterp[i] < abscisses[0])) {
                yInterpole[i] = Double.NaN;
            }
            else {
                int loc = Arrays.binarySearch(abscisses, abscissesInterp[i]);
                if (loc < -1) {
                    loc = -loc - 2;
                    yInterpole[i] = slope[loc] * abscissesInterp[i] + intercept[loc];
                }
                else {
                    yInterpole[i] = ordonnees[loc];
                }
            }
        }

        return yInterpole;
    }
}
