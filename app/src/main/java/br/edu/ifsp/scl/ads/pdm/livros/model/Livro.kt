package br.edu.ifsp.scl.ads.pdm.livros.model

import android.os.Parcelable
//import kotlinx.android.parcel.Parcelize Antigo pacote do kotlin-extensions
import kotlinx.parcelize.Parcelize // Novo kotlin-parcelize

@Parcelize
data class Livro(
    val titulo: String,
    val isbn: String,
    val primeiroAutor: String,
    val editora: String,
    val edicao: Int,
    val paginas: Int
) : Parcelable
