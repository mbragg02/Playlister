package com.mbragg.playlister.models;

import org.apache.commons.math3.distribution.MultivariateNormalDistribution;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author Michael Bragg
 */
public class MultivariateNormalDistributionModelTest {

    private static final double DELTA = 1e-15;
    private MultivariateNormalDistributionModel multivariateNormalDistributionModel;

    @Before
    public void setUp() throws Exception {
        multivariateNormalDistributionModel = new MultivariateNormalDistributionModel();
    }

    @Test
    public void testBuild() throws Exception {
        double[] actualVector1 = {4, 7};
        double[] actualVector2 = {2, 6};
        double[] actualVector3 = {7, 8};
        List<double[]> data = new ArrayList<>();
        data.add(actualVector1);
        data.add(actualVector2);
        data.add(actualVector3);

        MultivariateNormalDistribution actualModel = multivariateNormalDistributionModel.build(data);
        double[] actualMeans = actualModel.getMeans();
        RealMatrix actualCoMatrix = actualModel.getCovariances();

        assertEquals(4.333333333333333, actualMeans[0], DELTA);
        assertEquals(7.0, actualMeans[1], DELTA);

        assertEquals(6.333333333333333, actualCoMatrix.getEntry(0, 0), DELTA);
        assertEquals(2.5, actualCoMatrix.getEntry(0, 1), DELTA);
        assertEquals(2.5, actualCoMatrix.getEntry(1, 0), DELTA);
        assertEquals(1.0, actualCoMatrix.getEntry(1, 1), DELTA);
    }

    @Test
    public void testTwoIdenticalModelsShouldHaveAKullbackLeiblerDivergenceOfZero() {
        double[] actualVector1A = {4, 7};
        double[] actualVector2A = {2, 6};
        double[] actualVector3A = {7, 8};
        List<double[]> dataA = new ArrayList<>();
        dataA.add(actualVector1A);
        dataA.add(actualVector2A);
        dataA.add(actualVector3A);

        MultivariateNormalDistribution actualModelA = multivariateNormalDistributionModel.build(dataA);

        double[] actualVector1B = {4, 7};
        double[] actualVector2B = {2, 6};
        double[] actualVector3B = {7, 8};
        List<double[]> dataB = new ArrayList<>();
        dataB.add(actualVector1B);
        dataB.add(actualVector2B);
        dataB.add(actualVector3B);

        MultivariateNormalDistribution actualModelB = multivariateNormalDistributionModel.build(dataB);

        double skl = multivariateNormalDistributionModel.getSymmetricKullbackLeiblerDivergence(actualModelA, actualModelB);

        assertEquals(0.0, skl, DELTA);
    }

    @Test
    public void testGetSymmetricKullbackLeiblerDivergence() throws Exception {
        double[] actualVector1A = {4, 7};
        double[] actualVector2A = {2, 6};
        double[] actualVector3A = {7, 8};
        List<double[]> dataA = new ArrayList<>();
        dataA.add(actualVector1A);
        dataA.add(actualVector2A);
        dataA.add(actualVector3A);

        MultivariateNormalDistribution actualModelA = multivariateNormalDistributionModel.build(dataA);

        double[] actualVector1B = {5, 7};
        double[] actualVector2B = {1, 5};
        double[] actualVector3B = {8, 7};
        List<double[]> dataB = new ArrayList<>();
        dataB.add(actualVector1B);
        dataB.add(actualVector2B);
        dataB.add(actualVector3B);

        MultivariateNormalDistribution actualModelB = multivariateNormalDistributionModel.build(dataB);

        double skl = multivariateNormalDistributionModel.getSymmetricKullbackLeiblerDivergence(actualModelA, actualModelB);

        assertEquals(19.253086419753203, skl, DELTA);
    }

    @Test
    public void testGetKullbackLeiblerDivergence() throws Exception {
        double[] actualVector1A = {4, 7};
        double[] actualVector2A = {2, 6};
        double[] actualVector3A = {7, 8};
        List<double[]> dataA = new ArrayList<>();
        dataA.add(actualVector1A);
        dataA.add(actualVector2A);
        dataA.add(actualVector3A);

        MultivariateNormalDistribution actualModelA = multivariateNormalDistributionModel.build(dataA);

        double[] actualVector1B = {5, 7};
        double[] actualVector2B = {1, 5};
        double[] actualVector3B = {8, 7};
        List<double[]> dataB = new ArrayList<>();
        dataB.add(actualVector1B);
        dataB.add(actualVector2B);
        dataB.add(actualVector3B);

        MultivariateNormalDistribution actualModelB = multivariateNormalDistributionModel.build(dataB);

        double kll = multivariateNormalDistributionModel.getKullbackLeiblerDivergence(actualModelA, actualModelB);

        assertEquals(2.409043419845344, kll, DELTA);
    }

    @Test
    public void testCreateRealMatrix() throws Exception {
        double[] actualVector1 = {4, 7};
        double[] actualVector2 = {2, 6};
        List<double[]> data = new ArrayList<>();
        data.add(actualVector1);
        data.add(actualVector2);

        RealMatrix actualMatrix = multivariateNormalDistributionModel.createRealMatrix(data);

        assertEquals(actualVector1[0], actualMatrix.getEntry(0, 0), DELTA);
        assertEquals(actualVector1[1], actualMatrix.getEntry(0, 1), DELTA);
        assertEquals(actualVector2[0], actualMatrix.getEntry(1, 0), DELTA);
        assertEquals(actualVector2[1], actualMatrix.getEntry(1, 1), DELTA);
    }

    @Test
    public void testGetMeanVectorOfMatrix() throws Exception {

        RealMatrix matrixData = new Array2DRowRealMatrix(2, 2);
        double[] row1 = {4, 7};
        double[] row2 = {2, 6};
        matrixData.setRow(0, row1);
        matrixData.setRow(1, row2);

        RealVector actualMeanVector = multivariateNormalDistributionModel.getMeanVectorOfMatrix(matrixData);

        double[] vectorData = {3.0, 6.5};
        RealVector expectedVector = new ArrayRealVector(vectorData);

        assertEquals(expectedVector.getEntry(0), actualMeanVector.getEntry(0), DELTA);
        assertEquals(expectedVector.getEntry(1), actualMeanVector.getEntry(1), DELTA);
    }

    @Test
    public void testGetCovarianceMatrix() throws Exception {
        RealMatrix matrixData = new Array2DRowRealMatrix(2, 2);
        double[] row1 = {4, 7};
        double[] row2 = {2, 6};
        matrixData.setRow(0, row1);
        matrixData.setRow(1, row2);

        RealMatrix expectedCovarianceMatrix = new Array2DRowRealMatrix(2, 2);
        double[] expectedRow1 = {2, 1};
        double[] expectedRow2 = {1, 0.5};
        expectedCovarianceMatrix.setRow(0, expectedRow1);
        expectedCovarianceMatrix.setRow(1, expectedRow2);

        RealMatrix actualCovarianceMatrix = multivariateNormalDistributionModel.getCovarianceMatrix(matrixData);

        for (int row = 0; row < actualCovarianceMatrix.getRowDimension(); row++) {
            double[] rowData = actualCovarianceMatrix.getRow(row);
            for (int column = 0; column < rowData.length; column++) {
                double value = rowData[column];
                assertEquals(expectedCovarianceMatrix.getEntry(row, column), value, DELTA);
            }
        }

    }

    @Test
    public void testGetMultivariateNormalDistribution() throws Exception {
        double[] vectorData = {1, 2};
        RealVector expectedVector = new ArrayRealVector(vectorData);

        RealMatrix expectedMatrix = new Array2DRowRealMatrix(2, 2);
        double[] row1 = {4, 7};
        double[] row2 = {2, 6};
        expectedMatrix.setRow(0, row1);
        expectedMatrix.setRow(1, row2);

        MultivariateNormalDistribution model = multivariateNormalDistributionModel.getMultivariateNormalDistribution(expectedVector, expectedMatrix);

        RealMatrix actualMatrix = model.getCovariances();
        double[] actualVector = model.getMeans();

        assertEquals(expectedVector.getEntry(0), actualVector[0], DELTA);
        assertEquals(expectedVector.getEntry(1), actualVector[1], DELTA);

        assertEquals(expectedMatrix.getEntry(0, 0), actualMatrix.getEntry(0, 0), DELTA);
        assertEquals(expectedMatrix.getEntry(0, 1), actualMatrix.getEntry(0, 1), DELTA);
        assertEquals(expectedMatrix.getEntry(1, 0), actualMatrix.getEntry(1, 0), DELTA);
        assertEquals(expectedMatrix.getEntry(1, 1), actualMatrix.getEntry(1, 1), DELTA);

    }

    @Test
    public void testConvertToMatrix() throws Exception {
        double[] expectedVector = {1, 2, 3, 4};
        RealVector vector = new ArrayRealVector(expectedVector);

        RealMatrix matrix = multivariateNormalDistributionModel.convertToMatrix(vector, vector.getDimension());

        assertEquals(1, matrix.getColumnDimension(), DELTA);
        assertEquals(expectedVector.length, matrix.getRowDimension(), DELTA);

        assertEquals(expectedVector[0], matrix.getEntry(0, 0), DELTA);
        assertEquals(expectedVector[1], matrix.getEntry(1, 0), DELTA);
        assertEquals(expectedVector[2], matrix.getEntry(2, 0), DELTA);
        assertEquals(expectedVector[3], matrix.getEntry(3, 0), DELTA);
    }

    @Test
    public void testTranspose() throws Exception {
        RealMatrix matrix = new Array2DRowRealMatrix(2, 2);
        double[] row1 = {4, 7};
        double[] row2 = {2, 6};
        matrix.setRow(0, row1);
        matrix.setRow(1, row2);

         /*
        a = 4, b = 7, c = 2, d = 6
        Transpose: Turn all the rows of a given matrix into columns and vice-versa.
         */

        RealMatrix expected = new Array2DRowRealMatrix(2, 2);
        double[] expectedRow1 = {4, 2};
        double[] expectedRow2 = {7, 6};
        expected.setRow(0, expectedRow1);
        expected.setRow(1, expectedRow2);

        RealMatrix transpose = multivariateNormalDistributionModel.transpose(matrix);

        for (int row = 0; row < transpose.getRowDimension(); row++) {
            double[] rowData = transpose.getRow(row);
            for (int column = 0; column < rowData.length; column++) {
                double value = rowData[column];
                assertEquals(expected.getEntry(row, column), value, DELTA);
            }
        }

    }

    @Test
    public void testGetDeterminant() throws Exception {
        RealMatrix matrix = new Array2DRowRealMatrix(2, 2);
        double[] row1 = {4, 7};
        double[] row2 = {2, 6};
        matrix.setRow(0, row1);
        matrix.setRow(1, row2);

        /*
        a = 4, b = 7, c = 2, d = 6
        Determinant: ad - bc
         */
        double expected = (4 * 6) - (7 * 2);

        assertEquals(expected, multivariateNormalDistributionModel.getDeterminant(matrix), DELTA);

    }

    @Test
    public void testGetInverse() throws Exception {
        RealMatrix matrix = new Array2DRowRealMatrix(2, 2);
        double[] row1 = {4, 7};
        double[] row2 = {2, 6};
        matrix.setRow(0, row1);
        matrix.setRow(1, row2);

        /*
        a = 4, b = 7, c = 2, d = 6
        Inverse: swap the positions of a and d, put negatives in front of b and c, and divide everything by the determinant (ad-bc).
         */
        RealMatrix expected = new Array2DRowRealMatrix(2, 2);
        double determinant = (4 * 6) - (7 * 2);
        double[] expectedRow1 = {6 / determinant, -7 / determinant};
        double[] expectedRow2 = {-2 / determinant, 4 / determinant};
        expected.setRow(0, expectedRow1);
        expected.setRow(1, expectedRow2);

        RealMatrix inverse = multivariateNormalDistributionModel.getInverse(matrix);

        for (int row = 0; row < inverse.getRowDimension(); row++) {
            double[] rowData = inverse.getRow(row);
            for (int column = 0; column < rowData.length; column++) {
                double value = rowData[column];
                assertEquals(expected.getEntry(row, column), value, DELTA);
            }
        }

    }
}