package ca.uqam.info.inf5153.ptg;

import ca.uqam.ace.inf5153.mesh.io.Structs;

public class VegetationBiome extends Biome{

    @Override
    public Structs.Property colorier()
    {
        // Vert
        Structs.Property color = Structs.Property.newBuilder().setKey("color").setValue("0:84:4:175").build(); //transparence != color.vert

        return color;
    }


}
