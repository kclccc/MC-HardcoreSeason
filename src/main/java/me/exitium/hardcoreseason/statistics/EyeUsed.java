package me.exitium.hardcoreseason.statistics;

public class EyeUsed implements Comparable<EyeUsed> {

    private final String eyeUse;
    private final int numUsed;

    public EyeUsed(String eyeUse, int numUsed) {
        this.eyeUse = eyeUse;
        this.numUsed = numUsed;
    }

    public String getEyeUse() {
        return eyeUse;
    }

    public int getNumUsed() {
        return numUsed;
    }

    @Override
    public int compareTo(EyeUsed eu) {
        return Integer.compare(this.numUsed, eu.numUsed);
    }
}
