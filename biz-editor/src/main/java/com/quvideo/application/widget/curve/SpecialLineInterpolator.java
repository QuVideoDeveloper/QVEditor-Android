package com.quvideo.application.widget.curve;

public class SpecialLineInterpolator {

  private double[] sd;
  private double[] arrayX;
  private double[] arrayY;

  public void interpolate(double[] xvals, double[] yvals) {
    int n = xvals.length;
    arrayX = xvals;
    arrayY = yvals;

    // build the tridiagonal system
    // (assume 0 boundary conditions: y2[0]=y2[-1]=0)
    double[][] matrix = new double[n][3];
    double[] result = new double[n];
    matrix[0][1] = 1;
    for (int i = 1; i < n - 1; i++) {
      matrix[i][0] = (xvals[i] - xvals[i - 1]) / 6;
      matrix[i][1] = (xvals[i + 1] - xvals[i - 1]) / 3;
      matrix[i][2] = (xvals[i + 1] - xvals[i]) / 6;
      result[i] = (yvals[i + 1] - yvals[i]) / (xvals[i + 1] - xvals[i])
          - (yvals[i] - yvals[i - 1]) / (xvals[i] - xvals[i - 1]);
    }
    matrix[n - 1][1] = 1;

    // solving pass1 (up->down)
    for (int i = 1; i < n; i++) {
      double k = matrix[i][0] / matrix[i - 1][1];
      matrix[i][1] -= k * matrix[i - 1][2];
      matrix[i][0] = 0;
      result[i] -= k * result[i - 1];
    }
    // solving pass2 (down->up)
    for (int i = n - 2; i >= 0; i--) {
      double k = matrix[i][2] / matrix[i + 1][1];
      matrix[i][1] -= k * matrix[i + 1][0];
      matrix[i][2] = 0;
      result[i] -= k * result[i + 1];
    }

    // return second derivative value for each point P
    double[] y2 = new double[n];
    for (int i = 0; i < n; i++) {
      y2[i] = result[i] / matrix[i][1];
    }
    sd = y2;
  }

  public double value(double x) {
    if (arrayX == null || arrayY == null) {
      return 0;
    }
    for (int i = 0; i < arrayX.length - 1; i++) {
      if (arrayX[i] <= x && x < arrayX[i + 1]) {
        double t = (x - arrayX[i]) / (arrayX[i + 1] - arrayX[i]);
        double a = 1 - t;
        double b = t;
        double h = arrayX[i + 1] - arrayX[i];
        double y = a * arrayY[i] + b * arrayY[i + 1] + (h * h / 6) * ((a * a * a - a) * sd[i]
            + (b * b * b - b) * sd[i + 1]);
        return y;
      }
    }
    return 0;
  }
}
