package br.edu.ifsp.scl.ads.pdm.livros.model

import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

class LivroFirebase: LivroDao {
    private val LIVROS_LIST_ROOT_NODE = "livrosList"

    private val livrosList: MutableList<Livro> = mutableListOf()

    // Referência para o banco de dados
    private val livrosBd = Firebase.database.getReference(LIVROS_LIST_ROOT_NODE)
    init{
        livrosBd.addChildEventListener(object: ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val livro: Livro = snapshot.getValue<Livro>()?:Livro()

                if (livro != null) {
                    if (livrosList.indexOfFirst { it.titulo == livro.titulo } == -1) {
                        livrosList.add(livro)
                    }
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val livro: Livro = snapshot.getValue<Livro>()?:Livro()

                val posicao = livrosList.indexOfFirst { it.titulo == livro.titulo }
                livrosList[posicao] = livro
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                val livro: Livro = snapshot.getValue<Livro>()?:Livro()
                livrosList.remove(livro)
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                // NSA
            }

            override fun onCancelled(error: DatabaseError) {
                // NSA
            }
        })
    }

    override fun criarLivro(livro: Livro): Long {
        criaOuAtualizaLivro(livro)
        return 0L
    }

    override fun recuperarLivro(titulo: String): Livro {
        val posicao = livrosList.indexOfFirst { it.titulo == titulo }
        return livrosList[posicao]
    }

    override fun recuperarLivros(): MutableList<Livro> {
       return livrosList
    }

    override fun atualizarLivro(livro: Livro): Int {
        criaOuAtualizaLivro(livro)
        return 1
    }

    override fun removerLivro(titulo: String): Int {
        livrosBd.child(titulo).removeValue()
        return 1
    }

    private fun criaOuAtualizaLivro(livro: Livro) {
        livrosBd.child(livro.titulo).setValue(livro)
    }
}