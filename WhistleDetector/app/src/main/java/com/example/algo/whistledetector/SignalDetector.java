package com.example.algo.whistledetector;

public class SignalDetector {

    static {
        System.loadLibrary("native-lib");
    }

    static int[] getHighestAmplitude(double[] samples, FFTColumbiaIterative fftci) {

        // Compute FFT
        System.out.println("samples length: " + samples.length);
        double[] z = new double[nextPowerOfTwo(samples.length)*2];
        System.out.println("z length: " + z.length);
        for (int i = 0; i < samples.length; i++) {
            z[i] = samples[i];
            z[i+samples.length] = 0.0;
        }

        double[] nativeResult = fft(z, fftci.sin, fftci.cos);

        Complex[] x = new Complex[z.length/2];
        for (int i = 0; i < z.length/2; i++) {
            x[i] = new Complex(nativeResult[i], nativeResult[i+samples.length]);
        }

        // Get frequency with max amplitude
        double maxAmplitude = 0.0;
        double bestFrequency = 0.0;
        double frequencyFactor = (1.0 * ReadMic.SAMPLING_RATE) / (1.0 * z.length);
        for (int i = 0; i < x.length/2; i++) {
            double frequency = i * frequencyFactor;
            double amplitude = x[i].abs();
            if (frequency > ReadMic.FREQ_MAX) {
                break;
            }
            if (amplitude > maxAmplitude) {
                maxAmplitude = amplitude;
                bestFrequency = frequency;
            }
        }
        return new int[] {(int)bestFrequency, (int)maxAmplitude};
    }

    public static int nextPowerOfTwo(int v) {
        v--;
        v |= v >> 1;
        v |= v >> 2;
        v |= v >> 4;
        v |= v >> 8;
        v |= v >> 16;
        v++;
        return v;
    }

    public static native double[] fft(double[] arr, double[] sin, double[] cos);
}
