package com.example.logoscheme

import org.opencv.core.Scalar
import java.awt.Color
import java.awt.Dimension
import javax.swing.JLabel
import javax.swing.JPanel

class ColorDisplayPanel(colors: List<Scalar>) : JPanel() {
    init {
        val dominantColor = colors[0]
        val mainPaletteColors = colors.subList(1, colors.size)

        val dominantLabel = JLabel("Доминирующий цвет")

        val dominantColorPanel = JPanel()
        dominantColorPanel.background = colorFromScalar(dominantColor)
        dominantColorPanel.preferredSize = Dimension(50, 50)

        add(dominantLabel)
        add(dominantColorPanel)

        val mainPaletteLabel = JLabel("Основная палитра")
        add(mainPaletteLabel)

        for (color in mainPaletteColors) {
            val mainPaletteColorPanel = JPanel()
            mainPaletteColorPanel.background = colorFromScalar(color)
            mainPaletteColorPanel.preferredSize = Dimension(50, 50)

            val colorWithLabelPanel = JPanel()
            colorWithLabelPanel.add(mainPaletteColorPanel)
            add(colorWithLabelPanel)
        }
    }

    private fun colorFromScalar(scalar: Scalar): Color {
        val r = scalar.`val`[0].toInt()
        val g = scalar.`val`[1].toInt()
        val b = scalar.`val`[2].toInt()
        return Color(r, g, b)
    }
}