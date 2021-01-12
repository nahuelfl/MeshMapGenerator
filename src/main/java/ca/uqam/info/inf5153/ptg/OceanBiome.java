package ca.uqam.info.inf5153.ptg;

import ca.uqam.ace.inf5153.mesh.io.Structs;

public class OceanBiome extends Biome
{
    @Override
    protected Structs.Property colorier()
    {
        // Bleu Fonce
        String couleurRGBA = Color.BLEU_FONCE.associerValeursRGBA(Color.BLEU_FONCE);
        Structs.Property color = Structs.Property.newBuilder().setKey("color").setValue(couleurRGBA).build();

        return color;
    }

}
