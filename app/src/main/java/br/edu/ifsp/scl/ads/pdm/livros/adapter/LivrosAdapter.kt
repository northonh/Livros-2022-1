package br.edu.ifsp.scl.ads.pdm.livros.adapter

import android.content.Context
import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import br.edu.ifsp.scl.ads.pdm.livros.R
import br.edu.ifsp.scl.ads.pdm.livros.databinding.LayoutLivroBinding
import br.edu.ifsp.scl.ads.pdm.livros.model.Livro

class LivrosAdapter(val contexto: Context, val leiaute: Int, val listaLivros: MutableList<Livro>) :
    ArrayAdapter<Livro>(contexto, leiaute, listaLivros) {

    private data class LivroLayoutHolder(
        val tituloTv: TextView,
        val primeiroAutorTv: TextView,
        val editoraTv: TextView
    )

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val livroLayoutView: View
        if (convertView != null) {
            // View já existe
            livroLayoutView = convertView
        }
        else {
            // View precisa ser inflada
            val layoutLivroBinding = LayoutLivroBinding.inflate(contexto.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater, parent, false)
            livroLayoutView = layoutLivroBinding.root

            with (layoutLivroBinding) {
                val holder = LivroLayoutHolder(tituloTv, primeiroAutorTv, editoraTv)
                livroLayoutView.tag = holder
            }
        }

        // Preenchendo ou atualizando View
        val livro = listaLivros[position]
        with (livroLayoutView.tag as LivroLayoutHolder) {
            tituloTv.text = livro.titulo
            primeiroAutorTv.text = livro.primeiroAutor
            editoraTv.text = livro.editora
        }

        return livroLayoutView
    }
}