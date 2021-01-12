package ca.uqam.info.inf5153.ptg;

import ca.uqam.ace.inf5153.mesh.io.Structs;
import com.google.protobuf.Message;

public class GestionColorier
{
    Tuile[] tuiles;
    Structs.Mesh.Builder builder;
    int maxAltitude;

    // Constructeur
    public GestionColorier(String heatmap, Tuile[] tuiles, Structs.Mesh.Builder builder, int maxAltitude)
    {
        this.tuiles = tuiles;
        this.builder = builder;
        this.maxAltitude = maxAltitude;
    }

    /**
     * Fonction de gestion qui fait l'appel aux fonctions pertinentes pour le coloriage du Mesh selon heatmap
     *
     * @param heatmap String indiquant la vue (aspect) du mesh souhaitée par l'usager
     * @return le Mesh.builder
     */
    public Structs.Mesh.Builder colorier(String heatmap)
    {
        if(heatmap.equalsIgnoreCase("altitude"))
        {
            colorierAltitude();
        }
        else if(heatmap.equalsIgnoreCase("humidity"))
        {
            colorierAquifere(Color.BLEU_FONCE);
        }
        else if(heatmap.equalsIgnoreCase("ocean"))
        {
            colorierOcean(Color.BLEU_FONCE);
        }
        else if(heatmap.equalsIgnoreCase("oceanNoir"))
        {
            colorierOcean(Color.NOIR);
        }
        else
        {
            colorierBase();
            colorierAquifere(Color.VERT);
        }

        return builder;
    }

    /**
     * Fonction qui colorie selon l'altitude en augmentant la transparence d'un polygone plus l'altitude diminue
     */
    public void colorierAltitude()
    {
        String currentColor = "0";

        // Altitude viewer
        for(int j = 1; j <= this.maxAltitude; j++)
        {
            int couleursPossibles = 255 / this.maxAltitude;
            currentColor = Integer.toString(j * couleursPossibles);

            for (int i = 0; i < builder.getPolygonsCount(); i++) // Parcourt toutes les tuiles
            {
                if(tuiles[i].getAltitude() == j)
                {
                    Structs.Property color = Structs.Property.newBuilder().setKey("color").setValue("255:99:71:" + currentColor).build();
                    builder.getPolygonsBuilder(i).addProperties(color);
                }
            }
        }
    }

    /**
     *  Fonction qui colorie selon l'humidite de chaque tuile
     * @param couleurBiome
     */
    public void colorierAquifere(Color couleurBiome)
    {
        for (int i = 0; i < builder.getPolygonsCount(); i++) // Parcourt toutes les tuiles
        {
            Structs.Property color = Structs.Property.newBuilder().setKey("color").setValue("00:00:00").build();

            if (tuiles[i].getHumidite() == 100)
            {
                // tuile d'eau
                color = Structs.Property.newBuilder().setKey("color").setValue(couleurBiome.associerValeursRGBA(couleurBiome)).build();
            }
            else if (tuiles[i].getHumidite() != -1)
            {
                String couleur = Integer.toString(tuiles[i].getHumidite()+175); // sinon trop pale presque blanc
                color = Structs.Property.newBuilder().setKey("color").setValue(couleurBiome.associerValeursRGBA(couleurBiome) + ":" + couleur).build();
            }
            if( tuiles[i].polygon.getPropertiesCount() > 0 && color.getValue() != "00:00:00")
            {
                builder.getPolygonsBuilder(i).setProperties(0,color);
            }
            else if (tuiles[i].polygon.getPropertiesCount() == 0 &&  color.getValue() != "00:00:00")
            {
                builder.getPolygonsBuilder(i).addProperties(color);
            }
        }
    }

    /**
     * Colorie la base du Mesh selon les Biomes associés aux tuiles
     */
    public void colorierBase()
    {

        for (int i = 0; i < builder.getPolygonsCount(); i++) // Parcourt toutes les tuiles
        {
            // Colorier
            if(this.tuiles[i].getBiome() instanceof PlageBiome || (this.tuiles[i].getBiome() instanceof VegetationBiome && this.tuiles[i].getHumidite() == -1) || tuiles[i].getBiome() instanceof LagonBiome)
                builder.getPolygonsBuilder(i).addProperties(this.tuiles[i].getBiome().colorier());
        }

        this.builder = builder;
    }

    /**
     * Fonction qui colorie seulement les tuiles ayant un OceanBiome de la couleur souhaitee couleurBiome
     */
    public void colorierOcean(Color couleurBiome)
    {
        for (int i = 0; i < builder.getPolygonsCount(); i++) // Parcourt toutes les tuiles
        {
            // Colorier si tuile a OceanBiome associe
            if((this.tuiles[i].getBiome() instanceof OceanBiome))
            {
                Structs.Property color = Structs.Property.newBuilder().setKey("color").setValue(couleurBiome.associerValeursRGBA(couleurBiome)).build();
                builder.getPolygonsBuilder(i).addProperties(color);
            }
        }
    }
}
