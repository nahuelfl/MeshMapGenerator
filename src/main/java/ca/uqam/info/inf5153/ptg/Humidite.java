package ca.uqam.info.inf5153.ptg;

public enum Humidite
{
    DRY, REGULAR, WET; // Valeurs possibles pour l'humidite

    /**
     * Associe une valeur numérique à chaque état d el'humidité
     * et ensuite cette valeur est retournée
     *
     * @return valeur int
     */
    public int getValeur()
    {
        switch (this)
        {
            case DRY:
                return 30;

            case REGULAR:
                return 50;

            case WET:
                return 80;

            default:
                return 50;
        }
    }

    /**
     * Associe un état de l'enum Humidite dépendamment du String passé en parametre
     *
     * @param s String indiquant l'état d'humidité souhaité
     * @return
     */
    public static Humidite parseString(String s)
    {
        switch (s)
        {
            case "DRY":
                return DRY;

            case "REGULAR":
                return REGULAR;

            case "WET":
                return WET;

            default:
                return REGULAR;
        }
    }
}
