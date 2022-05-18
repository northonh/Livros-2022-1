package br.edu.ifsp.scl.ads.pdm.livros

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import br.edu.ifsp.scl.ads.pdm.livros.MainActivity.Extras.EXTRA_LIVRO
import br.edu.ifsp.scl.ads.pdm.livros.MainActivity.Extras.EXTRA_POSICAO
import br.edu.ifsp.scl.ads.pdm.livros.databinding.ActivityLivroBinding
import br.edu.ifsp.scl.ads.pdm.livros.model.Livro

class LivroActivity : AppCompatActivity() {
    private val activityLivroBinding: ActivityLivroBinding by lazy {
        ActivityLivroBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activityLivroBinding.root)

        // Verificando se recebi um livro e uma posição
        val livro: Livro? = intent.getParcelableExtra(EXTRA_LIVRO)
        val posicao: Int? = intent.getIntExtra(EXTRA_POSICAO, -1)
        if (livro != null) {
            with(activityLivroBinding) {
                tituloEt.setText(livro.titulo)
                isbnEt.setText(livro.isbn)
                primeiroAutorEt.setText(livro.primeiroAutor)
                editoraEt.setText(livro.editora)
                edicaoEt.setText(livro.edicao.toString())
                paginasEt.setText(livro.paginas.toString())
            }
            if (posicao == -1) {
                with( activityLivroBinding) {
                    tituloEt.isEnabled = false
                    isbnEt.isEnabled = false
                    primeiroAutorEt.isEnabled = false
                    editoraEt.isEnabled = false
                    edicaoEt.isEnabled = false
                    paginasEt.isEnabled = false
                    //salvarBt.visibility = View.GONE
                    salvarBt.setText("Voltar")
                }
            }
        }

        with (activityLivroBinding) {
            salvarBt.setOnClickListener {
                val livro: Livro = Livro(
                    tituloEt.text.toString(),
                    isbnEt.text.toString(),
                    primeiroAutorEt.text.toString(),
                    editoraEt.text.toString(),
                    edicaoEt.text.toString().toInt(),
                    paginasEt.text.toString().toInt()
                )

                val resultadoIntent: Intent = Intent()
                resultadoIntent.putExtra(EXTRA_LIVRO, livro)
                resultadoIntent.putExtra(EXTRA_POSICAO, posicao)

                setResult(RESULT_OK, resultadoIntent)
                finish()
            }
        }
    }
}