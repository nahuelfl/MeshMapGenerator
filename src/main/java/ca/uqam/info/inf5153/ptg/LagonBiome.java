package ca.uqam.info.inf5153.ptg;

import ca.uqam.ace.inf5153.mesh.io.Structs;

public class LagonBiome extends Biome
{
    @Override
    public Structs.Property colorier()
    {
        // Bleu clair
        String couleurRGBA = Color.BLEU_CLAIR.associerValeursRGBA(Color.BLEU_CLAIR);
        Structs.Property color = Structs.Property.newBuilder().setKey("color").setValue(couleurRGBA).build();

        return color;
    }
}
