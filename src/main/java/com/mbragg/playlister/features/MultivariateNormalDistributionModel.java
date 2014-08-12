package com.mbragg.playlister.features;

import org.apache.commons.math3.distribution.MultivariateNormalDistribution;
import org.apache.commons.math3.linear.*;
import org.apache.commons.math3.stat.correlation.Covariance;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Multivariate Normal Distribution Model
 *
 * @author Michael Bragg
 */
@Component
public class MultivariateNormalDistributionModel {

    private static final int SINGLE_COLUMN_DIMENSION = 1;
    private static final int INITIAL_COLUMN_ROW = 0;
    private static final double HALF_AS_DOUBLE = 0.5;

    /**
     * Method to parse the model
     *
     * @param data List<double[]>. feature vectors for each window.
     * @return Multivariate Normal Distribution Model
     */
    public MultivariateNormalDistribution build(List<double[]> data) {
        RealMatrix matrix = createRealMatrix(data);
        return getMultivariateNormalDistribution(getMeanVectorOfMatrix(matrix), getCovarianceMatrix(matrix));
    }

    /**
     * For a given list of double[]'s, construct a matrix.
     *
     * @param data List<double[]>. feature vectors for each window.
     * @return RealMatrix. Matrix representation.
     */
    protected RealMatrix createRealMatrix(List<double[]> data) {
        int featureVectorDimension = data.get(INITIAL_COLUMN_ROW).length;
        RealMatrix matrix = new Array2DRowRealMatrix(data.size(), featureVectorDimension);

        for (int i = INITIAL_COLUMN_ROW; i < data.size(); i++) {
            matrix.setRow(i, data.get(i));
        }
        return matrix;
    }

    /**
     * For a given RealMatrix, calculate a mean vector.
     *
     * @param matrix RealMatrix. Matrix or feature vectors for each window.
     * @return RealVector. The mean vector for the data.
     */
    protected RealVector getMeanVectorOfMatrix(RealMatrix matrix) {
        Mean mean = new Mean();

        int columnDimensions = matrix.getColumnDimension();
        RealVector vector = new ArrayRealVector(columnDimensions);

        for (int i = INITIAL_COLUMN_ROW; i < columnDimensions; i++) {
            vector.addToEntry(i, mean.evaluate(matrix.getColumn(i)));
        }
        return vector;
    }

    /**
     * For a given RealMatrix, return the Covariance matrix.
     *
     * @param matrix RealMatrix. Matrix or feature vectors for each window.
     * @return RealMatrix. Covariance matrix.
     */
    protected RealMatrix getCovarianceMatrix(RealMatrix matrix) {
        return new Covariance(matrix).getCovarianceMatrix();
    }

    /**
     * For a given covariance matrix & mean vector, return a new Multivariate Normal Distribution model.
     *
     * @param meanVector       RealVector. The mean vector for the data.
     * @param covarianceMatrix RealMatrix. The covariance matrix for the data.
     * @return a new Multivariate Normal Distribution model.
     */
    protected MultivariateNormalDistribution getMultivariateNormalDistribution(RealVector meanVector, RealMatrix covarianceMatrix) {
        return new MultivariateNormalDistribution(meanVector.toArray(), covarianceMatrix.getData());
    }

    /**
     * For two Multivariate Normal Distribution models, calculate the symmetric Kullback-Leibler divergence.
     *
     * @param dx MultivariateNormalDistribution x
     * @param dy MultivariateNormalDistribution y
     * @return double. Symmetric Kullback-Leibler divergence.
     */
    public double getSymmetricKullbackLeiblerDivergence(MultivariateNormalDistribution dx, MultivariateNormalDistribution dy) {
        double dxDy = getKullbackLeiblerDivergence(dx, dy);
        double dyDx = getKullbackLeiblerDivergence(dy, dx);
        return ((HALF_AS_DOUBLE * dxDy) + (HALF_AS_DOUBLE * dyDx));
    }

    /**
     * For two Multivariate Normal Distribution models, calculate the Kullback-Leibler divergence.
     *
     * @param dx MultivariateNormalDistribution x
     * @param dy MultivariateNormalDistribution y
     * @return double. Kullback-Leibler divergence.
     */
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

    /**
     * For a given vector, return the data in the form of a matrix.
     *
     * @param vector           RealVector.
     * @param vectorDimensions int.
     * @return RealMatrix. The same data/orientation but in the form of a matrix.
     */
    protected RealMatrix convertToMatrix(RealVector vector, int vectorDimensions) {
        RealMatrix matrix = new Array2DRowRealMatrix(vectorDimensions, SINGLE_COLUMN_DIMENSION);
        matrix.setColumnVector(INITIAL_COLUMN_ROW, vector);

        return matrix;
    }

    /**
     * Transpose a matrix.
     *
     * @param matrix RealMatrix. Matrix to transpose.
     * @return RealMatrix. Transposed matrix.
     */
    protected RealMatrix transpose(RealMatrix matrix) {
        return matrix.transpose();
    }

    /**
     * Get the determinant of matrix.
     *
     * @param covariancesMatrix RealMatrix.
     * @return double.
     */
    protected double getDeterminant(RealMatrix covariancesMatrix) {
        return new LUDecomposition(covariancesMatrix).getDeterminant();
    }

    /**
     * Get the inverse of a matrix.
     *
     * @param matrix RealMatrix.
     * @return RealMatrix. The inverse of the input matrix.
     */
    protected RealMatrix getInverse(RealMatrix matrix) {
        return new LUDecomposition(matrix).getSolver().getInverse();
    }

}