package ca.uqam.info.inf5153.ptg;

import java.util.Random;

public class Seed
{
    private int value;

    public Seed()
    {
        Random rand = new Random();
        int randInt = rand.nextInt();
        this.value = randInt;
    }

    public Seed(int value)
    {
        this.value = value;
    }

    public Seed(String input)
    {
        this.value = Integer.parseInt(input);
    }

    public int getValue()
    {
        return this.value;
    }

    /*
    * compare entre la valeur par default de seed et la valeur en entree
    * retourne le bon seed a passer comme parametre a tortuga
    * */
    public Seed effectiveSeed(Seed s)
    {
        Seed result;
        if (s.value != this.value && s.value > 0)
        {
            result = s;
        }
        else
        {
            result = new Seed();
        }
        return result;
    }
}
