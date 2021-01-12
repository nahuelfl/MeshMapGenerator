package ca.uqam.info.inf5153.ptg;

import ca.uqam.ace.inf5153.mesh.io.Structs.*;

public class Aquifere extends Tuile
{
    boolean surface; //boolean indiquant si l'aquifere est à la surface ou pas

    // Constructeur
    public Aquifere(Polygon p, Boolean s)
    {
        super(p);

        this.surface = s;

        if(surface)
        {
            //si le plan d'eau est a la surface, on mets automatiquement la tuile comme un lagon
            this.biome = new LagonBiome();

        }
        else
        {
            //sinon on mets vegetation -> peux etre on devrait passer la valeur de soil aussi ? TODO
            this.biome = new VegetationBiome();
        }

    }

    public boolean isSurface() {
        return surface;
    }
}