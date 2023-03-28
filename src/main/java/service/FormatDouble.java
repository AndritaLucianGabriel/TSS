package service;

public interface FormatDouble {

    static double format(double d) {
        return (double) Math.round(d * 100) / 100;
    }

}
