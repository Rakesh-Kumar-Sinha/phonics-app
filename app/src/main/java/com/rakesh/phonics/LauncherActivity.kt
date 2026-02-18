package com.rakesh.phonics

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.card.MaterialCardView

class LauncherActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launcher_test)
        window.statusBarColor = Color.parseColor("#026894")

//        val button = findViewById<MaterialButton>(R.id.btn_learn_single_letter_sound)
//        button.setOnClickListener {
//            startActivity(Intent(this, MainActivity::class.java))
//        }
//        val button2 = findViewById<MaterialButton>(R.id.btn_learn_letter_with_word_sounds)
//        button2.setOnClickListener {
//            startActivity(Intent(this, LearnSingleLetterActivity::class.java))
//        }
//
//        val button3 = findViewById<MaterialButton>(R.id.btn_learn_two_letter_sound)
//        button3.setOnClickListener {
//            startActivity(Intent(this, LearnDoubleLetter::class.java))
//        }
//        val button4 = findViewById<MaterialButton>(R.id.btn_learn_three_letter_sound)
//        button4.setOnClickListener {
//            startActivity(Intent(this, LearnTripleLetterActivity::class.java))
//        }
//
//        val button5 = findViewById<MaterialButton>(R.id.btn_learn_four_letter_sound)
//        button5.setOnClickListener {
//            startActivity(Intent(this, FourLetterActivity::class.java))
//        }

        findViewById<MaterialCardView>(R.id.card_practise_single_letter).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        findViewById<MaterialCardView>(R.id.card_learn_letter_with_word_sounds).setOnClickListener {
            startActivity(Intent(this, LearnSingleLetterActivity::class.java))
        }

        findViewById<MaterialCardView>(R.id.card_two_letter).setOnClickListener {
            startActivity(Intent(this, LearnDoubleLetter::class.java))
        }

        findViewById<MaterialCardView>(R.id.card_three_letter).setOnClickListener {
            startActivity(Intent(this, LearnTripleLetterActivity::class.java))
        }

        findViewById<MaterialCardView>(R.id.card_four_letter).setOnClickListener {
            startActivity(Intent(this, FourLetterActivity::class.java))
        }
        findViewById<MaterialCardView>(R.id.card_four_diagraphs).setOnClickListener {
//            startActivity(Intent(this, DigraphsActivity::class.java))
            startActivity(Intent(this, GraphsListActivity::class.java).putExtra("TYPE","digraphs"))
        }
        findViewById<MaterialCardView>(R.id.card_tri_graph).setOnClickListener {
//            startActivity(Intent(this, DigraphsActivity::class.java))
            startActivity(Intent(this, GraphsListActivity::class.java).putExtra("TYPE","trigraphs"))
        }
        findViewById<MaterialCardView>(R.id.card_split_di_graph).setOnClickListener {
//            startActivity(Intent(this, DigraphsActivity::class.java))
            startActivity(Intent(this, SplitDigraphList::class.java))
        }
        findViewById<MaterialCardView>(R.id.card_tricky_words).setOnClickListener {
//            startActivity(Intent(this, DigraphsActivity::class.java))
            startActivity(Intent(this, TrickyWordsActivity::class.java))
        }
    }
}