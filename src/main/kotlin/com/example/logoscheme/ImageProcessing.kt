package com.example.logoscheme

import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc
import org.opencv.core.Core
import org.opencv.core.Scalar
import weka.clusterers.SimpleKMeans
import weka.core.Instances
import weka.core.Attribute
import weka.core.DenseInstance


class ImageProcessing {
    private fun extractDominantColors(imagePath: String, numColors: Int): List<Scalar> {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME)

        val mat = Imgcodecs.imread(imagePath)

        if (mat.empty() || mat.channels() != 3) {
            println("ERROR RGB.")
            return emptyList()
        }

        Imgproc.cvtColor(mat, mat, Imgproc.COLOR_BGR2RGB)

        val pixelList = ArrayList<Scalar>()

        for (y in 0 until mat.rows()) {
            for (x in 0 until mat.cols()) {
                val pixel = mat.get(y, x)

                if (pixel.size < 3) {
                    println("Y= $y, X= $x")
                    return emptyList()
                }

                pixelList.add(Scalar(pixel[0], pixel[1], pixel[2]))
            }
        }

        return clusterColors(pixelList, numColors)
    }

    private fun clusterColors(colors: List<Scalar>, numColors: Int): List<Scalar> {
        if (numColors >= colors.size) {
            return colors
        }

        val attributes = ArrayList<Attribute>()
        attributes.add(Attribute("R"))
        attributes.add(Attribute("G"))
        attributes.add(Attribute("B"))

        val instances = Instances("Colors", attributes, colors.size)
        instances.setClassIndex(-1)

        for (color in colors) {
            val instance = DenseInstance(3)
            instance.setValue(0, color.`val`[0])
            instance.setValue(1, color.`val`[1])
            instance.setValue(2, color.`val`[2])
            instances.add(instance)
        }

        val kMeans = SimpleKMeans()
        kMeans.numClusters = numColors
        kMeans.buildClusterer(instances)

        val clusteredColors = ArrayList<Scalar>()
        for (i in 0 until numColors) {
            val clusterCenter = kMeans.clusterCentroids[i].toDoubleArray()
            clusteredColors.add(Scalar(clusterCenter[0], clusterCenter[1], clusterCenter[2]))
        }

        return clusteredColors
    }

    fun processImage(imagePath: String): List<Scalar> {
        println(imagePath)
        val numColors = 12
        val colors = extractDominantColors(imagePath, numColors)

        return colors

    }

}