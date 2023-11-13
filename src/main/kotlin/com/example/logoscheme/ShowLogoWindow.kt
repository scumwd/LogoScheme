package com.example.logoscheme

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import javax.swing.SwingUtilities

class ShowLogoWindow : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        ImageUploadDialog.show(project) { selectedImagePath ->
            val imageProcessing = ImageProcessing()
            val colors = imageProcessing.processImage(selectedImagePath)
            SwingUtilities.invokeLater { ShowColorsWindow(colors, e) }
        }
    }
}
