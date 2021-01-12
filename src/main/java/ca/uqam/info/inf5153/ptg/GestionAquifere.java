package ca.uqam.info.inf5153.ptg;

import ca.uqam.ace.inf5153.mesh.io.Structs;

import java.awt.*;
import java.util.List;
import java.util.Random;

public class GestionAquifere
{
    private int nombreAquifere;
    private String maxHumidite;
    private Tuile[] tuiles;

    // Constructeur
    public GestionAquifere(String nombreAquifere, String maxHumidite, Tuile[] tuiles)
    {
        int waterSpots = Integer.parseInt(nombreAquifere);

        this.nombreAquifere = waterSpots;
        this.maxHumidite = maxHumidite;
        this.tuiles = tuiles;
    }

    public Tuile[] placerAquiferes()
    {
        Humidite humiditeMax = Humidite.parseString(this.maxHumidite);

        Random rd = new Random();

        do
        {
            boolean aquiferePlace = false;
            for(int i = 0; i < tuiles.length; i++) // Parcourt toutes les tuiles
            {
                //seules les tuiles de vegetation peuvent devenir des plans d'eau
                if(tuiles[i].getBiome() instanceof VegetationBiome)
                {
                    if (tuiles[i].getHumidite() != 1 && aquiferePlace == false)
                    {
                        //placer les plans d'eau de facon aleatoire avec exactement nombreAquifere plans d'eau
                        if(rd.nextBoolean())
                        {
                            aquiferePlace = true;
                            // le plan d'eau sera aleatoirement visible ou non (surface ou sous terrain)
                            if(rd.nextBoolean())
                            {
                                //surface -> devient tuile d'eau
                                this.tuiles[i].setBiome(new LagonBiome());
                                tuiles[i].setVisible(true);
                                tuiles[i].setHumidite(100);
                            }
                            else
                            {
                                // sous terrain -> reste tuile vegetation max humidite = max
                                tuiles[i].setVisible(false);
                                tuiles[i].setHumidite(100);
                            }
                        }
                    }
                }

            }
            nombreAquifere -= 1;
        }
        while(nombreAquifere > 0);

        propagerHumidite(humiditeMax);

        return this.tuiles;
    }

    private void propagerHumidite(Humidite humiditeMax)
    {
        int humiditePropageeValeur = humiditeMax.getValeur();

        while (humiditePropageeValeur != 0)
        {
            for (int i = 0; i < tuiles.length; i++) // Parcourt toutes les tuiles
            {
                if (tuiles[i].getHumidite() == 100 || tuiles[i].getHumidite() == humiditePropageeValeur + 10 )
                {
                    //si c'est un plan d'eau on parcourt les voisins pour diffuser l'eau
                    for (int j = 0; j < tuiles[i].polygon.getNeighborsCount(); j++)
                    {
                        int centroidVoisin = tuiles[i].polygon.getNeighbors(j);

                        Tuile tuileVoisine = null;
                        for (int k = 0; k < tuiles.length; k++) // Parcourt toutes les tuiles pour trouver la bonne tuile
                        {
                            if (tuiles[k].polygon.getCentroidIdx() == centroidVoisin && tuiles[k].getBiome() instanceof VegetationBiome)
                            {
                                tuileVoisine = tuiles[k];
                                break;
                            }
                        }
                        if (tuileVoisine != null && tuileVoisine.getHumidite() == -1){
                            tuileVoisine.setHumidite(humiditePropageeValeur);
                        }
                    }
                }
            }
            humiditePropageeValeur = humiditePropageeValeur - 10;
        }
    }
}
