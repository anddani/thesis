package com.example.algo.whistledetector;

import static com.example.algo.whistledetector.Constants.SAMPLING_RATE;

public class ConvertDomain {

    /**
     * Windowing function appled to the real values in the samples array
     *
     * @param samples array of real and imaginary values from sample
     */
    public static void hanningWindow(double[] samples) {
        for (int i = 1; i < samples.length/2; i++) {
            samples[i] *= 0.5 * (1 - Math.cos((2 * Math.PI * i) / ((samples.length/2)-1f)));
        }
    }

    /**
     *  Applies windowing function to the input signal and converts
     *
     * @param freqDomain container for results from the FFT
     * @param doubleBuffer input to FFT, first half real, second half imaginary
     * @param trigTables pre-computed trigonometric tables
     */
    public static void timeToFrequency(Complex[] freqDomain, double[] doubleBuffer, TrigTables trigTables) {
        // Apply windowing function
        hanningWindow(doubleBuffer);

        // Run FFT and get sample in frequency domain
        SignalDetector.getFrequencyDomain(freqDomain, doubleBuffer, trigTables);
    }

    /**
     * Finds the frequency with the largest amplitude found in the frequency domain of the
     * sampled signal
     *
     * @param freqDomain output from FFT
     * @return array where first element is the frequency with the largest amplitude
     *         and the second element is the largest amplitude
     */
    public static int[] maxAmplitude(Complex[] freqDomain) {
        double maxAmplitude = 0.0;
        double bestFrequency = 0.0;

        double frequencyResolution = (1.0 * SAMPLING_RATE) / (1.0 * freqDomain.length);

        for (int i = 0; i < freqDomain.length/2; i++) {
            double frequency = i * frequencyResolution;
            double amplitude = freqDomain[i].abs();
            if (amplitude > maxAmplitude) {
                maxAmplitude = amplitude;
                bestFrequency = frequency;
            }
        }

        // Display frequency 0 if amplitude is 0
        bestFrequency = ((int)maxAmplitude == 0) ? 0.0 : bestFrequency;

        return new int[] {(int)bestFrequency, (int)maxAmplitude};
    }
}
