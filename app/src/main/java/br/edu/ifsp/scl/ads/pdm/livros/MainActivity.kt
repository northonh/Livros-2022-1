package br.edu.ifsp.scl.ads.pdm.livros

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import br.edu.ifsp.scl.ads.pdm.livros.adapter.LivrosAdapter
import br.edu.ifsp.scl.ads.pdm.livros.controller.LivroController
import br.edu.ifsp.scl.ads.pdm.livros.databinding.ActivityMainBinding
import br.edu.ifsp.scl.ads.pdm.livros.model.Livro
import br.edu.ifsp.scl.ads.pdm.livros.model.LivroDao
import br.edu.ifsp.scl.ads.pdm.livros.model.LivroSqlite

class MainActivity : AppCompatActivity() {
    companion object Extras {
        // const torna o objeto estático em java
        const val EXTRA_LIVRO = "EXTRA_LIVRO"
        const val EXTRA_POSICAO = "EXTRA_POSICAO"
    }
    private val activityMainBinding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    // Data source
    private val livrosList: MutableList<Livro> by lazy {
        livroController.buscarLivros()
    }

    // Adapter
    private val livrosAdapter: LivrosAdapter by lazy {
        LivrosAdapter(this, R.layout.layout_livro, livrosList)
    }

    // LivroActivityResultLauncher
    private lateinit var livroActivityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var editarLivroActivityResultLauncher: ActivityResultLauncher<Intent>

    // Referência para o controller
    private val livroController: LivroController by lazy {
        LivroController(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activityMainBinding.root)

        // Associar o ListView com o menu de contexto
        registerForContextMenu(activityMainBinding.livrosLv)

        // Associando Adapter ao ListView
        activityMainBinding.livrosLv.adapter = livrosAdapter

        // Registrando função callback para retorno de Activity
        livroActivityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { resultado ->
            if (resultado.resultCode == RESULT_OK) {
                val livro = resultado.data?.getParcelableExtra<Livro>(EXTRA_LIVRO)
                if (livro != null) {
                    livroController.inserirLivro(livro)
                    livrosAdapter.notifyDataSetChanged()
                }
            }
        }
        editarLivroActivityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { resultado ->
            if (resultado.resultCode == RESULT_OK) {
                val posicao = resultado.data?.getIntExtra(EXTRA_POSICAO, -1)
                resultado.data?.getParcelableExtra<Livro>(EXTRA_LIVRO)?.apply {
                    if (posicao != null && posicao != -1) {
                        // atualizando livro no banco
                        livroController.modificarLivro(this)
                        livrosAdapter.notifyDataSetChanged()
                    }
                }
            }
        }

        activityMainBinding.livrosLv.setOnItemClickListener(object: AdapterView.OnItemClickListener{
            override fun onItemClick(p0: AdapterView<*>?, p1: View?, posicao: Int, p3: Long) {
                val livro = livrosList[posicao]
                val consultarLivroIntent = Intent(this@MainActivity, LivroActivity::class.java)
                consultarLivroIntent.putExtra(EXTRA_LIVRO, livro)
                startActivity(consultarLivroIntent)
            }
        })

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.adicionarLivroMi -> {
            livroActivityResultLauncher.launch(Intent(this, LivroActivity::class.java))
            true
        }
        R.id.atualizarLivrosMi -> {
            livrosAdapter.notifyDataSetChanged()
            true
        }
        else -> {
            false
        }
    }

    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menuInflater.inflate(R.menu.context_menu_main, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val menuInfo = item.menuInfo as AdapterView.AdapterContextMenuInfo
        val posicao = menuInfo.position
        val livro = livrosList[posicao]

        return when(item.itemId) {
            R.id.editarLivroMi -> {
                val editarLivroIntent = Intent(this, LivroActivity::class.java)
                editarLivroIntent.putExtra(EXTRA_LIVRO, livro)
                editarLivroIntent.putExtra(EXTRA_POSICAO, posicao)
                editarLivroActivityResultLauncher.launch(editarLivroIntent)
                true
            }
            R.id.removerLivroMi -> {
                with(AlertDialog.Builder(this)) {
                    setTitle("Remoção de livro")
                    setMessage("Deseja realmente remover?")
                    setPositiveButton("Sim") { _, _ ->
                        livroController.apagarLivro(livro.titulo)
                        livrosAdapter.notifyDataSetChanged()
                        Toast.makeText(this@MainActivity, "Livro removido", Toast.LENGTH_SHORT).show()
                    }
                    setNegativeButton("Não") { _, _ ->
                        Toast.makeText(this@MainActivity, "Remoção cancelada", Toast.LENGTH_SHORT).show()
                    }
                    create()
                }.show()
                true
            }
            else -> { false }
        }
    }
}