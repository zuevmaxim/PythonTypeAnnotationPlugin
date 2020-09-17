import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.jetbrains.python.psi.PyNamedParameter
import com.jetbrains.python.psi.PyTargetExpression

class AddTypeAnnotationAction : IntentionAction {
    override fun startInWriteAction() = false
    override fun getText() = "Add 'int' type annotation."
    override fun getFamilyName() = "MyIntention"

    override fun isAvailable(project: Project, editor: Editor?, file: PsiFile?): Boolean {
        if (editor == null || file == null) {
            return false
        }
        val offset = editor.caretModel.offset
        if (!shouldAddAnnotationAtOffset(file, offset) && !shouldAddAnnotationAtOffset(file, offset - 1)) {
            return false
        }
        return true
    }

    override fun invoke(project: Project, editor: Editor?, file: PsiFile?) {
        if (editor == null || file == null) return

        val caretOffset = editor.caretModel.currentCaret.offset
        val possibleOffsets = listOf(caretOffset, caretOffset - 1)
        for (offset in possibleOffsets) {
            if (shouldAddAnnotationAtOffset(file, offset)) {
                addAnnotationAtOffset(project, editor, file, offset)
                break
            }
        }
    }

    private fun shouldAddAnnotationAtOffset(file: PsiFile, offset: Int): Boolean {
        val element = file.findElementAt(offset) ?: return false
        val parent = element.parent
        return shouldAddAnnotation(parent)
    }

    private fun addAnnotationAtOffset(project: Project, editor: Editor, file: PsiFile, offset: Int) {
        val element = file.findElementAt(offset) ?: return
        val parent = element.parent
        addAnnotation(project, editor, parent)
    }

    private fun shouldAddAnnotation(element: PsiElement) =
        shouldParameterHaveAnnotation(element) || shouldVariableHaveAnnotation(element)

    private fun shouldParameterHaveAnnotation(element: PsiElement) =
        element is PyNamedParameter && element.annotation == null

    private fun shouldVariableHaveAnnotation(element: PsiElement) =
        element is PyTargetExpression && element.annotation == null

    private fun addAnnotation(project: Project, editor: Editor, element: PsiElement) {
        val document = editor.document
        val annotation = ": int"
        val endOffset = element.textRange.endOffset
        WriteCommandAction.runWriteCommandAction(project) {
            document.insertString(endOffset, annotation)
        }
        editor.caretModel.currentCaret.moveToOffset(endOffset + annotation.length)
    }

}