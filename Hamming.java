// Use with "java Hamming.java".
// Edit main() to test function.

import java.util.HashSet;
import java.util.Set;

/**
*A hamming code class
*/
public class Hamming {
    public static void main(String[] args) {
      Hamming h = encode(1);
      System.out.println(h);
      h.inversBit(5);
      System.out.println(h);
      System.out.println("correct ? "+h.isCorrect());
      h.fix();
      System.out.println(h);
      System.out.println("correct ? "+h.isCorrect());
    }

    // end main ////////////////////////////////////////////////////////////////
    /**
     * x P1 P2 D1 P3 D2 D3 D4
     */
    private byte b;

    public byte getByte() {
        return b;
    }

    private Hamming(byte b) {
        this.b = b;
    }

    /**
     * to create Hamming code with error
     */
    public Hamming(int x) {
        this((byte) x);
    }

    public String toString() {
        String sr = "";
        for (int i = 6; i > -1; i--) {
            // if (i == 3) {
            //     sr = " ";
            // }
            sr = getBit(i)+sr;
        }
        return sr;
    }

    /**
     * {@summary Function to create a Hamming code.}
     */
    public static Hamming encode(int x) {
        if (x > 15) {
            x = 15;
        } else if (x < 0) {
            x = 0;
        }
        byte b = (byte) x;
        //encode parity bits.
        Hamming horig = new Hamming(b);
        Hamming h = new Hamming(b);

        // Les bits a encoder arrivent comme ça:
        // D1 D2 D3 D4
        // 3  2  1  0

        // Les parités sont calculées :
        // P1 = D1,D2,D4  (soit les bits d'indice 3,2,0)
        // P2 = D1,D3,D4  (soit les bits d'indice 3,1,0)
        // P3 = D2,D3,D4  (soit les bits d'indice 2,1,0)


        byte p1 = parity(h.getBit(3), h.getBit(2), h.getBit(0));
        byte p2 = parity(h.getBit(3), h.getBit(1), h.getBit(0));
        byte p3 = parity(h.getBit(2), h.getBit(1), h.getBit(0));

        // On compose le byte de Hamming de la façon suivante:
        // P1 P2 D1 P3 D2 D3 D4

        // Ex:
        // Encoder 11
        //  => D1 D2 D3 D4  : 1011

        // P1 = 0
        // P2 = 1
        // P3 = 0
        //
        // Le byte composé est :
        // =>      1  1  0  0  1  1  0
        //        P1 P2 D1 P3 D2 D3 D4
        h.setBit(0, p1);
        h.setBit(1, p2);
        h.setBit(3, p3);

        h.setBit(2, horig.getBit(3));
        h.setBit(4, horig.getBit(2));
        h.setBit(5, horig.getBit(1));
        h.setBit(6, horig.getBit(0));

        return h;
    }

    /**
     * {@summary Function to decode the Hamming code.}
     */
    public int decode() {
        fix();
        //return b%16;

        Hamming h = new Hamming(0);

        // On a:
        //        P1 P2 D1 P3 D2 D3 D4
        // On doit générer:
        // D1 D2 D3 D4
        h.setBit(0, getBit(6));
        h.setBit(1, getBit(5));
        h.setBit(2, getBit(4));
        h.setBit(3, getBit(2));
        return h.getByte();
    }

    /**
     * {@summary Function to find if there is an error in the Hamming code.}
     */
    public boolean isCorrect() {
        // P1 = D1,D2,D4
        // P2 = D1,D3,D4
        // P3 = D2,D3,D4
        // On compose    P1 P2 D1 P3 D2 D3 D4

        if (getBit(0) != parity(getBit(2), getBit(4), getBit(6))) {
            return false;
        }
        if (getBit(1) != parity(getBit(2), getBit(5), getBit(6))) {
            return false;
        }
        if (getBit(3) != parity(getBit(4), getBit(5), getBit(6))) {
            return false;
        }
        return true;
    }

    /**
     * {@summary Function to fix 1 error in the Hamming code.}
     */
    public boolean fix() {
        if (isCorrect()) {
            return false; //nothing have been fix.
        }

        // P1 = D1,D2,D4
        // P2 = D1,D3,D4
        // P3 = D2,D3,D4
        // On compose    P1 P2 D1 P3 D2 D3 D4
        boolean b1 = getBit(0) == parity(getBit(2), getBit(4), getBit(6));
        boolean b2 = getBit(1) == parity(getBit(2), getBit(5), getBit(6));
        boolean b3 = getBit(3) == parity(getBit(4), getBit(5), getBit(6));

        // System.out.println(b1 + " " + b2 + " " + b3);

        if (!b1 && !b2 && !b3) {
            inversBit(6);
        } // Erreur sur D4
        else if (!b1 && !b2) {
            inversBit(2);
        } // Erreur sur D1
        else if (!b2 && !b3) {
            inversBit(5);
        } // Erreur sur D3
        else if (!b1 && !b3) {
            inversBit(4);
        }  // Erreur sur D2
        return true; //something have been fix.

    }
    //private tools ---------------------------------------------

    /**
     * {@summary Function to do parity from 3 bits.}
     */
    private static byte parity(byte b1, byte b2, byte b3) {
        return (byte) ((b1 + b2 + b3) % 2);
    }

    /**
     * {@summary Function to get a bit from position.}
     */
    private byte getBit(int position) {
        return (byte) ((b >> position) & 1);
    }

    public void setBit(int position, byte value) {
        //System.out.println("setbit  pos " +position+" value " + value + "h "+ this);
        if (value != 0) {
            b = (byte) ((1 << position) | b);
        } else {
            b = (byte) (~(1 << position) & b);
        }
        // System.out.println("setbit apres" +this);

    }

    /**
     * {@summary Function to inverse a bit from position.}
     */
     private void inversBit(int position) {
       byte n = getBit(position);
       setBit(position, (byte) ((n+2)%2-1));
     }

}
