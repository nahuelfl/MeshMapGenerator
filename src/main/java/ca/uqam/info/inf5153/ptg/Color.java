package ca.uqam.info.inf5153.ptg;

public enum Color
{
    VERT, BLEU_FONCE, BLEU_CLAIR, JAUNE, NOIR; // Valeurs possibles de Color

    /**
     * Associe une String contenant le code RGBA dépendamment du Color passé en paramètre
     *
     * @param color Color duquel son association RGBA est cherchée
     * @return
     */
    public String associerValeursRGBA(Color color)
    {
        String RGBA = "00:00:00";

        switch(color)
        {
            case VERT: RGBA = "0:84:4"; break;
            case BLEU_FONCE: RGBA = "9:0:85"; break;
            case BLEU_CLAIR: RGBA = "44:201:198"; break;
            case JAUNE: RGBA = "255:216:56"; break;
            case NOIR: RGBA = "00:00:00"; break;
        }

        return RGBA;
    }
}


