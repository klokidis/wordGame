package com.example.wordgame

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import com.example.wordgame.databinding.ActivityMainBinding
import java.io.BufferedReader
import kotlin.random.Random
import java.io.InputStream
import java.io.InputStreamReader
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var binding:ActivityMainBinding
    private var letters : CharArray = CharArray(9)
    private var taken : CharArray = CharArray(9)
    private var takenLower : CharArray = CharArray(9)
    private var wordsSubmittedList = ArrayList<String>()
    private var countCorrect = 0
    private var words = 0



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setLetters()

        binding.TextInput.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_SPACE) {
                handleKeyEvent()
                return@OnKeyListener true
            }
            false
        })

        binding.button.setOnClickListener { // Handle button click here
            handleKeyEvent()
        }

        binding.buttonReset.setOnClickListener { // Handle button reset click here
            resetArray()
            setLetters()
            setExistedWords()
            words=0
        }
        setExistedWords()
    }

    private fun setExistedWords(){
        var takenString: String = String(letters)
        var amount = wordsPresent(takenString)
        binding.amountOfWords.text = amount.toString()
    }

    private fun setLetters(){
        resetArray()
        for(i in  0..8){
            letters[i] = generateRandomLetters()
            setTaken(letters[i],i)
        }
        setText()
    }

    private fun setText(){
        binding.text1.text = letters[0].toString()
        binding.text2.text = letters[1].toString()
        binding.text3.text = letters[2].toString()
        binding.text4.text = letters[3].toString()
        binding.text5.text = letters[4].toString()
        binding.text6.text = letters[5].toString()
        binding.text7.text = letters[6].toString()
        binding.text8.text = letters[7].toString()
        binding.text9.text = letters[8].toString()
    }

    private fun resetArray(){
        countCorrect=0
        letters.fill(' ')
        taken.fill(' ')
        takenLower.fill(' ')
        wordsSubmittedList.clear()
        binding.textOutPut.text = getString(R.string.output_text)
        binding.textOutPut.setTextColor(Color.WHITE)
        binding.count.text = countCorrect.toString()
    }

    private fun generateRandomLetters() : Char{
        var randomChar: Char
        var vowelCount = 0
        val vowels = listOf('A', 'E', 'I', 'O', 'U') // List of vowels

        do{
            randomChar = ('A'.code + Random.nextInt(26)).toChar()
            if (vowels.contains(randomChar)) {
                ++vowelCount
            }
        }while(checkExistence(randomChar))
        return randomChar
    }

    private fun checkExistence(aChar : Char): Boolean {
        for (i in taken){
            if(aChar == i){
                return true
            }
        }
        return false
    }

    private fun setTaken(aChar : Char, i : Int){
        taken[i] = aChar
        takenLower[i] = aChar.lowercaseChar()

    }

    private fun handleKeyEvent(){
            val inputtedWord: String = binding.TextInput.text.toString()
            println(inputtedWord)
            if (checkWordInFile(inputtedWord) && !wordIsAlreadyIn(inputtedWord) && inputtedWord.isNotEmpty() && inputtedWord.length>1 && checkWordExist(inputtedWord) && checkRepeat(inputtedWord)) {
                binding.textOutPut.setTextColor(Color.GREEN)
                if(words != 0){
                    words--
                    binding.amountOfWords.text=words.toString()
                }else{
                    binding.amountOfWords.text="you found them all!!:0"
                    binding.amountOfWords.setTextColor(Color.GREEN)
                }
                countCorrect++
                binding.count.text = countCorrect.toString()
                binding.textOutPut.text = getString(R.string.wrightInput)
                wordsSubmittedList.add(inputtedWord)
                binding.TextInput.text?.clear()
            }else{
                binding.textOutPut.setTextColor(Color.RED)
                binding.textOutPut.text = getString(R.string.wrongInput)
                binding.TextInput.text?.clear()
            }


        binding.TextInput.text?.clear()

    }

    private fun checkRepeat(word : String) : Boolean{
        for(i in word.indices){
            for(j in i+1..<word.length){
                if (word[i]==word[j]){
                    return false // Found a duplicate character
                }
            }
        }
        return true // No duplicates found
    }

    private fun checkWordExist(word : String) : Boolean{
        var b : Boolean
        for(element in word){
            val c: Char = element
            b=false
            for(element2 in takenLower){
                if(c == element2){
                    b=true
                    break
                }
            }
            if (!b) {
                return false;
            }
        }
        return true
    }

    private fun wordIsAlreadyIn(word:String):Boolean{
        for(i in 0..wordsSubmittedList.size){
            for(str in wordsSubmittedList){
                if(str == word){
                    return true
                }
            }
        }
        return false
    }

    private fun checkWordInFile(word: String): Boolean {
        val resourceId = resources.getIdentifier("filess", "raw", packageName)

        if (resourceId == 0) {
            // Resource not found, handle this error condition
            return false
        }

        try {
            val inputStream: InputStream = resources.openRawResource(resourceId)
            val reader = BufferedReader(InputStreamReader(inputStream))
            var inputString: String?

            while (reader.readLine().also { inputString = it } != null) {
                if (inputString?.lowercase(Locale.ROOT)?.equals(word.lowercase(Locale.ROOT)) == true) {
                    reader.close() // Close the reader when done
                    return true
                }
            }
            reader.close() // Close the reader when done
        } catch (e: Exception) {
            e.printStackTrace()
            // Handle exceptions and return false in case of an error
            return false
        }

        return false
    }

    private fun hasAllLetters(wordFromFile: String, existedLetter: String): Boolean {
        val charSet1 = wordFromFile.toLowerCase(Locale.ROOT).toSet()
        val charSet2 = existedLetter.toLowerCase(Locale.ROOT).toSet()

        return charSet1.all { charSet2.contains(it) }
    }

    private fun wordsPresent(givenLetters: String): Int {
        val resourceId = resources.getIdentifier("filess", "raw", packageName)
        val inputStream: InputStream = resources.openRawResource(resourceId)
        val reader = BufferedReader(InputStreamReader(inputStream))
        var inputString: String?

        while (reader.readLine().also { inputString = it } != null) {
            if (inputString?.let { hasAllLetters(it, givenLetters) } == true) {
                if(inputString!!.length>1 && checkRepeat(inputString!!)){
                    words++
                    Log.d("WordCriteria", "Passing word: $inputString")
                }
            }
        }
        reader.close()
        return words
    }

}