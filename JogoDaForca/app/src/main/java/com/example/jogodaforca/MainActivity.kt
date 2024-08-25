package com.example.jogodaforca

import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.jogodaforca.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    // Variável para acessar os elementos da interface do usuário
    private lateinit var binding: ActivityMainBinding
    // Variável para gerenciar a reprodução de som
    private lateinit var mediaPlayer: MediaPlayer

    // Categorias de palavras, cada uma mapeando para uma lista de palavras
    private val categorias = mapOf(
        "Tecnologia" to listOf("kotlin", "android", "desenvolvedor"),
        "Animais" to listOf("gato", "cachorro", "elefante"),
        "Cores" to listOf("vermelho", "azul", "amarelo")
    )

    // Variáveis para armazenar a palavra selecionada, sua categoria, e a palavra oculta
    private var palavraSelecionada: String = ""
    private var categoriaSelecionada: String = ""
    private var palavraOculta: CharArray = CharArray(0)
    // Número de tentativas restantes
    private var tentativas = 6
    // Lista de letras já adivinhadas
    private val letrasAdivinhadas = mutableListOf<Char>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Infla o layout usando o binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Seleciona uma categoria e uma palavra aleatória dessa categoria
        categoriaSelecionada = categorias.keys.random()
        palavraSelecionada = categorias[categoriaSelecionada]!!.random().uppercase()
        // Inicializa a palavra oculta com '_' para cada letra
        palavraOculta = CharArray(palavraSelecionada.length) { '_' }

        // Inicializa o MediaPlayer com o som de vitória
        mediaPlayer = MediaPlayer.create(this, R.raw.victory_sound)

        // Atualiza a interface inicial
        atualizarTela()

        // Define o comportamento do botão de adivinhar
        binding.buttonAdivinhar.setOnClickListener {
            val entrada = binding.editEntrada.text.toString().uppercase()

            if (entrada.isNotEmpty()) {
                val letra = entrada[0]

                if (letrasAdivinhadas.contains(letra)) {
                    // Exibe mensagem se a letra já foi utilizada
                    binding.textStatus.text = "Letra '$letra' já foi utilizada!"
                } else if (letra.isLetter()) {
                    // Adiciona a letra à lista de letras adivinhadas
                    letrasAdivinhadas.add(letra)
                    // Verifica se a letra está na palavra selecionada
                    if (palavraSelecionada.contains(letra)) {
                        for (i in palavraSelecionada.indices) {
                            if (palavraSelecionada[i] == letra) {
                                palavraOculta[i] = letra
                            }
                        }
                    } else {
                        // Decrementa as tentativas se a letra não estiver na palavra
                        tentativas--
                    }
                    // Limpa o campo de entrada e atualiza a interface
                    binding.editEntrada.text.clear()
                    atualizarTela()
                } else {
                    // Exibe mensagem de erro se a entrada não for uma letra válida
                    binding.textStatus.text = "Por favor, insira uma letra válida."
                }
            }
        }

        // Define o comportamento do botão de reiniciar
        binding.buttonReiniciar.setOnClickListener {
            reiniciarJogo()
        }

        // Define o comportamento do botão de voltar à tela inicial
        binding.buttonVoltarInicio.setOnClickListener {
            val intent = Intent(this, InicioActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    // Função para atualizar a interface do usuário com as informações mais recentes
    private fun atualizarTela() {
        // Exibe a palavra oculta e as letras adivinhadas
        binding.textOculto.text = palavraOculta.joinToString(" ")
        // Exibe a categoria da palavra
        binding.textCategoria.text = "Categoria: $categoriaSelecionada"
        // Exibe o número de tentativas restantes
        binding.textTentativas.text = "Tentativas restantes: $tentativas"
        // Exibe as letras já utilizadas
        binding.textLetrasUsadas.text = "Letras usadas: ${letrasAdivinhadas.joinToString(", ")}"

        // Verifica se o jogador ganhou ou perdeu e atualiza a interface
        if (!palavraOculta.contains('_')) {
            binding.textStatus.text = "Parabéns! Você acertou a palavra: $palavraSelecionada"
            binding.buttonAdivinhar.isEnabled = false
            mediaPlayer.start() // Toca o som de vitória
        } else if (tentativas <= 0) {
            binding.textStatus.text = "Você perdeu! A palavra era: $palavraSelecionada"
            binding.buttonAdivinhar.isEnabled = false
        }
    }

    // Função para reiniciar o jogo
    private fun reiniciarJogo() {
        // Seleciona uma nova palavra e categoria aleatória
        categoriaSelecionada = categorias.keys.random()
        palavraSelecionada = categorias[categoriaSelecionada]!!.random().uppercase()
        palavraOculta = CharArray(palavraSelecionada.length) { '_' }
        letrasAdivinhadas.clear()
        tentativas = 6
        binding.buttonAdivinhar.isEnabled = true
        binding.textStatus.text = ""
        mediaPlayer.stop()
        mediaPlayer.prepare()
        atualizarTela()
    }

    // Libera os recursos do MediaPlayer quando a atividade for destruída
    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }
}
