package ca.uqam.info.inf5153.ptg;

import ca.uqam.ace.inf5153.mesh.io.Structs.*;

public class Tuile
{
    Polygon polygon;
    Biome biome;

    //private Altitude altitude;
    private int altitude;
    private int humiditeValeur;
    private boolean visible;

    // Constructeur
    public Tuile(Polygon p)
    {
        this.polygon = p;
        this.biome = null;
        this.altitude = -1; //valeur par défaut
        this.humiditeValeur = -1; //valeur par défaut
    }

    /* Getters et setters de la fonction */

    public Biome getBiome() { return this.biome; }
    public void setBiome(Biome b)
    {
        this.biome = b;
    }

    public int getAltitude() { return this.altitude; }
    public void setAltitude(int altitude)
    {
        this.altitude = altitude;
    }

    public int getHumidite(){ return this.humiditeValeur; };
    public void setHumidite( int val){
        this.humiditeValeur = val;
    }

    public void setVisible( Boolean val){
        this.visible = val;
    }
}
