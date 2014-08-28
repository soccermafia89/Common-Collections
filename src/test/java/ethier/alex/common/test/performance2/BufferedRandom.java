/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ethier.alex.common.test.performance2;

import java.util.Random;

/**

In the case that true randomness isn't really important but speed is, create a preset number of semi-random values that can quickly be looked up.

 @author alex
 */
public class BufferedRandom {

    int[] intBuffer;
    double[] doubleBuffer;
    private int intBufferCount;
    private int doubleBufferCount;
    private int intMask;
    private int doubleMask;

    
    public BufferedRandom(long seed, int myIntMask, int myDoubleMask) {
        Random random = new Random(seed);

        intBufferCount = 0;
        doubleBufferCount = 0;

        intMask = myIntMask;
        doubleMask = myDoubleMask;

        intBuffer = new int[intMask];
        doubleBuffer = new double[doubleMask];

        for (int i = 0; i < intBuffer.length; i++) {
            intBuffer[i] = random.nextInt();
        }

        for (int i = 0; i < doubleBuffer.length; i++) {
            doubleBuffer[i] = random.nextDouble();
        }
    }
    
    public int nextInt(int maxSize) {
        return this.nextInt() & maxSize;
    }

    public int nextInt() {
        intBufferCount++;
        intBufferCount = intBufferCount & intMask;
        return intBuffer[intBufferCount];
    }

    public double nextDouble() {
        doubleBufferCount++;
        doubleBufferCount = doubleBufferCount & doubleMask;
        return doubleBuffer[doubleBufferCount];
    }
}
