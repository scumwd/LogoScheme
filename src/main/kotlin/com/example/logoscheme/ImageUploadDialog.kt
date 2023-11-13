package com.example.logoscheme

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.openapi.ui.VerticalFlowLayout
import javax.swing.JComponent
import javax.swing.JPanel

class ImageUploadDialog(
    private val project: Project,
    private val onImageSelected: (String) -> Unit
) : DialogWrapper(project) {
    private val imageFileField = TextFieldWithBrowseButton()

    init {
        title = "Upload Image"
        setOKButtonText("Upload")
        init()
    }

    override fun createCenterPanel(): JComponent? {
        val panel = JPanel(VerticalFlowLayout())

        imageFileField.addBrowseFolderListener(
            "Select Image",
            "Choose an image file to upload",
            project,
            com.intellij.openapi.fileChooser.FileChooserDescriptorFactory.createSingleFileDescriptor()
        )

        panel.add(imageFileField)

        return panel
    }

    override fun doOKAction() {
        val selectedImagePath = imageFileField.text
        onImageSelected(selectedImagePath)
        super.doOKAction()
    }

    companion object {
        fun show(project: Project, onImageSelected: (String) -> Unit) {
            val dialog = ImageUploadDialog(project, onImageSelected)
            dialog.show()
        }
    }
}
