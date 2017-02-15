package com.telecom_paristech.pact25.rhythmrun.interfaces.data;

/**
 * Created by Raphael Attali on 01/12/2016.
 */

/**
 * Interface liée aux sauvegardes dans la mémoire du téléphone
 */

import android.media.Image;

/**
 * Les fonctions de cette interface ont été trouvées avec l'aide de Haozhe Sun
 */

public interface DataSaveInterface {
    // encoder et sauvegarder dans la mémoire une image
    void imageDataEncoder(String pictureName);

    //encoder et sauvegarder dans la mémoire des informations chiffrées (nombre entier)
    void intDataEncoder(int dataNumber);

    // renvoie l'image appelée fileName située dans la mémoire
    Image imageDataDecoder(String fileName);

    // renvoie le nombre entier avec l'identifiant numberName
    int intDataDecoder(String numberName);

    // Déterminer le nombre de données maximales enregistrées
    void setDataMax(int dataMax);


}
