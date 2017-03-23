package com.example.algo.benchmarkapp.algorithms;

/******************************************************************************
 *  Compilation:  javac Complex.java
 *  Execution:    java Complex
 *
 *  Data type for complex numbers.
 *
 *  The data type is "immutable" so once you create and initialize
 *  a Complex object, you cannot change it. The "final" keyword
 *  when declaring re and im enforces this rule, making it a
 *  compile-time error to change the .re or .im instance variables after
 *  they've been initialized.
 *
 *  % java Complex
 *  a            = 5.0 + 6.0i
 *  b            = -3.0 + 4.0i
 *  Re(a)        = 5.0
 *  Im(a)        = 6.0
 *  b + a        = 2.0 + 10.0i
 *  a - b        = 8.0 + 2.0i
 *  a * b        = -39.0 + 2.0i
 *  b * a        = -39.0 + 2.0i
 *  a / b        = 0.36 - 1.52i
 *  (a / b) * b  = 5.0 + 6.0i
 *  conj(a)      = 5.0 - 6.0i
 *  |a|          = 7.810249675906654
 *  tan(a)       = -6.685231390246571E-6 + 1.0000103108981198i
 *
 ******************************************************************************/

import java.util.Objects;

public class FloatComplex {
    private final float re;   // the real part
    private final float im;   // the imaginary part

    // create a new object with the given real and imaginary parts
    public FloatComplex(float real, float imag) {
        re = real;
        im = imag;
    }

    // return a string representation of the invoking Complex object
    public String toString() {
        if (im == 0) return re + "";
        if (re == 0) return im + "i";
        if (im <  0) return re + " - " + (-im) + "i";
        return re + " + " + im + "i";
    }

    // return abs/modulus/magnitude
    public float abs() {
        return (float)Math.hypot(re, im);
    }

    // return a new Complex object whose value is (this + b)
    public FloatComplex plus(FloatComplex b) {
        FloatComplex a = this;             // invoking object
        float real = a.re + b.re;
        float imag = a.im + b.im;
        return new FloatComplex(real, imag);
    }

    // return a new Complex object whose value is (this - b)
    public FloatComplex minus(FloatComplex b) {
        FloatComplex a = this;
        float real = a.re - b.re;
        float imag = a.im - b.im;
        return new FloatComplex(real, imag);
    }

    // return a new Complex object whose value is (this * b)
    public FloatComplex times(FloatComplex b) {
        FloatComplex a = this;
        float real = a.re * b.re - a.im * b.im;
        float imag = a.re * b.im + a.im * b.re;
        return new FloatComplex(real, imag);
    }

    // return a new object whose value is (this * alpha)
    public FloatComplex scale(float alpha) {
        return new FloatComplex(alpha * re, alpha * im);
    }

    // return a new Complex object whose value is the conjugate of this
    public FloatComplex conjugate() {
        return new FloatComplex(re, -im);
    }

    // return a new Complex object whose value is the reciprocal of this
    public FloatComplex reciprocal() {
        float scale = re*re + im*im;
        return new FloatComplex(re / scale, -im / scale);
    }

    // return the real or imaginary part
    public float re() { return re; }
    public float im() { return im; }

    // return a / b
    public FloatComplex divides(FloatComplex b) {
        FloatComplex a = this;
        return a.times(b.reciprocal());
    }

    // return a new Complex object whose value is the complex exponential of this
    public FloatComplex exp() {
        return new FloatComplex((float)(Math.exp(re) * Math.cos(im)), (float)(Math.exp(re) * Math.sin(im)));
    }

    // return a new Complex object whose value is the complex sine of this
    public FloatComplex sin() {
        return new FloatComplex((float)(Math.sin(re) * Math.cosh(im)), (float)(Math.cos(re) * Math.sinh(im)));
    }

    // return a new Complex object whose value is the complex cosine of this
    public FloatComplex cos() {
        return new FloatComplex((float)(Math.cos(re) * Math.cosh(im)), (float)(-Math.sin(re) * Math.sinh(im)));
    }

    // return a new Complex object whose value is the complex tangent of this
    public FloatComplex tan() {
        return sin().divides(cos());
    }
    


    // a static version of plus
    public static FloatComplex plus(FloatComplex a, FloatComplex b) {
        float real = a.re + b.re;
        float imag = a.im + b.im;
        FloatComplex sum = new FloatComplex(real, imag);
        return sum;
    }

    // See Section 3.3.
    public boolean equals(Object x) {
        if (x == null) return false;
        if (this.getClass() != x.getClass()) return false;
        FloatComplex that = (FloatComplex) x;
        return (this.re == that.re) && (this.im == that.im);
    }

    // See Section 3.3.
    public int hashCode() {
        return Objects.hash(re, im);
    }

}
