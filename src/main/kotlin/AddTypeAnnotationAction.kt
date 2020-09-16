import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.command.WriteCommandAction

class AddTypeAnnotationAction : AnAction() {
    override fun actionPerformed(event: AnActionEvent) {
        val editor = event.getData(CommonDataKeys.EDITOR) ?: return
        val file = event.getData(CommonDataKeys.PSI_FILE) ?: return
        val project = event.getData(CommonDataKeys.PROJECT) ?: return

        val document = editor.document
        val offset = editor.caretModel.currentCaret.offset
        val element = file.findElementAt(offset) ?: return

        WriteCommandAction.runWriteCommandAction(project) {
            document.insertString(element.textRange.endOffset - 1, ": int")
        }
    }

}