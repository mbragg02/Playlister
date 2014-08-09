package com.mbragg.playlister.features;

import org.apache.commons.math3.distribution.MultivariateNormalDistribution;
import org.apache.commons.math3.linear.*;
import org.apache.commons.math3.stat.correlation.Covariance;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MultivariateNormalDistributionModel {

    public static final int SINGLE_COLUMN_DIMENSION = 1;
    public static final int INITIAL_COLUMN_ROW = 0;
    public static final double HALF_AS_DOUBLE = 0.5;

    public MultivariateNormalDistribution build(List<double[]> data) {
        RealMatrix matrix = createRealMatrix(data);
        return getMultivariateNormalDistribution(getMeanVectorOfMatrix(matrix), getCovarianceMatrix(matrix));

    }

    public double getSymmetricKullbackLeiblerDivergence(MultivariateNormalDistribution dx, MultivariateNormalDistribution dy) {
        double dxDy = getKullbackLeiblerDivergence(dx, dy);
        double dyDx = getKullbackLeiblerDivergence(dy, dx);
        return ((HALF_AS_DOUBLE * dxDy) + (HALF_AS_DOUBLE * dyDx));
    }

    public double getKullbackLeiblerDivergence(MultivariateNormalDistribution dx, MultivariateNormalDistribution dy) {

        RealVector meanVectorX = new ArrayRealVector(dx.getMeans());
        RealVector meanVectorY = new ArrayRealVector(dy.getMeans());

        final int meanVectorDimensions = meanVectorX.getDimension();

        RealMatrix covariancesMatrixX = dx.getCovariances();
        RealMatrix covariancesMatrixY = dy.getCovariances();

        RealMatrix inverseCovariancesMatrixY = getInverse(covariancesMatrixY);

        RealMatrix covariancesMultiplication = inverseCovariancesMatrixY.multiply(covariancesMatrixX);
        double traceOfCovariancesMultiplication = covariancesMultiplication.getTrace();

        RealMatrix meanMatrix = convertToMatrix(meanVectorY.subtract(meanVectorX), meanVectorDimensions);

        double logOfDeterminantDivision = Math.log(getDeterminant(covariancesMatrixX) / getDeterminant(covariancesMatrixY));

        double matrixReduction = transpose(meanMatrix)
                .multiply(inverseCovariancesMatrixY)
                .multiply(meanMatrix)
                .getEntry(INITIAL_COLUMN_ROW, INITIAL_COLUMN_ROW);

        return HALF_AS_DOUBLE * (traceOfCovariancesMultiplication + matrixReduction - meanVectorDimensions - logOfDeterminantDivision);

    }

    protected RealMatrix createRealMatrix(List<double[]> data) {
        int featureVectorDimension = data.get(INITIAL_COLUMN_ROW).length;
        RealMatrix matrix = new Array2DRowRealMatrix(data.size(), featureVectorDimension);

        for (int i = INITIAL_COLUMN_ROW; i < data.size(); i++) {
            matrix.setRow(i, data.get(i));
        }
        return matrix;

    }

    protected RealVector getMeanVectorOfMatrix(RealMatrix matrix) {
        Mean mean = new Mean();

        int columnDimensions = matrix.getColumnDimension();
        RealVector vector = new ArrayRealVector(columnDimensions);

        for (int i = INITIAL_COLUMN_ROW; i < columnDimensions; i++) {
            vector.addToEntry(i, mean.evaluate(matrix.getColumn(i)));
        }
        return vector;
    }

    protected RealMatrix getCovarianceMatrix(RealMatrix matrix) {
        return new Covariance(matrix).getCovarianceMatrix();
    }

    protected MultivariateNormalDistribution getMultivariateNormalDistribution(RealVector meanVector, RealMatrix covarianceMatrix) {
        return new MultivariateNormalDistribution(meanVector.toArray(), covarianceMatrix.getData());

    }

    protected RealMatrix convertToMatrix(RealVector vector, int vectorDimensions) {
        RealMatrix matrix = new Array2DRowRealMatrix(vectorDimensions, SINGLE_COLUMN_DIMENSION);
        matrix.setColumnVector(INITIAL_COLUMN_ROW, vector);

        return matrix;
    }

    protected RealMatrix transpose(RealMatrix matrix) {
        return matrix.transpose();
    }

    protected double getDeterminant(RealMatrix covariancesMatrix) {
        return new LUDecomposition(covariancesMatrix).getDeterminant();
    }

    protected RealMatrix getInverse(RealMatrix matrix) {
        return new LUDecomposition(matrix).getSolver().getInverse();
    }

}
