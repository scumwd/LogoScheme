package com.example.logoscheme

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.XmlElementFactory
import com.intellij.psi.xml.XmlFile
import com.intellij.psi.xml.XmlTag
import org.opencv.core.Scalar
import java.awt.Color
import java.awt.Dimension
import java.awt.GridLayout
import javax.swing.*
import javax.swing.border.LineBorder
import kotlin.math.max
import kotlin.math.min
import com.intellij.openapi.project.Project

class ShowColorsWindow(colors: List<Scalar>, val e: AnActionEvent) : JFrame() {
    init {
        title = "Color Display"
        defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE

        val colorDisplayPanel = ColorDisplayPanel(colors)
        contentPane.add(colorDisplayPanel)

        val palettes = createColorPalettes(colors)

        val buttonPanel = JPanel()
        val gridLayout = GridLayout(0, 1)
        buttonPanel.layout = gridLayout

        for ((index, palette) in palettes.withIndex()) {
            when (index) {
                0 -> buttonPanel.add(JLabel("Monochromatic"))
                1 -> buttonPanel.add(JLabel("Complementary"))
                2 -> buttonPanel.add(JLabel("Triadic"))
                3 -> buttonPanel.add(JLabel("Square"))
                4 -> buttonPanel.add(JLabel("Analogous"))
                else -> buttonPanel.add(JLabel("Palette $index"))
            }

            val paletteButton = createPaletteButton(palette)
            paletteButton.addActionListener { e ->
                handleButtonClick(palette)
            }
            buttonPanel.add(paletteButton)
        }

        val scrollPane = JScrollPane(buttonPanel)
        colorDisplayPanel.add(scrollPane)

        pack()
        setLocationRelativeTo(null)
        isVisible = true
    }

    private fun handleButtonClick(colors: List<Scalar>) {
        val project = e.project ?: return
        WriteCommandAction.runWriteCommandAction(project) {
            val psiFile = findColorsXmlFile(project)

            if (psiFile is XmlFile) {
                val factory = XmlElementFactory.getInstance(project)
                for ((index, color) in colors.withIndex()) {
                    var colorText = String()
                    when (index) {
                        0 -> colorText = "background"
                        1 -> colorText = "text_color"
                        2 -> colorText = "accent"
                    }
                    val colorHex = scalarToHexString(color)
                    val newColorTag = factory.createTagFromText("<color name=\"$colorText\">$colorHex</color>")
                    val rootTag = psiFile.rootTag

                    if (rootTag != null) {
                        rootTag.addSubTag(newColorTag, false)
                        FileDocumentManager.getInstance().saveDocument(psiFile.viewProvider.document)
                    }

                }

            }
        }
    }

    private fun findColorsXmlFile(project: Project): PsiFile? {
        val virtualFile = project.baseDir?.findFileByRelativePath("app/src/main/res/values/colors.xml")
        return virtualFile?.let { PsiManager.getInstance(project).findFile(it) }
    }

    private fun createPaletteButton(colors: List<Scalar>): JButton {
        val button = JButton()
        val palettePanel = JPanel()

        for (color in colors) {
            val colorPanel = JPanel()
            colorPanel.background = colorFromScalar(color)
            colorPanel.preferredSize = Dimension(30, 30)
            palettePanel.add(colorPanel)
        }

        button.preferredSize = Dimension(140, 45)
        button.add(palettePanel)
        button.border = LineBorder(Color.BLACK)

        return button
    }

    private fun scalarToHexString(scalar: Scalar): String {
        print(scalar)
        val red = scalar.`val`[0].toInt()
        val green = scalar.`val`[1].toInt()
        val blue = scalar.`val`[2].toInt()
        val redHex = red.toString(16).padStart(2, '0')
        val greenHex = green.toString(16).padStart(2, '0')
        val blueHex = blue.toString(16).padStart(2, '0')

        return "#$redHex$greenHex$blueHex"
    }

    private fun colorFromScalar(scalar: Scalar): Color {
        val r = scalar.`val`[0].toInt()
        val g = scalar.`val`[1].toInt()
        val b = scalar.`val`[2].toInt()

        return Color(r, g, b)
    }

    private fun createColorPalettes(colors: List<Scalar>): List<List<Scalar>> {
        val monoPalette = createMonochromaticPalette(colors).toList()
        val complementPalette = createComplementaryPalette(colors).toList()
        val triadicPalette = createTriadicPalette(colors).toList()
        val squarePalette = createSquarePalette(colors).toList()
        val analogousPalette = createAnalogousPalette(colors).toList()

        return listOf(monoPalette, complementPalette, triadicPalette, squarePalette, analogousPalette)
    }

    private fun createMonochromaticPalette(colors: List<Scalar>): Triple<Scalar, Scalar, Scalar> {
        val baseColor = colors.random()

        val darker = Scalar(
            max(0.0, baseColor.`val`[0] - 50),
            max(0.0, baseColor.`val`[1] - 50),
            max(0.0, baseColor.`val`[2] - 50)
        )

        val lighter = Scalar(
            min(255.0, baseColor.`val`[0] + 50),
            min(255.0, baseColor.`val`[1] + 50),
            min(255.0, baseColor.`val`[2] + 50)
        )

        return Triple(baseColor, darker, lighter)
    }

    private fun createComplementaryPalette(colors: List<Scalar>): Triple<Scalar, Scalar, Scalar> {
        val baseColor = colors.random()
        val complementaryColor = findComplementaryColor(baseColor)
        val textColor = findContrastTextColor(baseColor, complementaryColor, colors)

        return Triple(baseColor, textColor, complementaryColor)
    }

    // Цвета, находящихся на равном расстоянии от базового цвета в цветовом круге
    private fun createTriadicPalette(colors: List<Scalar>): Triple<Scalar, Scalar, Scalar> {
        val baseColor = colors.random()
        val triadicColor1 = findTriadicColor(baseColor, colors)
        val triadicColor2 = findTriadicColor(triadicColor1, colors)

        return Triple(baseColor, triadicColor1, triadicColor2)
    }

    private fun createSquarePalette(colors: List<Scalar>): Triple<Scalar, Scalar, Scalar> {
        val baseColor = colors.random()

        return findSquareColors(baseColor)
    }

    // Цвета находящихся соседними с базовым цветом в цветовом круге
    private fun createAnalogousPalette(colors: List<Scalar>): Triple<Scalar, Scalar, Scalar> {
        val baseColor = colors.random()
        val analogousColor = findAnalogousColor(baseColor, colors)
        val textColor = findContrastTextColor(baseColor, analogousColor, colors)

        return Triple(baseColor, textColor, analogousColor)
    }

    private fun findComplementaryColor(color: Scalar): Scalar {
        val r = 255.0 - color.`val`[0]
        val g = 255.0 - color.`val`[1]
        val b = 255.0 - color.`val`[2]

        return Scalar(r, g, b)
    }

    //Цвет находящийся на трети цветового круга от заданного цвета
    private fun findTriadicColor(color: Scalar, colors: List<Scalar>): Scalar {
        val index = colors.indexOf(color)
        val offset = colors.size / 3
        val triadicIndex = (index + offset) % colors.size
        return colors[triadicIndex]
    }

    private fun findSquareColors(color: Scalar): Triple<Scalar, Scalar, Scalar> {
        val hsv = colorToHSV(color)

        val angle1 = (hsv.`val`[0] + 90.0) % 360.0
        val angle2 = (hsv.`val`[0] + 180.0) % 360.0
        val angle3 = (hsv.`val`[0] + 270.0) % 360.0

        val squareColor1 = Scalar(angle1, hsv.`val`[1], hsv.`val`[2])
        val squareColor2 = Scalar(angle2, hsv.`val`[1], hsv.`val`[2])
        val squareColor3 = Scalar(angle3, hsv.`val`[1], hsv.`val`[2])

        return Triple(colorFromHSV(squareColor1), colorFromHSV(squareColor2), colorFromHSV(squareColor3))
    }


    private fun colorToHSV(color: Scalar): Scalar {
        val rgb = doubleArrayOf(color.`val`[0] / 255.0, color.`val`[1] / 255.0, color.`val`[2] / 255.0)
        val max = rgb.maxOrNull() ?: 0.0
        val min = rgb.minOrNull() ?: 0.0
        val delta = max - min
        val hsv = doubleArrayOf(0.0, 0.0, max * 100.0)

        when (max) {
            rgb[0] -> hsv[0] = 60.0 * ((rgb[1] - rgb[2]) / delta % 6)
            rgb[1] -> hsv[0] = 60.0 * ((rgb[2] - rgb[0]) / delta + 2)
            rgb[2] -> hsv[0] = 60.0 * ((rgb[0] - rgb[1]) / delta + 4)
        }

        if (max != 0.0) {
            hsv[1] = delta / max * 100.0
        }

        return Scalar(hsv)
    }

    private fun colorFromHSV(hsv: Scalar): Scalar {
        val c = hsv.`val`[2] / 100.0 * hsv.`val`[1] / 100.0
        val x = c * (1 - Math.abs(hsv.`val`[0] / 60.0 % 2 - 1))
        val m = hsv.`val`[2] / 100.0 - c
        val rgb: DoubleArray

        rgb = when {
            hsv.`val`[0] < 60 -> doubleArrayOf(c, x, 0.0)
            hsv.`val`[0] < 120 -> doubleArrayOf(x, c, 0.0)
            hsv.`val`[0] < 180 -> doubleArrayOf(0.0, c, x)
            hsv.`val`[0] < 240 -> doubleArrayOf(0.0, x, c)
            hsv.`val`[0] < 300 -> doubleArrayOf(x, 0.0, c)
            else -> doubleArrayOf(c, 0.0, x)
        }

        val r = (rgb[0] + m) * 255.0
        val g = (rgb[1] + m) * 255.0
        val b = (rgb[2] + m) * 255.0

        return Scalar(r, g, b)
    }


    //Цвет находящийся соседним с заданным цветом
    private fun findAnalogousColor(color: Scalar, colors: List<Scalar>): Scalar {
        val index = colors.indexOf(color)
        val offset = 1
        val analogousIndex = (index + offset) % colors.size
        return colors[analogousIndex]
    }

    private fun calculateLuminance(color: Scalar): Double {
        val r = color.`val`[0] / 255.0
        val g = color.`val`[1] / 255.0
        val b = color.`val`[2] / 255.0

        return 0.2126 * r + 0.7152 * g + 0.0722 * b
    }

    private fun calculateContrast(color1: Scalar, color2: Scalar): Double {
        val luminance1 = calculateLuminance(color1)
        val luminance2 = calculateLuminance(color2)

        val brightest = max(luminance1, luminance2)
        val darkest = min(luminance1, luminance2)

        return (brightest + 0.05) / (darkest + 0.05)
    }

    private fun findContrastTextColor(background: Scalar, accent: Scalar, colors: List<Scalar>): Scalar {
        val MIN_CONTRAST = 4.5

        val contrastColors = colors.filter { color ->
            val contrastWithBackgroundColor = calculateContrast(color, background)
            val contrastWithAccentColor = calculateContrast(color, accent)
            contrastWithBackgroundColor > MIN_CONTRAST && contrastWithAccentColor > MIN_CONTRAST
        }

        if (contrastColors.isNotEmpty()) {
            return contrastColors.first()
        }

        val hsvBackground = colorToHSV(background)
        val hsvAccent = colorToHSV(accent)

        val angle = ((hsvBackground.`val`[0] + hsvAccent.`val`[0]) / 2 + 180.0) % 360.0

        val generatedColor = Scalar(
            angle,
            (hsvBackground.`val`[1] + hsvAccent.`val`[1]) / 2,
            (hsvBackground.`val`[2] + hsvAccent.`val`[2]) / 2
        )

        return colorFromHSV(generatedColor)
    }

}