package ca.uqam.info.inf5153.ptg;

import ca.uqam.ace.inf5153.mesh.io.Structs;

public class PlageBiome extends Biome
{
    @Override
    public Structs.Property colorier()
    {
        // Jaune
        String couleurRGBA = Color.JAUNE.associerValeursRGBA(Color.JAUNE);
        Structs.Property color = Structs.Property.newBuilder().setKey("color").setValue(couleurRGBA).build();

        return color;
    }

}
